package controllers.admin;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Page;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.enwie.api.BaseResponse;
import com.enwie.util.CommonFunction;
import controllers.BaseController;
import models.*;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Http.MultipartFormData;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Html;
import views.html.admin.article._form;
import views.html.admin.article.detail;
import views.html.admin.article.list;
import views.html.admin.article.preview;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author hendriksaragih
 */
public class ArticleController extends BaseController {
    private final static Logger.ALogger logger = Logger.of(ArticleController.class);
    private static final String TITLE = "Article";
    private static final String featureKey = "article";

    private static Html htmlList(){
        RoleFeature feature = getUserAccessByFeature(featureKey);
        return list.render(TITLE, "List", feature);
    }

    private static Html htmlEdit(Form<Article> data){
        return _form.render(TITLE, "Edit", data, routes.ArticleController.update(), getListCategory(), getListTags(data.get().tags));
    }

    private static List<String> getListTags(List<Tag> tags){
        if (tags == null) return new ArrayList<>();
        return tags.stream().map(tag-> tag.name).collect(Collectors.toList());
    }

    private static Html htmlAdd(Form<Article> data){
        return _form.render(TITLE, "Add", data, routes.ArticleController.save(), getListCategory(), getListTags(data.get().tags));
    }

    private static Map<Integer, String> getListCategory(){
        Map<Integer, String> result = new LinkedHashMap<>();
        ArticleCategory.find.where()
                .eq("is_deleted", false)
                .eq("brand", getBrand())
                .findList().forEach(dt->result.put(dt.id.intValue(), dt.name));
        return result;
    }

    @Security.Authenticated(Secured.class)
    public static Result index() {
        return ok(htmlList());
    }

    @Security.Authenticated(Secured.class)
    public static Result add() {
        Form<Article> formData = Form.form(Article.class).fill(new Article());
        return ok(htmlAdd(formData));
    }

    @Security.Authenticated(Secured.class)
    public static Result edit(Long id) {
        Article dt = Article.findById(id, getBrandId());
        dt.article_category_id = dt.articleCategory.id;
        dt.imageLink = dt.getImageLink();
        Form<Article> formData = Form.form(Article.class).fill(dt);
        return ok(htmlEdit(formData));
    }

    @Security.Authenticated(Secured.class)
    public static Result lists() {
        RoleFeature feature = getUserAccessByFeature(featureKey);
        Map<String, String[]> params = request().queryString();

        Integer iTotalRecords = Article.findRowCount(getBrand());
        String name = params.get("search[value]")[0];
        String filter = String.valueOf(params.get("filter")[0]);

        Integer pageSize = Integer.valueOf(params.get("length")[0]);
        Integer page = Integer.valueOf(params.get("start")[0]) / pageSize;

        String sortBy = "updated_at";
        String order = params.get("order[0][dir]")[0];

        switch (Integer.valueOf(params.get("order[0][column]")[0])) {
            case 0 :  sortBy = "id"; break;
            case 1 :  sortBy = "name"; break;
            case 2 :  sortBy = "article_category_name"; break;
            case 3 :  sortBy = "status"; break;
        }

        Page<Article> datas = Article.page(page, pageSize, sortBy, order, name, filter, getBrand());
        Integer iTotalDisplayRecords = datas.getTotalRowCount();

        ObjectNode result = Json.newObject();

        result.put("draw", Integer.valueOf(params.get("draw")[0]));
        result.put("recordsTotal", iTotalRecords);
        result.put("recordsFiltered", iTotalDisplayRecords);

        ArrayNode an = result.putArray("data");
        int num = Integer.valueOf(params.get("start")[0]) + 1;
        for (Article dt : datas.getList()) {
            ObjectNode row = Json.newObject();
            String action = "";
            if(feature.isView()){
                action += "<a class=\"btn btn-default btn-sm action\" href=\""+controllers.admin.routes.ArticleController.detail(dt.id)+"\"><i class=\"fa fa-search\"></i></a>&nbsp;";
            }
            if(feature.isEdit()){
                action += "<a class=\"btn btn-primary btn-sm action\" href=\""+controllers.admin.routes.ArticleController.edit(dt.id)+"\"><i class=\"fa fa-pencil\"></i></a>&nbsp;";
            }
            if(feature.isDelete()){
                action += "<a class=\"btn btn-danger btn-sm action btn_delete\" href=\"#\" onclick=\"deleteItem("+dt.id+")\"><i class=\"fa fa-trash\"></i></a>";
            }

            String status = "";
            if(feature.isEdit()) {
                status = "<select class=\"form-control dd-status\" id=\"" + dt.id + "\" name=\"rowStatus\" >" +
                        "<option value=\"" + Article.DRAFT + "\" " + (Article.DRAFT.equals(dt.status) ? "selected" : "") + ">Draft</option>" +
                        "<option value=\"" + Article.PUBLISH + "\" " + (Article.PUBLISH.equals(dt.status) ? "selected" : "") + ">Publish</option>" +
                        "<option value=\"" + Article.INACTIVE + "\" " + (Article.INACTIVE.equals(dt.status) ? "selected" : "") + ">Inactive</option>" +
                        "</select>";
            }else {
                switch (dt.status){
                    case Article.DRAFT : status = "Draft";break;
                    case Article.PUBLISH : status = "Publish";break;
                    case Article.INACTIVE : status = "Inactive";break;
                }
            }

            row.put("0", dt.id.toString());
            row.put("1", num);
            row.put("2", dt.name);
            row.put("3", dt.articleCategoryName);
            row.put("4", status);
            row.put("5", action);
            an.add(row);
            num++;
        }

        return ok(result);
    }


    @Security.Authenticated(Secured.class)
    public static Result save() {
        Form<Article> form = Form.form(Article.class).bindFromRequest();
        DynamicForm dForm = Form.form().bindFromRequest();
        MultipartFormData body = request().body().asMultipartFormData();
        MultipartFormData.FilePart picture = body.getFile("imageHeaderUrl");

        if (form.hasErrors()){
            flash("error", "Please correct errors bellow.");
            return badRequest(htmlAdd(form));
        }else{
            Article data = form.get();
            Transaction txn = Ebean.beginTransaction();
            try {
                ArticleCategory ac = ArticleCategory.find.where()
                        .eq("id", Long.parseLong(dForm.get("article_category_id")))
                        .eq("brand", getBrand())
                        .findUnique();
                data.articleCategory = ac;
                data.articleCategoryName = ac.name;
                data.slug = CommonFunction.slugGenerate(data.articleCategoryName + "-" + data.title);
                data.brand = getBrand();
                Article uniqueCheck = Article.find.where()
                        .eq("slug", data.slug).eq("isDeleted", false)
                        .eq("brand", getBrand())
                        .findUnique();
                if (uniqueCheck != null){
                    flash("error", "Article with similar title already exist");
                    return badRequest(htmlAdd(form));
                }

                String imageName = CommonFunction.slugGenerate(data.imageName);
                File newFile = Photo.uploadImage(picture, "atc", imageName, Photo.articleHeaderSize, "jpg");
                File newFileThumb = Photo.uploadImageCrop(picture, "atc-thumb", imageName, Photo.articleThumbSize,
                        "jpg");

                if (picture != null && newFile == null) {
                    flash("error", "Please insert image.");
                    return badRequest(htmlAdd(form));
                }

                if (newFile != null) {
                    data.imageThumbnailUrl = (newFileThumb == null) ? null
                            : Photo.createUrl("atc-thumb", newFileThumb.getName());
                    data.imageHeaderUrl = Photo.createUrl("atc", newFile.getName());
                }

                UserCms actor = getUserCms();
                data.userCms = actor;
                data.status = Article.DRAFT;
                data.save();
                data.tags = Tag.applyTag(data.tags_list, getBrand());
                data.saveManyToManyAssociations("tags");
                data.update();

                if (newFile != null)
                    Photo.saveRecord("atc", newFile.getName(), "", newFileThumb.getName(), "", picture.getFilename(),
                            actor.id, actor.role.key, "Article", data.id);

                txn.commit();
            }catch (Exception e) {
                e.printStackTrace();
                txn.rollback();
                flash("error", "Please correct errors bellow.");
                return badRequest(htmlAdd(form));
            } finally {
                txn.end();
            }

            flash("success", TITLE + " instance created");
            if (data.save.equals("1")){
                return redirect(routes.ArticleController.detail(data.id));
            }else{
                return redirect(routes.ArticleController.add());
            }

        }
    }

    @Security.Authenticated(Secured.class)
    public static Result update() {
        Form<Article> form = Form.form(Article.class).bindFromRequest();
        DynamicForm dForm = Form.form().bindFromRequest();
        MultipartFormData body = request().body().asMultipartFormData();
        MultipartFormData.FilePart picture = body.getFile("imageHeaderUrl");
        if (form.hasErrors()){
            flash("error", "Please correct errors bellow.");
            return badRequest(htmlEdit(form));
        }else{
            Transaction txn = Ebean.beginTransaction();
            Article data = form.get();
            try {
                ArticleCategory ac = ArticleCategory.find.where().eq("id", Long.parseLong(dForm.get("article_category_id"))).findUnique();
                data.articleCategory = ac;
                data.articleCategoryName = ac.name;
                data.tags = Tag.applyTag(data.tags_list, getBrand());

                String imageName = CommonFunction.slugGenerate(data.imageName);
                File newFile = Photo.uploadImage(picture, "atc", imageName, Photo.articleHeaderSize, "jpg");
                File newFileThumb = Photo.uploadImageCrop(picture, "atc-thumb", imageName, Photo.articleThumbSize,
                        "jpg");

                if (newFile != null) {
                    data.imageThumbnailUrl = (newFileThumb == null) ? null
                            : Photo.createUrl("atc-thumb", newFileThumb.getName());
                    data.imageHeaderUrl = Photo.createUrl("atc", newFile.getName());
                }

                data.saveManyToManyAssociations("tags");
                data.update();

                if (newFile != null){
                    UserCms actor = getUserCms();
                    Photo.saveRecord("atc", newFile.getName(), "", newFileThumb.getName(), "", picture.getFilename(),
                            actor.id, actor.role.key, "Article", data.id);
                }

                txn.commit();
            }catch (Exception e) {
                e.printStackTrace();
                txn.rollback();
                flash("error", "Please correct errors bellow.");
                return badRequest(htmlEdit(form));
            } finally {
                txn.end();
            }

            flash("success", TITLE + " instance edited");
            return redirect(routes.ArticleController.index());
        }
    }

    @Security.Authenticated(Secured.class)
    public static Result delete(String id) {
        Ebean.beginTransaction();
        int status = 0;
        try {
            for (String aTmp : id.split(",")) {
                Article data = Article.findById(Long.parseLong(aTmp), getBrandId());
                data.isDeleted = true;
                data.update();
                status = 1;
            }

            Ebean.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            Ebean.rollbackTransaction();
            status = 0;
        } finally {
            Ebean.endTransaction();
        }

        BaseResponse response = new BaseResponse();
        String message = status == 1 ? "Data success deleted" : "Data failed deleted";

        response.setBaseResponse(status, offset, 1, message, null);
        return ok(Json.toJson(response));
    }

    @Security.Authenticated(Secured.class)
    public static Result saveCategory() {
        DynamicForm form = Form.form().bindFromRequest();
        String name = form.get("name");
        ArticleCategory newCategory = new ArticleCategory(name, getBrand());
        String check = ArticleCategory.validation(newCategory);
        BaseResponse response = new BaseResponse();

        if (check != null) {
            response.setBaseResponse(0, 0, 0, check, null);
            return badRequest(Json.toJson(response));
        }

        newCategory.userCms = getUserCms();
        newCategory.save();
        response.setBaseResponse(1, offset, 1, created, newCategory);
        return ok(Json.toJson(response));

    }

    @Security.Authenticated(Secured.class)
    public static Result comment() {
        DynamicForm form = Form.form().bindFromRequest();
        String comment = form.get("comment");
        Long article_id = Long.valueOf(form.get("article_id"));
        Long parentCommentId = form.get("comment_parent_id") == null ? null : Long.valueOf(form.get("comment_parent_id"));

        Article art = Article.findById(article_id, getBrandId());
        ArticleComment newComment = new ArticleComment(art, parentCommentId, comment, getUserCms().id, true, getBrand());
        String check = ArticleComment.validation(newComment);
        BaseResponse response = new BaseResponse();

        if (check != null) {
            response.setBaseResponse(0, 0, 0, check, null);
            return badRequest(Json.toJson(response));
        }

        newComment.userCms = getUserCms();
        newComment.save();
        response.setBaseResponse(1, offset, 1, created, newComment);
        return ok(Json.toJson(response));

    }

    @Security.Authenticated(Secured.class)
    public static Result detail(Long id) {
        Article dt = Article.findById(id, getBrandId());
        dt.tags_list = dt.tags.stream().map(tag-> tag.name).collect(Collectors.toList());
        return ok(detail.render(TITLE, "Detail", dt));
    }

    @Security.Authenticated(Secured.class)
    public static Result preview(Long id) {
        Article dt = Article.findById(id, getBrandId());
        return ok(preview.render(TITLE, "Preview", dt));
    }

    @Security.Authenticated(Secured.class)
    public static Result updateStatus(String id, String newStatus) {

        Ebean.beginTransaction();
        int status = 0;
        try {
            String[] tmp = id.split(",");
            for (int i=0; i < tmp.length; i++)
            {

                Article data = Article.findById(Long.parseLong(tmp[i]), getBrandId());
                data.updateStatus(newStatus);

                status = 1;
            }

            Ebean.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            Ebean.rollbackTransaction();
            status = 0;
        } finally {
            Ebean.endTransaction();
        }

        BaseResponse response = new BaseResponse();
        String message = "";
        if(status == 1)
            message = "Data success updated";
        else message = "Data failed updated";

        response.setBaseResponse(status, offset, 1, message, null);
        return ok(Json.toJson(response));
    }

}