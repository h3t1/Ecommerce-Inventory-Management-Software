package app.models;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import javax.persistence.*;

import java.util.List;

import static app.util.HibernateConfig.factory;

@Entity
@Table(name="products")
public class Product extends RecursiveTreeObject<Product> {
    @Id
    @Column(name = "id_product")
    private int idProduct;
    private String category;
    @Column(name = "product_name")
    private String productName;
    private String desc;
    @Column(name = "manufacturing_cost")
    private Double manufacturingCost;
    @Column(name = "buying_price")
    private Double buyingPrice;
    public Product(int idProduct, String category, String productName, String desc, Double manufacturingCost, Double buyingPrice) {
        this.idProduct = idProduct;
        this.category = category;
        this.productName = productName;
        this.desc = desc;
        this.manufacturingCost = manufacturingCost;
        this.buyingPrice = buyingPrice;
    }
    public Product(){}
    public int getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(int idProduct) {
        this.idProduct = idProduct;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Double getManufacturingCost() {
        return manufacturingCost;
    }

    public void setManufacturingCost(Double manufacturingCost) {
        this.manufacturingCost = manufacturingCost;
    }

    public Double getBuyingPrice() {
        return buyingPrice;
    }

    public void setBuyingPrice(Double buyingPrice) {
        this.buyingPrice = buyingPrice;
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
    public static ObservableList<?> getAll(String column){
        Session session = factory.getCurrentSession();
        List<Product> list = null;
        ObservableList<Product> listObs = null;
        String query = (column.equals("*"))?"FROM Product":"SELECT "+column+" FROM Product";

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

}
