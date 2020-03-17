-- 将流量上报kafka
local cjson = require("cjson")
local producer = require("resty.kafka.producer")

local broker_list = {
    { host = "172.20.3.173", port = 9092 },
    { host = "172.20.3.174", port = 9092 },
    { host = "172.20.3.175", port = 9092 }
}

local log_json = {}
log_json["headers"] = ngx.req.get_headers()
log_json["uri_args"] = ngx.req.get_uri_args()
log_json["body"] = ngx.req.read_body()
log_json["http_version"] = ngx.req.http_version()
log_json["method"] =ngx.req.get_method()
log_json["raw_reader"] = ngx.req.raw_header()
log_json["body_data"] = ngx.req.get_body_data()

local message = cjson.encode(log_json);

local productId = ngx.req.get_uri_args()["productId"]

-- todo:目前采用lua异步发送消息到kafka时，ngx.timer.every对象总是为nil，可能是缺少依赖。暂时采用同步方式
-- local async_producer = producer:new(broker_list, { producer_type = "async" })
local async_producer = producer:new(broker_list)
local ok, err = async_producer:send("eshop-access-log", productId, message)

if not ok then
    ngx.say(ngx.ERR, "kafka send err:", err)
    return
end

-- 处理请求
local uri_args = ngx.req.get_uri_args()
local productId = uri_args["productId"]
local shopId = uri_args["shopId"]

-- 在nginx.conf里面定义里nginx本地缓存名称 lua_shared_dict my_cache 128m;
local cache_ngx = ngx.shared.my_cache

local productCacheKey = "product_info_"..productId
local shopCacheKey = "shop_info_"..shopId

-- 尝试从nginx本地缓存读取数据
local productCache = cache_ngx:get(productCacheKey)
local shopCache = cache_ngx:get(shopCacheKey)

if productCache == "" or productCache == nil then
        local http = require("resty.http")
        local httpc = http.new()

        local resp, err = httpc:request_uri("http://172.20.3.173:8081",{
                method = "GET",
                path = "/ehcache/getProductInfo?productId="..productId,
                keepalive = false
        })

        if not resp then
            ngx.say("query productInfo error :", err)
            return;
        else
            productCache = resp.body
            -- 设置10分钟的过期时间
            cache_ngx:set(productCacheKey, productCache, 10 * 60)
        end
end

if shopCache == "" or shopCache == nil then
        local http = require("resty.http")
        local httpc = http.new()

        local resp, err = httpc:request_uri("http://172.20.3.173:8081",{
                method = "GET",
                path = "/ehcache/getShopInfo?shopId="..shopId,
                keepalive = false
        })

        if not resp then
            ngx.say("query shopInfo error :", err)
            return;
        else
            shopCache = resp.body
            cache_ngx:set(shopCacheKey, shopCache, 10 * 60)
        end
end

-- 利用cjson解析字符串。lua里面通过http请求获取的结果是序列化的字符串
local cjson = require("cjson")
local productCacheJSON = cjson.decode(productCache)
local shopCacheJSON = cjson.decode(shopCache)

-- 定义一个table
local context = {
        productId = productCacheJSON.id,
        productName = productCacheJSON.name,
        productPrice = productCacheJSON.price,
        productPictureList = productCacheJSON.pictureList,
        productSpecification = productCacheJSON.specification,
        productService = productCacheJSON.service,
        productColor = productCacheJSON.color,
        productSize = productCacheJSON.size,
        shopId = shopCacheJSON.id,
        shopName = shopCacheJSON.name,
        shopLevel = shopCacheJSON.level,
        shopGoodCommentRate = shopCacheJSON.goodCommentRate
}

local template = require("resty.template")

-- 读取$template_root下的模板文件(在lua.conf中设置的)，进行渲染并返回。
template.render("product.html", context)