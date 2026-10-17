
package com.parkingslot.parking_backend.controller;

import com.parkingslot.parking_backend.dto.CreatePaymentRequest;
import com.parkingslot.parking_backend.dto.VerifyPaymentRequest;
import com.parkingslot.parking_backend.service.PaymentService;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create-order")
    public String createOrder(@RequestBody CreatePaymentRequest request)
            throws Exception {
        return paymentService.createOrder(request).toString();
    }

    @PostMapping("/verify")
    public Object verifyPayment(@RequestBody VerifyPaymentRequest request)
            throws Exception {
        return paymentService.verifyPayment(request);
    }
}
