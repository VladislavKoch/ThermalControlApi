package ru.vladkochur.thermalControlApi.it.testcontainers;

import org.testcontainers.containers.PostgreSQLContainer;

public class MyTestContainer extends PostgreSQLContainer<MyTestContainer> {
    private static final String IMAGE_VERSION = "postgres:latest";
    private static MyTestContainer container;

    private MyTestContainer() {
        super(IMAGE_VERSION);
    }

    public static MyTestContainer getInstance() {
        if (container == null) {
            container = new MyTestContainer();
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("DB_URL", container.getJdbcUrl());
        System.setProperty("DB_USERNAME", container.getUsername());
        System.setProperty("DB_PASSWORD", container.getPassword());
    }

    @Override
    public void stop() {
    }

}
