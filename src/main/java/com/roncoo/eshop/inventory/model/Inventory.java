package com.roncoo.eshop.inventory.model;

public class Inventory {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column inventory.id
     *
     * @mbg.generated
     */
    private Integer id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column inventory.product_id
     *
     * @mbg.generated
     */
    private Long productId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column inventory.inventory_cnt
     *
     * @mbg.generated
     */
    private Long inventoryCnt;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column inventory.id
     *
     * @return the value of inventory.id
     *
     * @mbg.generated
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column inventory.id
     *
     * @param id the value for inventory.id
     *
     * @mbg.generated
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column inventory.product_id
     *
     * @return the value of inventory.product_id
     *
     * @mbg.generated
     */
    public Long getProductId() {
        return productId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column inventory.product_id
     *
     * @param productId the value for inventory.product_id
     *
     * @mbg.generated
     */
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column inventory.inventory_cnt
     *
     * @return the value of inventory.inventory_cnt
     *
     * @mbg.generated
     */
    public Long getInventoryCnt() {
        return inventoryCnt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column inventory.inventory_cnt
     *
     * @param inventoryCnt the value for inventory.inventory_cnt
     *
     * @mbg.generated
     */
    public void setInventoryCnt(Long inventoryCnt) {
        this.inventoryCnt = inventoryCnt;
    }
}