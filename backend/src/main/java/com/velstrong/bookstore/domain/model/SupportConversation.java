package com.velstrong.bookstore.domain.model;

import com.velstrong.bookstore.domain.model.enums.support.SupportSender;

import java.time.LocalDateTime;

public record SupportConversation(Long id, Long userId, LocalDateTime createdAt, LocalDateTime lastMessageAt,
                                  int staffUnreadCount, int customerUnreadCount, String lastMessagePreview, Long assignedStaffUserId) {
    public SupportConversation(Long id, Long userId, LocalDateTime createdAt, LocalDateTime lastMessageAt) {
        this(id, userId, createdAt, lastMessageAt, 0, 0, "", null);
    }
    public static SupportConversation openFor(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        return new SupportConversation(null, userId, now, now, 0, 0, "", null);
    }

    public SupportConversation touched(SupportSender sender, String body, boolean hasAttachments) {
        String preview = body == null || body.isBlank() ? (hasAttachments ? "Đã gửi ảnh" : "") : body;
        int staffUnread = sender == SupportSender.CUSTOMER ? increment(staffUnreadCount) : staffUnreadCount;
        int customerUnread = sender == SupportSender.STAFF ? increment(customerUnreadCount) : customerUnreadCount;
        return new SupportConversation(id, userId, createdAt, LocalDateTime.now(), staffUnread, customerUnread, preview, assignedStaffUserId);
    }

    public SupportConversation markStaffRead() {
        return new SupportConversation(id, userId, createdAt, lastMessageAt, 0, customerUnreadCount, lastMessagePreview, assignedStaffUserId);
    }

    public SupportConversation markCustomerRead() {
        return new SupportConversation(id, userId, createdAt, lastMessageAt, staffUnreadCount, 0, lastMessagePreview, assignedStaffUserId);
    }

    public SupportConversation claimIfUnassigned(Long staffUserId) {
        return assignedStaffUserId == null ? new SupportConversation(id, userId, createdAt, lastMessageAt, staffUnreadCount, customerUnreadCount, lastMessagePreview, staffUserId) : this;
    }

    private static int increment(int current) {
        return Math.min(Integer.MAX_VALUE, current + 1);
    }
}
