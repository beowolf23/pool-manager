package org.beowolf23.loadbalancer;

import java.util.Collections;
import java.util.List;

public class LoadBalancerManager<T> {

    private static LoadBalancerManager<?> instance;
    private List<T> nodes;

    private LoadBalancerManager() {
    }

    public static <T> LoadBalancerManager<T> getInstance() {
        if (instance == null) {
            synchronized (LoadBalancerManager.class) {
                if (instance == null) {
                    instance = new LoadBalancerManager<>();
                }
            }
        }
        return (LoadBalancerManager<T>) instance;
    }

    public void setNodes(List<T> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            throw new IllegalArgumentException("Node list cannot be null or empty");
        }
        this.nodes = Collections.unmodifiableList(nodes);
    }

    public List<T> getNodes() {
        if (nodes == null) {
            throw new IllegalStateException("Nodes have not been initialized");
        }
        return nodes;
    }
}
