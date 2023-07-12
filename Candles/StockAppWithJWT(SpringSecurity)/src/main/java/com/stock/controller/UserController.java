package com.stock.controller;




import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.stock.model.User;
import com.stock.service.UserService;




@RestController
public class UserController {

	@Autowired
	private UserService userService;
	@Autowired
	private PasswordEncoder passwordEncoder;

	/**
	 * Retrieves the details of the currently logged-in user.
	 * 
	 * @param auth The Authentication object containing the user's details
	 * @return ResponseEntity containing the user's name and a success message
	 */
	@GetMapping("/signIn")
	public ResponseEntity<String> getLoggedInUserDetailsHandler(Authentication auth) {
		System.out.println(auth);
		User user = userService.getUserDetailsByEmail(auth.getName());

		return new ResponseEntity<>(user.getName() + " Logged In Successfully", HttpStatus.ACCEPTED);
	}

	/**
	 * Handles user registration.
	 * 
	 * @param user The User object containing the user's registration details
	 * @return ResponseEntity containing the registered user object and a success
	 *         status
	 */
	@PostMapping("/users")
	public ResponseEntity<User> saveUserHandler(@RequestBody User user) {

		user.setPassword(passwordEncoder.encode(user.getPassword()));

		user.setRole("ROLE_" + user.getRole().toUpperCase());

		User registeredUser = userService.saveUser(user);

		return new ResponseEntity<>(registeredUser, HttpStatus.ACCEPTED);
	}

	/**
	 * Retrieves the details of a user by their email.
	 * 
	 * @param email The email of the user to retrieve
	 * @return ResponseEntity containing the user object and a success status
	 */
	@GetMapping("/users/{email}")
	public ResponseEntity<User> getUserDetailsByEmailHandler(@PathVariable String email) {

		User user = userService.getUserDetailsByEmail(email);

		return new ResponseEntity<>(user, HttpStatus.ACCEPTED);
	}

	/**
	 * Retrieves the details of all users.
	 * 
	 * @return ResponseEntity containing the list of user objects and a success
	 *         status
	 */
	@GetMapping("/users")
	public ResponseEntity<List<User>> getAllUserDetailsHandler() {

		List<User> users = userService.getAllUserDetails();

		return new ResponseEntity<>(users, HttpStatus.OK);
	}
}
