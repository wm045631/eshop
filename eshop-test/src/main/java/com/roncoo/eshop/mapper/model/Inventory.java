package com.roncoo.eshop.mapper.model;

import java.io.Serializable;
import javax.persistence.*;

public class Inventory implements Serializable {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "inventory_cnt")
    private Long inventoryCnt;

    private static final long serialVersionUID = 1L;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return product_id
     */
    public Long getProductId() {
        return productId;
    }

    /**
     * @param productId
     */
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    /**
     * @return inventory_cnt
     */
    public Long getInventoryCnt() {
        return inventoryCnt;
    }

    /**
     * @param inventoryCnt
     */
    public void setInventoryCnt(Long inventoryCnt) {
        this.inventoryCnt = inventoryCnt;
    }
}