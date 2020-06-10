package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.enwie.util.CommonFunction;
import com.enwie.util.Constant;
import play.mvc.Http;

import javax.imageio.ImageIO;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * @author hendriksaragih
 */
@Entity
@Table(name = "photo")
public class Photo extends BaseModel {
    public static final int[] articleHeaderSize                     = {800,600};
    public static final int[] articleThumbSize                      = {196,110};
    public static final int[] mainBannerSize                        = {1367,529};
    public static final int[] colorSize                        = {50,50};
    public static final int[] mainBannerResponsiveSize              = {400,204};
    public static final int[] socmedSize                        = {50,50};
    public static final int[] socmedResponsiveSize              = {50,50};
    public static final int[] instagramBannerSize                        = {291,291};
    public static final int[] instagramBannerResponsiveSize              = {291,291};
    public static final int[] bestSellerSize                        = {512,512};
    public static final int[] bestSellerResponsiveSize              = {304,304};
    public static final int[] bestSellerSizeEversac                        = {1138,450};
    public static final int[] bestSellerEversacResponsiveSize              = {569,200};
    public static final int[] mainBannerSizeEversac                        = {552,552};
    public static final int[] mainBannerResponsiveSizeEversac              = {352,240};
    public static final int[] mainBannerMobileSize                  = {400,204};
    public static final int[] promoImageSize                        = {620,160};
    public static final int[] promoResponsiveImageSize              = {182,275};
    public static final int[] fullImageSize                         = {1024,1497};
    public static final int[] mediumImageSize                       = {236,345};
    public static final int[] thumbImageSize                        = {236,345};
    public static final int[] blurImageSize                         = {10,10};
    public static final int[] iconImageSize                         = {236,345};
    public static final int   commonMaxWidth                        = 1200;
    public static final int[] categoryBannerImageSize               = {125,100};
    public static final int[] categoryBannerDetailImageSizeBig      = {570,265};
    public static final int[] categoryBannerDetailImageMedium    	= {380,265};
    public static final int[] categoryBannerDetailImageSmall    	= {190,265};
    public static final int[] categoryBannerMenuSize                = {331,119};
    public static final int[] mostpopularBannerImageSizeBig      	= {651,966};
    public static final int[] mostpopularBannerImageSizeMedium    	= {911,464};
    public static final int[] mostpopularBannerImageSizeSmall    	= {398,490};
    public static final int[] categoryImageSize                     = {180,180};
    public static final int[] categoryImageResponsiveL1Size         = {32,32};
    public static final int[] categoryImageResponsiveL3Size         = {64,64};
    public static final int[] subCategory1                          = {360,478};
    public static final int[] subCategory2                          = {347,410};
    public static final int[] brandImageSize                        = {136,61};
    public static final int[] bankImageSize                         = {55,40};
    public static final int[] courrierImageSize                     = {100,100};
    public static final int[] profileImageSize                     = {250,250};
    public static final int[] presentationImageSize                 = {1024,768};


    /**
     *
     */
    private static final long serialVersionUID = 1L;
    @JsonProperty("file_name")
    public String fileName;
    @JsonProperty("file_name_before")
    public String fileNameBefore;
    @JsonProperty("full_url")
    public String fullUrl;
    @JsonProperty("medium_url")
    public String mediumUrl;
    @JsonProperty("thumb_url")
    public String thumbUrl;
    @JsonProperty("blur_url")
    public String blurUrl;
    @JsonProperty("user_id")
    public Long userId;
    @JsonProperty("user_type")
    public String userType;
    public String module;
    @JsonProperty("module_id")
    public Long moduleId;

    public static Finder<Long, Photo> find = new Finder<Long, Photo>(Long.class, Photo.class);

    //Alex, 29-11-2016, method ini digunakan untuk menyimpan log gambar yang disimpan.
    public static String saveRecord(String code, String saveNameF, String saveNameM, String saveNameT, String saveNameB,
                                    String fileName, Long userId, String userType, String module, Long moduleId){
        Photo model = new Photo();
        model.fileName       = saveNameF;
        model.fileNameBefore = fileName;
        model.fullUrl	     = createUrl(code, saveNameF);
        model.mediumUrl	     = saveNameM.equals("") ? "" : createUrl(saveNameM);
        model.thumbUrl	     = saveNameT.equals("") ? "" : createUrl(saveNameT);
        model.blurUrl	     = saveNameB.equals("") ? "" : createUrl(saveNameB);
        model.userId         = userId;
        model.userType       = userType;
        model.module         = module;
        model.moduleId		 = moduleId;
        model.save();
        return model.fullUrl;
    }

    //Alex, 29-11-2016, method ini digunakan untuk membentuk url tempat gambar disimpan.
    public static String createUrl(String name){
        return name;
    }

    public static String createUrl(String code, String name){
        Map<String, String> listDir = listDirectory();
        return listDir.get(code) + "/" + name;
    }



    //Alex, 29-11-2016, method ini digunakan untuk menyesuaikan ukuran gambar dengan lebar minimum yang disetujui
    public static int[] getScaledResolution(int width, int height){
        if(width>commonMaxWidth){
            height = (int)(((((float)commonMaxWidth)/width)) * height);
            width  = commonMaxWidth;
        }
        return new int[] {width, height};
    }

    public static int[] getAppliedResolution(int width, int height, int widthF, int heightF){
        float widthRatio  = (((float) widthF) / width);
        float heightRatio = (((float) heightF) / height);
        int widthR  = widthF;
        int heightR = heightF;
        if (widthRatio > heightRatio) {
            widthR = (int) (heightRatio * width);
        } else if (widthRatio < heightRatio) {
            heightR = (int) (widthRatio * height);
        }
        return new int[] {widthR, heightR};
    }

    public static int[] getAppliedResolutionCrop(int width, int height, int widthF, int heightF){
        float widthRatio  = (((float) widthF) / width);
        float heightRatio = (((float) heightF) / height);
        int widthR  = widthF;
        int heightR = heightF;
        if (widthRatio > heightRatio) {
            heightR = (int) (widthRatio * height);
        } else if (widthRatio < heightRatio) {
            widthR = (int) (heightRatio * width);
        }
        return new int[] {widthR, heightR};
    }

    //Alex, 29-11-2016, method ini digunakan untuk menyimpan gambar yang di-resize dengan tambahan padding
    public static File uploadImage(Http.MultipartFormData.FilePart image,
                                   String code, String resName, int[] resolution, String resFormat) throws IOException{
        File result = null;
        Map<String, String> listDir = listDirectory();
        if(image!=null){
            if(image.getContentType().startsWith("image")){

                String filePath = Constant.getInstance().getImagePath() + listDir.get(code) + File.separator;
                File dir = new File(filePath);
                if(!dir.exists()){
                    dir.mkdir();
                }

                File srcFile = (File) image.getFile();
                File dstFile = (resName==null) ?
                        File.createTempFile(CommonFunction.getCurrentTime("ddMMYY-HHmmss")+"_", "."+resFormat , new File(filePath)):
                        new File(filePath+ CommonFunction.getCurrentTime("ddMMYY-HHmmss")+"_"+resName+"."+resFormat);
                BufferedImage imageR = ImageIO.read(srcFile);
                //original image size
                int width   = imageR.getWidth();
                int height  = imageR.getHeight();
                if(resolution==null){
                    resolution = getScaledResolution(width, height);
                }
                //result image frame size
                int widthF  = resolution[0];
                int heightF = resolution[1];
                int[]size = getAppliedResolution(width, height, widthF, heightF);
                // resize image size
                int widthR = size[0];
                int heightR = size[1];
                // start coordinate to drawing at result image frame
                int widthS = (widthF - widthR) / 2;
                int heightS = (heightF - heightR) / 2;

                Image imageR1 = imageR.getScaledInstance(widthR, heightR, Image.SCALE_SMOOTH);
                BufferedImage b1;
                Graphics2D bg;
                if(resFormat.equalsIgnoreCase("png")||resFormat.equalsIgnoreCase("gif")){
                    b1 = new BufferedImage(widthF, heightF, BufferedImage.TYPE_INT_ARGB);
                    bg = b1.createGraphics();
                    bg.setComposite(AlphaComposite.Clear);
                    bg.fillRect(0, 0, widthF, heightF);
                    bg.setComposite(AlphaComposite.Src);
                    bg.drawImage(imageR1, widthS, heightS, null);
                }
                else {
                    b1 = new BufferedImage(widthF, heightF, BufferedImage.TYPE_INT_RGB);
                    bg = b1.createGraphics();
                    bg.setColor(Color.WHITE);
                    bg.fillRect(0, 0, widthF, heightF);
                    bg.setComposite(AlphaComposite.Src);
                    bg.drawImage(imageR1, widthS, heightS, Color.WHITE, null);
                }
                bg.dispose();
                ImageIO.write(b1, resFormat, dstFile);
                result = dstFile;
            }
        }
        return result;
    }

    //Alex, 22-12-2016, method ini digunakan untuk menyimpan gambar yang di-crop jika dimensi gambar lebih besar dari dimensi yang diinginkan
    public static File uploadImageCrop(Http.MultipartFormData.FilePart image,
                                       String code, String resName, int[] resolution, String resFormat) throws IOException{
        File result = null;
        Map<String, String> listDir = listDirectory();
        if(image!=null){
            if(image.getContentType().startsWith("image") || image.getContentType().equals("application/octet-stream")){
                String filePath = Constant.getInstance().getImagePath() + listDir.get(code) + File.separator;
                File dir = new File(filePath);
                if(!dir.exists()){
                    dir.mkdir();
                }

                File srcFile = (File) image.getFile();
                File dstFile = (resName==null) ?
                        File.createTempFile(CommonFunction.getCurrentTime("ddMMYY-HHmmss")+"_", "."+resFormat , new File(filePath)):
                        new File(filePath + CommonFunction.getCurrentTime("ddMMYY-HHmmss")+"_"+resName+"."+resFormat);

                BufferedImage imageR = ImageIO.read(srcFile);
                //original image size
                int width   = imageR.getWidth();
                int height  = imageR.getHeight();
                if(resolution==null){
                    resolution = getScaledResolution(width, height);
                }
                //result image frame size
                int widthF  = resolution[0];
                int heightF = resolution[1];
                int[]size = getAppliedResolutionCrop(width, height, widthF, heightF);
                // resize image size
                int widthR = size[0];
                int heightR = size[1];
                // start coordinate to drawing at result image frame
                int widthS = (widthF - widthR) / 2;
                int heightS = (heightF - heightR) / 2;

                Image imageR1 = imageR.getScaledInstance(widthR, heightR, Image.SCALE_SMOOTH);
                BufferedImage b1;
                Graphics2D bg;
                if(resFormat.equalsIgnoreCase("png")||resFormat.equalsIgnoreCase("gif")){
                    b1 = new BufferedImage(widthF, heightF, BufferedImage.TYPE_INT_ARGB);
                    bg = b1.createGraphics();
                    bg.setComposite(AlphaComposite.Clear);
                    bg.fillRect(0, 0, widthF, heightF);
                    bg.setComposite(AlphaComposite.Src);
                    bg.drawImage(imageR1, widthS, heightS, null);
                }
                else {
                    b1 = new BufferedImage(widthF, heightF, BufferedImage.TYPE_INT_RGB);
                    bg = b1.createGraphics();
                    bg.setColor(Color.WHITE);
                    bg.fillRect(0, 0, widthF, heightF);
                    bg.setComposite(AlphaComposite.Src);
                    bg.drawImage(imageR1, widthS, heightS, Color.WHITE, null);
                }
                bg.dispose();
                ImageIO.write(b1, resFormat, dstFile);
                result = dstFile;
            }
        }
        return result;
    }

    public static File uploadImageCrop2(Http.MultipartFormData.FilePart image,
                                        String code, String resName, int startX, int startY, int width, int height, int[] resolution, String resFormat) throws IOException{
        File result = null;
        Map<String, String> listDir = listDirectory();
        if(image!=null){
            if(image.getContentType().startsWith("image") || image.getContentType().equals("application/octet-stream")){
                String filePath = Constant.getInstance().getImagePath() + listDir.get(code) + File.separator;
                File dir = new File(filePath);
                if(!dir.exists()){
                    dir.mkdir();
                }

                File srcFile = (File) image.getFile();
                File dstFile = (resName==null) ?
                        File.createTempFile(CommonFunction.getCurrentTime("ddMMYY-HHmmss")+"_", "."+resFormat , new File(filePath)):
                        new File(filePath + CommonFunction.getCurrentTime("ddMMYY-HHmmss")+"_"+resName+"."+resFormat);

                BufferedImage imageR = ImageIO.read(srcFile);
                //original image size
//                int width   = imageR.getWidth();
//                int height  = imageR.getHeight();
                BufferedImage b1 = cropImage(imageR, startX, startY, width, height, resolution, resFormat);
                ImageIO.write(b1, resFormat, dstFile);

                result = dstFile;
            }
        }
        return result;
    }

    public static BufferedImage cropImage(BufferedImage bufferedImage, int x, int y, int width, int height, int[] resolution, String resFormat){
        BufferedImage tempImage = bufferedImage.getSubimage(x, y, width, height);
        BufferedImage newImage = null;
        if(resFormat.equalsIgnoreCase("png")||resFormat.equalsIgnoreCase("gif")){
            newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            newImage.getGraphics().drawImage(tempImage.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);
        }
        else {
            newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            newImage.getGraphics().drawImage(tempImage.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);
        }
        if(width != resolution[0]){
            newImage = resizeImage(newImage, resolution);
        }
        return newImage;
    }

    //Alex, 29-11-2016, method ini digunakan untuk menyimpan list gambar
    public static List<File> uploadImages(List<Http.MultipartFormData.FilePart> images,
                                          String code, String name, int[] resolution, String imageFormat) throws IOException{
        List<File> newFiles = new ArrayList<File>();
        boolean success = true;
        int count = 1;
        for (Http.MultipartFormData.FilePart image : images) {
            File newFile = uploadImage(image, code, name+"-"+count, resolution, imageFormat);
            if(newFile!=null){
                newFiles.add(newFile);
                count++;
            }
            else{
                success = false;
                for (File file : newFiles) {
                    file.delete();
                }
                break;
            }
        }
        return success ? newFiles : null;
    }

    public static Map<String, String> listDirectory (){
        Map<String, String> result = new HashMap<>();
        result.put("ban", "banner");
        result.put("ban-res", "banner_responsive");
        result.put("soc", "socmed");
        result.put("soc-res", "socmed_responsive");
        result.put("na-ban", "new_arrival_banner");
        result.put("na-ban-res", "new_arrival_banner_responsive");
        result.put("os-ban", "on_sale_banner");
        result.put("os-ban-res", "on_sale_banner_responsive");
        result.put("bs-ban", "best_seller_banner");
        result.put("bs-ban-res", "best_seller_banner_responsive");
        result.put("ig-ban", "instagram_banner");
        result.put("ig-ban-res", "instagram_banner_responsive");
        result.put("cat-ban", "category_banner");
        result.put("cat-ban-res", "category_banner_responsive");
        result.put("ban-mob", "banner_mobile");
        result.put("prm", "promo");
        result.put("prm-res", "promo_responsive");
        result.put("cat", "category");
        result.put("cat-res", "category_responsive");
        result.put("brd", "brand");
        result.put("atc", "article");
        result.put("atc-thumb", "article_thumbnail");
        result.put("prod", "product");
        result.put("prod-med", "product_medium");
        result.put("prod-thumb", "product_thumbnail");
        result.put("prod-icon", "product_icon");
        result.put("prod-color", "product_color");
        result.put("bank", "bank");
        result.put("cou", "courier");
        result.put("ord", "order");
        result.put("ckeditor", "ckeditor");
        result.put("tmp-prod", "tmp_product");
        result.put("tmp-prod-med", "tmp_product_medium");
        result.put("tmp-prod-thumb", "tmp_product_thumbnail");
        result.put("tmp-prod-icon", "tmp_product_icon");
        result.put("ckeditor", "ckeditor");
        result.put("col", "color");
        result.put("you-thumb", "youtube_thumbnail");
        result.put("profile", "profile");
        result.put("presentation", "presentation");
        return result;
    }

    public static List<File> moveProductTemp (String code, String idImageTmp, String fileNameDest){
        List<File> resultFiles = new ArrayList<>();
        fileNameDest = CommonFunction.getCurrentTime("ddMMYY-HHmmss")+"_"+fileNameDest;

        try {
            Map<String, String> listDir = listDirectory();
            String sourcePath = Constant.getInstance().getImagePath() + listDir.get("tmp-" + code) + File.separator;
            String destPath = Constant.getInstance().getImagePath() + listDir.get(code) + File.separator;

            File dirDest = new File(destPath);
            if (!dirDest.exists()) {
                dirDest.mkdir();
            }

            File dir = new File(sourcePath);

            File[] matches = dir.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.endsWith(idImageTmp + ".jpg");
                }
            });
            int i = 0;
            if(matches != null) {
                for (i = 0; i < matches.length; i++) {
                    Files.move(Paths.get(sourcePath + matches[i].getName()), Paths.get(destPath + fileNameDest + "_" + (String.valueOf(i + 1)) + ".jpg"), REPLACE_EXISTING);
                    resultFiles.add(new File(destPath + fileNameDest + "_" + (String.valueOf(i + 1)) + ".jpg"));
                }
            }
        }catch (IOException e){

        }

        return resultFiles;
    }

    /**
     * This function resize the image file and returns the BufferedImage object that can be saved to file system.
     */
    public static BufferedImage resizeImage(BufferedImage image, int[] resolution) {
        final BufferedImage bufferedImage = new BufferedImage(resolution[0], resolution[1], BufferedImage.TYPE_INT_RGB);
        final Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.setComposite(AlphaComposite.Src);
        //below three lines are for RenderingHints for better image quality at cost of higher processing time
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.drawImage(image, 0, 0, resolution[0], resolution[1], null);
        graphics2D.dispose();
        return bufferedImage;
    }

    public static void deleteImages (String strImages, String code) {
        String[] listStrImages = Optional.ofNullable(strImages).orElse("").split(",");
        Map<String, String> listDir = listDirectory();
        for(String strImg : listStrImages) {
            String filePath = Constant.getInstance().getImagePath() + listDir.get(code) + File.separator + strImg ;
            File img = new File(filePath);
            img.delete();
        }
        
    }

}