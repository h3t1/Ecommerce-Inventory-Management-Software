package app.models;

import app.util.HibernateConfig;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import javax.persistence.*;
import java.util.List;

import static app.util.HibernateConfig.factory;

@Entity
@Table(name="clients",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"username", "email", "tel"})})
public class Client extends RecursiveTreeObject<Client> {
    @Id
    private int id;
    private String username;
    private String email;
    private String tel;
    @Column(name = "full_name")
    private String fullName;
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = City.class)
    @JoinColumn(name = "city", referencedColumnName = "ville")
    public City city;
    private String address;

    public Client(int id, String username, String email, String tel, String fullName, City city, String address) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.tel = tel;
        this.fullName = fullName;
        this.city = city;
        this.address = address;
    }
    public Client(int id, String username){
        this.id = id;
        this.username = username;
    }
    public Client(int id){
        this.id = id;
    }
    public Client(){}
    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setCity(City city) {
        this.city=city;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getTel() {
        return tel;
    }

    public String getFullName() {
        return fullName;
    }

    public City getCity() {
        return city;
    }

    public String getAddress() {
        return address;
    }



    public static ObservableList<?> getAll(String column){
        Session session = factory.getCurrentSession();
        List<Client> list = null;
        ObservableList<Client> listObs = null;
        String query = (column.equals("*"))?"FROM Client c JOIN FETCH c.city":"SELECT "+column+" FROM Client";

        try {
            session.beginTransaction();
            list = session.createQuery(query).getResultList();
            session.getTransaction().commit();
            listObs = FXCollections.observableArrayList(list);
        }catch (HibernateException e){
            e.printStackTrace();
        }finally {
            session.close();
        }
        return listObs;
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
    public static void delete(int clientId) {
        Session session = factory.getCurrentSession();
        session.beginTransaction();
        Client c = session.get(Client.class, clientId);
        session.delete(c);
        session.getTransaction().commit();
    }
    public boolean exists(String field,String value,int id){

        return true;
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", username='" + username + '\'' +
                '}';
    }

    public static Client getClient(int id){
        Session session = factory.getCurrentSession();
        session.beginTransaction();
        Client c = session.get(Client.class,id);
        session.getTransaction().commit();
        return c;
    }
}
