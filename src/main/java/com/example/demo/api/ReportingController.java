package com.example.demo.api;

import com.example.demo.Currency;
import com.example.demo.OrderStatus;
import com.example.demo.model.BalanceEntity;
import com.example.demo.model.BankAccountEntity;
import com.example.demo.model.BillEntity;
import com.example.demo.model.CustomerEntity;
import com.example.demo.model.Prices;
import com.example.demo.model.Reports;
import com.example.demo.model.TransactBitcoinEntity;
import com.example.demo.repository.BalanceRepository;
import com.example.demo.repository.BankAccountRepository;
import com.example.demo.repository.BillRepository;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.PriceRepository;
import com.example.demo.repository.TransactBitcoinRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;


@RestController
@RequestMapping(path = "/api/reports")
public class ReportingController {

	@Autowired
	TransactBitcoinRepository transactBitcoinRepository;

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	BalanceRepository balanceRepository;

	@Autowired
	BankAccountRepository bankAccountRepository;

	@Autowired
	PriceRepository priceRepository;
	
	@Autowired
	BillRepository billRepository;

	@ResponseBody
	@GetMapping
	public Reports get() {    	
		CustomerEntity customerEntity = (CustomerEntity) SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal();


		int totalOrders = transactBitcoinRepository.findAll().size();

		int totalFullfilledOrders = transactBitcoinRepository.findByStatus(OrderStatus.FULFILLED).size();

		int customers = customerRepository.findAll().size();

		BankAccountEntity bitcornerAccount = bankAccountRepository.findByAccountNumber("1111111111111");

		List<BalanceEntity> balance = null;

		if(bitcornerAccount != null) {
			balance = balanceRepository.findByBankAccount(bitcornerAccount);

		}
		
		List<Prices> prices = priceRepository.findAll();
		
		Prices price = null;
		
		if(prices.size() != 0) 
			price = prices.get(0);
		
		List<TransactBitcoinEntity> transactions = transactBitcoinRepository.findByCustomerId(customerEntity.getId());
		List<BillEntity> bills = billRepository.findByCustomerId(customerEntity.getId());
		List<BalanceEntity> balances = balanceRepository.findByBankAccountId(customerEntity.getBankAccount().getId());
		

		Reports report = new Reports();
		report.setBitcornerBalance(balance);
		report.setNoOfOrdersFulfilled(totalFullfilledOrders);
		report.setTotalOrdersCreated(totalOrders);
		report.setTotalCustomers(customers);
		report.setLastestPrices(price);
		report.setTransactions(transactions);
		report.setBills(bills);
		report.setBalances(balances);


		return report;



	}


}
