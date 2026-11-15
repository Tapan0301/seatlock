package com.seatlock.payment.provider;

public record ProviderResult(boolean approved, String providerRef, String failureReason) {

    public static ProviderResult approved(String providerRef){
        return new ProviderResult(true, providerRef, null);
    }

    public static ProviderResult declined(String failureReason){
        return  new ProviderResult(false, null, failureReason);
    }
}
