package com.example.demo.api;

import com.example.demo.model.CustomerEntity;
import com.example.demo.model.BillEntity;
import com.example.demo.repository.BillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/sendBill")
public class SendBillController {

    @Autowired
    BillRepository billRepository;

    @ResponseBody
    @GetMapping
    public List<BillEntity> get() {
        CustomerEntity customerEntity = (CustomerEntity) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        return billRepository.findByCustomerId(customerEntity.getId());
    }

    @ResponseBody
    @PostMapping
    public BillEntity create(@RequestBody @Valid BillEntity billEntity) {
        CustomerEntity customerEntity = (CustomerEntity) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        billEntity.setCustomer(customerEntity);
        return billRepository.save(billEntity);
    }

    @ResponseBody
    @PutMapping
    public BillEntity update(@RequestBody @Valid BillEntity billEntity) {
        CustomerEntity customerEntity = (CustomerEntity) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return billRepository.save(billEntity);
    }
}
