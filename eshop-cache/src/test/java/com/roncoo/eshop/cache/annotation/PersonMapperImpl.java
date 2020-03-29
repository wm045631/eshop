package com.roncoo.eshop.cache.annotation;

import java.lang.reflect.Method;

public class PersonMapperImpl implements PersonMapper {
    @Override
    public void addPerson(String name, int age) {
        try {
            // 通过反射获取接口的方法
            Method addPerson = PersonMapper.class.getDeclaredMethod("addPerson", String.class, int.class);
            // 获取方法上的注解
            WMInsert wmInsert = addPerson.getAnnotation(WMInsert.class);
            // 打印注解上的value信息
            System.out.println("the value in WMInsert: " + wmInsert.value());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        PersonMapper personMapple = new PersonMapperImpl();
        personMapple.addPerson("wangming", 30);
    }
}
