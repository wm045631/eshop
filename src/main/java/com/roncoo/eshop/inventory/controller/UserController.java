package com.roncoo.eshop.inventory.controller;

import com.roncoo.eshop.inventory.model.User;
import com.roncoo.eshop.inventory.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 用户Controller控制器
 *
 * @author Administrator
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/getUserInfo/{username}")
    @ResponseBody
    public User getUserInfo(@PathVariable String username) {
        User user = userService.getUserByName(username);
        return user;
    }

    @RequestMapping("/getCachedUserInfo")
    @ResponseBody
    public User getCachedUserInfo() {
        User user = userService.getCachedUserInfo();
        return user;
    }
}
