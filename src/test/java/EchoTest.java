import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.ExecutionException;

class EchoTest {
    @Test
    void connect() throws Exception {    	
    	Echo.connect("https://postman-echo.com/post", "hi there", "POST");
    }
    
    @Test
    void catchExceptionOnAWrongUrl() throws Exception {
    	assertThrows(IllegalArgumentException.class, () -> {
    		Echo.connect("http://", "hi there", "POST");
    	});
    }
    
    @Test
    void catchExceptionOnASemiValidUrl() throws Exception {
    	// I think I don't know how to catch or throw the ConnectException
    	assertThrows(ExecutionException.class, () -> {
    		Echo.connect("http://seemslikea.normalurl", "hi there", "POST");
    	});
    }
    
    @Test
    void catchExceptionOnWrongMethod() throws Exception {
    	assertThrows(Exception.class, () -> {
    		Echo.connect("https://postman-echo.com/post", "hi there", "PARTCH");
    	});
    }
}