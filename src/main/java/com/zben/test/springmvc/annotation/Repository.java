package com.zben.test.springmvc.annotation;

import java.lang.annotation.*;

/**
 * @Author: zben
 * @Description:持久化注解
 * @Date: 下午2:41 2018/3/27
 */
@Documented                                         //JavaDoc文档
@Target(ElementType.TYPE)     //修饰在那些地方：类，成员变量，方法....
@Retention(RetentionPolicy.RUNTIME)                 //限制Annotation的生命周期
public @interface Repository {
    public String value();
}
