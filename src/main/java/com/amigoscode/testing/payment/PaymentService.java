package com.amigoscode.testing.payment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.amigoscode.testing.customer.Customer;
import com.amigoscode.testing.customer.CustomerRepository;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final CustomerRepository customerRepository;
    private final CardPaymentCharger cardPaymentCharger;

    private final SmsService smsService ;

   private final static List<Currency> ACCEPTED_CURRENCIES = List.of(Currency.EUR, Currency.GNF, Currency.USD);

    public PaymentService(PaymentRepository paymentRepository, CustomerRepository customerRepository,
            CardPaymentCharger cardPaymentCharger,  SmsService smsService) {
        this.paymentRepository = paymentRepository;
        this.customerRepository = customerRepository;
        this.cardPaymentCharger = cardPaymentCharger;
        this.smsService=smsService;
    }

    void chargeCard(UUID costomerId, PaymentRequest paymentRequest) {

        // Does customer exists if not throw
        Optional<Customer> optionalCustomer = customerRepository.findById(costomerId);

        if (!optionalCustomer.isPresent()) {
            throw new IllegalStateException(String.format("Customer with ID  %s not found", costomerId));
        }

        // check if the currency is supported
        boolean isCurrencySupported = ACCEPTED_CURRENCIES.stream().anyMatch(currency->currency.equals(paymentRequest.getPayment().getCurrency()));
        
        if (!isCurrencySupported) {
            throw new IllegalStateException(String.format("the currency not supported: %s", paymentRequest.getPayment().getCurrency()));
        }

        // charge card
        CardPaymentCharge cardPaymentCharge = cardPaymentCharger.chargeCard(
                paymentRequest.getPayment().getSource(),
                paymentRequest.getPayment().getAmount(), 
                paymentRequest.getPayment().getCurrency(),
                paymentRequest.getPayment().getDescription());

        // not debited
        if (!cardPaymentCharge.isCardDebited()) {
            throw new IllegalStateException(String.format("Card not debited for the Customer %s", costomerId));
        }

        // save payment
        paymentRequest.getPayment().setCustomerId(costomerId);
        paymentRepository.save(paymentRequest.getPayment());

        // send SMS
        smsService.send(optionalCustomer.get().getPhoneNumber(), "62599000", paymentRequest.getPayment().getDescription());

      
    }



    public List<Payment> getPaymentByCustomer(UUID customerId){

        if (customerId==null) {
            throw new IllegalStateException("Customer ID is null");
        }

        List<Payment> payments= paymentRepository.findPaymentByCustomer(customerId);
        if (payments.isEmpty()) {
            throw new IllegalStateException(String.format("Payment not found for the customer %s", customerId)); 
        }

        return payments;
    }
}
