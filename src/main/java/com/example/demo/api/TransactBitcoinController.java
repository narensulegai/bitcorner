package com.example.demo.api;

import com.example.demo.Currency;
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
        
        return ResponseEntity.ok(transactBitcoinRepository.save(bitcoinTransaction));
    }
}
