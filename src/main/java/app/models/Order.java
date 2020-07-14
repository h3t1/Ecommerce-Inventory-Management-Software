package app.models;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.sun.istack.NotNull;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import javax.persistence.*;
import java.util.List;

import static app.util.HibernateConfig.factory;

@Entity
@Table(name="orders")
public class Order extends RecursiveTreeObject<Order> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_order")
    private int idOrder;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_client", referencedColumnName = "id")
    @NotNull
    public Client idClient;
    private String date;

    public Order(int id, Client client, String date) {
        this.idOrder=id;
        this.idClient= client;
        this.date=date;
    }
    public Order(Client idClient) {
        this.idClient = idClient;
    }
    public Order(){

    }
    public int getIdOrder() {
        return idOrder;
    }

    public void setIdOrder(int idOrders) {
        this.idOrder = idOrders;
    }

    public int getIdClient() {
        return idClient.getId();
    }

    public void setIdClient(int idClient) {
        this.idClient.setId(idClient);
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public static ObservableList<Order> getAll(String column) {
        Session session = factory.getCurrentSession();
        List<Order> list = null;
        ObservableList<Order> listObs = null;
        String c = (column.equals("*"))?"":"select "+column+" ";
        try {
            session.beginTransaction();
            list = session.createQuery(c+"From Order").getResultList();
            session.getTransaction().commit();
            listObs = FXCollections.observableArrayList(list);
        } catch (HibernateException e) {
            e.printStackTrace();
        } finally {
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
        }catch (HibernateException e){
            return false;
        }
        return true;
    }
    public static void delete(int ordertId) {
        Session session = factory.getCurrentSession();
        session.beginTransaction();
        Order o = session.get(Order.class, ordertId);
        session.delete(o);
        session.getTransaction().commit();
    }

}
