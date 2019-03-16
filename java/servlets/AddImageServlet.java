package app.servlets;

import app.utils.GalleryProvider;
import app.utils.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.Properties;

@WebServlet(name = "AddImageServlet", urlPatterns = {"/addImage"})
@MultipartConfig
public class AddImageServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getSession().getAttribute("admin")==null){
            response.sendRedirect("/admin");
            return;
        }
        Logger.log("AddImageServlet: Запрос получен");
        Part filePart = request.getPart("file");
        InputStream inputStream = filePart.getInputStream();
        Logger.log("AddImageServlet: Файл извлечён");
        String sDescr = request.getParameter("sdescr");
        String lDescr=request.getParameter("ldescr");
        if (sDescr==null || sDescr.equals(""))
            sDescr = " ";
        if (lDescr==null || lDescr.equals(""))
            lDescr = " ";
        String url = null;
        try {
            url = GalleryProvider.AddImage(inputStream, sDescr, lDescr);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        PrintWriter out = response.getWriter();
        out.write(url);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        out.write("Не нужно вызывать GET метод этого сервлета.");
    }
}
