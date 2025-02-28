package org.beowolf23.integration.loadbalancer;

import org.beowolf23.loadbalancer.LoadBalancer;
import org.beowolf23.loadbalancer.LoadBalancerManager;
import org.beowolf23.loadbalancer.RandomLoadBalancer;
import org.beowolf23.loadbalancer.RoundRobinLoadBalancer;
import org.beowolf23.ssh.SSHConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LoadBalancerTest {

    List<SSHConfiguration> servers = null;

    @BeforeEach
    void setUp() {

        servers = List.of(
                new SSHConfiguration("192.168.1.1", "22", "username", "password"),
                new SSHConfiguration("192.168.1.2", "22", "username", "password"),
                new SSHConfiguration("192.168.1.3", "22", "username", "password")
        );

        LoadBalancerManager<SSHConfiguration> manager = LoadBalancerManager.getInstance();
        manager.setNodes(servers);
    }

    @Test
    void when_serversAreLoadBalanced_then_roundRobinDistributesRequestsEqually() {

        LoadBalancer<SSHConfiguration> roundRobinBalancer0 = new RoundRobinLoadBalancer<>();
        SSHConfiguration server0 = roundRobinBalancer0.getNext();
        assertThat(server0.getHostname()).isEqualTo(servers.get(0).getHostname());
        System.out.println("server0 : " + server0.getHostname());

        LoadBalancer<SSHConfiguration> roundRobinBalancer1 = new RoundRobinLoadBalancer<>();
        SSHConfiguration server1 = roundRobinBalancer1.getNext();
        assertThat(server1.getHostname()).isEqualTo(servers.get(1).getHostname());
        System.out.println("server1 : " + server1.getHostname());

        LoadBalancer<SSHConfiguration> roundRobinBalancer2 = new RoundRobinLoadBalancer<>();
        SSHConfiguration server2 = roundRobinBalancer2.getNext();
        assertThat(server2.getHostname()).isEqualTo(servers.get(2).getHostname());
        System.out.println("server2 : " + server2.getHostname());


        LoadBalancer<SSHConfiguration> roundRobinBalancer3 = new RoundRobinLoadBalancer<>();
        SSHConfiguration server3 = roundRobinBalancer3.getNext();
        assertThat(server3.getHostname()).isEqualTo(servers.get(0).getHostname());
        System.out.println("server3 : " + server3.getHostname());

        LoadBalancer<SSHConfiguration> roundRobinBalancer4 = new RoundRobinLoadBalancer<>();
        SSHConfiguration server4 = roundRobinBalancer4.getNext();
        assertThat(server4.getHostname()).isEqualTo(servers.get(1).getHostname());
        System.out.println("server4 : " + server4.getHostname());

    }

    @Test
    void when_serversAreLoadBalanced_then_randomDistributesRequestsUnpredictably() {

        LoadBalancer<SSHConfiguration> randomBalancer = new RandomLoadBalancer<>();
        for (int i = 0; i < 10; i++) {
            SSHConfiguration server = randomBalancer.getNext();
            System.out.println("Selected server: " + server.getHostname());
        }
    }
}
