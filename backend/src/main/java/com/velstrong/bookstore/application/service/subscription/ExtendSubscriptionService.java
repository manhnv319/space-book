package com.velstrong.bookstore.application.service.subscription;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.velstrong.bookstore.application.command.subscription.ExtendSubscriptionCommand;
import com.velstrong.bookstore.application.response.subscription.CustomerSubscriptionResponse;
import com.velstrong.bookstore.domain.exception.EntityNotFoundException;
import com.velstrong.bookstore.domain.exception.InvalidOperationException;
import com.velstrong.bookstore.domain.model.CustomerSubscription;
import com.velstrong.bookstore.domain.port.in.subscription.ExtendSubscriptionUseCase;
import com.velstrong.bookstore.domain.port.out.CustomerSubscriptionRepository;


@Service
@Transactional
public class ExtendSubscriptionService implements ExtendSubscriptionUseCase {

    private final CustomerSubscriptionRepository customerSubscriptionRepository;

    public ExtendSubscriptionService(CustomerSubscriptionRepository customerSubscriptionRepository) {
        this.customerSubscriptionRepository = customerSubscriptionRepository;
    }

    @Override
    public CustomerSubscriptionResponse extend(ExtendSubscriptionCommand command) {
        CustomerSubscription cs = customerSubscriptionRepository.findById(command.customerSubscriptionId())
                .orElseThrow(() -> new EntityNotFoundException("CustomerSubscription", command.customerSubscriptionId()));

        if (!cs.getUserId().equals(command.userId()))
            throw new InvalidOperationException("Not authorized to extend this subscription");

        cs.extend(command.additionalDays());
        return CustomerSubscriptionResponse.from(customerSubscriptionRepository.save(cs));
    }
}
