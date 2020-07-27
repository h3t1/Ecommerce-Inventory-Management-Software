package app.models;

import app.Main;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

import static app.util.HibernateConfig.factory;

@Entity
@Table(name="villes")
public class City extends RecursiveTreeObject<City> implements Serializable {
    @Id
    private int id;

    private String ville;
    public City(int id, String city) {
        this.ville = ville;
    }
    public City(){}


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }
    public static City getCity(String city){
        City v=null;
        Session session = factory.getCurrentSession();
        try {
            session.beginTransaction();
            v = (City) session.createQuery("from City where ville = :c")
                    .setParameter("c", city)
                    .getSingleResult();
            session.getTransaction().commit();
        }catch (HibernateException | NoResultException | IllegalStateException e){
            System.out.println("City.java : getCity()");
        }
        return v;
    }
    public static ObservableList<?> getAll(String column) {
        Session session = factory.getCurrentSession();
        List<City> list = null;
        ObservableList<City> listObs = null;
        String c = (column.equals("*"))?"":"select "+column+" ";
        try {
            session.beginTransaction();
            list = session.createQuery(c+"from City").getResultList();
            session.getTransaction().commit();
            listObs = FXCollections.observableArrayList(list);
        } catch (HibernateException e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        return listObs;
    }
    public String toString(){
        return this.ville;
    }
}
