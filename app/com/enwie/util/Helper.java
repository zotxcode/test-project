package com.enwie.util;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import play.Logger;
import play.Play;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

/**
 * @author hendriksaragih
 */
public class Helper {
    private final static Logger.ALogger logger = Logger.of(Helper.class);
    private final static String SMTP = Play.application().configuration().getString("enwie.email.smtp");
    private final static String SMTP_USER = Play.application().configuration().getString("enwie.email.user");
    private final static String SMTP_PASSWORD = Play.application().configuration().getString("enwie.email.password");
    private final static String APP_NAME = Play.application().configuration().getString("enwie.name");
    private final static String APP_URL = Play.application().configuration().getString("enwie.app_url");
    public final static String BASE_URL = Play.application().configuration().getString("enwie.baseurl");
    public final static String CONTACT_EMAIL = Play.application().configuration().getString("enwie.contact_email");
    public final static String CONTACT_NAME = Play.application().configuration().getString("enwie.contact_name");
    public final static String BASE_IMAGE = Play.application().configuration().getString("enwie.base_image");

    
    public static String forgotPasswordUserTemplate(String reset_token){
        return "<html><p>Anda telah mengaktifkan lupa password.</p>" + "<a href=" + APP_URL
                + "/reset_password.html?TOKEN=" + reset_token + " target=\"_blank\">"
                + "Silahkan klik untuk merubah " + "password!</a></html>";
    }

    public static String forgotPasswordAdminTemplate(String reset_token){
        return "<html><p>Anda telah mengaktifkan lupa password.</p>" + "<a href=" + APP_URL
                + "/admin/reset_password.html?TOKEN=" + reset_token + " target=\"_blank\">"
                + "Silahkan klik untuk merubah " + "password!</a></html>";
    }

    public static String getVerificationCode(String reset_token){
        return "<html><p>Anda telah mengaktifkan request verifikasi kode.</p>" + "<a href=" + APP_URL
                + "/admin/aktivasi_registrasi.html?token=" + reset_token + " target=\"_blank\">"
                + "Silahkan klik untuk mengaktifkan user anda!</a></html>";
    }

    public static String getRandomString(){
        return RandomStringUtils.random(5, false, true).toUpperCase();
    }

    public static String getRandomString(int length){
        return getRandomString(length, false);
    }

    public static String getRandomString(int length, boolean letters){
        return RandomStringUtils.random(length, true, letters).toUpperCase();
    }

    private static String convertPhone(String nohp){
        if (nohp.startsWith("0")){
            return "62"+nohp.substring(1, nohp.length());
        }else{
            return nohp;
        }
    }

    public static String encodeImageToString(File file) {
        String imageString = null;
        try {
            byte[] imageBytes = IOUtils.toByteArray(file.toURI());
            imageString = Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageString;
    }

    public static String getDateFromTimeStamp(Date time) {
        SimpleDateFormat sdt = new SimpleDateFormat("dd/MM/yyyy");
        return sdt.format(time);
    }

    public static String getTimeFromTimeStamp(Date time) {
        SimpleDateFormat sdt = new SimpleDateFormat("HH:mm");
        return sdt.format(time);
    }

    public static Date getLast30day() {
        return getDate(addDate(nowFormat(), -30)+" 00:00:00");
    }

    public static Date getDate(String date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Date getDateYmd(String date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String nowFormat() {
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        return df.format(new Date());
    }

    public static String addDate(String date, int diff) {
        String result = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(date));
            c.add(Calendar.DATE, diff);
            result = sdf.format(c.getTime());
        } catch (ParseException ignored) {

        }

        return result;
    }

    public static Date getEndCurrentDay() {
        return getDate(nowFormat()+" 23:59:59");
    }


}