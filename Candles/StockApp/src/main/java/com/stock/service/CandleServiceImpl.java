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
import com.stock.model.CandleDTO;
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

		int endIndex = -1;
		int n = minutes / 5;
		// Find the highest high and lowest low
		double highestHigh = Double.MIN_VALUE;
		double lowestLow = Double.MAX_VALUE;

		for (int i = 0; i < n; i++) {
			Candle candle = candles.get(i);
			double high = candle.getHigh();
			double low = candle.getLow();
			if (high > highestHigh) {
				highestHigh = high;
			}
			if (low < lowestLow) {
				lowestLow = low;
			}
			endIndex = i;
		}

		if (endIndex == -1) {
			return "Opening range not found.";
		}

		// Find the ORB

		for (int i = endIndex + 1; i < candles.size(); i++) {
			Candle candle = candles.get(i);
			double close = candle.getClose();
			if (close > highestHigh || close < lowestLow) {
				return "ORB candle generated at " + candle.getLastTradeTime();
			}
		}

		return "Opening range breakout not found.";
	}

	public List<CandleDTO> getCombinedCandles(int interval) throws CandleException {
		List<Candle> candles = getAllCandles();
		if (candles.isEmpty()) {
			throw new CandleException("Something went wrong.");
		}

		// Sort the candles by trade time
		candles.sort(Comparator.comparing(Candle::getLastTradeTime));
		int n = interval / 5;
		List<CandleDTO> combinedCandles = new ArrayList<>();
		long id = 0;
		for (int i = 0; i < candles.size(); i = i + n) {

			Candle startCandle = candles.get(i);
			CandleDTO candleDTO = new CandleDTO();
			candleDTO.setId(id);
			candleDTO.setOpen(startCandle.getOpen());
			id++;
			double highestHigh = Double.MIN_VALUE;
			double lowestLow = Double.MAX_VALUE;
			long tradeQuantity = 0;
			if (i + n < candles.size()) {
				for (int j = i; j < i + n; j++) {
					Candle candle = candles.get(j);
					double high = candle.getHigh();
					double low = candle.getLow();
					if (high > highestHigh) {
						highestHigh = high;
					}
					if (low < lowestLow) {
						lowestLow = low;
					}
					long l = Long.parseLong(candle.getTradedQty());
					tradeQuantity += l;
				}
			} else {
				for (int j = i; j < candles.size(); j++) {
					Candle candle = candles.get(j);
					double high = candle.getHigh();
					double low = candle.getLow();
					if (high > highestHigh) {
						highestHigh = high;
					}
					if (low < lowestLow) {
						lowestLow = low;
					}
					long l = Long.parseLong(candle.getTradedQty());
					tradeQuantity += l;
				}
			}
			if (i + n < candles.size()) {
				Candle lastCandle = candles.get(i + n);
				candleDTO.setHigh(highestHigh);
				candleDTO.setLow(lowestLow);
				candleDTO.setTradedQty(String.valueOf(tradeQuantity));
				candleDTO.setLastTradeTime(lastCandle.getLastTradeTime());
				candleDTO.setOpenInterest(lastCandle.getOpenInterest());
				candleDTO.setQuotationLot(lastCandle.getQuotationLot());
				candleDTO.setClose(lastCandle.getClose());
			} else {
				Candle lastCandle = candles.get(candles.size() - 1);
				candleDTO.setHigh(highestHigh);
				candleDTO.setLow(lowestLow);
				candleDTO.setTradedQty(String.valueOf(tradeQuantity));
				candleDTO.setLastTradeTime(lastCandle.getLastTradeTime());
				candleDTO.setOpenInterest(lastCandle.getOpenInterest());
				candleDTO.setQuotationLot(lastCandle.getQuotationLot());
				candleDTO.setClose(lastCandle.getClose());

			}

			combinedCandles.add(candleDTO);
			
		}
		return combinedCandles;
	}

}
