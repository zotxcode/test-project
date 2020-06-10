package controllers.admin;

import com.avaje.ebean.*;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.enwie.api.BaseResponse;
import com.enwie.util.CommonFunction;
import controllers.BaseController;
import models.*;
import models.mapper.CountResultSet;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Html;
import views.html.admin.voucher._form;
import views.html.admin.voucher._form_edit;
import views.html.admin.voucher.detail;
import views.html.admin.voucher.list;

import javax.persistence.PersistenceException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by nugraha on 5/18/17.
 */
public class VoucherController extends BaseController {
    private static final String TITLE = "Voucher";
    private static final String featureKey = "voucher";

    private static Html htmlList(){
        return list.render(TITLE, "List");
    }

    private static Html htmlDetail(Voucher data){
        return detail.render(TITLE, "Detail", data);
    }

    private static Html htmlAdd(Form<Voucher> data){
        return _form.render(TITLE, "Generate", data, routes.VoucherController.save(), getType(), getFilter(), getAssignedTo(), getPriority(), getFurtherRuleProcessing(), getCanBeCombined(), new LinkedHashMap<>(), new LinkedHashMap<>());
    }

    private static Html htmlEdit(Form<Voucher> data, Voucher data2){
        return _form_edit.render(TITLE, "Edit", data, data2, routes.VoucherController.update(), getType(), getFilter(), getAssignedTo(), getPriority(), getFurtherRuleProcessing(), getCanBeCombined(), new LinkedHashMap<>(), getListProducts(data2.products));
    }

    private static Map<String, String> getType(){
        Map<String, String> result = new LinkedHashMap<>();
        result.put(Voucher.TYPE_DISCOUNT,"Discount");
        result.put(Voucher.TYPE_FREE_DELIVERY,"Free Delivery");
        return result;
    }

    private static Map<String, String> getFilter(){
        Map<String, String> result = new LinkedHashMap<>();
        result.put(Voucher.FILTER_STATUS_ALL, "All");
        result.put(Voucher.FILTER_STATUS_PRODUCT, "Custom Product");
        result.put(Voucher.FILTER_STATUS_CATEGORY, "Custom Category");
        return result;
    }

    private static Map<String, String> getAssignedTo(){
        Map<String, String> result = new LinkedHashMap<>();
        result.put(Voucher.ASSIGNED_TO_ALL, "All");
        result.put(Voucher.ASSIGNED_TO_CUSTOM, "Custom Customer");
        return result;
    }

    private static Map<Integer, String> getPriority(){
        Map<Integer, String> result = new LinkedHashMap<>();
        result.put(1, "1");
        result.put(2, "2");
        result.put(3, "3");
        result.put(4, "4");
        result.put(5, "5");
        result.put(6, "6");
        result.put(7, "7");
        result.put(8, "8");
        result.put(9, "9");
        result.put(10, "10");
        return result;
    }

    private static Map<Integer, String> getListProducts(List<Product> products){
        Map<Integer, String> listsProduct = new LinkedHashMap<>();
        for(Product product : products){
            listsProduct.put(product.id.intValue(), product.name);
        }
        return listsProduct;
    }

    private static Map<Integer, String> getFurtherRuleProcessing(){
        Map<Integer, String> result = new LinkedHashMap<>();
        result.put(0, "No");
        result.put(1, "Yes");
        return result;
    }

    private static Map<Integer, String> getCanBeCombined(){
        Map<Integer, String> result = new LinkedHashMap<>();
        result.put(0, "No");
        result.put(1, "Yes");
        return result;
    }

    @Security.Authenticated(Secured.class)
    public static Result index() {
        return ok(htmlList());
    }

    @Security.Authenticated(Secured.class)
    public static Result add() {
        Form<Voucher> formData = Form.form(Voucher.class).fill(new Voucher());
        return ok(htmlAdd(formData));
    }

    @Security.Authenticated(Secured.class)
    public static Result edit(Long id) {
        Voucher voucher = Voucher.findById(id, getBrandId());
        Form<Voucher> formData = Form.form(Voucher.class).fill(voucher);
        return ok(htmlEdit(formData, voucher));
    }

    @Security.Authenticated(Secured.class)
    public static Result detail(Long id) {
        Voucher dt = Voucher.find.ref(id);
        return ok(htmlDetail(dt));
    }

    @Security.Authenticated(Secured.class)
    public static Result lists() {
        Map<String, String[]> params = request().queryString();


        Integer iTotalRecords = Voucher.findRowCount();
        String filter = params.get("search[value]")[0];


        Integer pageSize = Integer.valueOf(params.get("length")[0]);
        Integer page = Integer.valueOf(params.get("start")[0]) / pageSize;

        String sortBy = "validTo";
        String order = params.get("order[0][dir]")[0];

        switch (Integer.valueOf(params.get("order[0][column]")[0])) {
            case 1 :  sortBy = "id"; break;
            case 2 :  sortBy = "name"; break;
        }

        Page<Voucher> datas = Voucher.page(page, pageSize, sortBy, order, filter, getBrandId());
        Integer iTotalDisplayRecords = datas.getTotalRowCount();


        ObjectNode result = Json.newObject();

        result.put("draw", Integer.valueOf(params.get("draw")[0]));
        result.put("recordsTotal", iTotalRecords);
        result.put("recordsFiltered", iTotalDisplayRecords);

        ArrayNode an = result.putArray("data");
        int num = Integer.valueOf(params.get("start")[0]) + 1;
        for (Voucher dt : datas.getList()) {
            int totalUsed = VoucherDetail.find.where().eq("voucher", dt).eq("status", 1).findRowCount();

            ObjectNode row = Json.newObject();
            String status = "";
            String action = "" +
                    "<a class=\"btn btn-default btn-sm action\" href=\""+ routes.VoucherController.detail(dt.id)+"\"><i class=\"fa fa-search\"></i></a>&nbsp;" +
                    "<a class=\"btn btn-primary btn-sm action\" href=\""+ routes.VoucherController.edit(dt.id)+"\"><i class=\"fa fa-pencil\"></i></a>" ;

            if((dt.validFrom.compareTo(new Date()) < 0 || dt.validFrom.compareTo(new Date()) == 0) &&
                    (dt.validTo.compareTo(new Date()) > 0 || dt.validTo.compareTo(new Date()) == 0)){
                status = "<input value=\""+dt.id+"\" class=\"cb-status\" data-toggle=\"toggle\" data-on=\"Active\" data-off=\"Inactive\" data-size=\"small\" data-style=\"ios\" data-offstyle=\"danger\" type=\"checkbox\" "+((dt.status)?"checked":"")+">";
            }else{
                if(dt.validTo.compareTo(new Date()) < 0){
                    status = "Expired";
                }else if(dt.validFrom.compareTo(new Date()) > 0){
                    status = "Inactive";
                }
            }



            row.put("0", dt.id.toString());
            row.put("1", dt.name);
            row.put("2", dt.description);
            row.put("3", dt.masking);
            row.put("4", dt.count);
            row.put("5", totalUsed);
            String value = "";
            switch (dt.discountType){
                case Voucher.DISCOUNT_TYPE_NOMINAL : value += CommonFunction.currencyFormat(dt.discount, "Rp"); break;
                case Voucher.DISCOUNT_TYPE_PERCENT : value += CommonFunction.discountFormat(dt.discount) + "%"; break;
            }

            row.put("6", value);
            row.put("7", dt.getValidFrom()+" - "+dt.getValidTo());
            row.put("8", dt.getTypeView());

            row.put("9", status);
            row.put("10", action);
            an.add(row);
            num++;
        }

        return ok(result);
    }

    @Security.Authenticated(Secured.class)
    public static Result listsDetail(Long id) {
        Map<String, String[]> params = request().queryString();

        Integer iTotalRecords = VoucherDetail.findRowCount(id);
        String filter = params.get("search[value]")[0];


        Integer pageSize = Integer.valueOf(params.get("length")[0]);
        Integer page = Integer.valueOf(params.get("start")[0]) / pageSize;

        String sortBy = "status";
        String order = params.get("order[0][dir]")[0];

//        switch (Integer.valueOf(params.get("order[0][column]")[0])) {
//            case 1 :  sortBy = "id"; break;
//            case 2 :  sortBy = "name"; break;
//        }

        Page<VoucherDetail> datas = VoucherDetail.page(id, page, pageSize, sortBy, order, filter);
        Integer iTotalDisplayRecords = datas.getTotalRowCount();


        ObjectNode result = Json.newObject();

        result.put("draw", Integer.valueOf(params.get("draw")[0]));
        result.put("recordsTotal", iTotalRecords);
        result.put("recordsFiltered", iTotalDisplayRecords);

        ArrayNode an = result.putArray("data");
        int num = Integer.valueOf(params.get("start")[0]) + 1;
        for (VoucherDetail dt : datas.getList()) {
            ObjectNode row = Json.newObject();
            row.put("0", dt.code);
            row.put("1", dt.getStatusName());
            row.put("2", (dt.member != null)? dt.member.fullName : "");
            row.put("3", dt.getUsedDate());
            an.add(row);
            num++;
        }

        return ok(result);
    }

    @Security.Authenticated(Secured.class)
    public static Result listsProduct(Long id) {
        Voucher voucher = Voucher.find.byId(id);

        Map<String, String[]> params = request().queryString();

        String filter = params.get("search[value]")[0];

        Integer pageSize = Integer.valueOf(params.get("length")[0]);
        Integer page = Integer.valueOf(params.get("start")[0]) / pageSize;

        String sortBy = "name";
        String order = params.get("order[0][dir]")[0];

        ExpressionList<Product> qry = Product.find
                .where()
                .ilike("name", "%" + filter + "%")
                .eq("is_deleted", false);

        if(voucher.filterStatus.equals("P")){
            List<Long> ids = new ArrayList();
            voucher.products.forEach(dt->ids.add(dt.id));
            qry = qry.in("id", ids);
        }else if(voucher.filterStatus.equals("C")){
            qry = qry.in("parentCategory", voucher.categories);
        }

        Page<Product> datas = qry.orderBy(sortBy + " " + order)
                        .findPagingList(pageSize)
                        .setFetchAhead(false)
                        .getPage(page);

        Integer iTotalRecords = qry.findRowCount();
        Integer iTotalDisplayRecords = datas.getTotalRowCount();


        ObjectNode result = Json.newObject();

        result.put("draw", Integer.valueOf(params.get("draw")[0]));
        result.put("recordsTotal", iTotalRecords);
        result.put("recordsFiltered", iTotalDisplayRecords);

        ArrayNode an = result.putArray("data");
        int num = Integer.valueOf(params.get("start")[0]) + 1;
        for (Product dt : datas.getList()) {
            ObjectNode row = Json.newObject();
            row.put("0", num);
            row.put("1", dt.name);
            an.add(row);
            num++;
        }

        return ok(result);
    }

    @Security.Authenticated(Secured.class)
    public static Result listsMember() {
        Map<String, String[]> params = request().queryString();
        ObjectNode result = Json.newObject();

        String filter = params.get("q")[0];

        Integer pageSize = 30;
        Integer page = 0;
        if(params.get("page") != null){
            page = Integer.valueOf(params.get("page")[0]);
        }

        String sortBy = "fullName";
        String order = "asc";


        Page<Member> datas = Member.find
                .where()
                .ilike("fullName", "%" + filter + "%")
                .eq("t0.is_deleted", false)
                .eq("isActive", true)
                .eq("brand_id", getBrandId())
                .orderBy(sortBy + " " + order)
                .findPagingList(pageSize)
                .setFetchAhead(false)
                .getPage(page);
        Integer iTotalDisplayRecords;
        try {
            iTotalDisplayRecords = datas.getTotalRowCount();
        }catch (PersistenceException e){
            iTotalDisplayRecords = 0;
        }

        result.put("total_count", iTotalDisplayRecords);
        result.put("incomplete_results", false);

        ArrayNode an = result.putArray("items");
        for (Member dt : datas.getList()) {
            ObjectNode row = Json.newObject();
            row.put("id", dt.id);
            row.put("name", dt.fullName);
            an.add(row);
        }

        return ok(result);
    }

    @Security.Authenticated(Secured.class)
    public static Result listsCustomer(Long id) {
        Voucher voucher = Voucher.find.byId(id);

        Map<String, String[]> params = request().queryString();

        Integer iTotalRecords = voucher.members.size();
        String filter = params.get("search[value]")[0];

        Integer pageSize = Integer.valueOf(params.get("length")[0]);
        Integer page = Integer.valueOf(params.get("start")[0]) / pageSize;

        String sortBy = "status";
        String order = params.get("order[0][dir]")[0];
        Integer iTotalDisplayRecords = voucher.members.size();


        ObjectNode result = Json.newObject();

        result.put("draw", Integer.valueOf(params.get("draw")[0]));
        result.put("recordsTotal", iTotalRecords);
        result.put("recordsFiltered", iTotalDisplayRecords);

        ArrayNode an = result.putArray("data");
        int num = Integer.valueOf(params.get("start")[0]) + 1;
        for (Member dt : voucher.members) {
            ObjectNode row = Json.newObject();
            row.put("0", num);
            row.put("1", dt.fullName);
            row.put("2", dt.email);
            an.add(row);
            num++;
        }

        return ok(result);
    }

    @Security.Authenticated(Secured.class)
    public static Result save() {
        Form<Voucher> form = Form.form(Voucher.class).bindFromRequest();
        DynamicForm dForm = Form.form().bindFromRequest();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

        if (form.hasErrors()){
            flash("error", "Please correct errors bellow.");
            return badRequest(htmlAdd(form));
        }else{
            Transaction txn = Ebean.beginTransaction();
            Voucher data = form.get();
            try {
                data.brand = getBrand();

                Voucher uniqueCheck = Voucher.find.where().eq("name", data.name).findUnique();
                if (uniqueCheck != null) {
                    flash("error", "Voucher with similar name already exist");
                    return badRequest(htmlAdd(form));
                }

                if(checkMaskingName(data.masking, dForm.get("fromDate") +" "+ dForm.get("fromTime"), dForm.get("toDate") +" "+ dForm.get("toTime"), null)){
                    flash("error", "Voucher with similar masking already exist for period "+dForm.get("fromDate") +" "+ dForm.get("fromTime")+" to "+dForm.get("toDate") +" "+ dForm.get("toTime"));
                    return badRequest(htmlAdd(form));
                }

                if(data.type.equals(Voucher.TYPE_FREE_DELIVERY)){
                    data.discount = 0D;
                    data.discountType = 0;
                }

                List<Category> subcategories = new ArrayList<>();
                if (data.category_list != null) {
                    for (String category : data.category_list) {
                        subcategories.add(Category.find.byId(Long.parseLong(category)));
                    }
                }
                data.categories = subcategories;

                List<Product> products = new ArrayList<>();
                if (data.product_list != null) {
                    for (String product : data.product_list) {
                        products.add(Product.find.byId(Long.parseLong(product)));
                    }
                }
                data.products = products;

                List<Member> members = new ArrayList<>();
                if (data.member_list != null) {
                    for (String member : data.member_list) {
                        members.add(Member.find.byId(Long.parseLong(member)));
                    }
                }
                data.members = members;

                data.validFrom = simpleDateFormat.parse(dForm.get("fromDate") +" "+ dForm.get("fromTime"));
                data.validTo = simpleDateFormat.parse(dForm.get("toDate") +" "+ dForm.get("toTime"));

                data.createdBy = getUserCms();
                data.status = true;
                data.save();

                String[] codes = Voucher.generateCode(data.count);
                for (int i = 0; i < codes.length; i++) {
                    VoucherDetail detail = new VoucherDetail();
                    detail.voucher = data;
                    detail.code = codes[i];
                    detail.save();
                }

                txn.commit();

            }catch (Exception e){
                Logger.debug("error = "+e.getMessage());
                txn.rollback();
            }

            flash("success", TITLE + " instance created");
            if (data.save.equals("1")){
                return redirect(routes.VoucherController.detail(data.id));
            }else{
                return redirect(routes.VoucherController.add());
            }
        }
    }

    @Security.Authenticated(Secured.class)
    public static Result update() {
        Form<Voucher> form = Form.form(Voucher.class).bindFromRequest();
        DynamicForm dForm = Form.form().bindFromRequest();

        if (form.hasErrors()){
            flash("error", "Please correct errors bellow.");
            Voucher data = form.get();
            Voucher voucher = Voucher.find.byId(data.id);
            return badRequest(htmlEdit(form, voucher));
        }else{
            Transaction txn = Ebean.beginTransaction();
            Voucher data = form.get();
            Voucher model = Voucher.find.byId(data.id);
            try {
                model.priority = data.priority;
                model.stopFurtherRulePorcessing = data.stopFurtherRulePorcessing;
                model.canBeCombined = data.canBeCombined;

                model.update();

//                OdooService.getInstance().createVoucher(data);
                txn.commit();

            }catch (Exception e){
                Logger.debug("error = "+e.getMessage());
                txn.rollback();
            }

            flash("success", TITLE + " instance updated");
            return redirect(routes.VoucherController.detail(data.id));
        }
    }

    @Security.Authenticated(Secured.class)
    public static Result delete(String id) {
        Ebean.beginTransaction();
        int status = 0;
        try {
            for (String aTmp : id.split(",")) {
                Voucher data = Voucher.find.ref(Long.parseLong(aTmp));
                data.isDeleted = true;
                data.update();
                status = 1;

                List<VoucherDetail> details = VoucherDetail.find.where().eq("voucher", data).findList();
                for(VoucherDetail detail : details){
                    detail.isDeleted = true;
                    detail.update();
                }
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
    public static Result updateStatus(String id, String newStatus) {

        Ebean.beginTransaction();
        int status = 0;
        int countUpdated = 0;
        String[] tmp = id.split(",");
        String errorMessage = "";
        try {
            for (int i=0; i < tmp.length; i++)
            {
                Voucher data = Voucher.find.ref(Long.parseLong(tmp[i]));
                if(newStatus.equals("active")){
                    if (!checkMaskingNameById(data.id)) {
                        data.updateStatus(newStatus);
                        countUpdated ++;
                    }else{
                        errorMessage = "Voucher with similar masking already exist";
                    }
                }else{
                    data.updateStatus(newStatus);
                    countUpdated ++;
                }

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
        if(status == 1){
            if(countUpdated == tmp.length)
                message = "Data success updated";
            else if(countUpdated == 0) {
                message = errorMessage;
                status = 0;
            }else message = "Some data can't be updated";
        }
        else message = "Data failed updated";

        response.setBaseResponse(status, offset, 1, message, null);
        return ok(Json.toJson(response));
    }

    private static boolean checkMaskingNameById(Long id) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
        Voucher voucher = Voucher.find.byId(id);
        return checkMaskingName(voucher.masking, simpleDateFormat.format(voucher.validFrom), simpleDateFormat.format(voucher.validTo), id);
    }

    private static boolean checkMaskingName(String masking, String startDate, String endDate, Long id) {

        boolean status = false;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
            SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date start = simpleDateFormat.parse(startDate);
            Date end = simpleDateFormat.parse(endDate);

            String whereId = (id != null)? " and id != "+id+" " : " ";

            String sql = "select count(*) as total from voucher " +
            "where masking ='"+masking+"' " + whereId + " and status = true and is_deleted = false and valid_to >= now() " +
            "and (valid_from BETWEEN '"+simpleDateFormat2.format(start)+"' and '"+simpleDateFormat2.format(end)+"' " +
            "or valid_to BETWEEN '"+simpleDateFormat2.format(start)+"' and '"+simpleDateFormat2.format(end)+"' " +
            "or '"+simpleDateFormat2.format(start)+"' BETWEEN valid_from and valid_to) " ;

            RawSql rawSqlCount = RawSqlBuilder.parse(sql)
                    .columnMapping("count(*)", "total")
                    .create();

            Query<CountResultSet> queryCount = Ebean.find(CountResultSet.class);
            queryCount.setRawSql(rawSqlCount);
            CountResultSet totalDataSet = queryCount.findUnique();
            if(totalDataSet.total > 0){
                status = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
            status = false;
        }
        return status;
    }
}
