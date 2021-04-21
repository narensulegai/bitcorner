package com.example.demo.repository;

import com.example.demo.model.BuyBitcoinEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BuyBitcoinRepository extends CrudRepository<BuyBitcoinEntity, Long> {
    public List<BuyBitcoinEntity> findByCustomerId(Long id);
}
