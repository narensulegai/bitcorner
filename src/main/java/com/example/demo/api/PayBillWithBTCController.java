package com.example.demo.api;

import com.example.demo.model.CustomerEntity;
import com.example.demo.model.TransactBitcoinEntity;
import com.example.demo.repository.TransactBitcoinRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/payBill")
public class PayBillWithBTCController {

    @Autowired
    TransactBitcoinRepository transactBitcoinRepository;

    @ResponseBody
    @GetMapping(path = "/getBTCLastBid")
    public TransactBitcoinEntity get() {
        CustomerEntity customerEntity = (CustomerEntity) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        return transactBitcoinRepository.findTopByOrderByIdDesc();
    }
}
