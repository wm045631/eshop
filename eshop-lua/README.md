#### 下载lua-resty-kafka依赖

```
git clone https://github.com/doujiang24/lua-resty-kafka.git
将kafka目录拷贝到工程的eshop/lualib/kafka目录下
```

注意以下几点

```lua
1.应用层的nginx.conf中设置resolver 8.8.8.8;否则DNS解析错误

2.需要在kafka中加入advertised.host.name = 172.20.3.173，重启三个kafka进程

3.采用异步发送到kafka暂时未调通。改成同步的方式
local broker_list = {
    { host = "172.20.3.173", port = 9092 },
    { host = "172.20.3.174", port = 9092 },
    { host = "172.20.3.175", port = 9092 }
}
-- local async_producer = producer:new(broker_list, { producer_type = "async" })
local async_producer = producer:new(broker_list)
```

```json
测试案例：
	http://cdh3:70/ehcache/getProductInfo?productId=2&shopId=1

发送到kafka的数据样例：
{
    "method":"GET",
    "http_version":1.1,
    "raw_reader":"GET /ehcache/getProductInfo?productId=2&shopId=1 HTTP/1.1 Host: 172.20.3.174:70 User-Agent: lua-resty-http/0.14 (Lua) ngx_lua/9014",
    "uri_args":{
        "productId":"2",
        "shopId":"1"
    },
    "headers":{
        "host":"172.20.3.174:70",
        "user-agent":"lua-resty-http/0.14 (Lua) ngx_lua/9014"
    }
}
```