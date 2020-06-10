package controllers.admin;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Page;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.enwie.api.BaseResponse;
import controllers.BaseController;
import models.ArticleComment;
import models.RoleFeature;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Html;
import views.html.admin.articleComment.list;

import java.util.Map;
/**
 * @author hendriksaragih
 */
public class CommentController extends BaseController {
    private final static Logger.ALogger logger = Logger.of(CommentController.class);
    private static final String TITLE = "Article Comment";
    private static final String featureKey = "article_comment";

    private static Html htmlList(){
        RoleFeature feature = getUserAccessByFeature(featureKey);
        return list.render(TITLE, "List", feature);
    }

    @Security.Authenticated(Secured.class)
    public static Result index() {
        return ok(htmlList());
    }

    @Security.Authenticated(Secured.class)
    public static Result lists() {
        Map<String, String[]> params = request().queryString();

        Integer iTotalRecords = ArticleComment.RowCount(getBrand());
        String name = params.get("search[value]")[0];
        Integer filter = Integer.valueOf(params.get("filter")[0]);

        Integer pageSize = Integer.valueOf(params.get("length")[0]);
        Integer page = Integer.valueOf(params.get("start")[0]) / pageSize;

        String sortBy = "comment";
        String order = params.get("order[0][dir]")[0];

        switch (Integer.valueOf(params.get("order[0][column]")[0])) {
            case 1 :  sortBy = "id"; break;
            case 2 :  sortBy = "comment"; break;
        }

        Page<ArticleComment> datas = ArticleComment.page(page, pageSize, sortBy, order, name, filter, getBrand());
        Integer iTotalDisplayRecords = datas.getTotalRowCount();

        ObjectNode result = Json.newObject();

        result.put("draw", Integer.valueOf(params.get("draw")[0]));
        result.put("recordsTotal", iTotalRecords);
        result.put("recordsFiltered", iTotalDisplayRecords);

        ArrayNode an = result.putArray("data");
        int num = Integer.valueOf(params.get("start")[0]) + 1;
        for (ArticleComment dt : datas.getList()) {
            ObjectNode row = Json.newObject();
            row.put("0", dt.id.toString());
            row.put("1", num);
            row.put("2", dt.article.name);
            row.put("3", dt.comment);
            row.put("4", dt.getStatusName());
            row.put("5", getButton(dt.isRemoved, dt.status, dt.id, dt.articleId()));
            an.add(row);
            num++;
        }

        return ok(result);
    }

    private static String getButton(boolean remove, Integer status, Long id, Long article){
        RoleFeature feature = getUserAccessByFeature(featureKey);
        String btn = "";
        if (!remove){

            if(feature.isEdit()){
                if (status == 0){
                    btn += "<a class=\"btn btn-success btn-sm\" href=\"javascript:updateStatus("+id+", 1);\">Approved</a>&nbsp;";
                }
                if (status == 1){
                    btn += "<a class=\"btn btn-success btn-sm\" href=\""+controllers.admin.routes.ArticleController.detail(article)+"\">Reply</a>&nbsp;";
                }
                if (status == 0){
                    btn += "<a class=\"btn btn-success btn-sm\" href=\"javascript:updateStatus("+id+", 2);\">Reject</a>&nbsp;";
                }
            }
            if(feature.isDelete()) {
                btn += "<a class=\"btn btn-danger btn-sm\" href=\"javascript:deleteData(" + id + ");\">Delete</a>&nbsp;";
            }
            if(feature.isView()) {
                btn += "<a class=\"btn btn-default btn-sm\" href=\"" + controllers.admin.routes.ArticleController.detail(article) + "\">Show</a>&nbsp;";
            }
        }

        return btn;
    }

    @Security.Authenticated(Secured.class)
    public static Result delete(String id) {
        Ebean.beginTransaction();
        int status = 0;
        try {
            for (String aTmp : id.split(",")) {
                ArticleComment data = ArticleComment.findById(Long.parseLong(aTmp), getBrandId());
//                data.isDeleted = true;
                data.isRemoved = true;
                for (ArticleComment reply : data.replies){
                    reply.isRemoved = true;
                    reply.update();
                }
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
    public static Result updateStatus(String id, Integer newStatus) {
        Ebean.beginTransaction();
        int status = 0;
        try {
            for (String aTmp : id.split(",")) {
                ArticleComment data = ArticleComment.findById(Long.parseLong(aTmp), getBrandId());
                if (newStatus == 1)
                    data.status = ArticleComment.APPROVED;
                else if (newStatus == 2)
                    data.status = ArticleComment.REJECT;
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
        String message = status == 1 ? "Data success updated" : "Data failed updated";

        response.setBaseResponse(status, offset, 1, message, null);
        return ok(Json.toJson(response));
    }
}