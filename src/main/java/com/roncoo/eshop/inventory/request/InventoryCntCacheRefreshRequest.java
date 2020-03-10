package com.roncoo.eshop.inventory.request;

import com.roncoo.eshop.inventory.model.Inventory;
import com.roncoo.eshop.inventory.service.ProductInventoryService;

public class InventoryCntCacheRefreshRequest implements Request {

    private Long productId;

    private ProductInventoryService inventoryService;

    public InventoryCntCacheRefreshRequest(Long productId, ProductInventoryService inventoryService) {
        this.productId = productId;
        this.inventoryService = inventoryService;
    }

    /**
     * 查询redis缓存为空时，
     * 1、需要直接查询数据库
     * 2、将查询结果刷新到redis
     */
    @Override
    public void process() {
        Inventory inventoryFromDb = inventoryService.getProductInventoryById(productId);
        inventoryService.updateProductInventoryCache(inventoryFromDb);
    }

    @Override
    public Long getProductId() {
        return productId;
    }
}
