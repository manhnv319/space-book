package com.velstrong.bookstore.infrastructure.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(BankTransferProperties.class)
public class BankTransferConfiguration {}
