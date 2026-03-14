package com.example.event.dto;

public class KycVerificationRequest {

    private String customerId;
    private String verificationStatus;
    private String kycProvider;

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getVerificationStatus() { return verificationStatus; }
    public void setVerificationStatus(String verificationStatus) { this.verificationStatus = verificationStatus; }

    public String getKycProvider() { return kycProvider; }
    public void setKycProvider(String kycProvider) { this.kycProvider = kycProvider; }
}
