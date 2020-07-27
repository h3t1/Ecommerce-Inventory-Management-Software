package app.models;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.sun.istack.NotNull;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

import static app.util.HibernateConfig.factory;

@Entity
@Table(name="order_lines")
public class OrderLine extends RecursiveTreeObject<OrderLine> implements Serializable {
    @Id
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Product.class)
    @JoinColumn(name = "id_product", referencedColumnName = "id_product")
    @NotNull
    private Product idProduct;
    @Id
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Order.class)
    @JoinColumn(name = "id_order", referencedColumnName = "id_order")
    @NotNull
    private Order idOrder;
    private int quantity;
    public OrderLine(Product idProduct, Order idOrder, int quantity) {
        this.idProduct = idProduct;
        this.idOrder = idOrder;
        this.quantity = quantity;
    }

    public OrderLine(){}
    public int getIdProduct() {
        return idProduct.getIdProduct();
    }

    public void setIdProduct(Product idProduct) {
        this.idProduct = idProduct;
    }

    public int getIdOrder() {
        return idOrder.getIdOrder();
    }

    public void setIdOrder(Order idOrder) {
        this.idOrder = idOrder;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public static ObservableList<OrderLine> getAll(String column) {
        Session session = factory.getCurrentSession();
        List<OrderLine> list = null;
        ObservableList<OrderLine> listObs = null;
        String c = (column.equals("*"))?"":"select "+column+" ";
        try {
            session.beginTransaction();
            list = session.createQuery(c+"From OrderLine").getResultList();
            session.getTransaction().commit();
            listObs = FXCollections.observableArrayList(list);
        } catch (HibernateException e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        return listObs;
    }

}
