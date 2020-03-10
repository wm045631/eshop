package com.roncoo.eshop.inventory.request;

import com.roncoo.eshop.inventory.model.Inventory;
import com.roncoo.eshop.inventory.service.ProductInventoryService;

public class InventoryCntCacheRefreshRequest implements Request {

    private Inventory inventory;

    private ProductInventoryService inventoryService;

    public InventoryCntCacheRefreshRequest(Inventory inventory, ProductInventoryService inventoryService) {
        this.inventory = inventory;
        this.inventoryService = inventoryService;
    }

    /**
     * 查询redis缓存为空时，
     * 1、需要直接查询数据库
     * 2、将查询结果刷新到redis
     */
    @Override
    public void process() {
        Inventory inventoryFromDb = inventoryService.getProductInventoryById(inventory);
        inventoryService.updateProductInventory(inventoryFromDb);
    }

    @Override
    public Long getProductId() {
        return inventory.getProductId();
    }
}
