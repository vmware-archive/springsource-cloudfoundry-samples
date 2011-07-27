package org.wgrus;

import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

public class OrderQueue {

	private final AtomicLong count = new AtomicLong();

	private Queue<String> queue = new LinkedBlockingQueue<String>(20);

	public synchronized void add(String order) {
		if (queue.size() >= 25) {
			queue.remove();
		}
		count.incrementAndGet();
		queue.add(order);
	}

	public long count() {
		return count.get();
	}

	public List<?> list() {
		return Arrays.asList(this.queue.toArray());
	}

}
