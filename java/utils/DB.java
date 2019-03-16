package app.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Properties;

public class DB{
    private static Connection con = null;

    public static Connection getCon(){return con;}

    public static void init(Properties props){
        Logger.log("БАЗА ДАННЫХ: Инициализация начата");
        String user = props.getProperty("bd_user");
        String pass = props.getProperty("bd_pass");
        String url = props.getProperty("bd_url");
        try{
            Class.forName("oracle.jdbc.driver.OracleDriver");
        }
        catch (Exception e){
            Logger.log("БАЗА ДАННЫХ: Не удалось зарегестрировать драйвер");
        }
        try {
            Locale.setDefault(Locale.ENGLISH);
            con = DriverManager.getConnection(url,user,pass);
        } catch (SQLException e) {
            Logger.log("БАЗА ДАННЫХ: Не удалось подключиться к БД");
            Logger.log("URL: "+url +" user: "+user+" pass: "+pass);
            Logger.log(e.getMessage());
        }
        Logger.log("БАЗА ДАННЫХ: Инициализация завершена");
    }
}
