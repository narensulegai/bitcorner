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

		boolean result = helper(buyOrders, list, sellOrders.getBitcoins(), 0);
		
		if(result == true) {
			
			for(TransactBitcoinEntity current : list) {
				
				BalanceEntity currentBalance = balanceRepository.findByBankAccountAndCurrency(current.getCustomer().getBankAccount(), current.getCurrency());
				
				BalanceEntity sellOrderBalance =  balanceRepository.findByBankAccountAndCurrency(sellOrders.getCustomer().getBankAccount(), current.getCurrency());
				if(current.isMarketOrder()) {
					currentBalance.setBalance(currentBalance.getBalance() - sellOrders.getAmount() * current.getBitcoins());
					sellOrderBalance.setBalance(sellOrderBalance.getBalance() + sellOrders.getAmount() * current.getBitcoins()); 
					
				} else {
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
		List<TransactBitcoinEntity> transactions = transactBitcoinRepository.findByIsBuyAndStatus(false, OrderStatus.OPEN);

		for(TransactBitcoinEntity current : transactions) {

			List<TransactBitcoinEntity> eligibleBuyOrders = new ArrayList<TransactBitcoinEntity>();

			List<TransactBitcoinEntity> allbuyOrders = transactBitcoinRepository.findByIsBuyAndStatusAndCurrency(true, OrderStatus.OPEN, current.getCurrency());
		
			

			for(TransactBitcoinEntity currentBuyOrder : allbuyOrders) {
				
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
					Map<String, String> map = new HashMap<String, String>();
					return ResponseEntity.status(HttpStatus.BAD_REQUEST)
							.body("Not enough balace");
				}
			}
		} else {
			BalanceEntity balance = balanceRepository.findByBankAccountAndCurrency(customerEntity.getBankAccount(), Currency.BITCOIN);
			if(bitcoinTransaction.getBitcoins() > balance.getBalance())
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body("Not enough balace");
		}

		bitcoinTransaction.setCustomer(customerEntity);
		TransactBitcoinEntity updatedEntity = transactBitcoinRepository.save(bitcoinTransaction);
		settleBitcoinTransactions();
		return ResponseEntity.ok(updatedEntity);


	}
}
