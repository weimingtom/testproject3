package org.ozsoft.projectbase.entities;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Release extends BaseEntity {

    private static final long serialVersionUID = -1541127939474309492L;

    @ManyToOne
    private Product product;

    @Basic
    private String name;

    @Temporal(TemporalType.DATE)
    private Date date;

    @Basic
    private String description;

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return String.format("Release(product = '%s', name = '%s')", product.getCode(), name);
    }
}
