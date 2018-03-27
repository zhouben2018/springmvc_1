package com.zben.test.springmvc.annotation;

import java.lang.annotation.*;

/**
 * @Author: zben
 * @Description:控制层注解
 * @Date: 下午2:34 2018/3/27
 */
@Documented                                         //JavaDoc文档
@Target(ElementType.TYPE)                           //修饰在那些地方：类，成员变量，方法....
@Retention(RetentionPolicy.RUNTIME)                 //限制Annotation的生命周期
public @interface Controller {

    /**
     * 作用于该类上的注解有一个VALUE属性  说白了就是Controller名称
     * @return
     */
    public String value();
}
