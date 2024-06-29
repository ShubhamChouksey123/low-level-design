package com.shubham.app.nullobject.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shubham.app.nullobject.dao.UserDao;
import com.shubham.app.nullobject.dao.UserDaoImpl;
import com.shubham.app.nullobject.entity.UserInterface;

public class MainClass {

    private static final Logger logger = LoggerFactory.getLogger(MainClass.class);

    public static void main(String[] args) {
        UserDao userDao = new UserDaoImpl();

        UserInterface user = userDao.getUser(12);
        logger.info("user : {}, ", user.getName());
    }
}
