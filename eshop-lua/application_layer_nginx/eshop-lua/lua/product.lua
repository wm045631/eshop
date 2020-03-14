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