package com.example.demo.repository;
import com.example.demo.model.Prices;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface PriceRepository extends CrudRepository<Prices, Long> {
	List<Prices> findAll();
}
