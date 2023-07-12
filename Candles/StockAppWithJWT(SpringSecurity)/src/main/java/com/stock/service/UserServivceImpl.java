package com.stock.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stock.exception.UserException;
import com.stock.model.User;
import com.stock.repository.UserRepository;

@Service
public class UserServivceImpl implements UserService{

	@Autowired
	private UserRepository userRepository;
	@Override
	public User saveUser(User user) throws UserException {
		return userRepository.save(user);
	}

	@Override
	public User getUserDetailsByEmail(String email) throws UserException {
		return userRepository.findByEmail(email).orElseThrow(() -> new UserException("User Not found with Email: "+email));
	}

	@Override
	public List<User> getAllUserDetails() throws UserException {
	List<User> customers= userRepository.findAll();
		
		if(customers.isEmpty())
			throw new UserException("No User found");
		
		return customers;
	}

}
