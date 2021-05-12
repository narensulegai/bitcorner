package com.example.demo.model;

import com.example.demo.BillStatus;
import com.example.demo.Currency;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "bill")
public class BillEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @ManyToOne
    CustomerEntity customer = null;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email must be a valid email address")
    String email = null;

    @NotBlank(message = "Description cannot be empty")
    String description = null;

    @Enumerated(EnumType.STRING)
    Currency currency = Currency.USD;

    @Min(value = 0, message = "Amount should be greater than 0")
    BigDecimal amount = BigDecimal.ZERO;

    @DateTimeFormat(pattern = "yyyy-mm-dd")
    @NotNull(message = "Due date cannot be empty")
    Date due;

    @Enumerated(EnumType.STRING)
    BillStatus status = BillStatus.WAITING;

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public BillStatus getStatus() {
        return status;
    }

    public void setStatus(BillStatus status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CustomerEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerEntity customer) {
        this.customer = customer;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getDue() {
        return due;
    }

    public void setDue(Date due) {
        this.due = due;
    }
}
