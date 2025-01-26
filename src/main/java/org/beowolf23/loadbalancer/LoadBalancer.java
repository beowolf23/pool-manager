package org.beowolf23.loadbalancer;

public interface LoadBalancer<T> {
    T getNext();
}


