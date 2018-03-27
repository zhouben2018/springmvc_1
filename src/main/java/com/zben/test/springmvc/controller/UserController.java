package com.zben.test.springmvc.controller;

import com.zben.test.springmvc.annotation.Controller;
import com.zben.test.springmvc.annotation.Qualifier;
import com.zben.test.springmvc.annotation.RequestMapping;
import com.zben.test.springmvc.service.UserService;

/**
 * @Author: zben
 * @Description:
 * @Date: 下午3:48 2018/3/27
 */
@Controller("userController")
@RequestMapping("/user")
public class UserController {

    @Qualifier("userService")
    private UserService userService;

    @RequestMapping("/add")
    public void insert() {
        userService.insert();
    }
}
