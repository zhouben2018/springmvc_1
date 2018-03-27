package com.zben.test.springmvc.annotation;

import java.lang.annotation.*;

/**
 * @Author: zben
 * @Description:字段映射注解
 * @Date: 下午2:34 2018/3/27
 */
@Documented                                         //JavaDoc文档
@Target(ElementType.FIELD)                          //修饰在那些地方：类，成员变量，方法....
@Retention(RetentionPolicy.RUNTIME)                 //限制Annotation的生命周期
public @interface Qualifier {

    /**
     * 作用于该属性字段上的注解有一个VALUE属性  说白了就是Qualifier名称
     * @return
     */
    public String value();
}
