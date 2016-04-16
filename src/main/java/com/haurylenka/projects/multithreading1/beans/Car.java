package com.haurylenka.projects.multithreading1.beans;

import org.apache.log4j.Logger;

public class Car implements Runnable {

	private static final Logger LOG = Logger.getLogger(Car.class);
    private static final long MAX_DISTANCE = 10000;
    private long friction; 
    private long distance; 
    private String name;
    private Race race;
    
    public Car(String name, long friction, Race race) {
        this.name = name;
        this.friction = friction;
        this.race = race;
    }
    
    @Override
    public void run() {
        try {
        	//the car is ready to start
        	race.informReadiness(this);
            while (distance < MAX_DISTANCE) {
                Thread.sleep(friction);
                distance += 100;
                LOG.info(name + " " + distance);
            }
            //the car has crossed the finish line
            race.finished(name);
        } catch (InterruptedException e) {
        	//the car has been disqualified
        	Thread.currentThread().interrupt();
        	LOG.info(name + " is disqualified");
        }
    }
	
}
