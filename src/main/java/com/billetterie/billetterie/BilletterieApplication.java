package com.billetterie.billetterie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Point d'entrée de l'application Billetterie.
 */
@SpringBootApplication
@EnableScheduling
public class BilletterieApplication {

	/**
	 * Lance l'application Spring Boot.
	 *
	 * @param args les arguments de la ligne de commande
	 */
	public static void main(String[] args) {
		SpringApplication.run(BilletterieApplication.class, args);
	}

}
