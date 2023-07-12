package com.stock.controller;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stock.exception.CandleException;
import com.stock.model.Candle;
import com.stock.service.CandleService;

@RestController
@RequestMapping("/api/candles")
public class CandleController {
	private static final Logger logger = LoggerFactory.getLogger(CandleController.class);

	@Autowired
	private CandleService candleService;

	/**
	 * Endpoint to store candles in the database. Reads the JSON file and saves the
	 * candles using CandleService.
	 *
	 * @return ResponseEntity with success or error message
	 */
	@PostMapping
	public ResponseEntity<String> storeCandles() {
		try {
			List<Candle> candles = candleService.getJsonData();
			candleService.saveCandles(candles);
			return new ResponseEntity<>("Candles stored successfully.", HttpStatus.OK);
		} catch (IOException e) {
			logger.error("Failed to store candles.", e);
			return new ResponseEntity<>("Failed to store candles.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Endpoint to get all candles from the database.
	 *
	 * @return ResponseEntity with the list of candles or NO_CONTENT status if empty
	 */
	@GetMapping
	public ResponseEntity<List<Candle>> getAllCandles() {
		List<Candle> candles = candleService.getAllCandles();
		if (candles.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(candles, HttpStatus.OK);
	}

	/**
	 * Endpoint to get the time of the first opening range breakout.
	 *
	 * @param minutes the duration of the opening range
	 * @return ResponseEntity with the time of the first opening range breakout or
	 *         error message
	 */
	@GetMapping("/opening-range-breakout")
	public ResponseEntity<String> getOpeningRangeBreakout(@RequestParam int minutes) {
		try {
			String result = candleService.getOpeningRangeBreakout(minutes);
			return new ResponseEntity<>(result, HttpStatus.OK);
		} catch (CandleException e) {
			logger.error("Error occurred while getting opening range breakout.", e);
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Endpoint to create a new JSON representing candles with the given interval.
	 *
	 * @param interval the interval in minutes
	 * @return ResponseEntity with the list of combined candles or error message
	 */
	@GetMapping("/combined-candles")
	public ResponseEntity<List<Candle>> getCombinedCandles(@RequestParam int interval) {
		try {
			List<Candle> combinedCandles = candleService.getCombinedCandles(interval);
			return new ResponseEntity<>(combinedCandles, HttpStatus.OK);
		} catch (CandleException e) {
			logger.error("Error occurred while getting combined candles.", e);
			return new ResponseEntity<>(Collections.emptyList(), HttpStatus.BAD_REQUEST);
		}
	}
}
