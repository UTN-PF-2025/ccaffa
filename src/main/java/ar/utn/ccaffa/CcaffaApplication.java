package ar.utn.ccaffa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class CcaffaApplication {

	public static void main(String[] args) {
		SpringApplication.run(CcaffaApplication.class, args);
	}

}
