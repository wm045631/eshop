-- 将热点数据的商品详情存在应用层nginx的缓存中
local cache_ngx = ngx.shared.my_cache

ngx.req.read_body()

local cjson = require("cjson")
local post_args = ngx.req.get_post_args()
local body = ngx.req.get_body_data()

if body ~= nil then
    local json_body = cjson.decode(body)
    local productId = json_body.id

    local product_cache_key = "product_info_"..productId
    cache_ngx:set(product_cache_key, body, 5 * 60)
end