package app.utils;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;
import java.io.*;
import java.net.MalformedURLException;
import java.util.Properties;

@WebListener
public class ServletContextListener implements javax.servlet.ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event){
        ServletContext servletContext = event.getServletContext();
        String path = null;
        try {
            path = servletContext.getResource("/WEB-INF").getPath()+"/config/cfg.properties";
        } catch (MalformedURLException e) {
            Logger.log("ServletContextListener: Не удалось найти папку WEB-INF");
        }

        Properties props = new Properties();
        try {
            props.load(new FileInputStream(new File(path)));
        } catch (IOException e) {
            Logger.log("ServletContextListener: Не удалось открыть конфигурационный файл");
            Logger.log("Путь: " + path);
        }
        props.setProperty("galleryAbsPath",servletContext.getRealPath(props.getProperty("gallery_path")));
        Writer w = null;
        try {
            w = new PrintWriter(servletContext.getRealPath("/WEB-INF/config/cfg.properties"));
            props.store(w,"");
        } catch (FileNotFoundException e) {
            Logger.log("Initializer: Не удалось найти файл конфигурации для записи. "+servletContext.getRealPath("/config/cfg.properties"));
        } catch (IOException e) {
            Logger.log("Initializer: Не удалось записать файл конфигурации");
        }
        DB.init(props);
        GalleryProvider.init(props);
    }
}
