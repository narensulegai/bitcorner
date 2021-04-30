package com.example.demo.repository;

import com.example.demo.model.BillEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BillRepository extends CrudRepository<BillEntity, Long> {
    public List<BillEntity> findByCustomerId(Long id);
    public List<BillEntity> findByEmail(String email);
}
