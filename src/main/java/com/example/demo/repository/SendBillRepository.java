package com.example.demo.repository;

import com.example.demo.model.SendBillEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SendBillRepository extends CrudRepository<SendBillEntity, Long> {
    public List<SendBillEntity> findByCustomerId(Long id);
}
