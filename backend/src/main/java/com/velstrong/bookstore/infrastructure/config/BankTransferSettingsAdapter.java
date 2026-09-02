package com.velstrong.bookstore.infrastructure.config;

import com.velstrong.bookstore.domain.port.out.BankTransferSettingsPort;
import org.springframework.stereotype.Component;

@Component
public class BankTransferSettingsAdapter implements BankTransferSettingsPort {
    private final BankTransferProperties properties;

    public BankTransferSettingsAdapter(BankTransferProperties properties) {
        this.properties = properties;
    }

    @Override
    public boolean isConfigured() {
        return properties.isBankConfigured();
    }

    @Override
    public String bankName() {
        return properties.bankName();
    }

    @Override
    public String bankBin() {
        return properties.bankBin();
    }

    @Override
    public String accountNumber() {
        return properties.accountNumber();
    }

    @Override
    public String accountName() {
        return properties.accountName();
    }

    @Override
    public int expiryMinutes() {
        return properties.expiryMinutes();
    }
}
