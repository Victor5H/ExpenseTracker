package com.github.Victor5H.ExpenseTracker.repository;

import com.github.Victor5H.ExpenseTracker.be.PaymentType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PaymentTypeRepository extends JpaRepository<PaymentType, Long> {
    Optional<PaymentType> findByType(String type);
}
