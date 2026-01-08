package dev.gamified.GamifiedPlatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GamifiedPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(GamifiedPlatformApplication.class, args);
	}

}
