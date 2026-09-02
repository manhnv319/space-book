package com.velstrong.bookstore.infrastructure.config.security;

import java.util.Set;

public final class KnownPermissions {

    public static final Set<String> VALUES = Set.of(
            "book:read",
            "order:create",
            "order:read:own",
            "rental:read:own",
            "rental:extend:own",
            "subscription:purchase",
            "order:read:all",
            "order:update-status",
            "rental:read:all",
            "rental:checkin",
            "order:create:counter",
            "payment:refund",
            "copy:manage",
            "stock:receive",
            "stock:adjust",
            "inventory:audit",
            "book:manage",
            "voucher:manage",
            "subscription:manage",
            "user:manage",
            "role:assign",
            "report:view",
            "config:manage"
    );

    private KnownPermissions() {
    }
}
