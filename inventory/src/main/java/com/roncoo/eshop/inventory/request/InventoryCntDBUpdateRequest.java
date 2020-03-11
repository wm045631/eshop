package com.roncoo.eshop.inventory.request;

import com.roncoo.eshop.inventory.model.Inventory;
import com.roncoo.eshop.inventory.service.ProductInventoryService;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
public class InventoryCntDBUpdateRequest implements Request {

    private Inventory inventory;

    private ProductInventoryService inventoryService;

    public InventoryCntDBUpdateRequest(Inventory inventory, ProductInventoryService inventoryService) {
        this.inventory = inventory;
        this.inventoryService = inventoryService;
    }

    /**
     * 处理request请求
     */
    @Override

    public void process() {
        log.info("InventoryCntCacheRefreshRequest, productId = {}", inventory.getProductId());
        inventoryService.removeProductInventoryCache(inventory);
//        try {
//            log.info("写请求：删除缓存，sleep 5s，再去更新数据库");
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        inventoryService.updateProductInventory(inventory);
        log.info("写请求：更新数据库成功");
    }

    @Override
    public Long getProductId() {
        return inventory.getProductId();
    }

    /**
     * 写请求永远不更新缓存
     *
     * @return
     */
    @Override
    public boolean isForceRefresh() {
        return false;
    }
}
