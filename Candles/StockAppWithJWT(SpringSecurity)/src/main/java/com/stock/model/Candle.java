package com.stock.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Candle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @JsonProperty("LastTradeTime")
    private String lastTradeTime;

    @JsonProperty("QuotationLot")
    private String quotationLot;

    @JsonProperty("TradedQty")
    private String tradedQty;

    @JsonProperty("OpenInterest")
    private String openInterest;
    
    @JsonProperty("Open")
    private double open;
    
    @JsonProperty("High")
    private double high;
    
    @JsonProperty("Low")
    private double low;
    @JsonProperty("Close")
    private double close;
}
