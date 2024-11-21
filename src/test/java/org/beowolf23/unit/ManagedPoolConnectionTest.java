package org.beowolf23.unit;

import org.beowolf23.pool.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Collections;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ManagedPoolConnectionTest {

//    @Mock
//    private ConnectionHandler<ConnectionConfiguration, ManagedConnection> connectionHandler;
//
//    @Mock
//    private ConnectionConfiguration mockConfig;
//
//    @Mock
//    private ManagedConnection mockConnection;
//
//    private ManagedConnectionPool<ConnectionConfiguration, ManagedConnection> pool;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        ManagedConnectionObjectFactory<ConnectionConfiguration, ManagedConnection> factory =
//                new ManagedConnectionObjectFactory<>(connectionHandler);
//        pool = new ManagedConnectionPool<>(factory);
//    }
//
//    @Test
//    void testBorrowObjectSuccessful() throws Exception {
//        // Configure mocks
//        when(connectionHandler.connect(mockConfig)).thenReturn(mockConnection);
//        when(connectionHandler.isValid(mockConnection)).thenReturn(true);
//
//        // Borrow object from pool
//        ManagedConnection connection = pool.borrowObject(mockConfig);
//
//        // Verify interactions
//        assertNotNull(connection);
//        verify(connectionHandler, times(1)).connect(mockConfig);
//        verify(connectionHandler, times(1)).isValid(mockConnection);
//    }
//
//    @Test
//    void testBorrowObjectWithExecution() throws Exception {
//        // Configure mocks
//        when(connectionHandler.connect(mockConfig)).thenReturn(mockConnection);
//        when(connectionHandler.isValid(mockConnection)).thenReturn(true);
//        GenericResponse<ManagedConnection> mockResponse = new GenericResponse<>();
//        mockResponse.setCode(0);
//        mockResponse.setStdout(Collections.singletonList("Mock command output"));
//
//        // Define the command to execute
//        Supplier<String> mockCommand = () -> "mock command";
//        when(connectionHandler.executeCommand(mockConnection, mockCommand)).thenReturn(mockResponse);
//
//        // Borrow object and execute a command
//        ManagedConnection connection = pool.borrowObject(mockConfig);
//        GenericResponse<ManagedConnection> response = connectionHandler.executeCommand(connection, mockCommand);
//
//        // Verify results
//        assertNotNull(response);
//        assertEquals(0, response.getCode());
//        assertEquals("Mock command output", response.getStdout().get(0));
//
//        // Verify interactions
//        verify(connectionHandler, times(1)).connect(mockConfig);
//        verify(connectionHandler, times(1)).isValid(mockConnection);
//        verify(connectionHandler, times(1)).executeCommand(mockConnection, mockCommand);
//    }
//
//    @Test
//    void testReturnObject() throws Exception {
//        // Configure mocks
//        when(connectionHandler.connect(mockConfig)).thenReturn(mockConnection);
//        when(connectionHandler.isValid(mockConnection)).thenReturn(true);
//
//        // Borrow and return object
//        ManagedConnection connection = pool.borrowObject(mockConfig);
//        pool.returnObject(mockConfig, connection);
//
//        // Verify the connection is returned to the pool
//        verify(connectionHandler, times(1)).connect(mockConfig);
//        verify(connectionHandler, times(2)).isValid(mockConnection);
//    }
//
//    @Test
//    void testInvalidateObject() throws Exception {
//        // Configure mocks
//        when(connectionHandler.connect(mockConfig)).thenReturn(mockConnection);
//        when(connectionHandler.isValid(mockConnection)).thenReturn(true);
//
//
//        // Borrow and invalidate the object
//        ManagedConnection connection = pool.borrowObject(mockConfig);
//        pool.invalidateObject(mockConfig, connection);
//
//        // Verify disconnect is called
//        verify(connectionHandler, times(1)).disconnect(mockConnection);
//    }
}
