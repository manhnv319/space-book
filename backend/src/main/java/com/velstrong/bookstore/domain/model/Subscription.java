package com.velstrong.bookstore.domain.model;

import com.velstrong.bookstore.domain.model.enums.subscription.SubscriptionStatus;

public class Subscription {

    private final Long id;
    private final String name;
    private final String description;
    private final Long price;
    private final Integer durationDays;
    private final Integer maxRentals;
    private SubscriptionStatus status;

    private Subscription(Long id, String name, String description, Long price,
                         Integer durationDays, Integer maxRentals, SubscriptionStatus status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.durationDays = durationDays;
        this.maxRentals = maxRentals;
        this.status = status;
    }

    public static Subscription create(String name, String description, Long price,
                                      Integer durationDays, Integer maxRentals) {
        return new Subscription(null, name, description, price, durationDays, maxRentals, SubscriptionStatus.ACTIVE);
    }

    public static Subscription reconstitute(Long id, String name, String description, Long price,
                                            Integer durationDays, Integer maxRentals, SubscriptionStatus status) {
        return new Subscription(id, name, description, price, durationDays, maxRentals, status);
    }

    public boolean isActive() { return status != null && status.isActive(); }
    public void deactivate() { this.status = SubscriptionStatus.INACTIVE; }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Long getPrice() { return price; }
    public Integer getDurationDays() { return durationDays; }
    public Integer getMaxRentals() { return maxRentals; }
    public SubscriptionStatus getStatus() { return status; }
}
