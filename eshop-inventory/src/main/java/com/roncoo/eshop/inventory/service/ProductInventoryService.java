package com.roncoo.eshop.inventory.service;

import com.roncoo.eshop.inventory.model.Inventory;

public interface ProductInventoryService {

    Inventory getProductInventoryById(Long productId);

    void updateProductInventory(Inventory inventory);

    void updateProductInventoryCache(Inventory inventory);

    Inventory getProductInventoryCache(Long productId);

    void removeProductInventoryCache(Inventory inventory);


}
