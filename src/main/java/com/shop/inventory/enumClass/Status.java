package com.shop.inventory.enumClass;

public enum Status {
    READY("READY"), ACTIVE("ACTIVE");

    private final String value;

    Status(final String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
