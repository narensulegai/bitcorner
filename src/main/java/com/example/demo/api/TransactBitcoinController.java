package com.example.demo.api;

import com.example.demo.Currency;
import com.example.demo.OrderStatus;
import com.example.demo.model.BalanceEntity;
import com.example.demo.model.CustomerEntity;
import com.example.demo.model.Prices;
import com.example.demo.model.TransactBitcoinEntity;
import com.example.demo.repository.BalanceRepository;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.PriceRepository;
import com.example.demo.repository.TransactBitcoinRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/transactBitcoin")
public class TransactBitcoinController {

	@Autowired
	TransactBitcoinRepository transactBitcoinRepository;
	@Autowired
	CustomerRepository customerRepository;
	@Autowired
	BalanceRepository balanceRepository;
	@Autowired
	PriceRepository priceRepository;

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
			BalanceEntity sellOrderInitialBalance =  balanceRepository.findByBankAccountAndCurrency(sellOrders.getCustomer().getBankAccount(), sellOrders.getCurrency());

			BigDecimal tradingFees = BigDecimal.valueOf(0.0001).multiply(sellOrders.getAmount().multiply(BigDecimal.valueOf(sellOrders.getBitcoins())));
			BigDecimal amountToBeDeducted = tradingFees.compareTo(BigDecimal.valueOf(1)) >= 0 ? BigDecimal.valueOf(1) : tradingFees;

			sellOrderInitialBalance.setBalance(sellOrderInitialBalance.getBalance().subtract(amountToBeDeducted));
			balanceRepository.save(sellOrderInitialBalance);


			for(TransactBitcoinEntity current : list) {

				BalanceEntity currentBalance = balanceRepository.findByBankAccountAndCurrency(current.getCustomer().getBankAccount(), current.getCurrency());

				BalanceEntity sellOrderBalance =  balanceRepository.findByBankAccountAndCurrency(sellOrders.getCustomer().getBankAccount(), current.getCurrency());


				BigDecimal bitcoinAmount = sellOrders.getAmount().multiply(BigDecimal.valueOf(current.getBitcoins()));
				currentBalance.setBalance(currentBalance.getBalance().subtract(bitcoinAmount));
				sellOrderBalance.setBalance(sellOrderBalance.getBalance().add(bitcoinAmount)); 



				balanceRepository.save(currentBalance);
				balanceRepository.save(sellOrderBalance);



				//Bitcoin settlement
				BalanceEntity bitcoinBalanceBuyer = balanceRepository.findByBankAccountAndCurrency(current.getCustomer().getBankAccount(), Currency.BITCOIN);
				bitcoinBalanceBuyer.setBalance(bitcoinBalanceBuyer.getBalance().add((BigDecimal.valueOf(current.getBitcoins()))));
				balanceRepository.save(bitcoinBalanceBuyer);

				BalanceEntity bitcoinBalanceSeller = balanceRepository.findByBankAccountAndCurrency(sellOrders.getCustomer().getBankAccount(), Currency.BITCOIN);
				bitcoinBalanceSeller.setBalance(bitcoinBalanceSeller.getBalance().subtract((BigDecimal.valueOf(current.getBitcoins()))));
				balanceRepository.save(bitcoinBalanceSeller);

				current.setStatus(OrderStatus.FULFILLED);
				try {
					Email.sendmail(current.getCustomer().getEmail().toString(), "Hey, we hope you are staying safe, "
							+ "Your order with id " + current.getId() + " has been fulfilled. Check portal for updates");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				transactBitcoinRepository.save(current);			
			}

			sellOrders.setStatus(OrderStatus.FULFILLED);
			try {
				Email.sendmail(sellOrders.getCustomer().getEmail().toString(), "Hey, we hope you are staying safe, "
						+ "Your order with id " + sellOrders.getId() + " has been fulfilled. Check portal for updates");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			transactBitcoinRepository.save(sellOrders);
			
			Prices price = priceRepository.findByCurrency(sellOrders.getCurrency());
			price.setLatestTransactionPrice(sellOrders.getAmount());
			priceRepository.save(price);
			
		} 

	}
	
	private void tryToSettleWithBuyToSellMapping(List<TransactBitcoinEntity> sellOrders, TransactBitcoinEntity buyOrder) {
		List<TransactBitcoinEntity> list = new ArrayList<TransactBitcoinEntity>();
		//the list which will store the required combination

		//the function which checks for the required combination, result basically tells us whether we have got required combination or not
		boolean result = helper(sellOrders, list, buyOrder.getBitcoins(), 0);
		
		System.out.println(list.size());
		
		if(result == true) {
			
			BigDecimal maxSellAmount = BigDecimal.valueOf(Integer.MIN_VALUE);
			
			for(TransactBitcoinEntity sellOrder : list) {
				if(sellOrder.getAmount().compareTo(maxSellAmount) > 0) 
					maxSellAmount = sellOrder.getAmount();
				
			}
			
			
	
			for(TransactBitcoinEntity current : list) {

				BalanceEntity currentBalance = balanceRepository.findByBankAccountAndCurrency(current.getCustomer().getBankAccount(), current.getCurrency());

				BalanceEntity buyOrderBalance =  balanceRepository.findByBankAccountAndCurrency(buyOrder.getCustomer().getBankAccount(), current.getCurrency());


				BigDecimal bitcoinAmount = maxSellAmount.multiply(BigDecimal.valueOf(current.getBitcoins()));
				currentBalance.setBalance(currentBalance.getBalance().add(bitcoinAmount));
				buyOrderBalance.setBalance(buyOrderBalance.getBalance().subtract(bitcoinAmount)); 



				balanceRepository.save(currentBalance);
				balanceRepository.save(buyOrderBalance);



				//Bitcoin settlement
				BalanceEntity bitcoinBalanceSeller = balanceRepository.findByBankAccountAndCurrency(current.getCustomer().getBankAccount(), Currency.BITCOIN);
				bitcoinBalanceSeller.setBalance(bitcoinBalanceSeller.getBalance().subtract((BigDecimal.valueOf(current.getBitcoins()))));
				balanceRepository.save(bitcoinBalanceSeller);

				BalanceEntity bitcoinBalanceBuyer = balanceRepository.findByBankAccountAndCurrency(buyOrder.getCustomer().getBankAccount(), Currency.BITCOIN);
				bitcoinBalanceBuyer.setBalance(bitcoinBalanceBuyer.getBalance().add((BigDecimal.valueOf(current.getBitcoins()))));
				balanceRepository.save(bitcoinBalanceBuyer);
				
				
				BalanceEntity sellOrderInitialBalance =  balanceRepository.findByBankAccountAndCurrency(current.getCustomer().getBankAccount(), buyOrder.getCurrency());

				BigDecimal tradingFees = BigDecimal.valueOf(0.0001).multiply(maxSellAmount.multiply(BigDecimal.valueOf(current.getBitcoins())));
				BigDecimal amountToBeDeducted = tradingFees.compareTo(BigDecimal.valueOf(1)) >= 0 ? BigDecimal.valueOf(1) : tradingFees;

				sellOrderInitialBalance.setBalance(sellOrderInitialBalance.getBalance().subtract(amountToBeDeducted));
				balanceRepository.save(sellOrderInitialBalance);

				current.setStatus(OrderStatus.FULFILLED);
				try {
					Email.sendmail(current.getCustomer().getEmail().toString(), "Hey, we hope you are staying safe, "
							+ "Your order with id " + current.getId() + " has been fulfilled. Check portal for updates");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				transactBitcoinRepository.save(current);			
			}

			buyOrder.setStatus(OrderStatus.FULFILLED);
			
			try {
				Email.sendmail(buyOrder.getCustomer().getEmail().toString(), "Hey, we hope you are staying safe, "
						+ "Your order with id " + buyOrder.getId() + " has been fulfilled. Check portal for updates");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			transactBitcoinRepository.save(buyOrder);
			
			Prices price = priceRepository.findByCurrency(buyOrder.getCurrency());
			price.setLatestTransactionPrice(maxSellAmount);
			priceRepository.save(price);
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

				BigDecimal buyAmount = current.getAmount().multiply(BigDecimal.valueOf(currentBuyOrder.getBitcoins()));
				if(currentBuyOrder.getAmount().compareTo(current.getAmount()) >= 0 
						&& balance.getBalance().compareTo(buyAmount) >= 0) {

					eligibleBuyOrders.add(currentBuyOrder);
				}		

			}


			//After we get eligible buy orders, we will try if we have correct combination of buy orders which can settle current sell order in full

			tryToSettle(eligibleBuyOrders, current);
		}
		
		
		
		List<TransactBitcoinEntity> buyTransactions = transactBitcoinRepository.findByIsBuyAndStatus(true, OrderStatus.OPEN);


		//This is loop where we try to settle all buy orders
		for(TransactBitcoinEntity current : buyTransactions) {


			//Initialised the empty list for eligible sell orders based on balances, bidding, ask price
			List<TransactBitcoinEntity> eligibleSellOrders = new ArrayList<TransactBitcoinEntity>();


			//This finds all open buy orders which will be run through eligibility criteria

			List<TransactBitcoinEntity> allSellOrders = transactBitcoinRepository.findByIsBuyAndStatusAndCurrency(false, OrderStatus.OPEN, current.getCurrency());

			System.out.println(allSellOrders.size());



			for(TransactBitcoinEntity currentSellOrder : allSellOrders) {

				System.out.println(currentSellOrder.getCustomer().getBankAccount().getId() == current.getCustomer().getBankAccount().getId());
				//This checks whether current buy order is of same customer, then we will ignore current buy order
				if(currentSellOrder.getCustomer().getBankAccount().getId() == current.getCustomer().getBankAccount().getId())
					continue;
				
				System.out.println("not same customer");
				


				BalanceEntity balance = balanceRepository.findByBankAccountAndCurrency(currentSellOrder.getCustomer().getBankAccount(), Currency.BITCOIN);

				if(current.getAmount().compareTo(currentSellOrder.getAmount()) >= 0 
						&& BigDecimal.valueOf(current.getBitcoins()).compareTo(BigDecimal.valueOf(currentSellOrder.getBitcoins())) >= 0) {

					eligibleSellOrders.add(currentSellOrder);
				}	
				
				

			}
			
			System.out.println(eligibleSellOrders.size());
			
			
			

			tryToSettleWithBuyToSellMapping(eligibleSellOrders, current);
		}
	}

	@ResponseBody
	@PostMapping
	public ResponseEntity<?> update(@RequestBody @Valid TransactBitcoinEntity bitcoinTransaction) throws Exception {
		CustomerEntity customerEntity = (CustomerEntity) SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal();

        Prices price = priceRepository.findByCurrency(bitcoinTransaction.getCurrency());
        

		if(bitcoinTransaction.isBuy()) {


			if(bitcoinTransaction.isMarketOrder()) {
				bitcoinTransaction.setAmount(price.getLatestTransactionPrice());
			} else {
				price.setLatestBidPrice(bitcoinTransaction.getAmount());
			}


			BalanceEntity balance = balanceRepository.findByBankAccountAndCurrency(customerEntity.getBankAccount(), bitcoinTransaction.getCurrency());
			if(bitcoinTransaction.getAmount().multiply(BigDecimal.valueOf(bitcoinTransaction.getBitcoins())).compareTo(balance.getBalance()) >= 0) {
				//This is the case where order is limit + buy order and you don;t have enough balance 
				Map<String, String> errors = new HashMap<String, String>();
				errors.put("err", "Not enough balance");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(errors);
			}

		} else {
			BalanceEntity balance = balanceRepository.findByBankAccountAndCurrency(customerEntity.getBankAccount(), Currency.BITCOIN);
			if(BigDecimal.valueOf(bitcoinTransaction.getBitcoins()).compareTo(balance.getBalance()) >= 0) {

				// This is the case where order is sell order annd you don't have enough bitcoins
				Map<String, String> errors = new HashMap<String, String>();
				errors.put("err", "Not enough balance");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(errors);
			}
			if(bitcoinTransaction.isMarketOrder()) {
				bitcoinTransaction.setAmount(price.getLatestTransactionPrice());
			} else {
				price.setLatestAskPrice(bitcoinTransaction.getAmount());
			}

		}

		bitcoinTransaction.setCustomer(customerEntity);
		TransactBitcoinEntity updatedEntity = transactBitcoinRepository.save(bitcoinTransaction);
		priceRepository.save(price);

		//After creating buy or sell order, we will call settle bitcoin transactions

		settleBitcoinTransactions();

		Email.sendmail(bitcoinTransaction.getCustomer().getEmail().toString(), "Hey, we hope you are staying safe, "
				+ "Your order with id " + bitcoinTransaction.getId() + " has been placed. Check portal for updates");
		return ResponseEntity.ok(updatedEntity);

	}
}
