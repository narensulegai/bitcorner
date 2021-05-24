package com.example.demo.model;


import java.math.BigDecimal;

import javax.persistence.*;
import javax.validation.constraints.Min;

@Entity
@Table(name = "prices")
public class Prices {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @Column(nullable = true)
    @Min(0)
    BigDecimal latestAskPrice = BigDecimal.valueOf(10);
    
    @Column(nullable = true)
    @Min(0)
    BigDecimal latestBidPrice = BigDecimal.valueOf(10);
    
    @Column(nullable = true)
    @Min(0)
    BigDecimal latestTransactionPrice = BigDecimal.valueOf(10);

	public BigDecimal getLatestAskPrice() {
		return latestAskPrice;
	}

	public void setLatestAskPrice(BigDecimal latestAskPrice) {
		this.latestAskPrice = latestAskPrice;
	}

	public BigDecimal getLatestBidPrice() {
		return latestBidPrice;
	}

	public void setLatestBidPrice(BigDecimal latestBidPrice) {
		this.latestBidPrice = latestBidPrice;
	}

	public BigDecimal getLatestTransactionPrice() {
		return latestTransactionPrice;
	}

	public void setLatestTransactionPrice(BigDecimal latestTransactionPrice) {
		this.latestTransactionPrice = latestTransactionPrice;
	}

    
}