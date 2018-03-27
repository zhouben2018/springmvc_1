package com.zben.test.springmvc.dao.impl;

import com.zben.test.springmvc.annotation.Repository;
import com.zben.test.springmvc.dao.UserDao;

/**
 * @Author: zben
 * @Description:
 * @Date: 下午3:55 2018/3/27
 */
@Repository("userDao")
public class UserDaoImpl implements UserDao {

    public void insert() {
        System.out.println("userDao insert");
    }
}
