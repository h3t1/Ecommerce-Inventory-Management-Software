package app.models;

import app.Main;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.Table;
import java.util.List;

import static app.util.HibernateConfig.factory;

@Entity
@Table(name="USERS")
public class User {

    @Id
    private int id;
    private String username;
    private String password;
    public User(){}
    public User(int id, String username, String password){
        this.id = id;
        this.username = username;
        this.password = password;
    }
    public static boolean check(String u, String p){
        Session session = factory.getCurrentSession();
        User user = null;
        boolean auth=true;
        try {
            session.beginTransaction();
            user = (User) session.createQuery("from User where username=:user and password=:pass")
                    .setParameter("user",u)
                    .setParameter("pass",p)
                    .getSingleResult();
            session.getTransaction().commit();
        }catch (HibernateException | NoResultException | IllegalStateException e){
            auth=false;
        }finally {
            session.close();
            return auth;
        }

    }
}
