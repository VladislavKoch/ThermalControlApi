package ru.vladkochur.thermalControlApi;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;

@ConfigurationPropertiesScan
@SpringBootApplication
public class ThermalControlRestApplication {
    public static void main(String[] args) {
        SpringApplication.run(ThermalControlRestApplication.class, args);
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

}
