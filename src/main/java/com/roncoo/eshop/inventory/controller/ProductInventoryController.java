package com.roncoo.eshop.inventory.controller;

import com.roncoo.eshop.inventory.config.InventoryConfig;
import com.roncoo.eshop.inventory.dao.RedisDAO;
import com.roncoo.eshop.inventory.model.Inventory;
import com.roncoo.eshop.inventory.request.InventoryCntCacheRefreshRequest;
import com.roncoo.eshop.inventory.request.InventoryCntDBUpdateRequest;
import com.roncoo.eshop.inventory.request.Request;
import com.roncoo.eshop.inventory.request.Response;
import com.roncoo.eshop.inventory.service.ProductInventoryService;
import com.roncoo.eshop.inventory.service.RequestAsyncProcessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/inventory")
public class ProductInventoryController {

    @Autowired
    InventoryConfig inventoryConfig;

    @Autowired
    private ProductInventoryService productInventoryService;

    // 请求的异步处理
    @Autowired
    private RequestAsyncProcessService requestAsyncProcessService;

    @Autowired
    private RedisDAO redisDAO;

    /**
     * 更新商品库存
     *
     * @param inventory
     * @return
     */
    @PostMapping("/updateProductInventory")
    public Response updateProductInventory(Inventory inventory) {
        Response response = null;
        try {
            Request dbUpdateRequest = new InventoryCntDBUpdateRequest(inventory, productInventoryService);
            requestAsyncProcessService.wrapper(dbUpdateRequest);
            response = new Response(Response.SUCCESS, Response.SUCCESS_CODE);
        } catch (Exception ex) {
            ex.printStackTrace();
            response = new Response(Response.FAILURE, 400);
        }
        return response;
    }

    /**
     * 获取商品库存
     *
     * @param productId
     * @return
     */
    @GetMapping("/getProductInventory/{productId}")
    public Inventory getProductInventory(@PathVariable Long productId) {
        Inventory inventory = null;
        inventory = productInventoryService.getProductInventoryCache(productId);

        // 从redis缓存能查到库存。直接返回
        if (inventory != null) {
            log.warn("get inventory from redis success.inventory = {}", inventory);
            return inventory;
        }
        log.warn("can not get inventory from redis. try to update redis cache. productId = {}", productId);
        try {
            Request cacheRefreshRequest = new InventoryCntCacheRefreshRequest(productId, productInventoryService);
            // 查询数据库更新缓存
            requestAsyncProcessService.wrapper(cacheRefreshRequest);

            Long startTime = System.currentTimeMillis();
            Long waitTime = 0L;
            while (true) {
                // 请求超过一定时间，直接break
                if (waitTime > inventoryConfig.getQueryTimeout()) {
                    log.warn("can not get inventory from redis. productId = {}", productId);
                    break;
                }
                inventory = productInventoryService.getProductInventoryCache(productId);
                // 从redis读取到结果，返回
                if (inventory != null) {
                    log.warn("get inventory from redis success. inventory = {}", inventory);
                    return inventory;
                } else {
                    Thread.sleep(20);
                    waitTime = System.currentTimeMillis() - startTime;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 到这：表示在规定时间内，没有查询到库存
        // 尝试从数据库读取一次
        inventory = productInventoryService.getProductInventoryById(productId);
        if (inventory != null) {
            log.warn("get inventory from redis success. inventory = {}", inventory);
            return inventory;
        }
        log.warn("can not get inventory from mysql. productId = {}", productId);
        // 从缓存和数据库都读不到，返回库存为： -1
        inventory = new Inventory();
        inventory.setProductId(productId);
        inventory.setInventoryCnt(-1L);
        return inventory;
    }
}
