package com.amigoscode.testing.payment;

public class CardPaymentCharge {
    private final boolean isCardDebited;

    CardPaymentCharge(boolean isCardDebited){
        this.isCardDebited=isCardDebited;
    }

    public boolean isCardDebited() {
        return isCardDebited;
    }


    
}
