package com.roncoo.eshop.inventory.request;

import com.roncoo.eshop.inventory.model.Inventory;
import com.roncoo.eshop.inventory.service.ProductInventoryService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InventoryCntCacheRefreshRequest implements Request {

    private Long productId;

    private ProductInventoryService inventoryService;

    /**
     * 是否强制刷新缓存。
     * 每次从redis中没有查到，但是从mysql中查到了，都强制刷新缓存
     */
    private boolean forceRefresh;

    public InventoryCntCacheRefreshRequest(Long productId, ProductInventoryService inventoryService, boolean forceRefresh) {
        this.productId = productId;
        this.inventoryService = inventoryService;
        this.forceRefresh = forceRefresh;
    }

    /**
     * 查询redis缓存为空时，
     * 1、需要直接查询数据库
     * 2、将查询结果刷新到redis
     */
    @Override
    public void process() {
        log.info("InventoryCntCacheRefreshRequest, productId = {}", productId);
        Inventory inventoryFromDb = inventoryService.getProductInventoryById(productId);
        inventoryService.updateProductInventoryCache(inventoryFromDb);
    }

    @Override
    public Long getProductId() {
        return productId;
    }
    @Override
    public boolean isForceRefresh() {
        return forceRefresh;
    }
}
