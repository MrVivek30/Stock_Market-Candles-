package com.stock.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalException  {

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorDetails> MycommonexcHandler(Exception ie,WebRequest req){
		ErrorDetails me= new ErrorDetails();
		me.setTimestamp(LocalDateTime.now());
		me.setDetails(req.getDescription(false));
		me.setMessage(ie.getMessage());
		return new ResponseEntity<>(me,HttpStatus.BAD_REQUEST);
	}
	
	
	@ExceptionHandler(CandleException.class)
	public ResponseEntity<ErrorDetails> MycommonexcHandler(CandleException ie,WebRequest req){
		ErrorDetails me= new ErrorDetails();
		me.setTimestamp(LocalDateTime.now());
		me.setDetails(req.getDescription(false));
		me.setMessage(ie.getMessage());
		return new ResponseEntity<>(me,HttpStatus.BAD_REQUEST);
	}
}