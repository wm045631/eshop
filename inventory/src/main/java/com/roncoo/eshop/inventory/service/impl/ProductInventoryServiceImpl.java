package com.roncoo.eshop.inventory.service.impl;

import com.roncoo.eshop.inventory.config.RedisKeys;
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
        List<Inventory> inventories = inventoryMapper.selectByExample(example);
        if (inventories == null) {
            inventoryMapper.insert(inventory);
        } else {
            inventoryMapper.updateByExampleSelective(inventory, example);
        }
    }

    @Override
    public void updateProductInventoryCache(Inventory inventory) {
        if (inventory == null) return;
        String key = RedisKeys.PRODUCT_INVENTORY + inventory.getProductId();
        redisDAO.set(key, String.valueOf(inventory.getInventoryCnt()));
    }

    @Override
    public Inventory getProductInventoryById(Long productId) {
        Inventory res = null;
        InventoryExample example = new InventoryExample();
        InventoryExample.Criteria criteria = example.createCriteria();
        criteria.andProductIdEqualTo(productId);
        List<Inventory> inventories = inventoryMapper.selectByExample(example);
        if (inventories != null && inventories.size() > 0) {
            res = inventories.get(0);
        }
        return res;
    }

    @Override
    public void removeProductInventoryCache(Inventory inventory) {
        String key = RedisKeys.PRODUCT_INVENTORY + inventory.getProductId();
        redisDAO.del(key);
    }


    @Override
    public Inventory getProductInventoryCache(Long productId) {
        Long count = 0L;
        String key = RedisKeys.PRODUCT_INVENTORY + productId;
        try {
            String s = redisDAO.get(key);
            if (s == null || "".equals(s.trim())) return null;
            count = Long.valueOf(s);
            Inventory inventory = new Inventory();
            inventory.setProductId(productId);
            inventory.setInventoryCnt(count);
            return inventory;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
