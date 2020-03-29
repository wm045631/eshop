package com.roncoo.eshop.cache.annotation;

public interface PersonMapper {
    @WMInsert(value = "insert into person (name,age) values(?,?)")
    void addPerson(String name, int age);
}
