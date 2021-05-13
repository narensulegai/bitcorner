package com.example.demo.model;
import java.util.*;

public class Reports {
	
	int totalCustomers;
	int totalOrdersCreated;
	int noOfOrdersFulfilled;
	List<BalanceEntity> bitcornerBalance;
	List<TransactBitcoinEntity> bidPrice;
	List<TransactBitcoinEntity> askPrice;

	
	
	public List<TransactBitcoinEntity> getBidPrice() {
		return bidPrice;
	}
	public void setBidPrice(List<TransactBitcoinEntity> bidPrice) {
		this.bidPrice = bidPrice;
	}
	public List<TransactBitcoinEntity> getAskPrice() {
		return askPrice;
	}
	public void setAskPrice(List<TransactBitcoinEntity> askPrice) {
		this.askPrice = askPrice;
	}
	public int getTotalCustomers() {
		return totalCustomers;
	}
	public void setTotalCustomers(int totalCustomers) {
		this.totalCustomers = totalCustomers;
	}
	public int getTotalOrdersCreated() {
		return totalOrdersCreated;
	}
	public void setTotalOrdersCreated(int totalOrdersCreated) {
		this.totalOrdersCreated = totalOrdersCreated;
	}
	public int getNoOfOrdersFulfilled() {
		return noOfOrdersFulfilled;
	}
	public void setNoOfOrdersFulfilled(int noOfOrdersFulfilled) {
		this.noOfOrdersFulfilled = noOfOrdersFulfilled;
	}
	public List<BalanceEntity> getBitcornerBalance() {
		return bitcornerBalance;
	}
	public void setBitcornerBalance(List<BalanceEntity> bitcornerBalance) {
		this.bitcornerBalance = bitcornerBalance;
	}

	
	
	

}
