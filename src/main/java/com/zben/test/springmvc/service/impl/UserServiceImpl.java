package com.zben.test.springmvc.service.impl;

import com.zben.test.springmvc.annotation.Qualifier;
import com.zben.test.springmvc.annotation.Service;
import com.zben.test.springmvc.dao.UserDao;
import com.zben.test.springmvc.service.UserService;

/**
 * @Author: zben
 * @Description:
 * @Date: 下午3:52 2018/3/27
 */
@Service("userService")
public class UserServiceImpl implements UserService {

    @Qualifier("userDao")
    private UserDao userDao;

    public void insert() {
        System.out.println("userService start");
        userDao.insert();
        System.out.println("userService end");
    }
}
