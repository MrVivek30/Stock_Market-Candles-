package com.stock.service;

import java.io.IOException;
import java.util.List;

import com.stock.exception.CandleException;
import com.stock.model.Candle;
import com.stock.model.CandleDTO;

public interface CandleService {

	public void saveCandles(List<Candle> candles);

	public String getOpeningRangeBreakout(int minutes) throws CandleException;

	public List<CandleDTO> getCombinedCandles(int interval) throws CandleException;

	public List<Candle> getAllCandles();

	public List<Candle> getJsonData() throws IOException;
}
