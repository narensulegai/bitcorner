package com.example.demo.api;

import com.example.demo.model.BalanceEntity;
import com.example.demo.model.BillEntity;
import com.example.demo.model.CustomerEntity;
import com.example.demo.repository.BalanceRepository;
import com.example.demo.repository.BillRepository;
import com.example.demo.repository.CustomerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping(path = "/payBill")
public class PayBillController {

    @Autowired
    BillRepository billRepository;
    
    @Autowired
    CustomerRepository customerRepository;
    
    @Autowired
    BalanceRepository balanceRepository;

    @ResponseBody
    @GetMapping
    public List<BillEntity> get() {
        CustomerEntity customerEntity = (CustomerEntity) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        return billRepository.findByEmail(customerEntity.getEmail());
    }

    @ResponseBody
    @PostMapping
    public BillEntity set(@RequestBody @Valid BillEntity billEntity) {
        CustomerEntity customerEntity = (CustomerEntity) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        billEntity.setEmail(customerEntity.getEmail());
        return billRepository.save(billEntity);
    }
    
    @ResponseBody
    @PutMapping
    public BillEntity settleBill(@RequestBody @Valid BillEntity billEntity ) {
    	
    	// payee adjust
    	BalanceEntity payeeBalanceEntity = balanceRepository.findByBankAccountAndCurrency(billEntity.getCustomer().getBankAccount(), billEntity.getCurrency());
    	Integer balance = payeeBalanceEntity.getBalance();
    	payeeBalanceEntity.setBalance(balance + billEntity.getAmount());
		return billRepository.save(billEntity);
    	
    }
}
