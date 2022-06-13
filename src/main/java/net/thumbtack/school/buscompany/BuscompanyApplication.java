package net.thumbtack.school.buscompany;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableWebMvc
public class BuscompanyApplication {

	public static void main(String[] args) {
		SpringApplication.run(BuscompanyApplication.class, args);
	}

}
