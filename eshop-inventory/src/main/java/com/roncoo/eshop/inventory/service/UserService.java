package com.roncoo.eshop.inventory.service;

import com.roncoo.eshop.inventory.model.User;

/**
 * 用户Service接口
 * @author Administrator
 *
 */
public interface UserService {

	/**
	 * 查询用户信息
	 * @return 用户信息
	 */
	public User getUserByName(String name);
	
	/**
	 * 查询redis中缓存的用户信息
	 * @return
	 */
	public User getCachedUserInfo();
	
}
