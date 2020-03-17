package com.roncoo.eshop.yaml;


import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

public class YamlTest {
    public static void main(String[] args) {
        Yaml yaml = new Yaml();
        //文件路径是相对类目录(src/main/java)的相对路径
        InputStream in = YamlTest.class.getClassLoader().getResourceAsStream("test.yaml"); //或者app.yaml
        Map<String, Object> map = yaml.loadAs(in, Map.class);

        String appid = map.getOrDefault("appid", "123").toString();
        System.out.println(appid);
        String port = ((Map<String, Object>) map.get("server")).get("port").toString();
        System.out.println(port);
    }
}
