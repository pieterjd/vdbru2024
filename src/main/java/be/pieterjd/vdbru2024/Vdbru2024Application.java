package be.pieterjd.vdbru2024;

import be.pieterjd.vdbru2024.todo.TodoClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Vdbru2024Application {

	public static void main(String[] args) {
		SpringApplication.run(Vdbru2024Application.class, args);
	}

}
