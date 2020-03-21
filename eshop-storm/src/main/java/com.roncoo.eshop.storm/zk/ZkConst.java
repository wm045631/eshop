package com.roncoo.eshop.storm.zk;

public class ZkConst {
    // lock
    public static final String TASK_ID_LIST_LOCK = "/taskid-list-lock";
    public static final String TASK_ID_STATUS_LOCK_PREFIX = "/taskid-status-lock-";

    // data
    public static final String DATA_PATH = "/taskid-list";
    public static final String TASK_ID_STATUS_DATA_PREFIX = "/taskid-status-data-";
    public static final String HOT_PRODUCT_PREFIX = "/task-hot-product-list-";
}
