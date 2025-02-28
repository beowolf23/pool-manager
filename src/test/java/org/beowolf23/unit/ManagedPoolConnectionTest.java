package org.beowolf23.unit;

import org.beowolf23.pool.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatObject;
import static org.mockito.Mockito.*;

class ManagedPoolConnectionTest {

    @Mock
    private Handler<Configuration, Connection> connectionHandler;

    @Mock
    private Configuration mockConfig;

    @Mock
    private Connection mockConnection;

    private ManagedConnectionPool<Configuration, Connection> pool;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ManagedConnectionObjectFactory<Configuration, Connection> factory =
                new ManagedConnectionObjectFactory<>(connectionHandler);
    }

    @Test
    void when_borrowingAConnectionFromThePool_then_returns_NoError() throws Exception {
        // Configure mocks
        when(connectionHandler.connect(mockConfig)).thenReturn(mockConnection);
        when(connectionHandler.isValid(mockConnection)).thenReturn(true);

        // Borrow object from pool
        Connection connection = pool.borrowObject(mockConfig);

        // Verify interactions
        assertThatObject(connection).isNotNull();

        verify(connectionHandler, times(1)).connect(mockConfig);
        verify(connectionHandler, times(1)).isValid(mockConnection);
    }

    @Test
    void when_returningAConnectionToThePool_then_noErrorsAndConnectionReturned() throws Exception {
        // Configure mocks
        when(connectionHandler.connect(mockConfig)).thenReturn(mockConnection);
        when(connectionHandler.isValid(mockConnection)).thenReturn(true);

        // Borrow and return object
        Connection connection = pool.borrowObject(mockConfig);
        pool.returnObject(mockConfig, connection);

        assertThat(pool.getNumIdle()).isEqualTo(1);

        // Verify the connection is returned to the pool
        verify(connectionHandler, times(1)).connect(mockConfig);
        verify(connectionHandler, times(2)).isValid(mockConnection);
    }

    @Test
    void when_invalidatingAConnection_then_connectionIsDisconnected() throws Exception {
        // Configure mocks
        when(connectionHandler.connect(mockConfig)).thenReturn(mockConnection);
        when(connectionHandler.isValid(mockConnection)).thenReturn(true);

        // Borrow and invalidate the object
        Connection connection = pool.borrowObject(mockConfig);
        pool.invalidateObject(mockConfig, connection);

        // Verify disconnect is called
        verify(connectionHandler, times(1)).disconnect(mockConnection);
    }
}
