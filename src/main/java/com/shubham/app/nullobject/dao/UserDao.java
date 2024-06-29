package com.shubham.app.nullobject.dao;

import com.shubham.app.nullobject.entity.UserInterface;

public interface UserDao {

    UserInterface getUser(Integer userId);
}
