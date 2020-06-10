package controllers;

import com.enwie.util.Constant;
import org.apache.commons.io.IOUtils;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;

public class ApplicationController extends Controller {

    public static Result preflight(String all) {
        response().setHeader("Access-Control-Allow-Origin", "*");
        response().setHeader("Allow", "*");
        response().setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, PATCH, DELETE, OPTIONS");
        response().setHeader("Access-Control-Allow-Headers",
                "Origin, X-Requested-With, Content-Type, Accept, Referer, User-Agent");
        return ok();
    }

    public static Result image(String filename) {
        ByteArrayInputStream input = null;
        byte[] byteArray;

        try {
            File file = new File(Constant.getInstance().getImagePath().concat(filename));
            byteArray = IOUtils.toByteArray(new FileInputStream(file));
            input = new ByteArrayInputStream(byteArray);
            String[] fileType = filename.split("\\.");
            return ok(input).as("image/" + fileType[fileType.length - 1]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return notFound();
    }

    public static Result imageByFolder(String folder, String filename) {
        ByteArrayInputStream input = null;
        byte[] byteArray;

        try {
            File file = new File(Constant.getInstance().getImagePath().concat(folder).concat(File.separator).concat(filename));
            byteArray = IOUtils.toByteArray(new FileInputStream(file));
            input = new ByteArrayInputStream(byteArray);
            String[] fileType = filename.split("\\.");
            return ok(input).as("image/" + fileType[fileType.length - 1]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return notFound();
    }

    public static Result ckeditorImage(String filename) {
        return getCkeditorImage(Constant.getInstance().getImagePath().concat("ckeditor").concat(File.separator).concat(filename));
    }

    public static Result ckeditorImageByFolder(String folder, String filename) {
        return getCkeditorImage(Constant.getInstance().getImagePath().concat("ckeditor").concat(File.separator).concat(folder).concat(File.separator).concat(filename));
    }

    public static Result ckeditorImageByFolder2(String folder1, String folder2, String filename) {
        return getCkeditorImage(Constant.getInstance().getImagePath().concat("ckeditor").concat(File.separator).concat(folder1).concat(File.separator).concat(folder2).concat(File.separator).concat(filename));
    }

    public static Result ckeditorImageByFolder3(String folder1, String folder2, String folder3, String filename) {
        return getCkeditorImage(Constant.getInstance().getImagePath().concat("ckeditor").concat(File.separator).concat(folder1).concat(File.separator).concat(folder2).concat(File.separator).concat(folder3).concat(File.separator).concat(filename));
    }

    public static Result ckeditorImageByFolder4(String folder1, String folder2, String folder3, String folder4, String filename) {
        return getCkeditorImage(Constant.getInstance().getImagePath().concat("ckeditor").concat(File.separator).concat(folder1).concat(File.separator).concat(folder2).concat(File.separator).concat(folder3).concat(File.separator).concat(folder4).concat(File.separator).concat(filename));
    }

    public static Result ckeditorImageByFolder5(String folder1, String folder2, String folder3, String folder4, String folder5, String filename) {
        return getCkeditorImage(Constant.getInstance().getImagePath().concat("ckeditor").concat(File.separator).concat(folder1).concat(File.separator).concat(folder2).concat(File.separator).concat(folder3).concat(File.separator).concat(folder4).concat(File.separator).concat(folder5).concat(File.separator).concat(filename));
    }

    public static Result getCkeditorImage(String filename) {
        ByteArrayInputStream input = null;
        byte[] byteArray;

        try {
            File file = new File(filename);
            byteArray = IOUtils.toByteArray(new FileInputStream(file));
            input = new ByteArrayInputStream(byteArray);
            String[] fileType = filename.split("\\.");
            return ok(input).as("image/" + fileType[fileType.length - 1]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return notFound();
    }

    public static Result index() {
        return ok("index");
    }

}