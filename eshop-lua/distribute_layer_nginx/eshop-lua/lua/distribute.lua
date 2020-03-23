local request_method = ngx.var.request_method

local host = {"172.20.3.173:70", "172.20.3.174:70"}

local http = require("resty.http")
local httpc = http.new()
local cjson = require("cjson")

-- 根据请求参数的productId进行路由。相同的productId路由到同一台服务
function urlLoadBalance(productId)
    local hash = ngx.crc32_long(productId)
    local index =  (hash % 2) + 1
    return "http://"..host[index]
end

-- 随机获取一台应用层nginx
function urlRandomGet()
    math.randomseed(tostring(os.time()):reverse():sub(1, 7))
    local index = math.random(1, 2)
    return "http://"..host[index]
then

if "GET" == request_method then
    args = ngx.req.get_uri_args()
    local uri_args = ngx.req.get_uri_args()

    local productId = uri_args["productId"]

    local hot_product_key = "hot_product_"..productId
    local hot_product_flag = cache_ngx:get(hot_product_key)

    local backend = ""
    -- 热点数据，自动降级随机到应用层nginx
    if hot_product_flag == "true" then
        backend = urlRandomGet()
    else
        backend = urlLoadBalance(productId)
    end

    local uri = ngx.var.uri
    local shopId = uri_args["shopId"]
    uri = uri.."?productId="..productId.."&shopId="..shopId
    -- ngx.say("backend = ,", backend, ", uri = ", uri)
    local resp, err = httpc:request_uri(backend, {
        method = "GET",
        path = uri,
        keepalive = false
    })

    if not resp then
        ngx.say("request error :", err)
        return
    end
    ngx.say(resp.body)
    httpc:close()

    -- todo: lua的POST请求暂时没调通
    elseif "POST" == request_method then
        ngx.req.read_body()
        local post_args = ngx.req.get_post_args()
        local body = ngx.req.get_body_data()

        if body ~= nil then
            local json_body = cjson.decode(body)
            local productId = json_body.productId

            local backend = urlLoadBalance(productId)
            local uri = ngx.var.uri
            backend = backend..uri
            -- ngx.say("backend = ", backend)
            local res, err_ = httpc:request_uri(backend, {
                method = "POST",
                body = body,
                keepalive = false,
            --    headers = {
            --        ["content-type"] = "application/json",
            --        ["content-type"] = "application/x-www-form-urlencode",
            --    }
            })

            if not resp then
                ngx.say("request error :", err)
                return
            end

            ngx.say(resp.body)
            httpc:close()
    end
end