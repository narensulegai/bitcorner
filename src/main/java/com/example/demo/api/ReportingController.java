package com.example.demo.api;

import com.example.demo.OrderStatus;
import com.example.demo.model.BalanceEntity;
import com.example.demo.model.BankAccountEntity;
import com.example.demo.model.Reports;
import com.example.demo.repository.BalanceRepository;
import com.example.demo.repository.BankAccountRepository;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.TransactBitcoinRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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

    @ResponseBody
    @GetMapping
    public Reports get() {    	
    
    	int totalOrders = transactBitcoinRepository.findAll().size();
    	
    	int totalFullfilledOrders = transactBitcoinRepository.findByStatus(OrderStatus.FULFILLED).size();
    	
    	int customers = customerRepository.findAll().size();
    	
    	BankAccountEntity bitcornerAccount = bankAccountRepository.findByAccountNumber("1111111111111");
    	
    	List<BalanceEntity> balance = null;
    	
    	if(bitcornerAccount != null) {
        	 balance = balanceRepository.findByBankAccount(bitcornerAccount);

    	}
    	
    	Reports report = new Reports();
    	report.setBitcornerBalance(balance);
    	report.setNoOfOrdersFulfilled(totalFullfilledOrders);
    	report.setTotalOrdersCreated(totalOrders);
    	report.setTotalCustomers(customers);
    	
    	
    	return report;
    	

        
    }


}
