package controllers.admin;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.Page;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.enwie.api.BaseResponse;
import com.enwie.util.CommonFunction;
import controllers.BaseController;
import models.*;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Html;
import views.html.admin.productvariantgroup._form;
import views.html.admin.productvariantgroup._form_edit;
import views.html.admin.productvariantgroup.detail;
import views.html.admin.productvariantgroup.list;

import java.util.*;

/**
 * @author hendriksaragih
 */
public class ProductVariantGroupController extends BaseController {
    private final static Logger.ALogger logger = Logger.of(ProductVariantGroupController.class);
    private static final String TITLE = "Grouping Variant Product";
    private static final String featureKey = "variant";

    private static Html htmlList(){
        RoleFeature feature = getUserAccessByFeature(featureKey);
        return list.render(TITLE, "List", feature);
    }

    private static Html htmlDetail(ProductVariantGroup data){
        return detail.render(TITLE, "Detail", data);
    }

    private static Html htmlAdd(Form<ProductVariantGroup> data){
        return _form.render(TITLE, "Add", data, routes.ProductVariantGroupController.save(), getListBaseAttribute());
    }

    private static Html htmlEdit(Form<ProductVariantGroup> data, ProductVariantGroup data2){
        return _form_edit.render(TITLE, "Edit", data, data2, routes.ProductVariantGroupController.update(), getListBaseAttribute());
    }

    @Security.Authenticated(Secured.class)
    public static Result index() {
        return ok(htmlList());
    }

    @Security.Authenticated(Secured.class)
    public static Result detail(Long id) {
        ProductVariantGroup data = ProductVariantGroup.findById(id, getBrandId());
        return ok(htmlDetail(data));
    }

    @Security.Authenticated(Secured.class)
    public static Result add() {

        ProductVariantGroup dt = new ProductVariantGroup();
        Form<ProductVariantGroup> formData = Form.form(ProductVariantGroup.class).fill(dt);
        return ok(htmlAdd(formData));
    }

    private static Map<Integer, String> getListBaseAttribute(){
        List<BaseAttribute> list = BaseAttribute.find.where()
                .eq("is_deleted", false)
                .eq("brand_id", getBrandId())
                .order("name asc").findList();
        Map<Integer, String> result = new LinkedHashMap<>();
        for(BaseAttribute item : list){
            result.put(item.id.intValue(), item.name);
        }
        return result;
    }

    @Security.Authenticated(Secured.class)
    public static Result edit(Long id) {

        ProductVariantGroup dt = ProductVariantGroup.findById(id, getBrandId());
        Form<ProductVariantGroup> formData = Form.form(ProductVariantGroup.class).fill(dt);
        return ok(htmlEdit(formData, dt));
    }

    @Security.Authenticated(Secured.class)
    public static Result lists() {
        RoleFeature feature = getUserAccessByFeature(featureKey);
        Map<String, String[]> params = request().queryString();

        Integer iTotalRecords = ProductVariantGroup.findRowCount(getBrandId());
        String filter = params.get("search[value]")[0];


        Integer pageSize = Integer.valueOf(params.get("length")[0]);
        Integer page = Integer.valueOf(params.get("start")[0]) / pageSize;

        String sortBy = "name";
        String order = params.get("order[0][dir]")[0];

        switch (Integer.valueOf(params.get("order[0][column]")[0])) {
            case 1 :  sortBy = "id"; break;
            case 2 :  sortBy = "name"; break;
        }

        Page<ProductVariantGroup> datas = ProductVariantGroup.page(page, pageSize, sortBy, order, filter, getBrandId());
        Integer iTotalDisplayRecords = datas.getTotalRowCount();

        ObjectNode result = Json.newObject();

        result.put("draw", Integer.valueOf(params.get("draw")[0]));
        result.put("recordsTotal", iTotalRecords);
        result.put("recordsFiltered", iTotalDisplayRecords);

        ArrayNode an = result.putArray("data");
        int num = Integer.valueOf(params.get("start")[0]) + 1;
        for (ProductVariantGroup dt : datas.getList()) {
            ObjectNode row = Json.newObject();
            String action = "";
            if(feature.isView()){
                action += "<a class=\"btn btn-default btn-sm action\" href=\""+ routes.ProductVariantGroupController.detail(dt.id)+"\"><i class=\"fa fa-search\"></i></a>&nbsp;";
            }
            if(feature.isEdit()){
                action += "<a class=\"btn btn-primary btn-sm action\" href=\""+ routes.ProductVariantGroupController.edit(dt.id)+"\"><i class=\"fa fa-pencil\"></i></a>&nbsp;";
            }
            if(feature.isDelete()){
                action += "<a class=\"btn btn-danger btn-sm action\" href=\"javascript:deleteData("+dt.id+");\"><i class=\"fa fa-trash\"></i></a>";
            }

            row.put("0", dt.id.toString());
            row.put("1", num);
            row.put("2", dt.name);
            row.put("3", action);
            an.add(row);
            num++;
        }

        return ok(result);
    }

    @Security.Authenticated(Secured.class)
    public static Result save() {
        Form<ProductVariantGroup> form = Form.form(ProductVariantGroup.class).bindFromRequest();

        if (form.hasErrors()){
            flash("error", "Please correct errors bellow.");
            return badRequest(htmlAdd(form));
        }else{
            ProductVariantGroup data = form.get();
            Transaction txn = Ebean.beginTransaction();
            try {
                List<BaseAttribute> baseAttributes = new ArrayList<>();
                if(data.base_attribute_list != null){
                    for(String attr : data.base_attribute_list){
                        BaseAttribute base = BaseAttribute.findById(Long.parseLong(attr), getBrandId());
                        baseAttributes.add(base);
                    }
                }
                data.baseAttributes = baseAttributes;
                data.brand = getBrand();

                Product lowestPriceProduct = null;
                List<Product> products = new ArrayList<>();
                for(String product : data.product_list){
                    if(!product.equals("0")){
                        Product prod = Product.findById(Long.parseLong(product), getBrandId());
                        products.add(prod);
                        if(lowestPriceProduct != null){
                            if(lowestPriceProduct.price > prod.price) {
                                lowestPriceProduct = prod;
                            }
                        }else{
                            lowestPriceProduct = prod;
                        }
                    }

                }

                UserCms cms = getUserCms();
                String roleKey = cms.role.key;
                data.lowestPriceProduct = lowestPriceProduct;
                data.userCms = cms;
                data.save();

                for(Product product : products){
                    product.productVariantGroup = data;
                    product.update();
                }

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
                return redirect(routes.ProductVariantGroupController.detail(data.id));
            }else{
                return redirect(routes.ProductVariantGroupController.add());
            }
        }
    }

    @Security.Authenticated(Secured.class)
    public static Result update() {
        Form<ProductVariantGroup> form = Form.form(ProductVariantGroup.class).bindFromRequest();

        if (form.hasErrors()){
            flash("error", "Please correct errors bellow.");
            ProductVariantGroup group = ProductVariantGroup.findById(form.get().id, getBrandId());
            return badRequest(htmlEdit(form, group));
        }else{
            ProductVariantGroup data = form.get();
            Transaction txn = Ebean.beginTransaction();
            try {
                List<BaseAttribute> baseAttributes = new ArrayList<>();
                if(data.base_attribute_list != null){
                    for(String attr : data.base_attribute_list){
                        BaseAttribute base = BaseAttribute.findById(Long.parseLong(attr), getBrandId());
                        baseAttributes.add(base);
                    }
                }
                data.baseAttributes.clear();
                data.baseAttributes.addAll(baseAttributes);

                Product lowestPriceProduct = null;
                List<Product> products = new ArrayList<>();
                for(String product : data.product_list){
                    if(!product.equals("0")){
                        Product prod = Product.findById(Long.parseLong(product), getBrandId());
                        products.add(prod);
                        if(lowestPriceProduct != null){
                            if(lowestPriceProduct.price > prod.price) {
                                lowestPriceProduct = prod;
                            }
                        }else{
                            lowestPriceProduct = prod;
                        }
                    }

                }

                UserCms cms = getUserCms();
                String roleKey = cms.role.key;
                data.lowestPriceProduct = lowestPriceProduct;
                data.userCms = cms;
                data.update();

                List<Product> oldProducts = Product.find.where()
                        .eq("productVariantGroup.id", data.id)
                        .eq("brand_id", getBrandId())
                        .findList();
                for(Product product : oldProducts){
                    product.productVariantGroup = null;
                    product.update();
                }

                for(Product product : products){
                    product.productVariantGroup = data;
                    product.update();
                }

                txn.commit();
            }catch (Exception e) {
                e.printStackTrace();
                txn.rollback();
                flash("error", "Please correct errors bellow.");
                ProductVariantGroup group = ProductVariantGroup.find.byId(form.get().id);
                return badRequest(htmlEdit(form, group));
            } finally {
                txn.end();
            }

            flash("success", TITLE + " instance updated");
            return redirect(routes.ProductVariantGroupController.detail(data.id));
        }
    }

    @Security.Authenticated(Secured.class)
    public static Result listsProductBySKUName() {
        Map<String, String[]> params = request().queryString();


        Integer iTotalRecords = Product.find.where()
                .eq("is_deleted", false)
                .eq("brand_id", getBrandId())
                .isNull("productVariantGroup").findRowCount();
        String filter = params.get("filter")[0];
        String idGroup = params.get("idGroup")[0];


        Integer pageSize = Integer.valueOf(params.get("length")[0]);
        Integer page = Integer.valueOf(params.get("start")[0]) / pageSize;

        String sortBy = "name";
        String order = params.get("order[0][dir]")[0];

        switch (Integer.valueOf(params.get("order[0][column]")[0])) {
            case 1 :  sortBy = "id"; break;
            case 2 :  sortBy = "name"; break;
        }
        Page<Product> datas = null;
        ObjectNode result = Json.newObject();

        result.put("draw", Integer.valueOf(params.get("draw")[0]));
        result.put("recordsTotal", 0);
        result.put("recordsFiltered", 0);
        ArrayNode an = result.putArray("data");
        if(!filter.equals("")) {
            datas = Product.find.where()
                    .or(Expr.ilike("sku", "%" + filter + "%"), Expr.ilike("name", "%" + filter + "%"))
                    .eq("is_deleted", false)
                    .eq("brand_id", getBrandId())
                    .isNull("productVariantGroup")
                    .orderBy(sortBy + " " + order)
                    .findPagingList(pageSize)
                    .setFetchAhead(false)
                    .getPage(page);

            Integer iTotalDisplayRecords = datas.getTotalRowCount();

            result.put("draw", Integer.valueOf(params.get("draw")[0]));
            result.put("recordsTotal", iTotalRecords);
            result.put("recordsFiltered", iTotalDisplayRecords);

            int num = Integer.valueOf(params.get("start")[0]) + 1;
            for (Product dt : datas.getList()) {

                ObjectNode row = Json.newObject();
                String action = "" +
                        "<input type=\"hidden\" name=\"product_list[]\" value=\"" + dt.id.toString() + "\"><button type=\"button\" class=\"btn btn-danger btn-sm action btn-delete\"><i class=\"fa fa-trash\"></i></button>&nbsp;";

                row.put("0", num);
                row.put("1", dt.sku);
                row.put("2", dt.name);
                row.put("3", CommonFunction.numberFormat(dt.price));
                row.put("4", action);
                an.add(row);
                num++;
            }

        }


        return ok(result);
    }

    @Security.Authenticated(Secured.class)
    public static Result listsDetail(Long id) {
        Map<String, String[]> params = request().queryString();

        ProductVariantGroup variant = ProductVariantGroup.findById(id, getBrandId());

        Integer iTotalRecords = Product.find.where()
                .eq("is_deleted", false)
                .eq("brand_id", getBrandId())
                .eq("productVariantGroup.id", id)
                .findRowCount();
        String filter = params.get("search[value]")[0];


        Integer pageSize = Integer.valueOf(params.get("length")[0]);
        Integer page = Integer.valueOf(params.get("start")[0]) / pageSize;

        String sortBy = "name";
        String order = params.get("order[0][dir]")[0];

        switch (Integer.valueOf(params.get("order[0][column]")[0])) {
            case 1 :  sortBy = "id"; break;
            case 2 :  sortBy = "name"; break;
        }
        Page<Product> datas = null;
        ObjectNode result = Json.newObject();

        result.put("draw", Integer.valueOf(params.get("draw")[0]));
        result.put("recordsTotal", 0);
        result.put("recordsFiltered", 0);
        ArrayNode an = result.putArray("data");
        datas = Product.find.where()
                .ilike("name", "%" + filter + "%")
                .eq("is_deleted", false)
                .eq("brand_id", getBrandId())
                .eq("productVariantGroup.id", id)
                .orderBy(sortBy + " " + order)
                .findPagingList(pageSize)
                .setFetchAhead(false)
                .getPage(page);

        Integer iTotalDisplayRecords = datas.getTotalRowCount();

        result.put("draw", Integer.valueOf(params.get("draw")[0]));
        result.put("recordsTotal", iTotalRecords);
        result.put("recordsFiltered", iTotalDisplayRecords);

        int num = Integer.valueOf(params.get("start")[0]) + 1;
        Boolean found = false;
        int i = 0;
        for (Product dt : datas.getList()) {

            ObjectNode row = Json.newObject();
            String action = "" +
                    "<input type=\"hidden\" name=\"product_list[]\" value=\"" + dt.id.toString() + "\"><button type=\"button\" class=\"btn btn-danger btn-sm action btn-delete\"><i class=\"fa fa-trash\"></i></button>&nbsp;";

            row.put("0", num);
            row.put("1", dt.sku);
            row.put("2", dt.name);
            row.put("3", CommonFunction.numberFormat(dt.price));
            i = 3;
            for(BaseAttribute base : variant.baseAttributes){
                found = false;
                for(Attribute att : dt.attributes){
                    if(Objects.equals(att.baseAttribute.id, base.id)){
                        found = true;
                        i++;
                        row.put(String.valueOf(i), att.value);
                    }
                }

                if(!found){
                    i++;
                    row.put(String.valueOf(i), "-");
                }
            }

            an.add(row);
            num++;
        }
        return ok(result);
    }

    @Security.Authenticated(Secured.class)
    public static Result listsDetailEdit(Long id) {
        Map<String, String[]> params = request().queryString();

        Integer iTotalRecords = Product.find.where()
                .eq("is_deleted", false)
                .eq("brand_id", getBrandId())
                .eq("productVariantGroup.id", id)
                .findRowCount();
        String filter = params.get("search[value]")[0];


        Integer pageSize = Integer.valueOf(params.get("length")[0]);
        Integer page = Integer.valueOf(params.get("start")[0]) / pageSize;

        String sortBy = "name";
        String order = params.get("order[0][dir]")[0];

        switch (Integer.valueOf(params.get("order[0][column]")[0])) {
            case 1 :  sortBy = "id"; break;
            case 2 :  sortBy = "name"; break;
        }
        Page<Product> datas = null;
        ObjectNode result = Json.newObject();

        result.put("draw", Integer.valueOf(params.get("draw")[0]));
        result.put("recordsTotal", 0);
        result.put("recordsFiltered", 0);
        ArrayNode an = result.putArray("data");
        datas = Product.find.where()
                .ilike("name", "%" + filter + "%")
                .eq("is_deleted", false)
                .eq("brand_id", getBrandId())
                .eq("productVariantGroup.id", id)
                .orderBy(sortBy + " " + order)
                .findPagingList(pageSize)
                .setFetchAhead(false)
                .getPage(page);

        Integer iTotalDisplayRecords = datas.getTotalRowCount();

        result.put("draw", Integer.valueOf(params.get("draw")[0]));
        result.put("recordsTotal", iTotalRecords);
        result.put("recordsFiltered", iTotalDisplayRecords);

        int num = Integer.valueOf(params.get("start")[0]) + 1;
        for (Product dt : datas.getList()) {

            ObjectNode row = Json.newObject();
            String action = "" +
                    "<input type=\"hidden\" name=\"product_list[]\" value=\"" + dt.id.toString() + "\"><button type=\"button\" class=\"btn btn-danger btn-sm action btn-delete\"><i class=\"fa fa-trash\"></i></button>&nbsp;";

            row.put("0", num);
            row.put("1", dt.sku);
            row.put("2", dt.name);
            row.put("3", CommonFunction.numberFormat(dt.price));
            row.put("4", action);
            an.add(row);
            num++;
        }
        return ok(result);
    }

    @Security.Authenticated(Secured.class)
    public static Result delete(String id) {
        Ebean.beginTransaction();
        int status = 0;
        try {
            for (String aTmp : id.split(",")) {
                ProductVariantGroup data = ProductVariantGroup.findById(Long.parseLong(aTmp), getBrandId());
                List<Product> oldProducts = Product.find.where()
                        .eq("productVariantGroup.id", data.id)
                        .eq("brand_id", getBrandId())
                        .findList();
                for(Product product : oldProducts){
                    product.productVariantGroup = null;
                    product.update();
                }

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
}