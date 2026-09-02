package com.velstrong.bookstore.application.service.rental;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;

class RentalFulfillmentRetryServiceTest {

    @Test
    void recordsFailureThenCompletesOnLaterRetry() {
        RentalFulfillmentRecordService records = mock(RentalFulfillmentRecordService.class);
        RentalFulfillmentService fulfillment = mock(RentalFulfillmentService.class);
        RentalFulfillmentRetryService retries = new RentalFulfillmentRetryService(records, fulfillment);
        when(records.beginAttempt(1L)).thenReturn(true);
        when(fulfillment.fulfillPaidOrder(1L)).thenThrow(new RuntimeException("No copy available")).thenReturn(List.of());

        retries.process(1L);
        retries.process(1L);

        verify(records).markFailed(eq(1L), any(RuntimeException.class));
        verify(records).markCompleted(1L);
        verify(fulfillment, times(2)).fulfillPaidOrder(1L);
    }

    @Test
    void doesNotDuplicateCompletedFulfillment() {
        RentalFulfillmentRecordService records = mock(RentalFulfillmentRecordService.class);
        RentalFulfillmentRetryService retries = new RentalFulfillmentRetryService(records, mock(RentalFulfillmentService.class));
        when(records.beginAttempt(1L)).thenReturn(false);

        retries.process(1L);

        verify(records, never()).markCompleted(any());
        verify(records, never()).markFailed(any(), any());
    }
}
