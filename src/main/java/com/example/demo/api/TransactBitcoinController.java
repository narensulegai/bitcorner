package com.example.demo.api;

import com.example.demo.Currency;
import com.example.demo.OrderStatus;
import com.example.demo.model.BalanceEntity;
import com.example.demo.model.CustomerEntity;
import com.example.demo.model.TransactBitcoinEntity;
import com.example.demo.repository.BalanceRepository;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.TransactBitcoinRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/transactBitcoin")
public class TransactBitcoinController {

	@Autowired
	TransactBitcoinRepository transactBitcoinRepository;
	@Autowired
	CustomerRepository customerRepository;
	@Autowired
	BalanceRepository balanceRepository;

	@ResponseBody
	@GetMapping
	public List<TransactBitcoinEntity> get() {
		CustomerEntity customerEntity = (CustomerEntity) SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal();

		return transactBitcoinRepository.findByCustomerId(customerEntity.getId());

	}

	private boolean helper(List<TransactBitcoinEntity> buyOrders, List<TransactBitcoinEntity> list, int target, int currentIndex) {

		
		if(target == 0)
			return true;
		
		if(currentIndex == buyOrders.size()) {
			if(target == 0)
				return true;
			else 
				return false;
		}


		list.add(buyOrders.get(currentIndex));

		 if(helper(buyOrders, list, target - buyOrders.get(currentIndex).getBitcoins(), currentIndex +1))
			 return true;
		 else {
			 list.remove(list.size() - 1);
			 return helper(buyOrders, list, target, currentIndex + 1);
		 }
	}

	private void tryToSettle(List<TransactBitcoinEntity> buyOrders, TransactBitcoinEntity sellOrders) {

		List<TransactBitcoinEntity> list = new ArrayList<TransactBitcoinEntity>();
		//the list which will store the required combination

		//the function which checks for the required combination, result basically tells us whether we have got required combination or not
		boolean result = helper(buyOrders, list, sellOrders.getBitcoins(), 0);
		
		if(result == true) {
			
			for(TransactBitcoinEntity current : list) {
				
				BalanceEntity currentBalance = balanceRepository.findByBankAccountAndCurrency(current.getCustomer().getBankAccount(), current.getCurrency());
				
				BalanceEntity sellOrderBalance =  balanceRepository.findByBankAccountAndCurrency(sellOrders.getCustomer().getBankAccount(), current.getCurrency());
				
				
				//if current is buy market order, then we will settle based on sell order amount
				if(current.isMarketOrder()) {
					currentBalance.setBalance(currentBalance.getBalance() - sellOrders.getAmount() * current.getBitcoins());
					sellOrderBalance.setBalance(sellOrderBalance.getBalance() + sellOrders.getAmount() * current.getBitcoins()); 
					
				} else {
					//if current is buy limit order but sell order is market order, then we will settle based on buy order amount
					// else we will settle on sell order amounnt

					if(sellOrders.isMarketOrder()) {
						currentBalance.setBalance(currentBalance.getBalance() - current.getAmount() * current.getBitcoins());
						sellOrderBalance.setBalance(sellOrderBalance.getBalance() + current.getAmount() * current.getBitcoins()); 

					} else {
						currentBalance.setBalance(currentBalance.getBalance() - sellOrders.getAmount() * current.getBitcoins());
						sellOrderBalance.setBalance(sellOrderBalance.getBalance() + sellOrders.getAmount() * current.getBitcoins()); 
					}
				}
				
				
				
				balanceRepository.save(currentBalance);
				balanceRepository.save(sellOrderBalance);

				
				
				//Bitcoin settlement
				BalanceEntity bitcoinBalanceBuyer = balanceRepository.findByBankAccountAndCurrency(current.getCustomer().getBankAccount(), Currency.BITCOIN);
				bitcoinBalanceBuyer.setBalance(bitcoinBalanceBuyer.getBalance() + current.getBitcoins());
				balanceRepository.save(bitcoinBalanceBuyer);
				
				BalanceEntity bitcoinBalanceSeller = balanceRepository.findByBankAccountAndCurrency(sellOrders.getCustomer().getBankAccount(), Currency.BITCOIN);
				bitcoinBalanceSeller.setBalance(bitcoinBalanceSeller.getBalance() - current.getBitcoins());
				balanceRepository.save(bitcoinBalanceSeller);
				
				current.setStatus(OrderStatus.FULFILLED);
				
				transactBitcoinRepository.save(current);			
			}
			
			sellOrders.setStatus(OrderStatus.FULFILLED);
			transactBitcoinRepository.save(sellOrders);
		} 
			
	}

	private void settleBitcoinTransactions() {
		
		//This finds all open sell orders
		List<TransactBitcoinEntity> transactions = transactBitcoinRepository.findByIsBuyAndStatus(false, OrderStatus.OPEN);

		
		//This is loop where we try to settle all sell orders
		for(TransactBitcoinEntity current : transactions) {

			
			//Initialised the empty list for eligible buy orders based on balances, bidding, ask price
			List<TransactBitcoinEntity> eligibleBuyOrders = new ArrayList<TransactBitcoinEntity>();
			
			
			//This finds all open buy orders which will be run through eligibility criteria

			List<TransactBitcoinEntity> allbuyOrders = transactBitcoinRepository.findByIsBuyAndStatusAndCurrency(true, OrderStatus.OPEN, current.getCurrency());
		
			

			//This filters all buy orders whether a particular buy order can be considered or not
			
			
			/*
			 * If both sell and buy order are market orders, then current buy order won't be considered
			 * 
			 * If sell order is market order, then we will check the sufficient balance based on buy order max price
			 * 
			 * If sell order is limit order and but order is market order,, then we will check the sufficient balance based on sell order max price
			 * 
			 * If both sell order and buy order are limit orders, then we will check two things
			 * 1. Amount of selling should be less than buying
			 * 2. Sufficient balance based on sell order price
			 * 
			 * 
			 */
			for(TransactBitcoinEntity currentBuyOrder : allbuyOrders) {
				
				
				//This checks whether current buy order is of same customer, then we will ignore current buy order
				if(currentBuyOrder.getCustomer().getBankAccount().getId() == current.getCustomer().getBankAccount().getId())
					continue;
				
				
				BalanceEntity balance = balanceRepository.findByBankAccountAndCurrency(currentBuyOrder.getCustomer().getBankAccount(), currentBuyOrder.getCurrency());


				if(current.isMarketOrder()) {
					if(!currentBuyOrder.isMarketOrder()) {    					
						if(balance.getBalance() >= currentBuyOrder.getAmount() * currentBuyOrder.getBitcoins()) 
							eligibleBuyOrders.add(currentBuyOrder);
					}	
					
					
					
				} else {
					if(currentBuyOrder.isMarketOrder()  && balance.getBalance() >= current.getAmount() * currentBuyOrder.getBitcoins()) {
						eligibleBuyOrders.add(currentBuyOrder);
					} else if(!currentBuyOrder.isMarketOrder() && currentBuyOrder.getAmount() >= current.getAmount() && balance.getBalance() >= current.getAmount() * currentBuyOrder.getBitcoins()) {

						eligibleBuyOrders.add(currentBuyOrder);
					}		
				}
			}
			
			
			//After we get eligible buy orders, we will try if we have correct combination of buy orders which can settle current sell order in full

			tryToSettle(eligibleBuyOrders, current);
		}
	}

	@ResponseBody
	@PostMapping
	public ResponseEntity<?> update(@RequestBody @Valid TransactBitcoinEntity bitcoinTransaction) {
		CustomerEntity customerEntity = (CustomerEntity) SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal();


		if(bitcoinTransaction.isBuy()) {
			
			if(!bitcoinTransaction.isMarketOrder()) {
				BalanceEntity balance = balanceRepository.findByBankAccountAndCurrency(customerEntity.getBankAccount(), bitcoinTransaction.getCurrency());
				if(bitcoinTransaction.getBitcoins() * bitcoinTransaction.getAmount() > balance.getBalance()) {
					//This is the case where order is limit + buy order and you don;t have enough balance 
					Map<String, String> errors = new HashMap<String, String>();
					errors.put("err", "Not enough balance");
					return ResponseEntity.status(HttpStatus.BAD_REQUEST)
							.body(errors);
				}
			}
		} else {
			BalanceEntity balance = balanceRepository.findByBankAccountAndCurrency(customerEntity.getBankAccount(), Currency.BITCOIN);
			if(bitcoinTransaction.getBitcoins() > balance.getBalance()) {
				
				// This is the case where order is sell order annd you don't have enough bitcoins
				Map<String, String> errors = new HashMap<String, String>();
				errors.put("err", "Not enough balance");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(errors);
			}
		}

		bitcoinTransaction.setCustomer(customerEntity);
		TransactBitcoinEntity updatedEntity = transactBitcoinRepository.save(bitcoinTransaction);
		
		//After creating buy or sell order, we will call settle bitcoin transactions
		
		settleBitcoinTransactions();
		return ResponseEntity.ok(updatedEntity);


	}
}
