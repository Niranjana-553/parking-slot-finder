
package com.parkingslot.parking_backend.dto;

public class VerifyPaymentRequest {
    public String razorpayOrderId;
    public String razorpayPaymentId;
    public String razorpaySignature;
}
