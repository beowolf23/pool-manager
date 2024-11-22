package org.beowolf23.unit;

import org.beowolf23.pool.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ManagedPoolConnectionTest {

    @Mock
    private ConnectionHandler<ConnectionConfiguration, ManagedConnection> connectionHandler;

    @Mock
    private ConnectionConfiguration mockConfig;

    @Mock
    private ManagedConnection mockConnection;

    private ManagedConnectionPool<ConnectionConfiguration, ManagedConnection> pool;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ManagedConnectionObjectFactory<ConnectionConfiguration, ManagedConnection> factory =
                new ManagedConnectionObjectFactory<>(connectionHandler);
        pool = new ManagedConnectionPool<>(factory);
    }

    @Test
    void when_borrowingAConnectionFromThePool_then_returns_NoError() throws Exception {

        // Configure mocks
        when(connectionHandler.connect(mockConfig)).thenReturn(mockConnection);
        when(connectionHandler.isValid(mockConnection)).thenReturn(true);

        // Borrow object from pool
        ManagedConnection connection = pool.borrowObject(mockConfig);

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
        ManagedConnection connection = pool.borrowObject(mockConfig);
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
        ManagedConnection connection = pool.borrowObject(mockConfig);
        pool.invalidateObject(mockConfig, connection);

        // Verify disconnect is called
        verify(connectionHandler, times(1)).disconnect(mockConnection);
    }
}
