package com.example.demo.repository;

import com.example.demo.Currency;
import com.example.demo.OrderStatus;
import com.example.demo.model.TransactBitcoinEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TransactBitcoinRepository extends CrudRepository<TransactBitcoinEntity, Long> {
    public List<TransactBitcoinEntity> findByCustomerId(Long id);
    public List<TransactBitcoinEntity> findByIsBuyAndStatus(Boolean isBuy, OrderStatus status);
    public List<TransactBitcoinEntity> findByIsBuyAndStatusAndCurrency(Boolean isBuy, OrderStatus status, Currency currency);
    public List<TransactBitcoinEntity> findAll();
    public List<TransactBitcoinEntity> findByStatus(OrderStatus status);
    public TransactBitcoinEntity findFirstByCurrencyAndIsMarketOrderOrderByIdDesc(Currency currency, Boolean isMarketOrder);
    public TransactBitcoinEntity findFirstByCurrencyAndIsBuyAndIsMarketOrderOrderByIdDesc(Currency currency, Boolean buyOrder, boolean marketOrder);


}
