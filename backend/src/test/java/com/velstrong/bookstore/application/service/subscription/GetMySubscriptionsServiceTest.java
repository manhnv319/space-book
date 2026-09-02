package com.velstrong.bookstore.application.service.subscription;

import com.velstrong.bookstore.domain.model.CustomerSubscription;
import com.velstrong.bookstore.domain.model.PageResult;
import com.velstrong.bookstore.domain.model.Subscription;
import com.velstrong.bookstore.domain.model.enums.subscription.CustomerSubscriptionStatus;
import com.velstrong.bookstore.domain.model.enums.subscription.SubscriptionStatus;
import com.velstrong.bookstore.domain.port.out.CustomerSubscriptionRepository;
import com.velstrong.bookstore.domain.port.out.SubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GetMySubscriptionsServiceTest {

    private CustomerSubscriptionRepository customerSubscriptionRepository;
    private SubscriptionRepository subscriptionRepository;
    private GetMySubscriptionsService service;

    @BeforeEach
    void setUp() {
        customerSubscriptionRepository = mock(CustomerSubscriptionRepository.class);
        subscriptionRepository = mock(SubscriptionRepository.class);
        service = new GetMySubscriptionsService(customerSubscriptionRepository, subscriptionRepository);
    }

    @Test
    @DisplayName("uses a single findByIds call instead of N findById calls (F16)")
    void avoidsNPlusOne() {
        CustomerSubscription cs1 = CustomerSubscription.reconstitute(
                1L, 7L, 11L, LocalDate.now(), LocalDate.now().plusDays(30),
                0, CustomerSubscriptionStatus.ACTIVE, null);
        CustomerSubscription cs2 = CustomerSubscription.reconstitute(
                2L, 7L, 12L, LocalDate.now(), LocalDate.now().plusDays(30),
                0, CustomerSubscriptionStatus.ACTIVE, null);
        Subscription sub1 = Subscription.reconstitute(11L, "Gold", null, 200_000L, 30, 5, SubscriptionStatus.ACTIVE);
        Subscription sub2 = Subscription.reconstitute(12L, "Silver", null, 100_000L, 30, 3, SubscriptionStatus.ACTIVE);

        when(customerSubscriptionRepository.findByUserId(7L, 0, 10))
                .thenReturn(new PageResult<>(List.of(cs1, cs2), 2L));
        when(subscriptionRepository.findByIds(any())).thenReturn(List.of(sub1, sub2));

        var response = service.getMySubscriptions(7L, 0, 10);

        verify(subscriptionRepository).findByIds(any());
        verify(subscriptionRepository, never()).findById(anyLong());
        assertThat(response.totalElements()).isEqualTo(2L);
        assertThat(response.content()).hasSize(2);
        assertThat(cs1.getSubscription()).isEqualTo(sub1);
        assertThat(cs2.getSubscription()).isEqualTo(sub2);
    }

    @Test
    @DisplayName("tolerates missing subscription records gracefully")
    void missingSubscriptionStaysNull() {
        CustomerSubscription cs1 = CustomerSubscription.reconstitute(
                1L, 7L, 99L, LocalDate.now(), LocalDate.now().plusDays(30),
                0, CustomerSubscriptionStatus.ACTIVE, null);
        when(customerSubscriptionRepository.findByUserId(7L, 0, 10))
                .thenReturn(new PageResult<>(List.of(cs1), 1L));
        when(subscriptionRepository.findByIds(any())).thenReturn(List.of());

        var response = service.getMySubscriptions(7L, 0, 10);

        assertThat(response.content()).hasSize(1);
        assertThat(response.content().get(0).subscription()).isNull();
    }

    @Test
    @DisplayName("passes an empty set to findByIds when the page is empty (avoids spurious query)")
    void emptyPageSkipsSubscriptionLookup() {
        when(customerSubscriptionRepository.findByUserId(7L, 0, 10))
                .thenReturn(new PageResult<>(List.of(), 0L));
        when(subscriptionRepository.findByIds(Set.of())).thenReturn(List.of());

        var response = service.getMySubscriptions(7L, 0, 10);

        assertThat(response.totalElements()).isEqualTo(0L);
        assertThat(response.content()).isEmpty();
    }
}
