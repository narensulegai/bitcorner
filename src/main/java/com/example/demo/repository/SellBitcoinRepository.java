package com.example.demo.repository;

import com.example.demo.model.SellBitcoinEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SellBitcoinRepository extends CrudRepository<SellBitcoinEntity, Long> {
    public List<SellBitcoinEntity> findByCustomerId(Long id);
}
