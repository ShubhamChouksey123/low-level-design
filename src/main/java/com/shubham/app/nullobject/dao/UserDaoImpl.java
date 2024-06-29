package com.shubham.app.nullobject.dao;

import com.shubham.app.nullobject.entity.User;
import com.shubham.app.nullobject.entity.UserInterface;
import com.shubham.app.nullobject.entity.UserNull;

public class UserDaoImpl implements UserDao {

    @Override
    public UserInterface getUser(Integer userId) {
        if (userId < 10) {
            return new User(userId, "shubham");
        }
        return new UserNull();
    }
}
