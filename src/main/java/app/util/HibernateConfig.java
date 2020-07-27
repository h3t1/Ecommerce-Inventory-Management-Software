package app.util;

import app.models.*;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateConfig {
    public static String host;
    public static String port;
    public static String db;
    public static String useSSL;
    public static String user;
    public static String password;


     static Configuration initialConfig = new Configuration()
            .configure("hibernate.cfg.xml")
            .addAnnotatedClass(User.class)
            .addAnnotatedClass(Client.class)
            .addAnnotatedClass(City.class)
            .addAnnotatedClass(Order.class)
            .addAnnotatedClass(OrderLine.class)
            .addAnnotatedClass(Product.class);
    public static SessionFactory factory = initialConfig.buildSessionFactory();


    public static boolean SetSessionFactory(String  host, String port, String db, String useSSL, String user, String password) {
        try {
            factory = initialConfig.setProperty("hibernate.connection.url", "jdbc:mysql://"+host+":"+port+"/"+db+"?useSSL="+useSSL+"&serverTimezone=UTC")
                    .setProperty("hibernate.connection.username", user)
                    .setProperty("hibernate.connection.password", password)
                    .setProperty("hibernate.dialect","org.hibernate.dialect.MySQLDialect")
                    .setProperty("hibernate.connection.driver_class","com.mysql.cj.jdbc.Driver")
                    .buildSessionFactory();
            setHC(host, port, db, useSSL, user, password);
            return true;
        } catch (Throwable ex) {
            System.out.println("Connexion error");
            //throw new ExceptionInInitializerError(ex);
            return false;
        }
    }
    private static void setHC(String  host, String port, String db, String useSSL, String user, String password){
        HibernateConfig.host=host;
        HibernateConfig.port=port;
        HibernateConfig.db=db;
        HibernateConfig.useSSL=useSSL;
        HibernateConfig.user=user;
        HibernateConfig.password=password;
    }

}
