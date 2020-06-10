package controllers.internal;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import com.enwie.api.BaseResponse;
import com.enwie.internal.InternalService;
import com.enwie.internal.response.model.PromoClient;
import com.enwie.midtrans.MidtransService;
import com.enwie.midtrans.model.TranscationResponse;
import com.enwie.shipping.rajaongkir.RajaOngkirService;
import com.enwie.shipping.rajaongkir.response.model.City;
import com.enwie.shipping.rajaongkir.response.model.Province;
import com.enwie.util.CommonFunction;
import controllers.BaseController;
import models.SalesOrder;
import play.libs.Json;
import play.mvc.Result;

import java.util.List;

/**
 * @author hendriksaragih
 */
public class InternalController extends BaseController {
    private static BaseResponse response = new BaseResponse();

    public static Result index() {
        return ok();
    }

    public static Result syncCity() {
        List<City> responses = RajaOngkirService.getInstance().getCity();
        Transaction txn = Ebean.beginTransaction();
        try {
            for (City p: responses){
                models.Province pr = models.Province.find.where().eq("code", p.getProvinceId()).setMaxRows(1).findUnique();
                models.City curCity = models.City.find.where().ilike("name", p.getCityName()).setMaxRows(1).findUnique();
                if (curCity == null) {
                    models.City prov = new models.City();
                    prov.code = p.getCityId();
                    prov.name = p.getCityName();
                    prov.province = pr;
                    prov.type = p.getType();
                    prov.postalCode = p.getPostalCode();
                    prov.save();
                } else {
                    curCity.code = p.getCityId();
                    curCity.name = p.getCityName();
                    curCity.province = pr;
                    curCity.type = p.getType();
                    curCity.postalCode = p.getPostalCode();
                    curCity.update();
                }
            }
            txn.commit();
        }catch (Exception e){
            txn.rollback();
        }finally {
            txn.end();
        }
        return ok("");
    }

    public static Result syncProvince() {
        List<Province> responses = RajaOngkirService.getInstance().getProvince();
        Transaction txn = Ebean.beginTransaction();
        try {
            for (Province p: responses){
                models.Province curProv = models.Province.find.where().ilike("name", p.getProvince()).setMaxRows(1).findUnique();
                if (curProv == null) {
                    models.Province prov = new models.Province();
                    prov.code = p.getProvinceId();
                    prov.name = p.getProvince();
                    prov.save();
                } else {
                    curProv.code = p.getProvinceId();
                    curProv.name = p.getProvince();
                    curProv.update();
                }
            }
            txn.commit();
        }catch (Exception e){
            txn.rollback();
        }finally {
            txn.end();
        }
        return ok("");
    }

    // public static Result gramedia() {
    //     PromoClient data = InternalService.getInstance().getPromoClients("loremipsum@gmail.com", "Lorem Ipsum", 60, 10000, 8000, "MLR", "milors");
    //     return ok(Json.toJson(data));
    // }

     public static Result testMidTrans() {
         SalesOrder so = new SalesOrder();
         so.orderNumber = CommonFunction.generateRandomString(5);
         so.subtotal = 1000D;
         TranscationResponse data = MidtransService.getInstance().createTransaction(so);
         return ok(Json.toJson(data));
     }
}
