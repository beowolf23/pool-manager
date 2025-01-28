package org.beowolf23.integration.loadbalancer;

import org.beowolf23.loadbalancer.LoadBalancer;
import org.beowolf23.loadbalancer.LoadBalancerManager;
import org.beowolf23.loadbalancer.RandomLoadBalancer;
import org.beowolf23.loadbalancer.RoundRobinLoadBalancer;
import org.beowolf23.ssh.SSHJConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class LoadBalancerTest {

    List<SSHJConfiguration> servers = null;

    @BeforeEach
    void setUp() {

        servers = List.of(
                new SSHJConfiguration("192.168.1.1", "22", "username", "password"),
                new SSHJConfiguration("192.168.1.2", "22", "username", "password"),
                new SSHJConfiguration("192.168.1.3", "22", "username", "password")
                );

        LoadBalancerManager<SSHJConfiguration> manager = LoadBalancerManager.getInstance();
        manager.setNodes(servers);
    }

    @Test
    void when_serversAreLoadBalanced_then_roundRobinDistributesRequestsEqually() {

        LoadBalancer<SSHJConfiguration> roundRobinBalancer0 = new RoundRobinLoadBalancer<>();
        SSHJConfiguration server0 = roundRobinBalancer0.getNext();
        assertThat(server0.getHostname()).isEqualTo(servers.get(0).getHostname());
        System.out.println("server0 : " + server0.getHostname());

        LoadBalancer<SSHJConfiguration> roundRobinBalancer1 = new RoundRobinLoadBalancer<>();
        SSHJConfiguration server1 = roundRobinBalancer1.getNext();
        assertThat(server1.getHostname()).isEqualTo(servers.get(1).getHostname());
        System.out.println("server1 : " + server1.getHostname());

        LoadBalancer<SSHJConfiguration> roundRobinBalancer2 = new RoundRobinLoadBalancer<>();
        SSHJConfiguration server2 = roundRobinBalancer2.getNext();
        assertThat(server2.getHostname()).isEqualTo(servers.get(2).getHostname());
        System.out.println("server2 : " + server2.getHostname());


        LoadBalancer<SSHJConfiguration> roundRobinBalancer3 = new RoundRobinLoadBalancer<>();
        SSHJConfiguration server3 = roundRobinBalancer3.getNext();
        assertThat(server3.getHostname()).isEqualTo(servers.get(0).getHostname());
        System.out.println("server3 : " + server3.getHostname());

        LoadBalancer<SSHJConfiguration> roundRobinBalancer4 = new RoundRobinLoadBalancer<>();
        SSHJConfiguration server4 = roundRobinBalancer4.getNext();
        assertThat(server4.getHostname()).isEqualTo(servers.get(1).getHostname());
        System.out.println("server4 : " + server4.getHostname());

    }

    @Test
    void when_serversAreLoadBalanced_then_randomDistributesRequestsUnpredictably() {

        LoadBalancer<SSHJConfiguration> randomBalancer = new RandomLoadBalancer<>();
        for (int i = 0; i < 10; i++) {
            SSHJConfiguration server = randomBalancer.getNext();
            System.out.println("Selected server: " + server.getHostname());
        }
    }
}
