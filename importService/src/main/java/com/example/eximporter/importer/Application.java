package com.example.eximporter.importer;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableBatchProcessing
@EnableScheduling
@ImportResource({"classpath:job/omnImportBatch.xml"})
public class Application
{
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
