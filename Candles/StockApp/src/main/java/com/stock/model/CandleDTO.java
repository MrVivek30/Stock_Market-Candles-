package com.stock.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CandleDTO {

	private Long id;

	private String lastTradeTime;

	private String quotationLot;

	private String tradedQty;

	private String openInterest;

	private double open;

	private double high;

	private double low;

	private double close;
}
