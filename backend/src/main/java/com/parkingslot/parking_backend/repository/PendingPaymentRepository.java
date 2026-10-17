
package com.parkingslot.parking_backend.repository;

import com.parkingslot.parking_backend.model.PendingPayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PendingPaymentRepository
        extends JpaRepository<PendingPayment, Long> {

    Optional<PendingPayment> findByRazorpayOrderId(String orderId);
}
