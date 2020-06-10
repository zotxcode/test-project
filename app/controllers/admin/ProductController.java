package controllers.admin;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.enwie.api.BaseResponse;
import com.enwie.util.CommonFunction;
import com.enwie.util.Constant;
import com.enwie.util.Helper;
import controllers.BaseController;
import models.*;
import models.Currency;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import play.Logger;
import play.api.mvc.Call;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Html;
import views.html.admin.product._form;
import views.html.admin.product._form_add;
import views.html.admin.product.detail;
import views.html.admin.product.list;

import play.mvc.Http.MultipartFormData;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author hendriksaragih
 */
public class ProductController  extends BaseController {
    private final static Logger.ALogger logger = Logger.of(ProductController.class);
    private static final String TITLE = "New Product";
    private static final String TITLE_EDIT = "Edit Product";
    private static final String featureKey = "product";
    private static final String featureKeyReview = "productreview";
    private static final String featureKeyMarketplace = "productmarketplace";
    private static final String featureKeyAuthProduct = "authproduct";
    private static final String featureKeyAuthProductStock = "authproductstock";
    private static final int defaultPosition = 10000;


    private final static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");

    private static Html htmlAdd(Form<Product> data){
        return _form_add.render(TITLE, "ADD", routes.ProductController.save(), data, getCategory(), new LinkedHashMap<>(), new LinkedHashMap<>(), getListType(), new ArrayList<BaseAttribute>(), new ArrayList<Attribute>(), getDiscountType(), getListCurrency(), ProductDetail.getListWarrantyType(), new LinkedHashMap<>(), new ArrayList<String>(), getListSize(), new ArrayList<>(), Constant.getInstance().isEversac());
    }

    private static Html htmlEdit(Form<Product> data, Map<Integer, String> listSubCategory, Map<Integer, String> listSubSubCategory, Map<Integer, String> listsShortDescription, List<Attribute> listAttribute, List<BaseAttribute> listBaseAttribute, List<String> listImage, List<Size> listSize, List<String> listColor, Map<Integer, String> listProducts){
        return _form.render(TITLE_EDIT, "EDIT", routes.ProductController.update(), data, getCategory(), listSubCategory, listSubSubCategory, getListType(), listBaseAttribute, listAttribute, getDiscountType(), getListCurrency(), ProductDetail.getListWarrantyType(), listsShortDescription, listImage, getListSize(), listSize, Constant.getInstance().isEversac(), listColor, listProducts);
    }

    private static Map<Integer, String> getCategory(){
        Map<Integer, String> result = new LinkedHashMap<>();
        Category.find.where()
                .eq("is_deleted", false)
                .eq("level", Category.START_LEVEL)
                .eq("brand_id", getBrandId())
                .findList().forEach(dt->result.put(dt.id.intValue(), dt.name));
        return result;
    }

    private static Map<Integer, String> getSubCategory(Category parent){
        Map<Integer, String> result = new LinkedHashMap<>();
        Category.find.where()
                .eq("is_deleted", false)
                .eq("parentCategory.id", parent.id)
                .eq("brand_id", getBrandId())
                .findList().forEach(dt->result.put(dt.id.intValue(), dt.name));
        return result;
    }

    private static Map<Integer, String> getListType(){
        Map<Integer, String> result = new LinkedHashMap<>();
        result.put(ProductType.productTypeOwn.getId(), ProductType.productTypeOwn.getName());
        result.put(ProductType.productTypeConsignment.getId(), ProductType.productTypeConsignment.getName());
        return result;
    }

    private static Map<Integer, String> getListBrand(){
        Map<Integer, String> result = new LinkedHashMap<>();
        Brand.find.where()
                .eq("is_deleted", false)
                .order("name")
                .findList().forEach(dt->result.put(dt.id.intValue(), dt.name));
        return result;
    }

    private static Map<String, String> getListCurrency(){
        Map<String, String> result = new LinkedHashMap<>();
        Currency.find.where()
                .order("code")
                .findList().forEach(dt->result.put(dt.code, dt.codeDisplay));
        return result;
    }

    private static Map<Integer, String> getDiscountType(){
        Map<Integer, String> result = new LinkedHashMap<>();
        result.put(1, "Value");
        result.put(2, "Percent");
        return result;
    }

    private static Map<Integer, String> getListSize(){
        Map<Integer, String> result = new LinkedHashMap<>();
        Size.find.where()
                .eq("is_deleted", false)
                .eq("brand_id", getBrandId())
                .order("sequence asc")
                .findList().forEach(dt->result.put(dt.id.intValue(), dt.international));
        return result;
    }

    @Security.Authenticated(Secured.class)
    public static Result add() {
        Product product = new Product();
        product.categoryId = Long.parseLong("0");
        product.discount = Double.parseDouble("0");
        product.currencyCode = "";
        product.idImagetmp = UUID.randomUUID().toString();
        Form<Product> formData = Form.form(Product.class).fill(product);

        return ok(htmlAdd(formData));
    }

    @Security.Authenticated(Secured.class)
    public static Result edit(Long id) {

        Product dt = Product.findById(id, getBrandId());
        dt.subSubCategoryId = dt.category.id;
        dt.subCategoryId = dt.parentCategory.id;
        dt.categoryId = dt.grandParentCategory.id;
        dt.discount = (dt.discount == null)? 0D:dt.discount;
        Map<Integer, String> listsSubSubCategory = getSubCategory(dt.parentCategory);
        Map<Integer, String> listsSubCategory = getSubCategory(dt.grandParentCategory);
        dt.brandId = dt.brand.id;

        List<BaseAttribute> listBaseAttribute = dt.category.listBaseAttribute;
        List<Attribute> listAttribute = new ArrayList<>(dt.attributes);
        List<Size> listSize = new ArrayList<>(dt.sizes);

        ProductDetail detail = ProductDetail.find.where()
                .eq("mainProduct", dt)
                .eq("brand_id", getBrandId())
                .findUnique();
        dt.weight = detail.weight;
        dt.dimension1 = detail.dimension1;
        dt.dimension2 = detail.dimension2;
        dt.dimension3 = detail.dimension3;
        dt.embededYoutube = detail.embededYoutube;
        dt.thumbnailYoutubeUrl = detail.thumbnailYoutubeUrl;


        dt.warrantyType = detail.warrantyType;
        dt.warrantyPeriod = detail.warrantyPeriod;
//        dt.currencyCode = dt.currency.code;
        if(dt.discountActiveFrom != null){
            dt.fromDate = Helper.getDateFromTimeStamp(dt.discountActiveFrom);
            dt.fromTime = Helper.getTimeFromTimeStamp(dt.discountActiveFrom);
        }
        if(dt.discountActiveTo != null){
            dt.toDate = Helper.getDateFromTimeStamp(dt.discountActiveTo);
            dt.toTime = Helper.getTimeFromTimeStamp(dt.discountActiveTo);
        }
        dt.whatInTheBox = detail.whatInTheBox;
        dt.feature = detail.feature;
        dt.specifications = detail.specifications;
        dt.recomendedCare = detail.recomendedCare;
        dt.waranty = detail.waranty;
        dt.productDescription = detail.description;
        dt.sizeGuide = detail.sizeGuide;

        List<String> tmpList = new ArrayList<>();
        if(detail.shortDescriptions!= null){
            tmpList = Arrays.asList(detail.shortDescriptions.split("##"));
        }

        Map<Integer, String> listsShortDescription = new LinkedHashMap<>();

        for(int i=0; i < tmpList.size(); i++){
            listsShortDescription.put(i,tmpList.get(i));
        }

        Map<Integer, String> listProducts = new LinkedHashMap<>();
        for(Product product : dt.productAlsoViews){
            listProducts.put(product.id.intValue(), product.name);
        }

        dt.profitSSValue = 0D;
        dt.profitSSPercentage = 0D;
        dt.profitDValue = 0D;
        dt.profitDPercentage = 0D;
        dt.profitSDValue = 0D;
        dt.profitSDPercentage = 0D;
        dt.profitRValue = 0D;
        dt.profitRPercentage = 0D;
        for(ProductProfit profit : dt.productProfits) {
            if(profit.type.equals(ProductProfit.typeSupSystem)) {
                dt.profitSSValue = profit.value;
                dt.profitSSPercentage = profit.percentage;
            } else if (profit.type.equals(ProductProfit.typeDistributor)) {
                dt.profitDValue = profit.value;
                dt.profitDPercentage = profit.percentage;
            } else if (profit.type.equals(ProductProfit.typeSubDistributor)) {
                dt.profitSDValue = profit.value;
                dt.profitSDPercentage = profit.percentage;
            } else if (profit.type.equals(ProductProfit.typeReseller)) {
                dt.profitRValue = profit.value;
                dt.profitRPercentage = profit.percentage;
            }
        }

        List<String> listImage = new ArrayList<>(Arrays.asList(detail.getImage3Link()));
        List<String> listColors = new ArrayList<>(Arrays.asList(detail.getImageColorLink()));
        Form<Product> formData = Form.form(Product.class).fill(dt);
        return ok(htmlEdit(formData, listsSubCategory, listsSubSubCategory, listsShortDescription, listAttribute, listBaseAttribute, listImage, listSize, listColors, listProducts));
    }

    @Security.Authenticated(Secured.class)
    public static Result save() {
        Form<Product> form = Form.form(Product.class).bindFromRequest();
        Http.MultipartFormData body = request().body().asMultipartFormData();
		
        MultipartFormData.FilePart pictureResponsive = body.getFile("thumbnailYoutubeUrl");
        if (form.hasErrors()){
            flash("error", "Please correct errors bellow.");
            return badRequest(htmlAdd(form));
        }else{
            Transaction txn = Ebean.beginTransaction();
            Product dataOri = form.get();
            try {
                List<Product> products = new ArrayList<>();
                for (Integer i =0; i<dataOri.numProduct; i++){
//                    Product data = form.get();
                    Product data = new Product(dataOri);
                    data.sku = form.data().get("sku"+i);
                    data.articleCode = form.data().get("articleCode"+i);
                    Product uniqueCheck = Product.find.where()
                            .eq("sku", data.sku)
                            .eq("is_deleted", false)
                            .eq("brand", getBrand())
                            .setMaxRows(1)
                            .findUnique();
                    if (uniqueCheck != null){
                        flash("error", "Product with similar sku already exist");
                        return badRequest(htmlAdd(form));
                    }
                    data.grandParentCategory = Category.findById(data.categoryId, getBrandId());
                    data.parentCategory = Category.findById(data.subCategoryId, getBrandId());
                    data.category = Category.findById(data.subSubCategoryId, getBrandId());
                    data.brand = getBrand();
                    data.currency = Currency.find.byId("RP");
                    data.strikeThroughDisplay = data.price;
                    if(data.discount == null) data.discount = 0.0;
                    if(data.discount == 0 ){
                        data.discountType = 0;
                        data.priceDisplay = data.price;
                    }else{
                        try {
                            if(data.discountType == 1){
                                data.priceDisplay = data.price - data.discount;
                            }else{
                                data.priceDisplay = data.price - (data.price * (data.discount/100));
                            }

                            data.discountActiveTo = simpleDateFormat.parse(data.toDate + " " + data.toTime);
                            data.discountActiveFrom = simpleDateFormat.parse(data.fromDate + " " + data.fromTime);
                        }catch (ParseException e){
                            data.discountActiveFrom = null;
                            data.discountActiveTo = null;
                        }
                    }

                    Set<BaseAttribute> listBaseAttribute = new HashSet<>();
                    List<Attribute> listAttribute = new LinkedList<>();
                    for (Integer j=0; j<dataOri.numAttributes; j++){
                        String idAttS = form.data().get("attributesIds["+i+"]["+j+"]");
                        if (idAttS != null && !idAttS.isEmpty()){
                            Long idAtt = Long.valueOf(idAttS);
                            if(idAtt > 0) {
                                Attribute attribute = Attribute.findById(idAtt, getBrandId());
                                listAttribute.add(attribute);
                                listBaseAttribute.add(attribute.baseAttribute);
                            }
                        }
                    }
                    data.baseAttributes = listBaseAttribute;
                    data.attributes = listAttribute;

                    Set<Size> listSize = new HashSet<>();
                    if(data.sizeids != null) {
                        for (Long idSize: data.sizeids) {
                            if (idSize > 0) {
                                Size size = Size.findById(idSize, getBrandId());
                                listSize.add(size);
                            }
                        }
                    }
                    data.sizes = listSize;
					   
//                    Http.MultipartFormData.FilePart pictureyt = body.getFile("thumbnailYoutubeUr1");
//					data.thumbnailYoutubeUr1W = Integer.valueOf(form.data().get("thumbnailYoutubeUr1W"));
//                    data.thumbnailYoutubeUr1Y = Integer.valueOf(form.data().get("thumbnailYoutubeUr1Y"));
//                    data.thumbnailYoutubeUr1X = Integer.valueOf(form.data().get("thumbnailYoutubeUr1X"));
//                    data.thumbnailYoutubeUr1H = Integer.valueOf(form.data().get("thumbnailYoutubeUr1H"));
//
//                    File fileThumbYT = Photo.uploadImageCrop2(pictureyt, "you-thumb", data.name + "-1",data.thumbnailYoutubeUr1X, data.thumbnailYoutubeUr1Y, data.thumbnailYoutubeUr1W, data.thumbnailYoutubeUr1H, Photo.thumbImageSize, "jpg");
                    data.thumbnailYoutubeUrl = "";

                    data.userCms = getUserCms();
					
                    data.firstPoStatus = 0;
                    data.approvedStatus = "A";
                    //data.sku = data.generateSKU();
                    data.status = true;
                    data.isShow = i == 0;
                    data.position = defaultPosition;
                    data.save();

                    data.slug = CommonFunction.slugGenerate(data.name+"-"+data.id);

                    Http.MultipartFormData.FilePart picture1 = body.getFile("imageUrl"+i+"_1");
                    Http.MultipartFormData.FilePart picture2 = body.getFile("imageUrl"+i+"_2");
                    Http.MultipartFormData.FilePart picture3 = body.getFile("imageUrl"+i+"_3");
                    Http.MultipartFormData.FilePart picture4 = body.getFile("imageUrl"+i+"_4");
                    Http.MultipartFormData.FilePart picture5 = body.getFile("imageUrl"+i+"_5");
                    File new1FileFull = null;
                    File new1FileMedium = null;
                    File new1FileThumb = null;
                    File new1FileIcon = null;
                    data.imageUrl1W = Integer.valueOf(form.data().get("imageUrl"+i+"_1W"));
                    data.imageUrl1Y = Integer.valueOf(form.data().get("imageUrl"+i+"_1Y"));
                    data.imageUrl1X = Integer.valueOf(form.data().get("imageUrl"+i+"_1X"));
                    data.imageUrl1H = Integer.valueOf(form.data().get("imageUrl"+i+"_1H"));
                    if (data.imageUrl1W < 100){
                        new1FileFull = Photo.uploadImage(picture1, "prod", data.slug + "-1", Photo.fullImageSize, "jpg");
                        new1FileThumb = Photo.uploadImage(picture1, "prod-thumb", data.slug + "-1", Photo.thumbImageSize, "jpg");
                        new1FileMedium = Photo.uploadImage(picture1, "prod-med", data.slug + "-1", Photo.mediumImageSize, "jpg");
                        new1FileIcon = Photo.uploadImage(picture1, "prod-icon", data.slug + "-1", Photo.iconImageSize, "jpg");
                    }else{
                        new1FileFull = Photo.uploadImageCrop2(picture1, "prod", data.slug + "-1",data.imageUrl1X, data.imageUrl1Y, data.imageUrl1W, data.imageUrl1H, Photo.fullImageSize, "jpg");
                        new1FileMedium = Photo.uploadImageCrop2(picture1, "prod-med", data.slug + "-1",data.imageUrl1X, data.imageUrl1Y, data.imageUrl1W, data.imageUrl1H, Photo.mediumImageSize, "jpg");
                        new1FileThumb = Photo.uploadImageCrop2(picture1, "prod-thumb", data.slug + "-1",data.imageUrl1X, data.imageUrl1Y, data.imageUrl1W, data.imageUrl1H, Photo.thumbImageSize, "jpg");
                        new1FileIcon = Photo.uploadImageCrop2(picture1, "prod-icon", data.slug + "-1",data.imageUrl1X, data.imageUrl1Y, data.imageUrl1W, data.imageUrl1H, Photo.iconImageSize, "jpg");
                    }


                    File new2FileFull = null;
                    File new2FileMedium = null;
                    File new2FileThumb = null;
                    File new2FileIcon = null;
                    data.imageUrl2W = Integer.valueOf(form.data().get("imageUrl"+i+"_2W"));
                    data.imageUrl2Y = Integer.valueOf(form.data().get("imageUrl"+i+"_2Y"));
                    data.imageUrl2X = Integer.valueOf(form.data().get("imageUrl"+i+"_2X"));
                    data.imageUrl2H = Integer.valueOf(form.data().get("imageUrl"+i+"_2H"));
                    if (data.imageUrl2W < 100){
                        new2FileFull = Photo.uploadImage(picture2, "prod", data.slug+"-2", Photo.fullImageSize, "jpg");
                        new2FileMedium = Photo.uploadImage(picture2, "prod-med", data.slug+"-2", Photo.mediumImageSize, "jpg");
                        new2FileIcon = Photo.uploadImage(picture2, "prod-icon", data.slug+"-2", Photo.iconImageSize, "jpg");
                        new2FileThumb = Photo.uploadImage(picture2, "prod-thumb", data.slug+"-2", Photo.thumbImageSize, "jpg");
                    }else{
                        new2FileFull = Photo.uploadImageCrop2(picture2, "prod", data.slug + "-2",data.imageUrl2X, data.imageUrl2Y, data.imageUrl2W, data.imageUrl2H, Photo.fullImageSize, "jpg");
                        new2FileMedium = Photo.uploadImageCrop2(picture2, "prod-med", data.slug + "-2",data.imageUrl2X, data.imageUrl2Y, data.imageUrl2W, data.imageUrl2H, Photo.mediumImageSize, "jpg");
                        new2FileThumb = Photo.uploadImageCrop2(picture2, "prod-thumb", data.slug + "-2",data.imageUrl2X, data.imageUrl2Y, data.imageUrl2W, data.imageUrl2H, Photo.thumbImageSize, "jpg");
                        new2FileIcon = Photo.uploadImageCrop2(picture2, "prod-icon", data.slug + "-2",data.imageUrl2X, data.imageUrl2Y, data.imageUrl2W, data.imageUrl2H, Photo.iconImageSize, "jpg");
                    }


                    File new3FileFull = null;
                    File new3FileMedium = null;
                    File new3FileThumb = null;
                    File new3FileIcon = null;
                    data.imageUrl3W = Integer.valueOf(form.data().get("imageUrl"+i+"_3W"));
                    data.imageUrl3Y = Integer.valueOf(form.data().get("imageUrl"+i+"_3Y"));
                    data.imageUrl3X = Integer.valueOf(form.data().get("imageUrl"+i+"_3X"));
                    data.imageUrl3H = Integer.valueOf(form.data().get("imageUrl"+i+"_3H"));
                    if (data.imageUrl3W < 100){
                        new3FileMedium = Photo.uploadImage(picture3, "prod-med", data.slug+"-3", Photo.mediumImageSize, "jpg");
                        new3FileFull = Photo.uploadImage(picture3, "prod", data.slug+"-3", Photo.fullImageSize, "jpg");
                        new3FileThumb = Photo.uploadImage(picture3, "prod-thumb", data.slug+"-3", Photo.thumbImageSize, "jpg");
                        new3FileIcon = Photo.uploadImage(picture3, "prod-icon", data.slug+"-3", Photo.iconImageSize, "jpg");
                    }else{
                        new3FileFull = Photo.uploadImageCrop2(picture3, "prod", data.slug + "-3",data.imageUrl3X, data.imageUrl3Y, data.imageUrl3W, data.imageUrl3H, Photo.fullImageSize, "jpg");
                        new3FileMedium = Photo.uploadImageCrop2(picture3, "prod-med", data.slug + "-3",data.imageUrl3X, data.imageUrl3Y, data.imageUrl3W, data.imageUrl3H, Photo.mediumImageSize, "jpg");
                        new3FileThumb = Photo.uploadImageCrop2(picture3, "prod-thumb", data.slug + "-3",data.imageUrl3X, data.imageUrl3Y, data.imageUrl3W, data.imageUrl3H, Photo.thumbImageSize, "jpg");
                        new3FileIcon = Photo.uploadImageCrop2(picture3, "prod-icon", data.slug + "-3",data.imageUrl3X, data.imageUrl3Y, data.imageUrl3W, data.imageUrl3H, Photo.iconImageSize, "jpg");
                    }


                    File new4FileFull = null;
                    File new4FileMedium = null;
                    File new4FileThumb = null;
                    File new4FileIcon = null;
                    data.imageUrl4W = Integer.valueOf(form.data().get("imageUrl"+i+"_4W"));
                    data.imageUrl4Y = Integer.valueOf(form.data().get("imageUrl"+i+"_4Y"));
                    data.imageUrl4X = Integer.valueOf(form.data().get("imageUrl"+i+"_4X"));
                    data.imageUrl4H = Integer.valueOf(form.data().get("imageUrl"+i+"_4H"));
                    if (data.imageUrl4W < 100){
                        new4FileMedium = Photo.uploadImage(picture4, "prod-med", data.slug+"-4", Photo.mediumImageSize, "jpg");
                        new4FileThumb = Photo.uploadImage(picture4, "prod-thumb", data.slug+"-4", Photo.thumbImageSize, "jpg");
                        new4FileFull = Photo.uploadImage(picture4, "prod", data.slug+"-4", Photo.fullImageSize, "jpg");
                        new4FileIcon = Photo.uploadImage(picture4, "prod-icon", data.slug+"-4", Photo.iconImageSize, "jpg");
                    }else{
                        new4FileFull = Photo.uploadImageCrop2(picture4, "prod", data.slug + "-4",data.imageUrl4X, data.imageUrl4Y, data.imageUrl4W, data.imageUrl4H, Photo.fullImageSize, "jpg");
                        new4FileMedium = Photo.uploadImageCrop2(picture4, "prod-med", data.slug + "-4",data.imageUrl4X, data.imageUrl4Y, data.imageUrl4W, data.imageUrl4H, Photo.mediumImageSize, "jpg");
                        new4FileThumb = Photo.uploadImageCrop2(picture4, "prod-thumb", data.slug + "-4",data.imageUrl4X, data.imageUrl4Y, data.imageUrl4W, data.imageUrl4H, Photo.thumbImageSize, "jpg");
                        new4FileIcon = Photo.uploadImageCrop2(picture4, "prod-icon", data.slug + "-4",data.imageUrl4X, data.imageUrl4Y, data.imageUrl4W, data.imageUrl4H, Photo.iconImageSize, "jpg");
                    }

                    File new5FileFull = null;
                    File new5FileMedium = null;
                    File new5FileThumb = null;
                    File new5FileIcon = null;
                    data.imageUrl5W = Integer.valueOf(form.data().get("imageUrl"+i+"_5W"));
                    data.imageUrl5Y = Integer.valueOf(form.data().get("imageUrl"+i+"_5Y"));
                    data.imageUrl5X = Integer.valueOf(form.data().get("imageUrl"+i+"_5X"));
                    data.imageUrl5H = Integer.valueOf(form.data().get("imageUrl"+i+"_5H"));
                    if (data.imageUrl5W < 100){
                        new5FileFull = Photo.uploadImage(picture5, "prod", data.slug+"-5", Photo.fullImageSize, "jpg");
                        new5FileMedium = Photo.uploadImage(picture5, "prod-med", data.slug+"-5", Photo.mediumImageSize, "jpg");
                        new5FileThumb = Photo.uploadImage(picture5, "prod-thumb", data.slug+"-5", Photo.thumbImageSize, "jpg");
                        new5FileIcon = Photo.uploadImage(picture5, "prod-icon", data.slug+"-5", Photo.iconImageSize, "jpg");
                    }else{
                        new5FileMedium = Photo.uploadImageCrop2(picture5, "prod-med", data.slug + "-5",data.imageUrl5X, data.imageUrl5Y, data.imageUrl5W, data.imageUrl5H, Photo.mediumImageSize, "jpg");
                        new5FileThumb = Photo.uploadImageCrop2(picture5, "prod-thumb", data.slug + "-5",data.imageUrl5X, data.imageUrl5Y, data.imageUrl5W, data.imageUrl5H, Photo.thumbImageSize, "jpg");
                        new5FileFull = Photo.uploadImageCrop2(picture5, "prod", data.slug + "-5",data.imageUrl5X, data.imageUrl5Y, data.imageUrl5W, data.imageUrl5H, Photo.fullImageSize, "jpg");
                        new5FileIcon = Photo.uploadImageCrop2(picture5, "prod-icon", data.slug + "-5",data.imageUrl5X, data.imageUrl5Y, data.imageUrl5W, data.imageUrl5H, Photo.iconImageSize, "jpg");
                    }


                    data.imageUrl = Photo.createUrl("prod-med", new1FileMedium.getName());
                    data.thumbnailUrl = Photo.createUrl("prod-thumb", new1FileMedium.getName());


					Http.MultipartFormData.FilePart pictureIcon1 = body.getFile("imageIcon0_1");
                    data.imageIcon1W = Integer.valueOf(form.data().get("imageIcon0_1W"));
                    data.imageIcon1Y = Integer.valueOf(form.data().get("imageIcon0_1Y"));
                    data.imageIcon1X = Integer.valueOf(form.data().get("imageIcon0_1X"));
                    data.imageIcon1H = Integer.valueOf(form.data().get("imageIcon0_1H"));
                    File iconFile1 = Photo.uploadImageCrop2(pictureIcon1, "prod-color", data.slug + "-1",data.imageIcon1X, data.imageIcon1Y, data.imageIcon1W, data.imageIcon1H, Photo.colorSize, "jpg");

 /*
                    Http.MultipartFormData.FilePart pictureIcon1 = body.getFile("imageIcon"+i+"_1");
                    Http.MultipartFormData.FilePart pictureIcon2 = body.getFile("imageIcon"+i+"_2");
                    Http.MultipartFormData.FilePart pictureIcon3 = body.getFile("imageIcon"+i+"_3");
                    Http.MultipartFormData.FilePart pictureIcon4 = body.getFile("imageIcon"+i+"_4");
                    Http.MultipartFormData.FilePart pictureIcon5 = body.getFile("imageIcon"+i+"_5");
                    data.imageIcon1W = Integer.valueOf(form.data().get("imageIcon"+i+"_1W"));
                    data.imageIcon1Y = Integer.valueOf(form.data().get("imageIcon"+i+"_1Y"));
                    data.imageIcon1X = Integer.valueOf(form.data().get("imageIcon"+i+"_1X"));
                    data.imageIcon1H = Integer.valueOf(form.data().get("imageIcon"+i+"_1H"));
                    File iconFile1 = Photo.uploadImageCrop2(pictureIcon1, "prod-color", data.slug + "-1",data.imageIcon1X, data.imageIcon1Y, data.imageIcon1W, data.imageIcon1H, Photo.colorSize, "jpg");


                    data.imageIcon2W = Integer.valueOf(form.data().get("imageIcon"+i+"_2W"));
                    data.imageIcon2Y = Integer.valueOf(form.data().get("imageIcon"+i+"_2Y"));
                    data.imageIcon2X = Integer.valueOf(form.data().get("imageIcon"+i+"_2X"));
                    data.imageIcon2H = Integer.valueOf(form.data().get("imageIcon"+i+"_2H"));
                    File iconFile2 = Photo.uploadImageCrop2(pictureIcon2, "prod-color", data.slug + "-1",data.imageIcon2X, data.imageIcon2Y, data.imageIcon2W, data.imageIcon2H, Photo.colorSize, "jpg");


                    data.imageIcon3W = Integer.valueOf(form.data().get("imageIcon"+i+"_3W"));
                    data.imageIcon3Y = Integer.valueOf(form.data().get("imageIcon"+i+"_3Y"));
                    data.imageIcon3X = Integer.valueOf(form.data().get("imageIcon"+i+"_3X"));
                    data.imageIcon3H = Integer.valueOf(form.data().get("imageIcon"+i+"_3H"));
                    File iconFile3 = Photo.uploadImageCrop2(pictureIcon3, "prod-color", data.slug + "-1",data.imageIcon3X, data.imageIcon3Y, data.imageIcon3W, data.imageIcon3H, Photo.colorSize, "jpg");

                    data.imageIcon4W = Integer.valueOf(form.data().get("imageIcon"+i+"_4W"));
                    data.imageIcon4Y = Integer.valueOf(form.data().get("imageIcon"+i+"_4Y"));
                    data.imageIcon4X = Integer.valueOf(form.data().get("imageIcon"+i+"_4X"));
                    data.imageIcon4H = Integer.valueOf(form.data().get("imageIcon"+i+"_4H"));
                    File iconFile4 = Photo.uploadImageCrop2(pictureIcon4, "prod-color", data.slug + "-1",data.imageIcon4X, data.imageIcon4Y, data.imageIcon4W, data.imageIcon4H, Photo.colorSize, "jpg");

                    data.imageIcon5W = Integer.valueOf(form.data().get("imageIcon"+i+"_5W"));
                    data.imageIcon5Y = Integer.valueOf(form.data().get("imageIcon"+i+"_5Y"));
                    data.imageIcon5X = Integer.valueOf(form.data().get("imageIcon"+i+"_5X"));
                    data.imageIcon5H = Integer.valueOf(form.data().get("imageIcon"+i+"_5H"));
                    File iconFile5 = Photo.uploadImageCrop2(pictureIcon5, "prod-color", data.slug + "-1",data.imageIcon5X, data.imageIcon5Y, data.imageIcon5W, data.imageIcon5H, Photo.colorSize, "jpg");
					*/

                    data.update();
                    products.add(data);

                    ProductDetail detail = new ProductDetail();
                    detail.mainProduct = data;
                    detail.embededYoutube = data.embededYoutube;
                    detail.thumbnailYoutubeUrl = data.thumbnailYoutubeUrl;
                    detail.brand = getBrand();
                    detail.weight= data.weight;
                    detail.dimension1= data.dimension1;
                    detail.dimension2= data.dimension2;
                    detail.dimension3= data.dimension3;
                    detail.warrantyType= data.warrantyType;
                    if(data.warrantyType != 0){
                        detail.warrantyPeriod = data.warrantyPeriod;
                    }
                    detail.whatInTheBox = data.whatInTheBox;
                    detail.feature = data.feature;
                    detail.specifications = data.specifications;
                    detail.recomendedCare = data.recomendedCare;
                    detail.waranty = data.waranty;
                    detail.description = data.productDescription;
                    detail.sizeGuide = data.sizeGuide;
                    detail.setShortDescriptions(data.listShortDescriptions);

                    String[] fullImageUrls = new String[5];
                    String[] mediumImageUrls = new String[5];
                    String[] thumbnailImageUrls = new String[5];
                    String[] threesixtyImageUrls = new String[5];
                    String[] iconImageUrls = new String[5];

                    int idx = 0;
                    if(new1FileFull != null){
                        fullImageUrls[idx] = Photo.createUrl("prod", new1FileFull.getName());
                        thumbnailImageUrls[idx] = Photo.createUrl("prod-thumb", new1FileThumb.getName());
                        mediumImageUrls[idx] = Photo.createUrl("prod-med", new1FileMedium.getName());
                        threesixtyImageUrls[idx] = Photo.createUrl("prod-icon", new1FileIcon.getName());
                        iconImageUrls[idx] = iconFile1 != null ?  Photo.createUrl("prod-color", iconFile1.getName()) : "";
                        idx++;
                    }
                    if(new2FileFull != null){
                        fullImageUrls[idx] = Photo.createUrl("prod", new2FileFull.getName());
                        threesixtyImageUrls[idx] = Photo.createUrl("prod-icon", new2FileIcon.getName());
                        mediumImageUrls[idx] = Photo.createUrl("prod-med", new2FileMedium.getName());
                        thumbnailImageUrls[idx] = Photo.createUrl("prod-thumb", new2FileThumb.getName());
                        iconImageUrls[idx] = iconFile1 != null ?  Photo.createUrl("prod-color", iconFile1.getName()) : "";
                        idx++;
                    }
                    if(new3FileFull != null){
                        mediumImageUrls[idx] = Photo.createUrl("prod-med", new3FileMedium.getName());
                        thumbnailImageUrls[idx] = Photo.createUrl("prod-thumb", new3FileThumb.getName());
                        threesixtyImageUrls[idx] = Photo.createUrl("prod-icon", new3FileIcon.getName());
                        fullImageUrls[idx] = Photo.createUrl("prod", new3FileFull.getName());
                        iconImageUrls[idx] = iconFile1 != null ?  Photo.createUrl("prod-color", iconFile1.getName()) : "";
                        idx++;
                    }
                    if(new4FileFull != null){
                        thumbnailImageUrls[idx] = Photo.createUrl("prod-thumb", new4FileThumb.getName());
                        fullImageUrls[idx] = Photo.createUrl("prod", new4FileFull.getName());
                        mediumImageUrls[idx] = Photo.createUrl("prod-med", new4FileMedium.getName());
                        threesixtyImageUrls[idx] = Photo.createUrl("prod-icon", new4FileIcon.getName());
                        iconImageUrls[idx] = iconFile1 != null ?  Photo.createUrl("prod-color", iconFile1.getName()) : "";
                        idx++;
                    }
                    if(new5FileFull != null){
                        mediumImageUrls[idx] = Photo.createUrl("prod-med", new5FileMedium.getName());
                        thumbnailImageUrls[idx] = Photo.createUrl("prod-thumb", new5FileThumb.getName());
                        fullImageUrls[idx] = Photo.createUrl("prod", new5FileFull.getName());
                        threesixtyImageUrls[idx] = Photo.createUrl("prod-icon", new5FileIcon.getName());
                        iconImageUrls[idx] = iconFile1 != null ?  Photo.createUrl("prod-color", iconFile1.getName()) : "";
                    }

                    detail.fullImageUrls = Json.toJson(fullImageUrls).toString();
                    detail.mediumImageUrls = Json.toJson(mediumImageUrls).toString();
                    detail.thumbnailImageUrls = Json.toJson(thumbnailImageUrls).toString();
                    detail.threesixtyImageUrls = Json.toJson(threesixtyImageUrls).toString();
                    detail.colorImageUrls = Json.toJson(iconImageUrls).toString();

                    detail.save();

                    ProductProfit profitSS = new ProductProfit();
                    profitSS.type = ProductProfit.typeSupSystem;
                    profitSS.product = data;
                    profitSS.value = data.profitSSValue;
                    profitSS.percentage = data.profitSSPercentage;
                    profitSS.save();

                    ProductProfit profitD = new ProductProfit();
                    profitD.type = ProductProfit.typeDistributor;
                    profitD.product = data;
                    profitD.value = data.profitDValue;
                    profitD.percentage = data.profitDPercentage;
                    profitD.save();

                    ProductProfit profitSD = new ProductProfit();
                    profitSD.type = ProductProfit.typeSubDistributor;
                    profitSD.product = data;
                    profitSD.value = data.profitSDValue;
                    profitSD.percentage = data.profitSDPercentage;
                    profitSD.save();

                    ProductProfit profitR = new ProductProfit();
                    profitR.type = ProductProfit.typeReseller;
                    profitR.product = data;
                    profitR.value = data.profitRValue;
                    profitR.percentage = data.profitRPercentage;
                    profitR.save();
                }

                ProductVariantGroup pvg = new ProductVariantGroup();

                List<BaseAttribute> baseAttributes = new ArrayList<>();
                BaseAttribute base = BaseAttribute.findById(Constant.getInstance().getColorId(), getBrandId());
                baseAttributes.add(base);

                pvg.name = dataOri.name;
                pvg.baseAttributes = baseAttributes;
                pvg.brand = getBrand();
                UserCms cms = getUserCms();

                Product lowestPriceProduct = null;
                for(Product prod : products){
                    if(lowestPriceProduct != null){
                        if(lowestPriceProduct.price > prod.price) {
                            lowestPriceProduct = prod;
                        }
                    }else{
                        lowestPriceProduct = prod;
                    }
                }

                pvg.lowestPriceProduct = lowestPriceProduct;
                pvg.userCms = cms;
                pvg.save();

                for(Product product : products){
                    product.productVariantGroup = pvg;
                    product.update();
                }

                txn.commit();
                flash("success", "Product instance created");
            }catch (Exception e){
                e.printStackTrace();
                txn.rollback();
                flash("error", "Please correct errors bellow."+e);
                return badRequest(htmlAdd(form));
            }finally {
                txn.end();
            }

            if (dataOri.save.equals("1")){
                return redirect(routes.ProductController.index());
            }else{
                return redirect(routes.ProductController.add());
            }
        }
    }

    @Security.Authenticated(Secured.class)
    public static Result update() {
        Form<Product> form = Form.form(Product.class).bindFromRequest();
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart picture1 = body.getFile("imageUrl1");
        Http.MultipartFormData.FilePart picture2 = body.getFile("imageUrl2");
        Http.MultipartFormData.FilePart picture3 = body.getFile("imageUrl3");
        Http.MultipartFormData.FilePart picture4 = body.getFile("imageUrl4");
        Http.MultipartFormData.FilePart picture5 = body.getFile("imageUrl5");
        MultipartFormData.FilePart pictureResponsive = body.getFile("thumbnailYoutubeUrl");
        if (form.hasErrors()){
            flash("error", "Please correct errors bellow.");
            return badRequest(htmlAdd(form));
        }else{
            Product data = form.get();
            Product product = null;
            Transaction txn = Ebean.beginTransaction();
            try {
                product = Product.findById(data.id, getBrandId());

//                Http.MultipartFormData.FilePart pictureyt = body.getFile("thumbnailYoutubeUr1");
//                data.thumbnailYoutubeUr1W = Integer.valueOf(form.data().get("thumbnailYoutubeUr1W"));
//                data.thumbnailYoutubeUr1Y = Integer.valueOf(form.data().get("thumbnailYoutubeUr1Y"));
//                data.thumbnailYoutubeUr1X = Integer.valueOf(form.data().get("thumbnailYoutubeUr1X"));
//                data.thumbnailYoutubeUr1H = Integer.valueOf(form.data().get("thumbnailYoutubeUr1H"));
//
//                File fileThumbYT = Photo.uploadImageCrop2(pictureyt, "you-thumb", data.name + "-1",data.thumbnailYoutubeUr1X, data.thumbnailYoutubeUr1Y, data.thumbnailYoutubeUr1W, data.thumbnailYoutubeUr1H, Photo.thumbImageSize, "jpg");


                ProductDetail detail = ProductDetail.find.where()
                        .eq("mainProduct", product)
                        .eq("brand_id", getBrandId())
                        .findUnique();
//                detail.mainProduct = data;
                data.thumbnailYoutubeUrl = "";
                detail.weight = data.weight;
                detail.dimension1 = data.dimension1;
                detail.dimension2 = data.dimension2;
                detail.dimension3 = data.dimension3;
                detail.warrantyType = data.warrantyType;
                if (data.warrantyType != 0) {
                    detail.warrantyPeriod = data.warrantyPeriod;
                } else {
                    detail.warrantyPeriod = 0;
                }
                detail.whatInTheBox = data.whatInTheBox;
                detail.feature = data.feature;
                detail.specifications = data.specifications;
                detail.recomendedCare = data.recomendedCare;
                detail.waranty = data.waranty;
                detail.description = data.productDescription;
                detail.sizeGuide = data.sizeGuide;
                detail.embededYoutube = data.embededYoutube;
                detail.thumbnailYoutubeUrl = data.thumbnailYoutubeUrl;
                detail.setShortDescriptions(data.listShortDescriptions);

                String[] fullImages = new String[5];
                for (int i = 0; i < detail.getImage1().length; i++) {
                    fullImages[i] = detail.getImage1()[i];
                }
                String[] mediumImages = new String[5];
                for (int i = 0; i < detail.getImage2().length; i++) {
                    mediumImages[i] = detail.getImage2()[i];
                }
                String[] thumbnailImages = new String[5];
                for (int i = 0; i < detail.getImage3().length; i++) {
                    thumbnailImages[i] = detail.getImage3()[i];
                }
                String[] iconImages = new String[5];
                for (int i = 0; i < detail.getImage5().length; i++) {
                    iconImages[i] = detail.getImage5()[i];
                }
                String[] iconImageUrls = new String[5];
                for (int i = 0; i < detail.getImageColor().length; i++) {
                    iconImageUrls[i] = detail.getImageColor()[i];
                }

                Integer i = 0;
				
				Http.MultipartFormData.FilePart pictureIcon1 = body.getFile("imageIcon0_1");
				data.imageIcon1W = Integer.valueOf(form.data().get("imageIcon0_1W"));
				data.imageIcon1Y = Integer.valueOf(form.data().get("imageIcon0_1Y"));
				data.imageIcon1X = Integer.valueOf(form.data().get("imageIcon0_1X"));
				data.imageIcon1H = Integer.valueOf(form.data().get("imageIcon0_1H"));
				File iconFile1 = Photo.uploadImageCrop2(pictureIcon1, "prod-color", data.slug + "-1",data.imageIcon1X, data.imageIcon1Y, data.imageIcon1W, data.imageIcon1H, Photo.colorSize, "jpg");

               /* Http.MultipartFormData.FilePart pictureIcon1 = body.getFile("imageIcon"+i+"_1");
                Http.MultipartFormData.FilePart pictureIcon2 = body.getFile("imageIcon"+i+"_2");
                Http.MultipartFormData.FilePart pictureIcon3 = body.getFile("imageIcon"+i+"_3");
                Http.MultipartFormData.FilePart pictureIcon4 = body.getFile("imageIcon"+i+"_4");
                Http.MultipartFormData.FilePart pictureIcon5 = body.getFile("imageIcon"+i+"_5");
                data.imageIcon1W = Integer.valueOf(form.data().get("imageIcon"+i+"_1W"));
                data.imageIcon1Y = Integer.valueOf(form.data().get("imageIcon"+i+"_1Y"));
                data.imageIcon1X = Integer.valueOf(form.data().get("imageIcon"+i+"_1X"));
                data.imageIcon1H = Integer.valueOf(form.data().get("imageIcon"+i+"_1H"));
                File iconFile1 = Photo.uploadImageCrop2(pictureIcon1, "prod-color", data.slug + "-1",data.imageIcon1X, data.imageIcon1Y, data.imageIcon1W, data.imageIcon1H, Photo.colorSize, "jpg");


                data.imageIcon2W = Integer.valueOf(form.data().get("imageIcon"+i+"_2W"));
                data.imageIcon2Y = Integer.valueOf(form.data().get("imageIcon"+i+"_2Y"));
                data.imageIcon2X = Integer.valueOf(form.data().get("imageIcon"+i+"_2X"));
                data.imageIcon2H = Integer.valueOf(form.data().get("imageIcon"+i+"_2H"));
                File iconFile2 = Photo.uploadImageCrop2(pictureIcon2, "prod-color", data.slug + "-1",data.imageIcon2X, data.imageIcon2Y, data.imageIcon2W, data.imageIcon2H, Photo.colorSize, "jpg");


                data.imageIcon3W = Integer.valueOf(form.data().get("imageIcon"+i+"_3W"));
                data.imageIcon3Y = Integer.valueOf(form.data().get("imageIcon"+i+"_3Y"));
                data.imageIcon3X = Integer.valueOf(form.data().get("imageIcon"+i+"_3X"));
                data.imageIcon3H = Integer.valueOf(form.data().get("imageIcon"+i+"_3H"));
                File iconFile3 = Photo.uploadImageCrop2(pictureIcon3, "prod-color", data.slug + "-1",data.imageIcon3X, data.imageIcon3Y, data.imageIcon3W, data.imageIcon3H, Photo.colorSize, "jpg");

                data.imageIcon4W = Integer.valueOf(form.data().get("imageIcon"+i+"_4W"));
                data.imageIcon4Y = Integer.valueOf(form.data().get("imageIcon"+i+"_4Y"));
                data.imageIcon4X = Integer.valueOf(form.data().get("imageIcon"+i+"_4X"));
                data.imageIcon4H = Integer.valueOf(form.data().get("imageIcon"+i+"_4H"));
                File iconFile4 = Photo.uploadImageCrop2(pictureIcon4, "prod-color", data.slug + "-1",data.imageIcon4X, data.imageIcon4Y, data.imageIcon4W, data.imageIcon4H, Photo.colorSize, "jpg");

                data.imageIcon5W = Integer.valueOf(form.data().get("imageIcon"+i+"_5W"));
                data.imageIcon5Y = Integer.valueOf(form.data().get("imageIcon"+i+"_5Y"));
                data.imageIcon5X = Integer.valueOf(form.data().get("imageIcon"+i+"_5X"));
                data.imageIcon5H = Integer.valueOf(form.data().get("imageIcon"+i+"_5H"));
                File iconFile5 = Photo.uploadImageCrop2(pictureIcon5, "prod-color", data.slug + "-1",data.imageIcon5X, data.imageIcon5Y, data.imageIcon5W, data.imageIcon5H, Photo.colorSize, "jpg");
*/

                File new1FileFull = null;
                File new1FilesMedium = null;
                File new1FilesThumb = null;
                File new1FilesIcon = null;
                if (data.imageUrl1W < 100){
                    new1FileFull = Photo.uploadImage(picture1, "prod", product.slug + "-1", Photo.fullImageSize, "jpg");
                    new1FilesMedium = Photo.uploadImage(picture1, "prod-med", product.slug + "-1", Photo.mediumImageSize, "jpg");
                    new1FilesThumb = Photo.uploadImage(picture1, "prod-thumb", product.slug + "-1", Photo.thumbImageSize, "jpg");
                    new1FilesIcon = Photo.uploadImage(picture1, "prod-icon", product.slug + "-1", Photo.iconImageSize, "jpg");
                }else{
                    new1FileFull = Photo.uploadImageCrop2(picture1, "prod", product.slug + "-1",data.imageUrl1X, data.imageUrl1Y, data.imageUrl1W, data.imageUrl1H, Photo.fullImageSize, "jpg");
                    new1FilesMedium = Photo.uploadImageCrop2(picture1, "prod-med", product.slug + "-1",data.imageUrl1X, data.imageUrl1Y, data.imageUrl1W, data.imageUrl1H, Photo.mediumImageSize, "jpg");
                    new1FilesThumb = Photo.uploadImageCrop2(picture1, "prod-thumb", product.slug + "-1",data.imageUrl1X, data.imageUrl1Y, data.imageUrl1W, data.imageUrl1H, Photo.thumbImageSize, "jpg");
                    new1FilesIcon = Photo.uploadImageCrop2(picture1, "prod-icon", product.slug + "-1",data.imageUrl1X, data.imageUrl1Y, data.imageUrl1W, data.imageUrl1H, Photo.iconImageSize, "jpg");
                }
                if (new1FileFull != null) {
                    fullImages[0] = Photo.createUrl("prod", new1FileFull.getName());
                    mediumImages[0] = Photo.createUrl("prod-med", new1FilesMedium.getName());
                    thumbnailImages[0] = Photo.createUrl("prod-thumb", new1FilesThumb.getName());
                    iconImages[0] = Photo.createUrl("prod-icon", new1FilesIcon.getName());
                }

                if (fullImages[0]!= null && !fullImages[0].isEmpty()){
                    iconImageUrls[0] = iconFile1 != null ?  Photo.createUrl("prod-color", iconFile1.getName()) : iconImageUrls[0];
                }

                File new2FileFull = null;
                File new2FilesMedium = null;
                File new2FilesThumb = null;
                File new2FilesIcon = null;
                if (data.imageUrl2W < 100){
                    new2FileFull = Photo.uploadImage(picture2, "prod", product.slug+"-2", Photo.fullImageSize, "jpg");
                    new2FilesMedium = Photo.uploadImage(picture2, "prod-med", product.slug+"-2", Photo.mediumImageSize, "jpg");
                    new2FilesThumb = Photo.uploadImage(picture2, "prod-thumb", product.slug+"-2", Photo.thumbImageSize, "jpg");
                    new2FilesIcon = Photo.uploadImage(picture2, "prod-icon", product.slug+"-2", Photo.iconImageSize, "jpg");
                }else{
                    new2FileFull = Photo.uploadImageCrop2(picture2, "prod", product.slug + "-2",data.imageUrl2X, data.imageUrl2Y, data.imageUrl2W, data.imageUrl2H, Photo.fullImageSize, "jpg");
                    new2FilesMedium = Photo.uploadImageCrop2(picture2, "prod-med", product.slug + "-2",data.imageUrl2X, data.imageUrl2Y, data.imageUrl2W, data.imageUrl2H, Photo.mediumImageSize, "jpg");
                    new2FilesThumb = Photo.uploadImageCrop2(picture2, "prod-thumb", product.slug + "-2",data.imageUrl2X, data.imageUrl2Y, data.imageUrl2W, data.imageUrl2H, Photo.thumbImageSize, "jpg");
                    new2FilesIcon = Photo.uploadImageCrop2(picture2, "prod-icon", product.slug + "-2",data.imageUrl2X, data.imageUrl2Y, data.imageUrl2W, data.imageUrl2H, Photo.iconImageSize, "jpg");
                }
                if (new2FileFull != null) {
                    fullImages[1] = Photo.createUrl("prod", new2FileFull.getName());
                    mediumImages[1] = Photo.createUrl("prod-med", new2FilesMedium.getName());
                    thumbnailImages[1] = Photo.createUrl("prod-thumb", new2FilesThumb.getName());
                    iconImages[1] = Photo.createUrl("prod-icon", new2FilesIcon.getName());

                }

               /* if (fullImages[1]!= null && !fullImages[1].isEmpty()){
                    iconImageUrls[1] = iconFile2 != null ?  Photo.createUrl("prod-color", iconFile2.getName()) : iconImageUrls[1];
                }*/

                File new3FileFull = null;
                File new3FilesMedium = null;
                File new3FilesThumb = null;
                File new3FilesIcon = null;
                if (data.imageUrl3W < 100){
                    new3FileFull = Photo.uploadImage(picture3, "prod", product.slug+"-3", Photo.fullImageSize, "jpg");
                    new3FilesMedium = Photo.uploadImage(picture3, "prod-med", product.slug+"-3", Photo.mediumImageSize, "jpg");
                    new3FilesThumb = Photo.uploadImage(picture3, "prod-thumb", product.slug+"-3", Photo.thumbImageSize, "jpg");
                    new3FilesIcon = Photo.uploadImage(picture3, "prod-icon", product.slug+"-3", Photo.iconImageSize, "jpg");
                }else{
                    new3FileFull = Photo.uploadImageCrop2(picture3, "prod", product.slug + "-3",data.imageUrl3X, data.imageUrl3Y, data.imageUrl3W, data.imageUrl3H, Photo.fullImageSize, "jpg");
                    new3FilesMedium = Photo.uploadImageCrop2(picture3, "prod-med", product.slug + "-3",data.imageUrl3X, data.imageUrl3Y, data.imageUrl3W, data.imageUrl3H, Photo.mediumImageSize, "jpg");
                    new3FilesThumb = Photo.uploadImageCrop2(picture3, "prod-thumb", product.slug + "-3",data.imageUrl3X, data.imageUrl3Y, data.imageUrl3W, data.imageUrl3H, Photo.thumbImageSize, "jpg");
                    new3FilesIcon = Photo.uploadImageCrop2(picture3, "prod-icon", product.slug + "-3",data.imageUrl3X, data.imageUrl3Y, data.imageUrl3W, data.imageUrl3H, Photo.iconImageSize, "jpg");
                }
                if (new3FileFull != null) {
                    fullImages[2] = Photo.createUrl("prod", new3FileFull.getName());
                    mediumImages[2] = Photo.createUrl("prod-med", new3FilesMedium.getName());
                    thumbnailImages[2] = Photo.createUrl("prod-thumb", new3FilesThumb.getName());
                    iconImages[2] = Photo.createUrl("prod-icon", new3FilesIcon.getName());
                }
               /* if (fullImages[2]!= null && !fullImages[2].isEmpty()){
                    iconImageUrls[2] = iconFile3 != null ?  Photo.createUrl("prod-color", iconFile3.getName()) : iconImageUrls[2];
                }*/

                File new4FileFull = null;
                File new4FilesMedium = null;
                File new4FilesThumb = null;
                File new4FilesIcon = null;
                if (data.imageUrl4W < 100){
                    new4FileFull = Photo.uploadImage(picture4, "prod", product.slug+"-4", Photo.fullImageSize, "jpg");
                    new4FilesMedium = Photo.uploadImage(picture4, "prod-med", product.slug+"-4", Photo.mediumImageSize, "jpg");
                    new4FilesThumb = Photo.uploadImage(picture4, "prod-thumb", product.slug+"-4", Photo.thumbImageSize, "jpg");
                    new4FilesIcon = Photo.uploadImage(picture4, "prod-icon", product.slug+"-4", Photo.iconImageSize, "jpg");
                }else{
                    new4FileFull = Photo.uploadImageCrop2(picture4, "prod", product.slug + "-4",data.imageUrl4X, data.imageUrl4Y, data.imageUrl4W, data.imageUrl4H, Photo.fullImageSize, "jpg");
                    new4FilesMedium = Photo.uploadImageCrop2(picture4, "prod-med", product.slug + "-4",data.imageUrl4X, data.imageUrl4Y, data.imageUrl4W, data.imageUrl4H, Photo.mediumImageSize, "jpg");
                    new4FilesThumb = Photo.uploadImageCrop2(picture4, "prod-thumb", product.slug + "-4",data.imageUrl4X, data.imageUrl4Y, data.imageUrl4W, data.imageUrl4H, Photo.thumbImageSize, "jpg");
                    new4FilesIcon = Photo.uploadImageCrop2(picture4, "prod-icon", product.slug + "-4",data.imageUrl4X, data.imageUrl4Y, data.imageUrl4W, data.imageUrl4H, Photo.iconImageSize, "jpg");
                }
                if (new4FileFull != null) {
                    fullImages[3] = Photo.createUrl("prod", new4FileFull.getName());
                    mediumImages[3] = Photo.createUrl("prod-med", new4FilesMedium.getName());
                    thumbnailImages[3] = Photo.createUrl("prod-thumb", new4FilesThumb.getName());
                    iconImages[3] = Photo.createUrl("prod-icon", new4FilesIcon.getName());
                }
               /* if (fullImages[3]!= null && !fullImages[3].isEmpty()){
                    iconImageUrls[3] = iconFile4 != null ?  Photo.createUrl("prod-color", iconFile4.getName()) : iconImageUrls[3];
                }*/

                File new5FileFull = null;
                File new5FilesMedium = null;
                File new5FilesThumb = null;
                File new5FilesIcon = null;
                if (data.imageUrl5W < 100){
                    new5FileFull = Photo.uploadImage(picture5, "prod", product.slug+"-5", Photo.fullImageSize, "jpg");
                    new5FilesMedium = Photo.uploadImage(picture5, "prod-med", product.slug+"-5", Photo.mediumImageSize, "jpg");
                    new5FilesThumb = Photo.uploadImage(picture5, "prod-thumb", product.slug+"-5", Photo.thumbImageSize, "jpg");
                    new5FilesIcon = Photo.uploadImage(picture5, "prod-icon", product.slug+"-5", Photo.iconImageSize, "jpg");
                }else{
                    new5FileFull = Photo.uploadImageCrop2(picture5, "prod", product.slug + "-5",data.imageUrl5X, data.imageUrl5Y, data.imageUrl5W, data.imageUrl5H, Photo.fullImageSize, "jpg");
                    new5FilesMedium = Photo.uploadImageCrop2(picture5, "prod-med", product.slug + "-5",data.imageUrl5X, data.imageUrl5Y, data.imageUrl5W, data.imageUrl5H, Photo.mediumImageSize, "jpg");
                    new5FilesThumb = Photo.uploadImageCrop2(picture5, "prod-thumb", product.slug + "-5",data.imageUrl5X, data.imageUrl5Y, data.imageUrl5W, data.imageUrl5H, Photo.thumbImageSize, "jpg");
                    new5FilesIcon = Photo.uploadImageCrop2(picture5, "prod-icon", product.slug + "-5",data.imageUrl5X, data.imageUrl5Y, data.imageUrl5W, data.imageUrl5H, Photo.iconImageSize, "jpg");
                }
                if (new5FileFull != null) {
                    fullImages[4] = Photo.createUrl("prod", new5FileFull.getName());
                    mediumImages[4] = Photo.createUrl("prod-med", new5FilesMedium.getName());
                    thumbnailImages[4] = Photo.createUrl("prod-thumb", new5FilesThumb.getName());
                    iconImages[4] = Photo.createUrl("prod-icon", new5FilesIcon.getName());
                }
                /*if (fullImages[4]!= null && !fullImages[4].isEmpty()){
                    iconImageUrls[4] = iconFile5 != null ?  Photo.createUrl("prod-color", iconFile5.getName()) : iconImageUrls[4];
                }*/


                detail.fullImageUrls = Json.toJson(fullImages).toString();
                detail.mediumImageUrls = Json.toJson(mediumImages).toString();
                detail.thumbnailImageUrls = Json.toJson(thumbnailImages).toString();
                detail.threesixtyImageUrls = Json.toJson(iconImages).toString();
                detail.colorImageUrls = Json.toJson(iconImageUrls).toString();
                detail.update();

                ProductProfit profitSS = ProductProfit.find.where().eq("product", data).eq("type", 0)
                        .eq("is_deleted", false).findUnique();
                if (profitSS == null) {
                    profitSS = new ProductProfit();
                    profitSS.type = ProductProfit.typeSupSystem;
                    profitSS.product = data;
                    profitSS.value = data.profitSSValue;
                    profitSS.percentage = data.profitSSPercentage;
                    profitSS.save();
                } else {
                    profitSS.value = data.profitSSValue;
                    profitSS.percentage = data.profitSSPercentage;
                    profitSS.update();
                }

                ProductProfit profitD = ProductProfit.find.where().eq("product", data).eq("type", 1)
                        .eq("is_deleted", false).findUnique();
                if (profitD == null) {
                    profitD = new ProductProfit();
                    profitD.type = ProductProfit.typeDistributor;
                    profitD.product = data;
                    profitD.value = data.profitDValue;
                    profitD.percentage = data.profitDPercentage;
                    profitD.save();
                } else {
                    profitD.value = data.profitDValue;
                    profitD.percentage = data.profitDPercentage;
                    profitD.update();
                }

                ProductProfit profitSD = ProductProfit.find.where().eq("product", data).eq("type", 2)
                        .eq("is_deleted", false).findUnique();
                if (profitSD == null) {
                    profitSD = new ProductProfit();
                    profitSD.type = ProductProfit.typeSubDistributor;
                    profitSD.product = data;
                    profitSD.value = data.profitSDValue;
                    profitSD.percentage = data.profitSDPercentage;
                    profitSD.save();
                } else {
                    profitSD.value = data.profitSDValue;
                    profitSD.percentage = data.profitSDPercentage;
                    profitSD.update();
                }

                ProductProfit profitR = ProductProfit.find.where().eq("product", data).eq("type", 3)
                        .eq("is_deleted", false).findUnique();
                if (profitR == null) {
                    profitR = new ProductProfit();
                    profitR.type = ProductProfit.typeReseller;
                    profitR.product = data;
                    profitR.value = data.profitRValue;
                    profitR.percentage = data.profitRPercentage;
                    profitR.save();
                } else {
                    profitR.value = data.profitRValue;
                    profitR.percentage = data.profitRPercentage;
                    profitR.update();
                }


                if (new1FileFull != null) {
                    product.imageUrl = Photo.createUrl("prod", new1FileFull.getName());
                    product.thumbnailUrl = Photo.createUrl("prod-thumb", new1FilesThumb.getName());
                }
                product.grandParentCategory = Category.findById(data.categoryId, getBrandId());
                product.parentCategory = Category.findById(data.subCategoryId, getBrandId());
                product.category = Category.findById(data.subSubCategoryId, getBrandId());


//                if(data.brandId != null){
//                    product.brand = Brand.find.byId(data.brandId);
//                }

                //product.currency = Currency.find.byId(data.currencyCode);
                product.currency = Currency.find.byId("RP");
                product.discount = data.discount;
                product.price = data.price;
                product.buyPrice = data.buyPrice;
                product.discount = data.discount;
                product.discountType = data.discountType;
                product.strikeThroughDisplay = data.price;
                product.articleCode = data.articleCode;
                product.slug = data.slug;
                /*if(data.discount == 0 ){
                    product.priceDisplay = data.price;
                    product.discountType = 0;
                    product.discountActiveFrom = null;
                    product.discountActiveTo = null;
                }else{
                    try {
                        if(data.discountType == 1){
                            product.priceDisplay = data.price - data.discount;
                        }else{
                            product.priceDisplay = data.price - (data.price * (data.discount/100));
                        }

                        product.discountActiveFrom = simpleDateFormat.parse(data.fromDate + " " + data.fromTime);
                        product.discountActiveTo = simpleDateFormat.parse(data.toDate + " " + data.toTime);
                    }catch (ParseException e){
                        product.discountActiveFrom = null;
                        product.discountActiveTo = null;
                    }
                }*/

                Set<BaseAttribute> listBaseAttribute =  new HashSet<>();
                List<Attribute> listAttribute = new LinkedList<>();
                for(Long idAtt : data.listAttribute){
                    if(idAtt > 0) {
                        Attribute attribute = Attribute.findById(idAtt, getBrandId());
                        listAttribute.add(attribute);
                        listBaseAttribute.add(attribute.baseAttribute);
                    }
                }

                Set<Size> listSize = new HashSet<>();
                if(data.sizeids != null) {
                    for (Long idSize : data.sizeids) {
                        if (idSize > 0) {
                            Size size = Size.findById(idSize, getBrandId());
                            listSize.add(size);
                        }
                    }
                }
                product.sizes = listSize;

                product.baseAttributes = listBaseAttribute;
                product.attributes = listAttribute;
                product.name = data.name;
                product.title = data.title;
                product.keyword = data.keyword;
                product.description = data.description;

                List<Product> products = new ArrayList<>();
                if(data.product_list != null) {
                    for (String p : data.product_list) {
                        products.add(Product.find.byId(Long.parseLong(p)));
                    }
                }
                product.productAlsoViews = products;

                product.update();

                txn.commit();
            }catch (Exception e){
                e.printStackTrace();
                txn.rollback();
                flash("error", "Please correct errors bellow.");
                return badRequest(htmlAdd(form));
            }finally {
                txn.end();
            }
            flash("success", "Product success edited");
            return redirect(routes.ProductController.index());
        }
    }

    private static Html htmlList(){
        RoleFeature feature = getUserAccessByFeature(featureKey);
        return list.render("Product List", "List", feature);
    }
    private static Html htmlDetail(Product product, ProductDetail productDetail, List<ProductReview> reviews, Call back){
        RoleFeature feature = getUserAccessByFeature(featureKeyReview);
        return detail.render("Product Detail", product, productDetail, reviews, routes.ProductController.saveReview(), back, feature);
    }

    @Security.Authenticated(Secured.class)
    public static Result index() {
        return ok(htmlList());
    }

    @Security.Authenticated(Secured.class)
    public static Result lists() {
        RoleFeature feature = getUserAccessByFeature(featureKey);
        Map<String, String[]> params = request().queryString();

        Integer iTotalRecords = Product.findRowCount(getBrand());
        String name = params.get("search[value]")[0];
        Integer filter = Integer.valueOf(params.get("filter")[0]);


        Integer pageSize = Integer.valueOf(params.get("length")[0]);
        Integer page = Integer.valueOf(params.get("start")[0]) / pageSize;

        String sortBy = "name";
        String order = params.get("order[0][dir]")[0];

        switch (Integer.valueOf(params.get("order[0][column]")[0])) {
            case 1 :  sortBy = "id"; break;
            case 2 :  sortBy = "name"; break;
        }

        ExpressionList<Product> qry = Product.find
                .where()
                .ilike("name", "%" + name + "%")
                .eq("is_deleted", false);

        switch (filter){
            case 1: qry.eq("status", true);
                break;
            case 2: qry.eq("status", false);
                break;
        }

        Page<Product> datas = qry
                .eq("brand_id", getBrandId())
                .orderBy(sortBy + " " + order)
                .findPagingList(pageSize)
                .setFetchAhead(false)
                .getPage(page);
        Integer iTotalDisplayRecords = datas.getTotalRowCount();

        ObjectNode result = Json.newObject();

        result.put("draw", Integer.valueOf(params.get("draw")[0]));
        result.put("recordsTotal", iTotalRecords);
        result.put("recordsFiltered", iTotalDisplayRecords);

        ArrayNode an = result.putArray("data");
        int num = Integer.valueOf(params.get("start")[0]) + 1;
        for (Product dt : datas.getList()) {
            ObjectNode row = Json.newObject();
            String action = "";
            if(feature.isView()){
                action += "<a class=\"btn btn-default btn-sm action\" title=\"View\" href=\""+ routes.ProductController.detail(dt.id)+"\"><i class=\"fa fa-search\"></i></a>&nbsp;";
            }
            if(feature.isEdit()){
                action += "<a class=\"btn btn-primary btn-sm action\" title=\"Edit\" href=\"javascript:editData("+dt.id+");\"><i class=\"fa fa-pencil\"></i></a>&nbsp;" ;

            }
            if(feature.isEdit() || feature.isAdd()){
                action += "<a class=\"btn btn-success btn-sm action\" title=\"Update Stock\" href=\"javascript:updateStock(" + dt.id + ", "+dt.itemCount+");\"><i class=\"fa fa-pencil\"></i></a>&nbsp;";
            }
            if(feature.isEdit()){
                action += "<a class=\"btn btn-success btn-sm action\" title=\"Manual Like\" href=\"javascript:updateLike(" + dt.id + ", "+dt.likeCount+");\"><i class=\"fa fa-thumbs-o-up\"></i></a>&nbsp;";
            }
            if(feature.isDelete()){
                action += "<a class=\"btn btn-danger btn-sm action\" title=\"Delete\" href=\"javascript:deleteData("+dt.id+");\"><i class=\"fa fa-trash\"></i></a>&nbsp;";
            }
            if(feature.isEdit()) {
                action += "<input value=\"" + dt.id + "\" class=\"cb-status\" data-toggle=\"toggle\" data-on=\"Active\" data-off=\"Inactive\" data-size=\"small\" data-style=\"ios\" data-offstyle=\"danger\" type=\"checkbox\" " + ((dt.status) ? "checked" : "") + ">";
            }
//            if(feature.isAdd() || feature.isEdit()){
//                action += "<a class=\"btn btn-default btn-sm action\" href=\""+ routes.ProductController.translation(dt.id)+"\"><i class=\"fa fa-search\"></i></a>";
//            }

            row.put("0", dt.id.toString());
            row.put("1", num);
            row.put("2", dt.name);
            row.put("3", dt.category.name);
            row.put("4", CommonFunction.numberFormat(dt.price));
            row.put("5", dt.itemCount);
            row.put("6", dt.likeCount);
            row.put("7", dt.getRealLikeCount());
            row.put("8", action);
            an.add(row);
            num++;
        }

        return ok(result);
    }

    @Security.Authenticated(Secured.class)
    public static Result detail(Long id) {
        Product product = Product.findById(id, getBrandId());
        ProductDetail proddetail = product.getProductDetail(getBrandId());
        List<ProductReview> reviews = ProductReview.find.where()
                .eq("is_deleted", false)
                .eq("brand_id", getBrandId())
                .eq("product", product)
                .orderBy("createdAt").findList();
        Call back = routes.ProductController.index();

        return ok(htmlDetail(product, proddetail, reviews, back));
    }


    @Security.Authenticated(Secured.class)
    public static Result delete(String id) {
        Ebean.beginTransaction();
        int status = 0;
        try {
            for (String aTmp : id.split(",")) {
                Product data = Product.findById(Long.parseLong(aTmp), getBrandId());
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


    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Security.Authenticated(Secured.class)
    public static Result updateStatus(String id, String newStatus) {

        Ebean.beginTransaction();
        int status = 0;
        try {
            String[] tmp = id.split(",");
            for (int i=0; i < tmp.length; i++)
            {

                Product data = Product.findById(Long.parseLong(tmp[i]), getBrandId());
                data.updateStatus(newStatus);
//                data.update();

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

    @Security.Authenticated(Secured.class)
    public static Result saveReview() {
        DynamicForm dForm = Form.form().bindFromRequest();
        Long productId = Long.parseLong(dForm.get("product_id"));
        Transaction txn = Ebean.beginTransaction();
        try {
            String review = dForm.get("review");
            int rating = Integer.parseInt(dForm.get("rating"));
            ProductReview productReview = new ProductReview();
            productReview.approvedStatus = "A";
            productReview.reviewer = dForm.get("reviewer") != "" ? dForm.get("reviewer") : null;
            productReview.rating = rating;
            productReview.comment = review;
            productReview.brand = getBrand();
            productReview.product = Product.findById(productId, getBrandId());
            productReview.user = getUserCms();
            productReview.save();
            txn.commit();
            flash("success", "Product review instance created");
        }catch (Exception e){
            flash("error", "Product review instance create failed");
        }finally {
            txn.end();
        }

        return redirect(routes.ProductController.detail(productId));

    }

    @Security.Authenticated(Secured.class)
    public static Result importProduct() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DynamicForm dForm = Form.form().bindFromRequest();
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart file = body.getFile("import");

        Set<BaseAttribute> listBaseAttribute = new HashSet<>();
        Set<Attribute> listAttribute = new HashSet<>();
        Category category = null;
        Brand brand = null;


        int line = 0;
        int cell = 0;
        String row = "";
        String errorMessage = null;
        Boolean isFirstLine = true;
        try  {
            FileInputStream excelFile = new FileInputStream(file.getFile());
            XSSFWorkbook workbook = new XSSFWorkbook(excelFile);
            XSSFSheet datatypeSheet = workbook.getSheetAt(0);
            Iterator<Row> iterator = datatypeSheet.iterator();

            while (iterator.hasNext()) {
                Row currentRow = iterator.next();
                Iterator<Cell> cellIterator = currentRow.iterator();
                if(isFirstLine){
                    isFirstLine = false;
                } else {
                    line++;
                    cell = 0;
                    String name = "";
                    String title = "";
                    String keyword = "";
                    String description = "";
                    String categoryId = "";
                    String vendorId = "";
                    String brandId = "";
                    String weight = "";
                    String dimension = "";
                    String currency = "Rp";
                    String buyPrice = "";
                    String price = "";
                    String discount = "";
                    String discountType = "";
                    String discountValidFrom = "";
                    String discountValidTo = "";
                    String warranty = "";
                    String warrantyPeriod = "";
                    String attribute = "";
                    String shortDescription = "";
                    String longDescription = "";
                    String whatsInTheBox = "";
                    String currentCellValue = "";
                    while (cellIterator.hasNext()) {
                        Cell currentCell = cellIterator.next();
                        if (currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            currentCellValue = String.valueOf( (int) currentCell.getNumericCellValue());
                        }else currentCellValue = currentCell.getStringCellValue();
                        switch (cell){
                            case 0 : name = currentCellValue; break;
                            case 1 : title = currentCellValue; break;
                            case 2 : keyword = currentCellValue; break;
                            case 3 : description = currentCellValue; break;
                            case 4 : categoryId = currentCellValue; break;
                            case 6 : vendorId = currentCellValue; break;
                            case 7 : brandId = currentCellValue; break;
                            case 8 : weight = currentCellValue; break;
                            case 9 : dimension = currentCellValue; break;
                            case 10 : buyPrice = currentCellValue; break;
                            case 11 : price = currentCellValue; break;
                            case 12 : discount = currentCellValue; break;
                            case 13 : discountType = currentCellValue; break;
                            case 14 : discountValidFrom = currentCellValue; break;
                            case 15 : discountValidTo = currentCellValue; break;
                            case 16 : warranty = currentCellValue; break;
                            case 17 : warrantyPeriod = currentCellValue; break;
                            case 18 : attribute = currentCellValue; break;
                            case 19 : shortDescription = currentCellValue; break;
                            case 20 : longDescription = currentCellValue; break;
                            case 21 : whatsInTheBox = currentCellValue; break;
                        }

                        cell ++;
                    }

                    if(errorMessage == null && name.trim().equals("")){
                        errorMessage = "Name is required. Line " + line;
                    }
                    if(errorMessage == null && title.trim().equals("")){
                        errorMessage = "Meta title is required. Line " + line;
                    }
                    if(errorMessage == null && keyword.trim().equals("")){
                        errorMessage = "Meta keyword is required. Line " + line;
                    }
                    if(errorMessage == null && description.trim().equals("")){
                        errorMessage = "Meta description is required. Line " + line;
                    }
                    if(errorMessage == null && categoryId.trim().equals("")){
                        errorMessage = "Category ID is required. Line " + line;
                    }

                    if(errorMessage == null && vendorId.trim().equals("")){
                        errorMessage = "Vendor ID is required. Line " + line;
                    }
                    if(errorMessage == null && brandId.trim().equals("")){
                        errorMessage = "Brand ID is required. Line " + line;
                    }
                    if(errorMessage == null){
                        if(weight.trim().equals("")){
                            errorMessage = "Weight is required. Line " + line;
                        }else{
                            try{
                                Double doubleWeight = Double.parseDouble(weight);
                            }catch (Exception e){
                                errorMessage = "Invalid weight. Line " + line;
                            }
                        }
                    }
                    if(errorMessage == null){
                        if(dimension.trim().equals("")){
                            errorMessage = "Dimension is required. Line " + line;
                        }else{
                            try{
                                String[] tmpDimension = dimension.toLowerCase().split("x");
                                if(tmpDimension.length != 3){
                                    errorMessage = "Invalid dimension. Line " + line;
                                }else{
                                    Double doubleDimension1 = Double.parseDouble(tmpDimension[0]);
                                    Double doubleDimension2 = Double.parseDouble(tmpDimension[1]);
                                    Double doubleDimension3 = Double.parseDouble(tmpDimension[2]);
                                }
                            }catch (Exception e){
                                errorMessage = "Invalid dimension. Line " + line;
                            }
                        }
                    }
                    if(errorMessage == null){
                        if(buyPrice.trim().equals("")){
                            errorMessage = "Buy price is required. Line " + line;
                        }else {
                            try{
                                Double DBuyPrice = Double.parseDouble(buyPrice);
                            }catch (Exception e){
                                errorMessage = "Invalid buy price. Line " + line;
                            }
                        }
                    }
                    if(errorMessage == null){
                        if(price.trim().equals("")){
                            errorMessage = "Price is required. Line " + line;
                        }else {
                            try{
                                Double DPrice = Double.parseDouble(price);
                            }catch (Exception e){
                                errorMessage = "Invalid price. Line " + line;
                            }
                        }
                    }
                    if(errorMessage == null && !discount.trim().equals("")){
                        try{
                            Double DDiscount = Double.parseDouble(discount);
                        }catch (Exception e){
                            errorMessage = "Invalid discount. Line " + line;
                            Logger.debug("invalid 1");
                        }

                        if(errorMessage == null){
                            try{
                                Double DDiscount = Double.parseDouble(discount);
                                if(DDiscount > 0 && discountType.trim().equals("")){
                                    errorMessage = "Discount type is required. Line " + line;
                                    Logger.debug("invalid 2");
                                }else if(DDiscount > 0 && !discountType.trim().equals("1") && !discountType.trim().equals("2")){
                                    errorMessage = "Invalid discount type. Line " + line;
                                    Logger.debug("invalid 3");
                                }
                            }catch (Exception e){
                                errorMessage = "Invalid discount. Line " + line;
                                Logger.debug("invalid 4");
                            }
                        }

                        if(errorMessage == null){
                            if(discountValidFrom.trim().equals("")){
                                errorMessage = "Discount from is required. Line " + line;
                            }
                            if(discountValidTo.trim().equals("")){
                                errorMessage = "Discount to is required. Line " + line;
                            }
                        }

                        if(errorMessage == null){
                            try {
                                Date tmpDateFrom = simpleDateFormat.parse(discountValidFrom);
                            }catch (Exception e){
                                errorMessage = "Invalid discount from format. Line " + line;
                            }
                        }
                        if(errorMessage == null){
                            try {
                                Date tmpDateFrom = simpleDateFormat.parse(discountValidTo);
                            }catch (Exception e){
                                errorMessage = "Invalid discount to format. Line " + line;
                            }
                        }
                    }
                    if(errorMessage == null){
                        if(warranty.trim().equals("")){
                            errorMessage = "Warranty is required. Line " + line;
                        }
                    }
                    if(errorMessage == null){
                        if(!warranty.trim().equals("0")){
                            try{
                                int period = Integer.parseInt(warrantyPeriod);
                            }catch (Exception e){
                                errorMessage = "Invalid warranty period. Line " + line;
                            }
                        }
                    }
                    if(errorMessage == null && attribute.trim().equals("")){
                        errorMessage = "Attribute is required. Line " + line;
                    }
                    if(errorMessage == null){
                        String[] attributeTmp = attribute.split(";");
                        for (String attr : attributeTmp) {
                            Attribute attribut = null;
                            try {
                                attribut = Attribute.findById(Long.parseLong(attr), getBrandId());
                                listAttribute.add(attribut);
                                listBaseAttribute.add(attribut.baseAttribute);
                            } catch (Exception e) {
                                errorMessage = "Invalid attribute. Line " + line;
                            }
                        }
                    }
                    if(errorMessage == null && shortDescription.trim().equals("")){
                        errorMessage = "Short description is required. Line " + line;
                    }
                    if(errorMessage == null && longDescription.trim().equals("")){
                        errorMessage = "Detail description is required. Line " + line;
                    }
                    if(errorMessage == null && whatsInTheBox.trim().equals("")){
                        errorMessage = "What's in the box is required. Line " + line;
                    }
                    if (errorMessage == null) {
                        try {
                            category = Category.findById(Long.valueOf(categoryId), getBrandId());
                            if(category.parentCategory == null || category.parentCategory.parentCategory == null){
                                errorMessage = "Invalid category on line " + line;
                            }
                        } catch (Exception e) {
                            errorMessage = "Invalid category on line " + line;
                        }
                    }
//                    if (errorMessage == null) {
//                        try {
//                            brand = Brand.find.byId(Long.valueOf(brandId));
//                        } catch (Exception e) {
//                            errorMessage = "Invalid brand on line " + line;
//                        }
//                    }

                    if(errorMessage == null) {
                        Product data = null;
                        ProductDetail detail = null;
                        boolean isNew = true;
                        try {
                            data = Product.find.where()
                                    .eq("name", name)
                                    .eq("brand_id", getBrandId())
                                    .setMaxRows(1).findUnique();
                            detail = ProductDetail.find.where()
                                    .eq("mainProduct.id", data.id)
                                    .eq("brand_id", getBrandId())
                                    .setMaxRows(1).findUnique();
                            isNew = false;
                        }catch (Exception e){
                            data = new Product();
                            data.approvedStatus = "A";
                            data.status = true;
                            data.itemCount = 0L;

                            detail = new ProductDetail();
                        }

                        data.name = name;
                        data.title = title;
                        data.keyword = keyword;
                        data.description = description;
                        data.grandParentCategory = category.parentCategory.parentCategory;
                        data.parentCategory = category.parentCategory;
                        data.category = category;
                        data.brand = brand;
                        data.currency = Currency.find.byId(currency);
                        data.buyPrice = Double.parseDouble(buyPrice);
                        data.price = Double.parseDouble(price);
                        data.discount = Double.parseDouble(discount);
                        data.strikeThroughDisplay = data.price;
                        if (data.discount == 0) {
                            data.discountType = 0;
                        } else {
                            data.discountType = Integer.parseInt(discountType);
                            if (data.discountType == 1) {
                                data.priceDisplay = data.price - data.discount;
                            } else {
                                data.priceDisplay = data.price - (data.price * (data.discount / 100));
                            }

                            try {
                                data.discountActiveFrom = simpleDateFormat.parse(discountValidFrom);
                                data.discountActiveTo = simpleDateFormat.parse(discountValidTo);
                            } catch (ParseException e) {
                                data.discountActiveFrom = null;
                                data.discountActiveTo = null;
                            }
                        }

                        data.baseAttributes.clear();
//                        data.baseAttributes = listBaseAttribute;
                        data.attributes.clear();
//                        data.attributes = listAttribute;

                        data.userCms = getUserCms();
                        if(isNew) {
                            //data.sku = data.generateSKU();
                            data.position = defaultPosition;
                            data.save();
                            data.slug = CommonFunction.slugGenerate(data.name + "-" + data.id);
                            data.update();
                        }else data.update();

                        detail.mainProduct = data;
                        detail.weight = Double.parseDouble(weight);
                        String[] dimensionTmp = dimension.split("x");
                        detail.dimension1 = Double.parseDouble(dimensionTmp[0]);
                        detail.dimension2 = Double.parseDouble(dimensionTmp[1]);
                        detail.dimension3 = Double.parseDouble(dimensionTmp[2]);
                        detail.warrantyType = Integer.parseInt(warranty);
                        if (detail.warrantyType != 0) {
                            detail.warrantyPeriod = Integer.parseInt(warrantyPeriod);
                        }
                        detail.whatInTheBox = whatsInTheBox;
                        detail.description = longDescription;
                        String[] shortDescriptionTmp = shortDescription.split(";");
                        detail.setShortDescriptions(Arrays.asList(shortDescriptionTmp));

                        if(isNew) {
                            detail.save();
                        }else {
                            detail.update();
                        }
                    }
                }

                if(errorMessage != null){
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        BaseResponse response = new BaseResponse();
        String message = "";
        int status = 1;
        if(errorMessage == null) {
            message = "Import success";
        }else {
            message = "Import failed. "+errorMessage;
            status = 0;
        }

        response.setBaseResponse(status, offset, 1, message, null);
        return ok(Json.toJson(response));
    }

    @Security.Authenticated(Secured.class)
    public static Result updateStock() {
        DynamicForm dForm = Form.form().bindFromRequest();

        Long id = Long.parseLong(dForm.get("id"));
        Long stock = Long.parseLong(dForm.get("stock"));

        int status = 1;
        Product product = null;
        Ebean.beginTransaction();
        try {
            product = Product.findById(id, getBrandId());
            product.itemCount = stock;
            product.update();
            Ebean.commitTransaction();
            status = 1;
        }catch (Exception e){
            status = 0;
            Ebean.rollbackTransaction();
        }

        BaseResponse response = new BaseResponse();
        String message = "";
        if(status == 1)
            message = "Stock update successfully";
        else message = "Stock update failed";

        response.setBaseResponse(status, offset, 1, message, product);
        return ok(Json.toJson(response));
    }

    @Security.Authenticated(Secured.class)
    public static Result updateLike() {
        DynamicForm dForm = Form.form().bindFromRequest();

        Long id = Long.parseLong(dForm.get("id"));
        Integer like = Integer.valueOf(dForm.get("like"));

        int status = 1;
        Product product = null;
        Ebean.beginTransaction();
        try {
            product = Product.findById(id, getBrandId());
            product.likeCount = like;
            product.update();
            Ebean.commitTransaction();
            status = 1;
        }catch (Exception e){
            status = 0;
            Ebean.rollbackTransaction();
        }

        BaseResponse response = new BaseResponse();
        String message = "";
        if(status == 1)
            message = "Like update successfully";
        else message = "Like update failed";

        response.setBaseResponse(status, offset, 1, message, product);
        return ok(Json.toJson(response));
    }

    @Security.Authenticated(Secured.class)
    public static Result checkEditStock(Long id){
        Product dataTmp = Product.findById(id, getBrandId());
        int status = 1;
        if(dataTmp != null){
            status = 0;
        }

        BaseResponse response = new BaseResponse();
        String message = "";

        response.setBaseResponse(status, offset, 1, "", null);
        return ok(Json.toJson(response));
    }

    @Security.Authenticated(Secured.class)
    public static Result checkEdit(Long id){
        Product dataTmp = Product.findById(id, getBrandId());
        int status = 1;
        if(dataTmp != null){
            status = 0;
        }

        BaseResponse response = new BaseResponse();
        String message = "";

        response.setBaseResponse(status, offset, 1, "", null);
        return ok(Json.toJson(response));
    }


    @Security.Authenticated(Secured.class)
    public static Result downloadPositionFile() {
        File file = null;
        try {

            String filename = "filePosition";
            response().setContentType("text/csv");
            response().setHeader("Content-Disposition", "attachment;filename=\"" + filename + ".csv" + "\"");
            file = File.createTempFile(filename, ".csv");
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            List<Product> items = Product.find.where()
                    .ge("position", 0).findList();
            bufferedWriter.write("SKU,Position");
            for(Product item : items){
                bufferedWriter.newLine();
                bufferedWriter.write(item.sku+","+item.position);
            }

            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e){
            Logger.debug("e = "+e.getMessage());
        }finally {
//            if(file != null){
//                file.delete();
//            }
        }

        return ok(file);
    }


    @Security.Authenticated(Secured.class)
    public static Result importProductPosition() {
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart file = body.getFile("import");

        String line = "";
        String cvsSplitBy = ",";

        try (BufferedReader br = new BufferedReader
                (new FileReader(file.getFile()))) {
            int i = 0;
            while ((line = br.readLine()) != null) {
                i++;
                if(i > 1 && !line.trim().equals("")) {
                    // use comma as separator
                    String[] record = line.split(cvsSplitBy);

                    String sku = record[0];
                    String position = record[1];

                    Product data = Product.find.where().eq("sku", sku).findUnique();
                    data.position = Integer.parseInt(position);
                    data.update();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        BaseResponse response = new BaseResponse();
        String message = "";
        int status = 1;
        if(status == 1)
            message = "Import success";
        else message = "Import failed";

        response.setBaseResponse(status, offset, 1, message, null);
        return ok(Json.toJson(response));
    }

    @Security.Authenticated(Secured.class)
    public static Result upload(String idImageTmp) {
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart file = body.getFile("uploadfile");
        try {
            Photo.uploadImageCrop(file, "tmp-prod", idImageTmp, Photo.fullImageSize, "jpg");
            Photo.uploadImageCrop(file, "tmp-prod-med", idImageTmp, Photo.mediumImageSize, "jpg");
            Photo.uploadImageCrop(file, "tmp-prod-thumb", idImageTmp, Photo.thumbImageSize, "jpg");
            Photo.uploadImageCrop(file, "tmp-prod-icon", idImageTmp, Photo.iconImageSize, "jpg");
        }catch (IOException e){
            Logger.debug("error = "+e.getMessage());
        }
        return ok("success");
    }

    @Security.Authenticated(Secured.class)
    public static Result downloadTemplate(){
        response().setContentType("application/vnd.ms-excel");
        response().setHeader("Content-Disposition", "attachment;filename=template_product.xlsx");

        File file = new File(Constant.getInstance().getMediaPath() + "templates" + File.separator + "template_product.xlsx");
        return ok(file);
    }


    @Security.Authenticated(Secured.class)
    public static Result downloadTemplateInfo() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");


        File file = null;
        try {
            file = File.createTempFile("template_info", ".xlsx");
            file.deleteOnExit();

            FileOutputStream fileOut = new FileOutputStream(file);
            XSSFWorkbook wb = new XSSFWorkbook();
            //Workbook wb = new XSSFWorkbook(); Doesn't work either
            XSSFSheet sheet = wb.createSheet("Sheet1");

            XSSFFont fontBold = wb.createFont();
            /* set the weight of the font */
            fontBold.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);

            XSSFCellStyle styleTitle = wb.createCellStyle();
            /* XLSX File borders now */
            styleTitle.setFont(fontBold);

            XSSFCellStyle styleHeader = wb.createCellStyle();
            /* XLSX File borders now */
            styleHeader.setBorderLeft(XSSFCellStyle.BORDER_THIN);
            styleHeader.setBorderRight(XSSFCellStyle.BORDER_THIN);
            styleHeader.setBorderTop(XSSFCellStyle.BORDER_THIN);
            styleHeader.setBorderBottom(XSSFCellStyle.BORDER_THIN);
            styleHeader.setFont(fontBold);

            XSSFCellStyle styleContent = wb.createCellStyle();
            /* XLSX File borders now */
            styleContent.setBorderLeft(XSSFCellStyle.BORDER_THIN);
            styleContent.setBorderRight(XSSFCellStyle.BORDER_THIN);
            styleContent.setBorderTop(XSSFCellStyle.BORDER_THIN);
            styleContent.setBorderBottom(XSSFCellStyle.BORDER_THIN);


            int rNum = 0;
            XSSFRow row = sheet.createRow(rNum);
            createCell(wb, row, (short) 0, "List Template Info", XSSFCellStyle.ALIGN_CENTER, XSSFCellStyle.VERTICAL_TOP, styleTitle);
            sheet.addMergedRegion(new CellRangeAddress(0,0,0,3));

            rNum++;
            rNum++;
            row = sheet.createRow(rNum);
            createCell(wb, row, (short) 0, "List Product Type", XSSFCellStyle.ALIGN_LEFT, XSSFCellStyle.VERTICAL_TOP, styleTitle);
            rNum++;
            row = sheet.createRow(rNum);
            createCell(wb, row, (short) 0, "Product Type", XSSFCellStyle.ALIGN_CENTER, XSSFCellStyle.VERTICAL_TOP, styleHeader);
            createCell(wb, row, (short) 1, "Name", XSSFCellStyle.ALIGN_CENTER, XSSFCellStyle.VERTICAL_TOP, styleHeader);
            rNum++;
            row = sheet.createRow(rNum);
            createCell(wb, row, (short) 0, "1", XSSFCellStyle.ALIGN_LEFT, XSSFCellStyle.VERTICAL_TOP, styleContent);
            createCell(wb, row, (short) 1, "Own Product", XSSFCellStyle.ALIGN_LEFT, XSSFCellStyle.VERTICAL_TOP, styleContent);
            rNum++;
            row = sheet.createRow(rNum);
            createCell(wb, row, (short) 0, "2", XSSFCellStyle.ALIGN_LEFT, XSSFCellStyle.VERTICAL_TOP, styleContent);
            createCell(wb, row, (short) 1, "Consignment", XSSFCellStyle.ALIGN_LEFT, XSSFCellStyle.VERTICAL_TOP, styleContent);

            rNum++;
            rNum++;
            row = sheet.createRow(rNum);
            createCell(wb, row, (short) 0, "List Category", XSSFCellStyle.ALIGN_LEFT, XSSFCellStyle.VERTICAL_TOP, styleTitle);
            rNum++;
            row = sheet.createRow(rNum);
            createCell(wb, row, (short) 0, "Category ID", XSSFCellStyle.ALIGN_CENTER, XSSFCellStyle.VERTICAL_TOP, styleHeader);
            createCell(wb, row, (short) 1, "Category Name", XSSFCellStyle.ALIGN_CENTER, XSSFCellStyle.VERTICAL_TOP, styleHeader);
            rNum++;
            int num = 1;
            List<Category> datas = Category.find.where()
                    .eq("level", Category.START_LEVEL)
                    .eq("isDeleted", false)
                    .orderBy("sequence asc").findList();
            for (Category dt : datas) {
                row = sheet.createRow(rNum);
                createCell(wb, row, (short) 0, "-", XSSFCellStyle.ALIGN_LEFT, XSSFCellStyle.VERTICAL_TOP, styleContent);
                createCell(wb, row, (short) 1, dt.name, XSSFCellStyle.ALIGN_LEFT, XSSFCellStyle.VERTICAL_TOP, styleContent);
                rNum++;
                List<Category> listChildCategory = Category.find.where()
                        .eq("parentCategory", dt)
                        .eq("isDeleted", false)
                        .orderBy("sequence asc").findList();
                if(listChildCategory.size() > 0){
                    for (Category dt2 : listChildCategory) {
                        row = sheet.createRow(rNum);
                        createCell(wb, row, (short) 0, "-", XSSFCellStyle.ALIGN_LEFT, XSSFCellStyle.VERTICAL_TOP, styleContent);
                        createCell(wb, row, (short) 1, "    "+dt2.name, XSSFCellStyle.ALIGN_LEFT, XSSFCellStyle.VERTICAL_TOP, styleContent);
                        rNum++;
                        List<Category> listGrantChildCategory = Category.find.where()
                                .eq("parentCategory", dt2)
                                .eq("isDeleted", false)
                                .orderBy("sequence asc").findList();
                        if(listGrantChildCategory.size() > 0){
                            for (Category dt3 : listGrantChildCategory) {
                                row = sheet.createRow(rNum);
                                createCell(wb, row, (short) 0, String.valueOf(dt3.id), XSSFCellStyle.ALIGN_LEFT, XSSFCellStyle.VERTICAL_TOP, styleContent);
                                createCell(wb, row, (short) 1, "        " + dt3.name, XSSFCellStyle.ALIGN_LEFT, XSSFCellStyle.VERTICAL_TOP, styleContent);
                                rNum++;
                            }
                        }
                    }
                }
            }

            rNum++;
            rNum++;
            row = sheet.createRow(rNum);
            createCell(wb, row, (short) 0, "List Brand", XSSFCellStyle.ALIGN_LEFT, XSSFCellStyle.VERTICAL_TOP, styleTitle);
            rNum++;
            row = sheet.createRow(rNum);
            createCell(wb, row, (short) 0, "Brand ID", XSSFCellStyle.ALIGN_CENTER, XSSFCellStyle.VERTICAL_TOP, styleHeader);
            createCell(wb, row, (short) 1, "Brand Name", XSSFCellStyle.ALIGN_CENTER, XSSFCellStyle.VERTICAL_TOP, styleHeader);
            rNum++;
            List<Brand> datasBrand = Brand.find.where()
                    .eq("isDeleted", false)
                    .orderBy("name asc").findList();
            for (Brand dt : datasBrand) {
                row = sheet.createRow(rNum);
                createCell(wb, row, (short) 0, String.valueOf(dt.id), XSSFCellStyle.ALIGN_LEFT, XSSFCellStyle.VERTICAL_TOP, styleContent);
                createCell(wb, row, (short) 1, dt.name, XSSFCellStyle.ALIGN_LEFT, XSSFCellStyle.VERTICAL_TOP, styleContent);
                rNum++;
            }

            rNum++;
            rNum++;
            row = sheet.createRow(rNum);
            createCell(wb, row, (short) 0, "List Attribute", XSSFCellStyle.ALIGN_LEFT, XSSFCellStyle.VERTICAL_TOP, styleTitle);
            rNum++;
            row = sheet.createRow(rNum);
            createCell(wb, row, (short) 0, "Attribute ID", XSSFCellStyle.ALIGN_CENTER, XSSFCellStyle.VERTICAL_TOP, styleHeader);
            createCell(wb, row, (short) 1, "Base Attribute Name", XSSFCellStyle.ALIGN_CENTER, XSSFCellStyle.VERTICAL_TOP, styleHeader);
            createCell(wb, row, (short) 2, "Attribute Value", XSSFCellStyle.ALIGN_CENTER, XSSFCellStyle.VERTICAL_TOP, styleHeader);
            rNum++;
            List<Attribute> datasAttribute = Attribute.find.where()
                    .eq("isDeleted", false)
                    .orderBy("baseAttribute.id asc").findList();
            for (Attribute dt : datasAttribute) {
                row = sheet.createRow(rNum);
                createCell(wb, row, (short) 0, String.valueOf(dt.id), XSSFCellStyle.ALIGN_LEFT, XSSFCellStyle.VERTICAL_TOP, styleContent);
                createCell(wb, row, (short) 1, dt.baseAttribute.name, XSSFCellStyle.ALIGN_LEFT, XSSFCellStyle.VERTICAL_TOP, styleContent);
                createCell(wb, row, (short) 2, dt.value, XSSFCellStyle.ALIGN_LEFT, XSSFCellStyle.VERTICAL_TOP, styleContent);
                num++;
                rNum++;
            }

            /*rNum++;
            rNum++;
            row = sheet.createRow(rNum);
            createCell(wb, row, (short) 0, "List Vendor", XSSFCellStyle.ALIGN_LEFT, XSSFCellStyle.VERTICAL_TOP, styleTitle);
            rNum++;
            row = sheet.createRow(rNum);
            createCell(wb, row, (short) 0, "ID", XSSFCellStyle.ALIGN_CENTER, XSSFCellStyle.VERTICAL_TOP, styleHeader);
            createCell(wb, row, (short) 1, "Name", XSSFCellStyle.ALIGN_CENTER, XSSFCellStyle.VERTICAL_TOP, styleHeader);
            createCell(wb, row, (short) 2, "City", XSSFCellStyle.ALIGN_CENTER, XSSFCellStyle.VERTICAL_TOP, styleHeader);
            createCell(wb, row, (short) 3, "Address", XSSFCellStyle.ALIGN_CENTER, XSSFCellStyle.VERTICAL_TOP, styleHeader);
            rNum++;
            List<Vendor> datasVendor = Vendor.find.where()
                    .eq("isDeleted", false)
                    .orderBy("fullName asc").findList();
            for (Vendor dt : datasVendor) {
                row = sheet.createRow(rNum);
                createCell(wb, row, (short) 0, String.valueOf(dt.id), XSSFCellStyle.ALIGN_LEFT, XSSFCellStyle.VERTICAL_TOP, styleContent);
                createCell(wb, row, (short) 1, dt.fullName, XSSFCellStyle.ALIGN_LEFT, XSSFCellStyle.VERTICAL_TOP, styleContent);
                createCell(wb, row, (short) 2, dt.cityName, XSSFCellStyle.ALIGN_LEFT, XSSFCellStyle.VERTICAL_TOP, styleContent);
                createCell(wb, row, (short) 3, dt.address, XSSFCellStyle.ALIGN_LEFT, XSSFCellStyle.VERTICAL_TOP, styleContent);
                num++;
                rNum++;
            }*/
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);
            sheet.autoSizeColumn(3);
            wb.write(fileOut);
            fileOut.close();
        }catch (FileNotFoundException e){

        }catch (IOException e){

        }
        response().setContentType("application/x-download");
        response().setHeader("Content-disposition","attachment; filename=template_info.xlsx");
        return ok(file);

    }

    private static void createCell(XSSFWorkbook wb, XSSFRow row, short column, String value, short halign, short valign, XSSFCellStyle style) {
        XSSFCell cell = row.createCell(column);
        cell.setCellValue(value);
        style.setAlignment(halign);
        style.setVerticalAlignment(valign);
        cell.setCellStyle(style);
    }

}