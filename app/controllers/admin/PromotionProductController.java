package controllers.admin;

import com.avaje.ebean.Ebean;
import com.enwie.api.BaseResponse;
import controllers.BaseController;
import models.Product;
import models.PromotionProduct;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Security;


/**
 * Created by hilmanzaky.
 */

public class PromotionProductController extends BaseController {

    @Security.Authenticated(Secured.class)
    public static Result delete(String id) {
        Ebean.beginTransaction();
        int status = 0;
        try {
            for (String aTmp : id.split(",")) {
                PromotionProduct data = PromotionProduct.find.ref(Long.parseLong(aTmp));
                Product product = data.product;
                product.discountType = 0;
                product.discount = 0D;
                product.update();
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
