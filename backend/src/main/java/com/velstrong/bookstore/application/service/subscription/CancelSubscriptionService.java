package com.velstrong.bookstore.application.service.subscription;

import com.velstrong.bookstore.application.command.subscription.CancelSubscriptionCommand;
import com.velstrong.bookstore.application.response.subscription.CustomerSubscriptionResponse;
import com.velstrong.bookstore.domain.exception.EntityNotFoundException;
import com.velstrong.bookstore.domain.port.in.subscription.CancelSubscriptionUseCase;
import com.velstrong.bookstore.domain.port.out.CustomerSubscriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CancelSubscriptionService implements CancelSubscriptionUseCase {

    private final CustomerSubscriptionRepository customerSubscriptionRepository;

    public CancelSubscriptionService(CustomerSubscriptionRepository customerSubscriptionRepository) {
        this.customerSubscriptionRepository = customerSubscriptionRepository;
    }

    @Override
    public CustomerSubscriptionResponse cancel(CancelSubscriptionCommand command) {
        var customerSubscription = customerSubscriptionRepository.findById(command.customerSubscriptionId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "CustomerSubscription", command.customerSubscriptionId()));
        customerSubscription.cancel();
        return CustomerSubscriptionResponse.from(customerSubscriptionRepository.save(customerSubscription));
    }
}
