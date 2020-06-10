package com.example.schemabuilder;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SchemabuilderApplication {

	public static void main(String[] args) throws  Exception {
		SpringApplication.run(SchemabuilderApplication.class, args);

		ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

		Metadata metadata = context.getBean(Metadata.class);
		metadata.DisplayMetaData();

	}
}


