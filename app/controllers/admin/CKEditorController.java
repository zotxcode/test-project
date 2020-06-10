package controllers.admin;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import com.enwie.util.Constant;
import controllers.BaseController;
import models.Photo;
import play.Logger;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Html;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import views.html.ckeditor.*;

import javax.imageio.ImageIO;

/**
 * Created by nugraha on 6/4/17.
 */
public class CKEditorController extends BaseController {
    private static final String TITLE = "Brand";
    private static String rootPath = Constant.getInstance().getImagePath() + "ckeditor"
            + File.separator;
    private static String baseImageUrl = Constant.getInstance().getCKEditorImageUrl();

    @Security.Authenticated(Secured.class)
    public static Result upload(String Type, String CKEditor, String CKEditorFuncNum, String langCode) {
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Transaction txn = Ebean.beginTransaction();
        Http.MultipartFormData.FilePart picture = body.getFile("upload");
        String fileName = picture.getFilename().toString();
        fileName = fileName.substring(0,fileName.length()-4);
        try {
            File newFile = uploadImageCrop(picture, "", fileName, null, "jpg");
        }catch (IOException e){
            Logger.debug("error : "+e.getMessage());
        }
        String url = baseImageUrl +"/"+fileName+".jpg";
        return ok(htmlResult2(CKEditorFuncNum, url, "Upload Successfull"));

    }

    @Security.Authenticated(Secured.class)
    public static Result connectorUpload(String Command, String Type, String CurrentFolder, String NewFolderName) {
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Transaction txn = Ebean.beginTransaction();
        Http.MultipartFormData.FilePart picture = body.getFile("NewFile");
        String fileName = picture.getFilename().toString();
        fileName = fileName.substring(0,fileName.length()-4);
        try {
            File newFile = uploadImageCrop(picture, CurrentFolder, fileName, null, "jpg");
        }catch (IOException e){
            Logger.debug("error : "+e.getMessage());
        }
        String url = baseImageUrl+CurrentFolder+fileName+".jpg";
        return ok(htmlResult(url, fileName+".jpg"));
    }

    private static Html htmlResult(String url, String fileName){
        return uploadresult.render(url, fileName);
    }

    private static Html htmlResult2(String CKEditorFuncNum, String url, String message){
        return uploadresult2.render(CKEditorFuncNum, url, message);
    }

    private static Html htmlBrowser(){
        return browser.render();
    }

    @Security.Authenticated(Secured.class)
    public static Result browser() {
        return ok(htmlBrowser());
    }

    private static Html htmlfrmresourcetype(){
        return frmresourcetype.render();
    }

    @Security.Authenticated(Secured.class)
    public static Result frmresourcetype() {
        return ok(htmlfrmresourcetype());
    }

    private static Html htmlfrmfolders(){
        return frmfolders.render();
    }

    @Security.Authenticated(Secured.class)
    public static Result frmfolders() {
        return ok(htmlfrmfolders());
    }

    private static Html htmlfrmactualfolder(){
        return frmactualfolder.render();
    }

    @Security.Authenticated(Secured.class)
    public static Result frmactualfolder() {
        return ok(htmlfrmactualfolder());
    }

    private static Html htmlfrmcreatefolder(){
        return frmcreatefolder.render();
    }

    @Security.Authenticated(Secured.class)
    public static Result frmcreatefolder() {
        return ok(htmlfrmcreatefolder());
    }

    private static Html htmlfrmresourceslist(){
        return frmresourceslist.render();
    }

    @Security.Authenticated(Secured.class)
    public static Result frmresourceslist() {
        return ok(htmlfrmresourceslist());
    }

    private static Html htmlfrmupload(){
        return frmupload.render();
    }

    @Security.Authenticated(Secured.class)
    public static Result frmupload() {
        return ok(htmlfrmupload());
    }

    public static Result connector(String Command, String Type, String CurrentFolder, String NewFolderName) {
        String xml = "";
        String url = baseImageUrl + CurrentFolder;
        if(Command.equals("GetFoldersAndFiles")){
            List<String> folders = getListFolders(CurrentFolder);
            List<File> files = getListFiles(CurrentFolder);
            xml = "<Connector command=\"GetFoldersAndFiles\" resourceType=\"Image\"><CurrentFolder path=\"/\" url=\""+url+"\" /><Folders>";
            for(String folder : folders){
                xml +=  "<Folder name=\""+folder+"\" />";
            }
            xml +=  "</Folders>";
            xml +=  "<Files>";
            for(File file : files){
                xml +=  "<File name=\""+file.getName()+"\" size=\""+(file.length()/1024)+"\"/>";
            }
            xml +=  "</Files>";
            xml +=  "</Connector>";
        }else if(Command.equals("GetFolders")){
            List<String> folders = getListFolders(CurrentFolder);
            xml = "<Connector command=\"GetFolders\" resourceType=\"Image\"><CurrentFolder path=\"/\" url=\""+url+"\" /><Folders>";
            for(String folder : folders){
                xml +=  "<Folder name=\""+folder+"\" />";
            }
            xml +=  "</Folders>";
            xml +=  "</Connector>";
        }else if(Command.equals("CreateFolder")){
            createFolder(CurrentFolder, NewFolderName);
            xml = "<Connector command=\"CreateFolder\" resourceType=\"Image\"><CurrentFolder path=\"/\" url=\""+url+"\" /><Error number=\"0\" />";
        }
        return ok(xml).as("application/xml");
    }

    private static List<String> getListFolders(String directory){
        List<String> results = new ArrayList<>();
        File file = new File(rootPath+directory);
        File[] files = file.listFiles();
        if(files != null){
            for(File f: files){
                if(f.isDirectory()){
                    results.add(f.getName());
                }
            }
        }


        return results;
    }

    private static List<File> getListFiles(String directory){
        List<File> results = new ArrayList<>();
        File file = new File(rootPath+directory);
        File[] files = file.listFiles();
        if(files != null) {
            for (File f : files) {
                if (f.isFile()) {
                    results.add(f);
                }
            }
        }

        return results;
    }

    private static void createFolder(String directory, String newFolder){
        List<File> results = new ArrayList<>();
        File file = new File(rootPath+directory+newFolder);
        if(!file.exists()){
            file.mkdir();
        }
    }

    private static File uploadImageCrop(Http.MultipartFormData.FilePart image,
                                        String directory, String resName, int[] resolution, String resFormat) throws IOException{
        File result = null;
        if(image!=null){
            if(image.getContentType().startsWith("image") || image.getContentType().equals("application/octet-stream")){
                String filePath = Constant.getInstance().getImagePath() + "ckeditor" + File.separator + directory;
                File dir = new File(filePath);
                if(!dir.exists()){
                    dir.mkdir();
                }

                File srcFile = (File) image.getFile();
                File dstFile = (resName==null) ?
                        File.createTempFile("", "."+resFormat , new File(filePath)):
                        new File(filePath + resName+"."+resFormat);

                BufferedImage imageR = ImageIO.read(srcFile);
                //original image size
                int width   = imageR.getWidth();
                int height  = imageR.getHeight();
                if(resolution==null){
                    resolution = Photo.getScaledResolution(width, height);
                }
                //result image frame size
                int widthF  = resolution[0];
                int heightF = resolution[1];
                int[]size = Photo.getAppliedResolutionCrop(width, height, widthF, heightF);
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
}
