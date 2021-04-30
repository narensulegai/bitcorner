package com.example.demo.repository;

import com.example.demo.model.TransactBitcoinEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TransactBitcoinRepository extends CrudRepository<TransactBitcoinEntity, Long> {
    public List<TransactBitcoinEntity> findByCustomerId(Long id);
}
