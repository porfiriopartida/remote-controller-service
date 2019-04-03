package com.porfiriopartida.remotecontroller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@ComponentScan({"com.porfiriopartida", "com.porfiriopartida.remotecontroller.screen.config"})
@SpringBootApplication
public class TestRemoteControllerApplication {
	public static void main(String[] args) {
		System.setProperty("java.awt.headless", "true");
		SpringApplication.run(TestRemoteControllerApplication.class, args);
	}
}
