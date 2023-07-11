package com.stock.controller;

import com.stock.model.Candle;
import com.stock.service.CandleServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/candles")
public class CandleController {
	@Autowired
    private  CandleServiceImpl candleService;

    @PostMapping
    public ResponseEntity<String> storeCandles() {
        try {
            List<Candle> candles = candleService.getJsonData();
            candleService.saveCandles(candles);
            return new ResponseEntity<>("Candles stored successfully.", HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to store candles.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<List<Candle>> getAllCandles() {
        List<Candle> candles = candleService.getAllCandles();
        if (candles.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(candles, HttpStatus.OK);
    }

    @GetMapping("/opening-range-breakout")
    public ResponseEntity<String> getOpeningRangeBreakout(@RequestParam int minutes) {
        String result = candleService.getOpeningRangeBreakout(minutes);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/combined-candles")
    public ResponseEntity<List<Candle>> getCombinedCandles(@RequestParam int interval) {
        List<Candle> combinedCandles = candleService.getCombinedCandles(interval);
        return new ResponseEntity<>(combinedCandles, HttpStatus.OK);
    }
}
