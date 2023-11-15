package ru.ermolaev.services.subscriber.manager.constant;

public enum NotificationTemplate {
    NEGATIVE_BALANCE("NEGATIVE_BALANCE", "balance.negative"),
    CONNECTION_INFO("CONNECTION_INFO", "info.connection");

    private final String name;

    private final String code;

    NotificationTemplate(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public String getCode() {
        return this.code;
    }

}
