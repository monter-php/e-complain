package tgi.ecomplain.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Slf4j
@SpringBootApplication(scanBasePackages = "tgi.ecomplain")
@EnableJpaRepositories(basePackages = "tgi.ecomplain.infrastructure")
@EntityScan(basePackages = "tgi.ecomplain.infrastructure")
public class EcomplainApplication {

	public static void main(String[] args) {
		SpringApplication.run(EcomplainApplication.class, args);
	}

}
