package org.beowolf23.loadbalancer;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinLoadBalancer<T> implements LoadBalancer<T> {

    // If multiple threads call getNext() concurrently, AtomicInteger ensures that the currentIndex is updated correctly without race conditions.
    private static AtomicInteger currentIndex = new AtomicInteger(-1);

    @Override
    public T getNext() {
        List<T> nodes = LoadBalancerManager.<T>getInstance().getNodes();
        if (nodes.isEmpty()) {
            throw new IllegalStateException("No nodes available");
        }
        int index = currentIndex.updateAndGet(i -> (i + 1) % nodes.size());
        return nodes.get(index);
    }
}
