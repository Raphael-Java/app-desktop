package model.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class Sales implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private Date date;
    private Double amount;
    private String status;
    private Seller seller;

    public Sales() {}

    public Sales(Integer id, Date date, Double amount, String status, Seller seller) {
        this.id = id;
        this.date = date;
        this.amount = amount;
        this.status = status;
        this.seller = seller;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Seller getSeller() { return seller; }
    public void setSeller(Seller seller) { this.seller = seller; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sales sales = (Sales) o;
        return Objects.equals(id, sales.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}