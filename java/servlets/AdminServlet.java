package app.servlets;

import app.utils.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "AdminServlet", urlPatterns = {"/admin"})
public class AdminServlet extends HttpServlet {
    private static String PASSWORD = "1234";
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getSession().getAttribute("admin")==null){
            if (request.getParameter("pass").equals(PASSWORD)){
                request.getSession().setAttribute("admin", true);
                Logger.log("Администратор успешно авторизовался. IP: "+request.getRemoteAddr());
            }
        }
        doGet(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getSession().getAttribute("admin")==null) {
            RequestDispatcher requestDispatcher = req.getRequestDispatcher("/WEB-INF/views/login.html");
            requestDispatcher.forward(req, resp);
        }
        else{
            RequestDispatcher requestDispatcher = req.getRequestDispatcher("/WEB-INF/views/admin.html");
            requestDispatcher.forward(req, resp);
        }
    }
}
