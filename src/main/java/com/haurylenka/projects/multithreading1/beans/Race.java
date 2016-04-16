package com.haurylenka.projects.multithreading1.beans;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

import org.apache.log4j.Logger;

import com.haurylenka.projects.multithreading1.factories.CarFactory;

public class Race {

	private static final Logger LOG = Logger.getLogger(Race.class);
	private static final int COMPETITORS = 3;
	private static final long DISQUALIFICATION_TIMEOUT = 5 * 1000L;
	private static final double DISQUALIFICATION_RATE = 0.1;
	private List<Car> participants;
	private final CyclicBarrier barrier;
	private final CountDownLatch latch;
	private boolean isReady = true;
	private String winner;
	
	public Race() {
		this.latch = new CountDownLatch(1);
		this.participants = CarFactory.getParticipants(this, COMPETITORS);
		this.barrier = new CyclicBarrier(COMPETITORS, new Runnable() {
			 /* The thread that checks, weather all race participants
			 * are ready to start, and fires the start.*/
			@Override
			public void run() {
				try {
					if (isReady) {
						LOG.info("READY!");
						Thread.sleep(1000);
						LOG.info("STEADY!");
						Thread.sleep(1000);
						LOG.info("GO!");
						latch.countDown();
					} else {
						LOG.info("The race is cancelled");
					}
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		});
	}
	
	public void start() {
		try {
			List<Thread> threads = new ArrayList<>();
			//getting all the cars to the start line
			for (Car car : participants) {
				Thread thread = new Thread(car);
				threads.add(thread);
				thread.start();
			}
			//waiting for the race to start
			latch.await();
			//checking the race for cancellation
			if (isReady) {
				//disqualifying some cars after the timeout expires
				Thread.sleep(DISQUALIFICATION_TIMEOUT);
				doDisqualification(threads);
				/* waiting for all car threads to end before 
				 * announcing the winner*/
				for (Thread t : threads) {
					t.join();
				}
				LOG.info("Winner is " + winner);
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
	
	/**
	 * Disqualifies some of the cars
	 */
	private static void doDisqualification(List<Thread> threads) {
		double goal = threads.size() * DISQUALIFICATION_RATE;
		int disqualified = 0;
		Set<Integer> idxUsed = new HashSet<>();
		Random r = new Random();
		while (disqualified < goal) {
			int idx = r.nextInt(threads.size());
			while (!idxUsed.add(idx)) {
				idx = r.nextInt(threads.size());
			}
			Thread t = threads.get(idx);
			t.interrupt();
			disqualified++;
		}
	}
	
	/**
	 * Informs about the car readiness to start the race.
	 */
	public void informReadiness(Car car) {
		try {
			barrier.await();
		} catch (InterruptedException | BrokenBarrierException e) {
			isReady = false;
		}
	}
	
	/**
	 * Informs about the car crossing the finish line.
	 */
	public synchronized void finished(String car) {
		if (winner == null) {
			winner = car;
		}
	}
	
	
}
