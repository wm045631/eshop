package com.roncoo.eshop.cache.model;

import lombok.Data;

import java.util.Date;

@Data
public class ProductInfo {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column product_info.id
     *
     * @mbg.generated
     */
    private Long id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column product_info.name
     *
     * @mbg.generated
     */
    private String name;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column product_info.price
     *
     * @mbg.generated
     */
    private Double price;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column product_info.pictureList
     *
     * @mbg.generated
     */
    private String picturelist;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column product_info.specification
     *
     * @mbg.generated
     */
    private String specification;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column product_info.service
     *
     * @mbg.generated
     */
    private String service;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column product_info.color
     *
     * @mbg.generated
     */
    private String color;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column product_info.size
     *
     * @mbg.generated
     */
    private Double size;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column product_info.shop_id
     *
     * @mbg.generated
     */
    private Long shopId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column product_info.update_time
     *
     * @mbg.generated
     */
    private Date updateTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column product_info.create_time
     *
     * @mbg.generated
     */
    private Date createTime;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column product_info.id
     *
     * @return the value of product_info.id
     *
     * @mbg.generated
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column product_info.id
     *
     * @param id the value for product_info.id
     *
     * @mbg.generated
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column product_info.name
     *
     * @return the value of product_info.name
     *
     * @mbg.generated
     */
    public String getName() {
        return name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column product_info.name
     *
     * @param name the value for product_info.name
     *
     * @mbg.generated
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column product_info.price
     *
     * @return the value of product_info.price
     *
     * @mbg.generated
     */
    public Double getPrice() {
        return price;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column product_info.price
     *
     * @param price the value for product_info.price
     *
     * @mbg.generated
     */
    public void setPrice(Double price) {
        this.price = price;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column product_info.pictureList
     *
     * @return the value of product_info.pictureList
     *
     * @mbg.generated
     */
    public String getPicturelist() {
        return picturelist;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column product_info.pictureList
     *
     * @param picturelist the value for product_info.pictureList
     *
     * @mbg.generated
     */
    public void setPicturelist(String picturelist) {
        this.picturelist = picturelist;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column product_info.specification
     *
     * @return the value of product_info.specification
     *
     * @mbg.generated
     */
    public String getSpecification() {
        return specification;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column product_info.specification
     *
     * @param specification the value for product_info.specification
     *
     * @mbg.generated
     */
    public void setSpecification(String specification) {
        this.specification = specification;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column product_info.service
     *
     * @return the value of product_info.service
     *
     * @mbg.generated
     */
    public String getService() {
        return service;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column product_info.service
     *
     * @param service the value for product_info.service
     *
     * @mbg.generated
     */
    public void setService(String service) {
        this.service = service;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column product_info.color
     *
     * @return the value of product_info.color
     *
     * @mbg.generated
     */
    public String getColor() {
        return color;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column product_info.color
     *
     * @param color the value for product_info.color
     *
     * @mbg.generated
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column product_info.size
     *
     * @return the value of product_info.size
     *
     * @mbg.generated
     */
    public Double getSize() {
        return size;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column product_info.size
     *
     * @param size the value for product_info.size
     *
     * @mbg.generated
     */
    public void setSize(Double size) {
        this.size = size;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column product_info.shop_id
     *
     * @return the value of product_info.shop_id
     *
     * @mbg.generated
     */
    public Long getShopId() {
        return shopId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column product_info.shop_id
     *
     * @param shopId the value for product_info.shop_id
     *
     * @mbg.generated
     */
    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column product_info.update_time
     *
     * @return the value of product_info.update_time
     *
     * @mbg.generated
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column product_info.update_time
     *
     * @param updateTime the value for product_info.update_time
     *
     * @mbg.generated
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column product_info.create_time
     *
     * @return the value of product_info.create_time
     *
     * @mbg.generated
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column product_info.create_time
     *
     * @param createTime the value for product_info.create_time
     *
     * @mbg.generated
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}