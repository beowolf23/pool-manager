package org.beowolf23.loadbalancer;

import java.util.List;
import java.util.Random;

public class RandomLoadBalancer<T> implements LoadBalancer<T> {

    private static Random random = new Random();

    @Override
    public T getNext() {
        List<T> nodes = LoadBalancerManager.<T>getInstance().getNodes();
        if (nodes.isEmpty()) {
            throw new IllegalStateException("No nodes available");
        }
        int index = random.nextInt(nodes.size());
        return nodes.get(index);
    }
}
