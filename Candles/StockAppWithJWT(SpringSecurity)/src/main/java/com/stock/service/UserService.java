package com.stock.service;

import java.util.List;


import com.stock.exception.UserException;
import com.stock.model.User;

public interface UserService {

	public User saveUser(User user)throws UserException;
	public User getUserDetailsByEmail(String email)throws UserException;
	public List<User> getAllUserDetails()throws UserException;
}
