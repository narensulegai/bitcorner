package com.example.demo.api;

import com.example.demo.BillStatus;
import com.example.demo.Currency;
import com.example.demo.model.BalanceEntity;
import com.example.demo.model.BankAccountEntity;
import com.example.demo.model.BillEntity;
import com.example.demo.model.CustomerEntity;
import com.example.demo.repository.BalanceRepository;
import com.example.demo.repository.BankAccountRepository;
import com.example.demo.repository.BillRepository;
import com.example.demo.repository.CustomerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
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
    
    @Autowired
    BankAccountRepository bankAccountRepository;

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
    @Transactional
    @PutMapping
    public ResponseEntity<?> settleBill(@RequestBody @Valid BillEntity billEntity) {
    	// Payer adjust
        CustomerEntity customerEntity = (CustomerEntity) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        BigDecimal serviceFee = billEntity.getAmount().multiply(BigDecimal.valueOf(0.0001));
        Map<String, String> errors = new HashMap<>();
        BalanceEntity payerBalanceEntity = balanceRepository.findByBankAccountAndCurrency(customerEntity.getBankAccount(), billEntity.getCurrency());
        BigDecimal payerBalance = payerBalanceEntity.getBalance();
        if( payerBalance.compareTo(billEntity.getAmount()) == -1) {
        	errors.put("err", "Not enough balance");
        }
        else {
        	payerBalanceEntity.setBalance(payerBalance.subtract(billEntity.getAmount()).subtract(serviceFee));
        	
        	
        	// failed to save payer balance
    		if(balanceRepository.save(payerBalanceEntity) == null) {
            	errors.put("err", "Payer balance issue");
    			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
    						.body(errors);
    		}
        }
        // bitcorner adjust service fee
        CustomerEntity bitcorner = customerRepository.findByEmail("bitcorner@gmail.com");
        if(bitcorner == null) {
        	bitcorner = new CustomerEntity();
        	bitcorner.setEmail("bitcorner@gmail.com");
        	bitcorner.setName("Bitcorner");
            customerRepository.save(bitcorner);
        } 
        if (bitcorner.getBankAccount() == null) {
        	BankAccountEntity bankAccountEntity = new BankAccountEntity();
        	bankAccountEntity.setAddress("123 NYSE, New York");
        	bankAccountEntity.setBankName("Chase");
        	bankAccountEntity.setCountry("United States");
        	bankAccountEntity.setOwnerName(bitcorner.getName());
        	bankAccountEntity.setPrimaryCurrency(Currency.USD);
        	bankAccountEntity.setAccountNumber("1111111111111");
			bankAccountRepository.save(bankAccountEntity);
        	bitcorner.setBankAccount(bankAccountEntity);
            customerRepository.save(bitcorner);
            
            if(balanceRepository.findByBankAccountId(bankAccountEntity.getId()).size() == 0) {
            	for(Currency c: Currency.values()) {
                    BalanceEntity balanceEntity = new BalanceEntity();
                    balanceEntity.setBankAccount(bitcorner.getBankAccount());
                    balanceEntity.setBalance(BigDecimal.ZERO);
                    balanceEntity.setCurrency(c);
                    balanceRepository.save(balanceEntity);
                }
            }
        }
        BalanceEntity bitcornerBalanceEntity = balanceRepository.findByBankAccountAndCurrency(bitcorner.getBankAccount(), billEntity.getCurrency());
        BigDecimal bitcornerBalance = bitcornerBalanceEntity.getBalance();
        bitcornerBalanceEntity.setBalance(bitcornerBalance.add(serviceFee));
        balanceRepository.save(bitcornerBalanceEntity);
        
    	// payee adjust
    	BalanceEntity payeeBalanceEntity = balanceRepository.findByBankAccountAndCurrency(billEntity.getCustomer().getBankAccount(), billEntity.getCurrency());
    	BigDecimal payeeBalance = payeeBalanceEntity.getBalance();
    	payeeBalanceEntity.setBalance(payeeBalance.add(billEntity.getAmount()));
		
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
			return ResponseEntity.ok(payerBalanceEntity);
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(errors);
		
    	
    }
}
