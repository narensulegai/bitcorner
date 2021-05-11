package com.example.demo.api;

import com.example.demo.BillStatus;
import com.example.demo.model.BalanceEntity;
import com.example.demo.model.BillEntity;
import com.example.demo.model.CustomerEntity;
import com.example.demo.repository.BalanceRepository;
import com.example.demo.repository.BillRepository;
import com.example.demo.repository.CustomerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    public ResponseEntity<?> settleBill(@RequestBody @Valid BillEntity billEntity) {
    	// Payer adjust
        CustomerEntity customerEntity = (CustomerEntity) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        Map<String, String> errors = new HashMap<>();
        BalanceEntity payerBalanceEntity = balanceRepository.findByBankAccountAndCurrency(customerEntity.getBankAccount(), billEntity.getCurrency());
        Integer payerBalance = payerBalanceEntity.getBalance();
        if( payerBalance < billEntity.getAmount()) {
        	errors.put("err", "Not enough balance");
        }
        else {
        	payerBalanceEntity.setBalance(payerBalance - billEntity.getAmount());
        	
        	
        	// failed to save payer balance
    		if(balanceRepository.save(payerBalanceEntity) == null) {
            	errors.put("err", "Payer balance issue");
    			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
    						.body(errors);
    		}
        }
        // bitcorner adjust
    	// CustomerEntity bitcorner = customerRepository.findByEmail("bitcorner@gmail.com");
        
    	// payee adjust
    	BalanceEntity payeeBalanceEntity = balanceRepository.findByBankAccountAndCurrency(billEntity.getCustomer().getBankAccount(), billEntity.getCurrency());
    	Integer balance = payeeBalanceEntity.getBalance();
    	payeeBalanceEntity.setBalance(balance + billEntity.getAmount());
		
    	if(balanceRepository.save(payeeBalanceEntity) == null) {
			errors.put("err", "Payee balance issue");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(errors);
		}
    	Optional<BillEntity> payeeBillEntity = billRepository.findById(billEntity.getId());
    	if(payeeBillEntity.get() != null) {
    		payeeBillEntity.get().setStatus(BillStatus.PAID);
    		
    	}
    	else {
    		errors.put("err", "Bill status couldn't set");
    		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
    					.body(errors);
    	}
		if(billRepository.save(payeeBillEntity.get()) != null)
			return ResponseEntity.ok("Transaction successful");
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(errors);
		
    	
    }
}
