package com.stock.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.stock.exception.CandleException;
import com.stock.model.Candle;
import com.stock.repository.CandleRepository;

@Service
public class CandleServiceImpl implements CandleService {

	@Autowired
	private CandleRepository candleRepository;

	public List<Candle> getJsonData() throws IOException {
		String filePath = "data.json";
		ObjectMapper objectMapper = new ObjectMapper();

		// register with javatime module

		objectMapper.registerModule(new JavaTimeModule());
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filePath);
		List<Candle> candleList = objectMapper.readValue(inputStream, new TypeReference<List<Candle>>() {
		});

		return candleList;
	}

	public void saveCandles(List<Candle> candles) {
		candleRepository.saveAll(candles);
	}

	public List<Candle> getAllCandles() {
		return candleRepository.findAll();
	}

	public String getOpeningRangeBreakout(int minutes) throws CandleException {
		List<Candle> candles = getAllCandles();
		if (candles.isEmpty()) {
			throw new CandleException("No candles found.");
		}

		// Sort the candles by trade time
		candles.sort(Comparator.comparing(Candle::getLastTradeTime));

		// Find the index of the first candle after the opening range
		int endIndex = -1;
		for (int i = 0; i < candles.size(); i++) {
			Candle candle = candles.get(i);
			if (isWithinOpeningRange(candle, minutes)) {
				endIndex = i;
				break;
			}
		}

		if (endIndex == -1) {
			return "Opening range not found.";
		}

		// Find the highest high and lowest low within the opening range

		double highestHigh = Double.MIN_VALUE;
		double lowestLow = Double.MAX_VALUE;
		for (int i = 0; i <= endIndex; i++) {
			Candle candle = candles.get(i);
			double high = candle.getHigh();
			double low = candle.getLow();
			if (high > highestHigh) {
				highestHigh = high;
			}
			if (low < lowestLow) {
				lowestLow = low;
			}
		}

		// Find the first candle after the opening range breakout
		// ---interval k bad ka close agra high se jada rha ya close low se kam rha toh
//		return orb candle

		for (int i = endIndex + 1; i < candles.size(); i++) {
			Candle candle = candles.get(i);
			double close = candle.getClose();
			if (close > highestHigh || close < lowestLow) {
				return "ORB candle generated at " + candle.getLastTradeTime();
			}
		}

		return "Opening range breakout not found.";
	}

	// ye candle k obj aur interval ko check krega agar true hai toh impl hoga
	private boolean isWithinOpeningRange(Candle candle, int minutes) {
		// Parse the trade time from the candle

		String tradeTime = candle.getLastTradeTime().toString();

		// Extract the hour and minute from the trade time

		int hour = Integer.parseInt(tradeTime.substring(11, 13));
		int minute = Integer.parseInt(tradeTime.substring(14, 16));

		// Calculate the minutes passed since 9:15 AM

		int totalMinutes = (hour - 9) * 60 + minute;

		// Check if the total minutes is within the specified range

		return totalMinutes <= minutes;
	}

	public List<Candle> getCombinedCandles(int interval) throws CandleException {
		List<Candle> candles = getAllCandles();
		if (candles.isEmpty()) {
			throw new CandleException("SomeThing Went wrong.");
		}

		// Sort the candles by trade time
		candles.sort(Comparator.comparing(Candle::getLastTradeTime));

		List<Candle> combinedCandles = new ArrayList<>();
		int currentInterval = interval;
		Candle currentCandle = candles.get(0);
		for (int i = 1; i < candles.size(); i++) {
			Candle nextCandle = candles.get(i);

			if (currentInterval < interval) {
				currentCandle.setHigh(Math.max(currentCandle.getHigh(), nextCandle.getHigh()));
				currentCandle.setLow(Math.min(currentCandle.getLow(), nextCandle.getLow()));
				currentCandle.setTradedQty(currentCandle.getTradedQty() + nextCandle.getTradedQty());
				currentInterval += 5;
			} else {
				currentCandle.setClose(nextCandle.getClose());
				combinedCandles.add(currentCandle);

				currentCandle = new Candle();
				currentCandle.setLastTradeTime(nextCandle.getLastTradeTime());
				currentCandle.setQuotationLot(nextCandle.getQuotationLot());
				currentCandle.setTradedQty(nextCandle.getTradedQty());
				currentCandle.setOpen(nextCandle.getOpen());
				currentCandle.setHigh(nextCandle.getHigh());
				currentCandle.setLow(nextCandle.getLow());
				currentCandle.setOpenInterest(nextCandle.getOpenInterest());

				currentInterval = 5;
			}
		}

		return combinedCandles;
	}

}
