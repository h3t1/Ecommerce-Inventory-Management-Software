package app.models;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import javax.persistence.*;

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

}
