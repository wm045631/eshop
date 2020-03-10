package com.roncoo.eshop.inventory.service.impl;

import javax.annotation.Resource;

import com.roncoo.eshop.inventory.model.UserExample;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.roncoo.eshop.inventory.dao.RedisDAO;
import com.roncoo.eshop.inventory.mapper.UserMapper;
import com.roncoo.eshop.inventory.model.User;
import com.roncoo.eshop.inventory.service.UserService;

/**
 * 用户Service实现类
 * @author Administrator
 *
 */
@Service("userService")  
public class UserServiceImpl implements UserService {

	@Resource
	private UserMapper userMapper;

	@Resource
	private RedisDAO redisDAO;
	
	@Override
	public User getUserByName(String name) {
		UserExample example = new UserExample();
        UserExample.Criteria criteria = example.createCriteria();
        criteria.andNameEqualTo(name);
        return userMapper.selectByExample(example).get(0);
	}

	@Override
	public User getCachedUserInfo() {
		redisDAO.set("cached_user_lisi", "{\"id\":1, \"name\": \"lisi\", \"age\":28}");
		
		String userJSON = redisDAO.get("cached_user_lisi");  
		JSONObject userJSONObject = JSONObject.parseObject(userJSON);
		
		User user = new User();
		user.setName(userJSONObject.getString("name"));   
		user.setAge(userJSONObject.getInteger("age"));
        user.setId(userJSONObject.getInteger("id"));
        return user;
	}
}
