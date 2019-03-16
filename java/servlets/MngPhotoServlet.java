package app.servlets;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "MngPhotoServlet", urlPatterns = {"/mphoto"})
public class MngPhotoServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request,response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getSession().getAttribute("admin")==null){
            response.sendRedirect("/admin");
        }
        else {
            RequestDispatcher requestDispatcher = request.getRequestDispatcher("/WEB-INF/views/photoM.html");
            requestDispatcher.forward(request, response);
        }
    }
}
