package controllers.admin;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import controllers.BaseController;
import models.*;
import play.Logger;
import play.data.Form;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Html;
import views.html.admin.productAlsoView._form;

import java.util.*;

/**
 * @author hendriksaragih
 */
public class ProductAlsoViewController extends BaseController {
    private final static Logger.ALogger logger = Logger.of(ProductAlsoViewController.class);
    private static final String TITLE = "Also View Product";
    private static final String featureKey = "alsoview";

	
	
	private static Html htmlAdd(Form<ProductAlsoView> data){
        return _form.render(TITLE, "Add", data, routes.ProductAlsoViewController.save(), new LinkedHashMap<>());
	}
	
	@Security.Authenticated(Secured.class)
    public static Result add(Long id) {
	    ProductAlsoView dt = new ProductAlsoView();
        
		int i = id.intValue();
        dt.productId = i;
        Form<ProductAlsoView> formData = Form.form(ProductAlsoView.class).fill(dt);
        return ok(htmlAdd(formData));
    }


    
	@Security.Authenticated(Secured.class)
    public static Result save() {
        Form<ProductAlsoView> form = Form.form(ProductAlsoView.class).bindFromRequest();

        if (form.hasErrors()){
            flash("error", "Please correct errors bellow.");
            return badRequest(htmlAdd(form));
        }else{
            ProductAlsoView data = form.get();
            Transaction txn = Ebean.beginTransaction();
            try {
                List<Product> products = new ArrayList<>();
                if(data.product_list != null) {
                    for (String product : data.product_list) {
						ProductAlsoView data1 = new ProductAlsoView();
						data1.productId=data.productId;
						data1.productAlsoViewId=Integer.valueOf(product);
						data1.save();
                    }
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
            
            return redirect("");
            
        }
    }
	
	
}