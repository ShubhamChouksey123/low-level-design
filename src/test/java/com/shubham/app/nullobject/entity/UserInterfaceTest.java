package com.shubham.app.nullobject.entity;

import org.junit.jupiter.api.Test;

import com.shubham.app.nullobject.dao.UserDao;
import com.shubham.app.nullobject.dao.UserDaoImpl;

import static org.junit.jupiter.api.Assertions.*;

class UserInterfaceTest {

    @Test
    void testUserInterface() {
        UserDao userDao = new UserDaoImpl();

        UserInterface user1 = userDao.getUser(2);
        UserInterface user2 = userDao.getUser(20);

        assertNotNull(user1);
        assertNotNull(user2);

        assertEquals("shubham", user1.getName());
        assertEquals(2, user1.getUserId());

        assertEquals("", user2.getName());
        assertEquals(0, user2.getUserId());
    }
}
