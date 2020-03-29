package com.roncoo.eshop.cache.annotation;

import java.lang.annotation.*;

@Documented                          // 无论何时使用指定的注释，都应使用Javadoc工具记录这些元素。
@Retention(RetentionPolicy.RUNTIME) // 标记的注释由JVM保留，因此运行时环境可以使用它。
@Target(ElementType.METHOD)         // 可以应用于方法级注释。
public @interface WMInsert {
    String value();
}
