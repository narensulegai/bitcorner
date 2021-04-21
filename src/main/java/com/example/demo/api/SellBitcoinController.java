package com.example.demo.api;

import com.example.demo.model.CustomerEntity;
import com.example.demo.model.SellBitcoinEntity;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.SellBitcoinRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/sellBitcoin")
public class SellBitcoinController {

    @Autowired
    SellBitcoinRepository sellBitcoinRepository;
    @Autowired
    CustomerRepository customerRepository;

    @ResponseBody
    @GetMapping
    public List<SellBitcoinEntity> get() {
        CustomerEntity customerEntity = (CustomerEntity) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        return sellBitcoinRepository.findByCustomerId(customerEntity.getId());
    }

    @ResponseBody
    @PostMapping
    public SellBitcoinEntity update(@RequestBody @Valid SellBitcoinEntity sellBitcoinEntity) {
        CustomerEntity customerEntity = (CustomerEntity) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        sellBitcoinEntity.setCustomer(customerEntity);
        return sellBitcoinRepository.save(sellBitcoinEntity);
    }
}
