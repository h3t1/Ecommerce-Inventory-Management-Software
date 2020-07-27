package app.models;

import org.exolab.castor.types.DateTime;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import javax.persistence.*;

import java.time.LocalDateTime;

import static app.util.HibernateConfig.factory;

@Entity
@Table(name="USERS")
public class User {
    static public User connected = null;
    @Id
    private int id;
    private String username;
    private String password;
    private String email;
    @Column(name = "last_login")
    private String lastLogin;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }


    public User(){}
    public User(int id, String username, String password, String lastLogin){
        this.id = id;
        this.username = username;
        this.password = password;
        this.lastLogin = lastLogin;
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
            user.setLastLogin(String.valueOf(LocalDateTime.now()));
            session.update(user);
            session.getTransaction().commit();
        }catch (HibernateException | NoResultException | IllegalStateException e){
            auth=false;
        }finally {
            connected = user;
            session.close();
            return auth;
        }

    }
    public boolean save() {
        Session session = factory.getCurrentSession();
        try {
            session.beginTransaction();
            session.saveOrUpdate(this);
            session.getTransaction().commit();
        }catch (HibernateException | IllegalStateException e){
            return false;
        }
        return true;
    }
}
