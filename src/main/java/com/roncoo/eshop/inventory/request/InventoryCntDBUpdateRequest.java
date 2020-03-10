package com.roncoo.eshop.inventory.request;

import com.roncoo.eshop.inventory.model.Inventory;
import com.roncoo.eshop.inventory.service.ProductInventoryService;

/**
 * 数据更新的请求。由controller层封装
 * <p>
 * 商品发生交易后，修改对应的库存
 * <p>
 * 时效性高的数据
 * <p>
 * 1、删除缓存
 * 2、更新数据库
 */
public class InventoryCntDBUpdateRequest implements Request {

    private Inventory inventory;

    private ProductInventoryService inventoryService;

    public InventoryCntDBUpdateRequest(Inventory inventory, ProductInventoryService inventoryService) {
        this.inventory = inventory;
        this.inventoryService = inventoryService;
    }

    /**
     *  处理request请求
     */
    @Override
    public void process() {
        inventoryService.removeProductInventoryCache(inventory);
        inventoryService.updateProductInventory(inventory);
    }

    @Override
    public Long getProductId() {
        return inventory.getProductId();
    }
}
