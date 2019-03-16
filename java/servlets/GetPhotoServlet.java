package app.servlets;

import app.entities.Photo;
import app.utils.GalleryProvider;
import app.utils.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;

@WebServlet(name = "GetPhotoServlet", urlPatterns = {"/getPhoto"})
public class GetPhotoServlet extends HttpServlet {
    private static int PAGESCOUNTVIS; // Число страниц
    private static int PAGESCOUNTHID;
    private static final int IMAGESONPAGE = 12; // Число фотографий на странице
    private static int IMAGESCOUNTVIS; // Число фотографий в галерее
    private static int IMAGESCOUNTHID;
    private String template = ""; // Шаблон для отображения фотографии
    private static final String TEMPLATEPATH = "/WEB-INF/views/photoBlock.html";

    private void updatePagesCount(){
        try {
            IMAGESCOUNTVIS = GalleryProvider.GetImageCount(1);
            IMAGESCOUNTHID = GalleryProvider.GetImageCount(0);
        } catch (SQLException e) {
            Logger.log("GetPhotoServlet не удалось получить кол-во изображений");
        }
        PAGESCOUNTVIS = (IMAGESCOUNTVIS%IMAGESONPAGE!=0?1:0)+IMAGESCOUNTVIS/IMAGESONPAGE;
        PAGESCOUNTHID = (IMAGESCOUNTHID%IMAGESONPAGE!=0?1:0)+IMAGESCOUNTHID/IMAGESONPAGE;
    }
    @Override
    public  void init(ServletConfig config) throws ServletException{
        Logger.log("GetPhotoServlet начал инициализацию");
        try {
            ServletContext sc = config.getServletContext();
            InputStream input = sc.getResourceAsStream(TEMPLATEPATH);
            Reader fr = new InputStreamReader(input);
            int c;
            while((c=fr.read())!=-1){
                template+=((char)c);
            }
            Logger.log("GetPhotoServlet загрузил шаблон: "+template.substring(0,5)+"...");

        } catch (FileNotFoundException e) {
            Logger.log("GetPhotoServlet не нашёл шаблон.");
        } catch (IOException e) {
            Logger.log("IOEXCEPTION at getphotoservlet");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        updatePagesCount();
        int visible = 1;
        if (request.getParameter("vis")!=null)
            visible = Integer.parseInt(request.getParameter("vis"));

        PrintWriter out = response.getWriter();

        String s;
        if (visible==0)
            s = "{\"pages\":"+PAGESCOUNTHID+",\"images\":"+IMAGESCOUNTHID+",\"onpage\":"+IMAGESONPAGE+"}";
        else
            s = "{\"pages\":"+PAGESCOUNTVIS+",\"images\":"+IMAGESCOUNTVIS+",\"onpage\":"+IMAGESONPAGE+"}";
        out.write(s);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();

        if (request.getParameter("num")!=null) {
            int num = Integer.parseInt(request.getParameter("num"));
            int visible = Integer.parseInt(request.getParameter("vis"));
            Photo photo = null;
            try {
                photo = GalleryProvider.getImage(num,visible);
            } catch (SQLException e) {
                Logger.log("GetPhotoServlet error");
                e.printStackTrace();
            }
            String t = template.replaceAll("IMAGEURI", photo.getUrl());
            t = t.replaceAll("SDESCR",photo.getsDescr());
            t = t.replaceAll("LDESCR",photo.getlDescr());
            t = t.replaceAll("\r\n","");
            t = t.replace("\"","\\\"");
            String res = "{\"count\":1,\"content\":\""+t+"\"}";
            out.write(res);
            return;
        }

        int page=Integer.parseInt(request.getParameter("page"));
        int visible = Integer.parseInt(request.getParameter("vis"));
        String s = "";
        ArrayList<Photo> imgs = new ArrayList<Photo>();
        try {
            imgs = GalleryProvider.getImages((page-1)*IMAGESONPAGE+1, page*IMAGESONPAGE, visible);
        } catch (SQLException e) {
            Logger.log("GetPhotoServlet: Запрос не удался");
        }
        for(Photo photo: imgs) {
            String t = template.replaceAll("IMAGEURI", photo.getUrl());
            t = t.replaceAll("SDESCR",photo.getsDescr());
            t = t.replaceAll("LDESCR",photo.getlDescr());
            t = t.replaceAll("\r\n","");
            t = t.replace("\"","\\\"");
            s+=t;
        }
        Logger.log("Page N"+page+" imgs: "+imgs.size());
        String res = "{\"count\":"+imgs.size()+",\"content\":\""+s+"\"}";
        out.write(res);
    }
}
