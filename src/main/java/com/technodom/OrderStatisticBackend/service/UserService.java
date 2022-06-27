package com.technodom.OrderStatisticBackend.service;


import com.technodom.OrderStatisticBackend.entity.Role;
import com.technodom.OrderStatisticBackend.entity.User;

import java.util.List;

public interface UserService {

    User saveUser(User user);

    Role saveRole(Role role);

    void addRoleToUser(String userName,String roleName);

    User getUser(String userName);

    List<User> getUsers();
}
