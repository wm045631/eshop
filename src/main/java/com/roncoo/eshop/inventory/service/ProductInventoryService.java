package com.roncoo.eshop.inventory.service;

import com.roncoo.eshop.inventory.model.Inventory;

public interface ProductInventoryService {

    Inventory getProductInventoryById(Inventory inventory);

    void updateProductInventory(Inventory inventory);


    void initProductInventoryCache(Inventory inventory);

    Integer getProductInventoryCache(Inventory inventory);

    void removeProductInventoryCache(Inventory inventory);


}
