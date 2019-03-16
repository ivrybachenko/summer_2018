package app.servlets;

import app.utils.GalleryProvider;
import app.utils.Logger;
import sun.reflect.generics.reflectiveObjects.LazyReflectiveObjectGenerator;
import sun.rmi.runtime.Log;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

@WebServlet(name = "UpdPhotoServlet", urlPatterns = {"/updPhoto"})
public class UpdPhotoServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Logger.log("UpdPhotoServlet post received");
        if (request.getSession().getAttribute("admin")==null){
            response.sendRedirect("/admin");
            return;
        }
        String url = request.getParameter("url");
        String type = request.getParameter("type");
        if (type.equals("vis")){
            int visible = Integer.parseInt(request.getParameter("visible"));            try {
                GalleryProvider.UpdateImage(url,visible);
            } catch (SQLException e) {
                Logger.log("UpdPhotoServler: SQL-error");
                e.printStackTrace();
            }

        }
        if (type.equals("descr")) {
            String sdescr = request.getParameter("sdescr");
            String ldescr = request.getParameter("ldescr");
            if (sdescr==null)
                sdescr=" ";
            if (ldescr==null)
                ldescr=" ";
            Logger.log("url: " + url);
            Logger.log("sd: " + sdescr);
            try {
                GalleryProvider.UpdateImage(url, sdescr, ldescr);
            } catch (SQLException e) {
                Logger.log("UpdPhotoServler: SQL-error");
                e.printStackTrace();
            }
        }
        if (type.equals("del")){
            try {
                GalleryProvider.DeleteImage(url);
            } catch (SQLException e) {
                Logger.log("UpdPhotoServlet: Не полуилось удалить строку");
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
