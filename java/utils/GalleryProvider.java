package app.utils;

import app.entities.Photo;
import sun.util.resources.CalendarData;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.util.*;

public class GalleryProvider {
    private static String galleryPath;
    private static String galleryAbsPath;

    // Получить изображения с позиции старт по енд включитьельно
    public static ArrayList<Photo> getImages(int start, int end, int visible) throws SQLException {
        ArrayList<Photo> images = new ArrayList<Photo>();
        Connection con = DB.getCon();
        PreparedStatement ps = con.prepareStatement("SELECT * FROM (select a.*, rownum rnum from(SELECT t.url, t.DESCR_LONG, t.DESCR_SHORT FROM SYSTEM.PHOTO t where VISIBLE=? and type='tn' order by t.upload_date) a where rownum<=?) WHERE rnum>=?");
        ps.setInt(1,visible);
        ps.setInt(2,end);
        ps.setInt(3,start);
        ResultSet rs = ps.executeQuery();
        while (rs.next()){
            String url = rs.getString("url");
            String sDescr = rs.getString("descr_short");
            String lDescr = rs.getString("descr_long");
            Photo photo = new Photo(url,sDescr,lDescr);
            images.add(photo);
        }
        return images;
    }

    public static Photo getImage(int num, int visible) throws SQLException {
        Connection con = DB.getCon();
        PreparedStatement ps = con.prepareStatement("SELECT * from (SELECT t.url, t.DESCR_LONG, t.DESCR_SHORT, rownum rnum FROM SYSTEM.PHOTO t where VISIBLE=? and type='tn' order by t.upload_date) WHERE rnum=?");
        ps.setInt(1,visible);
        ps.setInt(2,num);
        ResultSet rs = ps.executeQuery();
        if (rs.next()){
            String url = rs.getString("url");
            String sDescr = rs.getString("descr_short");
            String lDescr = rs.getString("descr_long");
            Photo photo = new Photo(url,sDescr,lDescr);
            return photo;
        }
        throw new SQLException("image not found");
    }

    public static void init(Properties props){
        galleryPath = props.getProperty("gallery_path");
        galleryAbsPath = props.getProperty("galleryAbsPath");
    }


    public static int GetImageCount(int visible) throws SQLException {
        int k = 0;
        Connection con = DB.getCon();
        PreparedStatement ps = con.prepareStatement("select count(*) from photo where type='tn' and VISIBLE=?");
        ps.setInt(1,Math.min(1,Math.abs(visible)));
        ResultSet rs = ps.executeQuery();
        if (rs.next()){
            k = rs.getInt(1);
        }
        return k;
    }

    public static void DeleteImage(String url) throws SQLException {
        Connection con = DB.getCon();
        String path = "";
        PreparedStatement ps = con.prepareStatement("select REALPATH from photo where url=?");
        ps.setString(1,url);
        ResultSet rs = ps.executeQuery();
        while (rs.next()){
            path = rs.getString("REALPATH");
            File file = new File(path);
            file.delete();
        }

        ps = con.prepareStatement("delete from photo where url=?");
        ps.setString(1,url);
        ps.execute();
    }


    public static String AddImage(InputStream inputStream, String sDescr, String lDescr) throws IOException, SQLException {
        String name = genName();

        String type="full";
        String extension = "png";
        String url = galleryPath + "/" + name + "-" + type+"."+extension;
        Logger.log("AddImage: Добавляем фото "+name);

        String realPath = galleryAbsPath+"/"+name+"-"+type+"."+extension;
        File file = new File(realPath);
        file.createNewFile();
        Path path = file.toPath();
        Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);

        Connection con = DB.getCon();
        Logger.log("AddImage: Сохранили фото на диск");
        PreparedStatement ps= con.prepareStatement("insert into photo(REALPATH, url, name, type, extension, upload_date, visible, DESCR_SHORT, DESCR_LONG) values(?,?,?,?,?,sysdate,?,?,?)");
        ps.setString(1,realPath);
        ps.setString(2,url);
        ps.setString(3,name);
        ps.setString(4,type);
        ps.setString(5,extension);
        ps.setInt(6,1);
        ps.setString(7,sDescr);
        ps.setString(8,lDescr);
        ps.execute();

        ImageUtils.makeThumbNail(new File(realPath),galleryAbsPath,name);
        realPath = galleryAbsPath+"/"+name+"-"+"tn"+"."+extension;
        url = galleryPath + "/" + name + "-" + "tn"+"."+extension;
        ps.setString(1,realPath);
        ps.setString(2,url);
        ps.setString(4,"tn");
        ps.execute();

        Logger.log("AddImage: Создали запись в БД");
        return url;
    }

    public static void UpdateImage(String url, String sdescr,String ldescr) throws SQLException {
        Connection con = DB.getCon();
        PreparedStatement ps= con.prepareStatement("update photo set DESCR_SHORT=?, DESCR_LONG=? where URL=?");
        ps.setString(1,sdescr);
        ps.setString(2,ldescr);
        ps.setString(3,url);
        ps.execute();
    }

    public static void UpdateImage(String url, int visible) throws SQLException {
        Connection con = DB.getCon();
        PreparedStatement ps= con.prepareStatement("update photo set VISIBLE=? where URL=?");
        ps.setInt(1,visible);
        ps.setString(2,url);
        ps.execute();
    }

    private static String genName(){
        Random r = new Random();
        return Long.toString(Math.abs(r.nextLong())%1000000);
    }

}
