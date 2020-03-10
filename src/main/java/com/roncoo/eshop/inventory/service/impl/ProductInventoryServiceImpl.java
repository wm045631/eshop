package com.roncoo.eshop.inventory.service.impl;

import com.roncoo.eshop.inventory.dao.RedisDAO;
import com.roncoo.eshop.inventory.mapper.InventoryMapper;
import com.roncoo.eshop.inventory.model.Inventory;
import com.roncoo.eshop.inventory.model.InventoryExample;
import com.roncoo.eshop.inventory.service.ProductInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 更改商品库存的service
 * 1、先删除缓存
 * 2、再更新数据库
 */
@Service
public class ProductInventoryServiceImpl implements ProductInventoryService {

    @Autowired
    private InventoryMapper inventoryMapper;

    @Autowired
    private RedisDAO redisDAO;

    @Override
    public void updateProductInventory(Inventory inventory) {
        InventoryExample example = new InventoryExample();
        InventoryExample.Criteria criteria = example.createCriteria();
        criteria.andProductIdEqualTo(inventory.getProductId());
        inventoryMapper.updateByExample(inventory, example);
    }

    @Override
    public Inventory getProductInventoryById(Inventory inventory) {
        Inventory res = null;
        InventoryExample example = new InventoryExample();
        InventoryExample.Criteria criteria = example.createCriteria();
        criteria.andProductIdEqualTo(inventory.getProductId());
        List<Inventory> inventories = inventoryMapper.selectByExample(example);
        if (inventories != null && inventories.size() > 0) {
            res = inventories.get(0);
        }
        return res;
    }

    @Override
    public void removeProductInventoryCache(Inventory inventory) {
        String key = "product:inventory:" + inventory.getProductId();
        redisDAO.del(key);
    }

    @Override
    public void initProductInventoryCache(Inventory inventory) {
        String key = "product:inventory:" + inventory.getProductId();
        redisDAO.set(key, String.valueOf(inventory.getInventoryCnt()));
    }

    @Override
    public Integer getProductInventoryCache(Inventory inventory) {
        String key = "product:inventory:" + inventory.getProductId();
        return Integer.valueOf(redisDAO.get(key));
    }
}
