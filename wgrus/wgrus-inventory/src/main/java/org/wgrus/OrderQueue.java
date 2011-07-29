package org.wgrus;

import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

public class OrderQueue {

	private static final int CAPACITY = 25;

	private final AtomicLong count = new AtomicLong();

	private Queue<String> queue = new LinkedBlockingQueue<String>(CAPACITY);

	public synchronized void add(String order) {
		if (queue.size() >= CAPACITY) {
			queue.remove();
		}
		count.incrementAndGet();
		queue.add(order);
	}

	public long count() {
		return count.get();
	}

	public synchronized List<?> list() {
		return Arrays.asList(this.queue.toArray());
	}

}
