
package com.example.parkingslotfinder;

public class VerifyPaymentRequest {
    public String razorpayOrderId;
    public String razorpayPaymentId;
    public String razorpaySignature;

    public VerifyPaymentRequest(
            String orderId,
            String paymentId,
            String signature) {
        this.razorpayOrderId = orderId;
        this.razorpayPaymentId = paymentId;
        this.razorpaySignature = signature;
    }
}
