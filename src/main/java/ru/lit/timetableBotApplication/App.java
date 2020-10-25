package ru.lit.timetableBotApplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class App {


    public static void main(String[] args) {
        //Add this line to initialize bots context
        ApiContextInitializer.init();
        SpringApplication.run(App.class, args);
    }
}
