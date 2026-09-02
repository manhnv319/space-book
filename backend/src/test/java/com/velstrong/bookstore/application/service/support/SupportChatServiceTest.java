package com.velstrong.bookstore.application.service.support;

import com.velstrong.bookstore.application.response.support.SupportConversationResponse;
import com.velstrong.bookstore.domain.exception.InvalidOperationException;
import com.velstrong.bookstore.domain.model.SupportConversation;
import com.velstrong.bookstore.domain.model.SupportMessage;
import com.velstrong.bookstore.domain.model.enums.support.SupportSender;
import com.velstrong.bookstore.domain.port.out.SupportChatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class SupportChatServiceTest {

    private SupportChatRepository chats;
    private SupportChatService service;

    private static final SupportConversation EXISTING =
            new SupportConversation(7L, 1L, LocalDateTime.now(), LocalDateTime.now());

    @BeforeEach
    void setUp() {
        chats = mock(SupportChatRepository.class);
        when(chats.save(any(SupportConversation.class))).thenAnswer(call -> {
            SupportConversation given = call.getArgument(0);
            return given.id() == null
                    ? new SupportConversation(7L, given.userId(), given.createdAt(), given.lastMessageAt())
                    : given;
        });
        when(chats.findMessages(anyLong())).thenReturn(List.of());
        when(chats.saveMessage(any(SupportMessage.class))).thenAnswer(call -> {
            SupportMessage given = call.getArgument(0);
            return new SupportMessage(11L, given.conversationId(), given.sender(), given.senderUserId(), given.body(), given.createdAt());
        });
        service = new SupportChatService(chats);
    }

    @Test
    void openingSupportWithoutWritingLeavesNothingBehind() {
        // A conversation created just by visiting the page would sit in the staff
        // queue with nothing in it.
        when(chats.findByUserId(1L)).thenReturn(Optional.empty());

        SupportConversationResponse response = service.myConversation(1L);

        assertThat(response.id()).isNull();
        assertThat(response.messages()).isEmpty();
        verify(chats, never()).save(any(SupportConversation.class));
    }

    @Test
    void theFirstCustomerMessageOpensTheConversation() {
        when(chats.findByUserId(1L)).thenReturn(Optional.empty());

        service.sendAsCustomer(1L, "Cho mình hỏi về đơn hàng");

        ArgumentCaptor<SupportMessage> sent = ArgumentCaptor.forClass(SupportMessage.class);
        verify(chats).saveMessage(sent.capture());
        assertThat(sent.getValue().sender()).isEqualTo(SupportSender.CUSTOMER);
        assertThat(sent.getValue().body()).isEqualTo("Cho mình hỏi về đơn hàng");
    }

    @Test
    void aSecondMessageReusesTheSameConversation() {
        when(chats.findByUserId(1L)).thenReturn(Optional.of(EXISTING));

        service.sendAsCustomer(1L, "Thêm một câu nữa");

        ArgumentCaptor<SupportMessage> sent = ArgumentCaptor.forClass(SupportMessage.class);
        verify(chats).saveMessage(sent.capture());
        assertThat(sent.getValue().conversationId()).isEqualTo(7L);
    }

    @Test
    void staffRepliesAreMarkedAsStaff() {
        when(chats.findById(7L)).thenReturn(Optional.of(EXISTING));

        service.replyAsStaff(7L, 99L, "Chào bạn, mình kiểm tra ngay");

        ArgumentCaptor<SupportMessage> sent = ArgumentCaptor.forClass(SupportMessage.class);
        verify(chats).saveMessage(sent.capture());
        assertThat(sent.getValue().sender()).isEqualTo(SupportSender.STAFF);
        // Who answered is kept even though the customer only sees "STAFF".
        assertThat(sent.getValue().senderUserId()).isEqualTo(99L);
    }

    @Test
    void rejectsAnEmptyOrOverlongMessageAsTheSendersFault() {
        when(chats.findByUserId(1L)).thenReturn(Optional.of(EXISTING));

        assertThatThrownBy(() -> service.sendAsCustomer(1L, "   "))
                .isInstanceOf(InvalidOperationException.class);
        assertThatThrownBy(() -> service.sendAsCustomer(1L, "x".repeat(2001)))
                .isInstanceOf(InvalidOperationException.class);
        verify(chats, never()).saveMessage(any());
    }

    @Test
    void sendingBumpsTheConversationSoItRisesInTheStaffQueue() {
        SupportConversation stale = new SupportConversation(7L, 1L,
                LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(2));
        when(chats.findByUserId(1L)).thenReturn(Optional.of(stale));

        service.sendAsCustomer(1L, "Còn ai hỗ trợ không ạ?");

        ArgumentCaptor<SupportConversation> saved = ArgumentCaptor.forClass(SupportConversation.class);
        verify(chats).save(saved.capture());
        assertThat(saved.getValue().lastMessageAt()).isAfter(stale.lastMessageAt());
    }
}
