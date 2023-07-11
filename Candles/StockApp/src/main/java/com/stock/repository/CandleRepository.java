package com.stock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stock.model.Candle;

@Repository
public interface CandleRepository extends JpaRepository<Candle, Long> {
}

