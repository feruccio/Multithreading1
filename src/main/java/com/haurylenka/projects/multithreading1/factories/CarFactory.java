package com.haurylenka.projects.multithreading1.factories;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.haurylenka.projects.multithreading1.beans.Car;
import com.haurylenka.projects.multithreading1.beans.Race;

public class CarFactory {
	
	private static final int MIN_FRICTION = 100;
	private static final int MAX_FRICTION = 150;

	private CarFactory() {}
	
	public static List<Car> getParticipants(Race race, int quantity) {
		List<Car> participants = new ArrayList<>();
		Random r = new Random();
		for (int i = 0; i < quantity; i++) {
			long friction = r.nextInt(MAX_FRICTION - MIN_FRICTION + 1)
									+ MIN_FRICTION;
			participants.add(new Car("car" + (i + 1), friction, race));
		}
		return participants;
	}
	
}
