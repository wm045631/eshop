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
    public Response updateProductInventory(@RequestBody Inventory inventory) {
        log.info("START: 产生一个写请求");
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
        log.info("START: 产生一个读请求");
        Inventory inventory = null;
        inventory = productInventoryService.getProductInventoryCache(productId);

        // 从redis缓存能查到库存。直接返回
        if (inventory != null) {
            log.warn("get inventory from redis success.inventory = {}", inventory);
            return inventory;
        }
        //todo:如果缓存中没有，不一定直接请求数据库、更新缓存！！！考虑缓存穿透的问题   ---》 后面对读请求进行了过滤
        log.warn("can not get inventory from redis. try to update redis cache. productId = {}", productId);
        Request cacheRefreshRequest = new InventoryCntCacheRefreshRequest(productId, productInventoryService, false);
        try {
            // 查询数据库更新缓存
            requestAsyncProcessService.wrapper(cacheRefreshRequest);

            Long startTime = System.currentTimeMillis();
            Long waitTime = 0L;
            while (true) {
                // 请求超过一定时间，直接break
//                if (waitTime > inventoryConfig.getQueryTimeout()) {
                if (waitTime > 10000) {
                    log.warn("can not get inventory from redis. productId = {}，waitTime = {}", productId, waitTime);
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
        log.warn("读请求：从数据库读取库存。{}", inventory);
        if (inventory != null) {
            // 在controller层查数据库成功，也刷入缓存。
            // 因为后面做了读请求去重，redis数据被LRU了，flagMap标志位一直是false。导致后面的读请求从redis查不到，每次都去mysql查，但是查到后又没有更新到redis
            // productInventoryService.updateProductInventoryCache(inventory); 这个操作不是在线程队列中，还是有线程安全问题的。
            // 重新发送一个更新缓存的请求。(强制刷新)  代码一般很少走到这
            log.warn("get inventory from mysql success. inventory = {}", inventory);
            Request forceRefreshCacheRequest = new InventoryCntCacheRefreshRequest(productId, productInventoryService, true);
            requestAsyncProcessService.wrapper(forceRefreshCacheRequest);
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
