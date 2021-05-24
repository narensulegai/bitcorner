package com.example.demo.model;
import java.util.*;

public class Reports {
	
	int totalCustomers;
	int totalOrdersCreated;
	int noOfOrdersFulfilled;
	List<BalanceEntity> bitcornerBalance;
    Prices lastestPrices;
    
	List<TransactBitcoinEntity> transactions;
	List<BalanceEntity> balances;
	List<BillEntity> bills;



	
	
	public List<TransactBitcoinEntity> getTransactions() {
		return transactions;
	}
	public void setTransactions(List<TransactBitcoinEntity> transactions) {
		this.transactions = transactions;
	}
	public List<BalanceEntity> getBalances() {
		return balances;
	}
	public void setBalances(List<BalanceEntity> balances) {
		this.balances = balances;
	}
	public List<BillEntity> getBills() {
		return bills;
	}
	public void setBills(List<BillEntity> bills) {
		this.bills = bills;
	}
	public Prices getLastestPrices() {
		return lastestPrices;
	}
	public void setLastestPrices(Prices lastestPrices) {
		this.lastestPrices = lastestPrices;
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
