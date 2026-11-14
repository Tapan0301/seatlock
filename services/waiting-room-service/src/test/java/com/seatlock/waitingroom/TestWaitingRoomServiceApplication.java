package com.seatlock.waitingroom;

import org.springframework.boot.SpringApplication;

public class TestWaitingRoomServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(WaitingRoomServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
