import java.net.URI;
import java.net.http.HttpRequest;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Echo {

    static Logger log;
    public static final Set<String> AllowedMethods = Set.of("POST", "GET", "PUT", "PATCH", "DELETE", "HEAD", "CONNECT", "OPTIONS", "TRACE");
    public static void main(String[] args) throws Exception{
        Scanner in = new Scanner(System.in);

        System.out.println("Enter URL ");
        String url = in.nextLine();
        System.out.println("Enter message to be echoed ");
        String msg = in.nextLine();
        System.out.println("Enter Method (POST, GET, PUT, PATCH, DELETE, HEAD, CONNECT, OPTIONS, TRACE) [POST]");
        String method = in.nextLine();
        if (method == "") {
        	method = "POST";
        }

        in.close();
        connect(url, msg, method);
    }
    
    /**
     * Checks the method given by the user is one of the methods listed on the screen
     * @param  method A HTTP method
     * @return Whether the method is valid or not
     */
    static boolean checkMethod(String method) {
        if (!AllowedMethods.contains(method.toUpperCase())) {  // Check for allowed HTTP methods
        	log.severe("Unrecognised method " + method);
        	return false;
        }
        return true;
    }
    
    /**
     * Helper function to build a request given a url, a message and the method
     * @param url    Url to where the request will go
     * @param msg    A text given by the user
     * @param method A valid HTTP method for the request
     * @return A built request with the given Url, message and method
     */
    static HttpRequest buildRequest(String url, String msg, String method) {
        log.info("Building " + method + " request");
        var request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "text/plain")
                .method(method.toUpperCase(), HttpRequest.BodyPublishers.ofString(msg)) // Set method given by user
                .build();
    	return request;
    }


    static void connect(String url,String msg, String method) throws Exception{
        log = Logger.getLogger("com.variacode.echo");
        log.setLevel(Level.ALL);

        log.info("Validating Method");
        if (!checkMethod(method)) {
        	throw new Exception("Method not allowed");
        }
        log.info(method + " is valid");
        try {
        	var request = buildRequest(url, msg, method);
	
	        ExecutorService executor = Executors.newSingleThreadExecutor();
	        var client = HttpClient.newBuilder().executor(executor).build();

	        log.info("Connecting to " + url);
	        var responseFuture = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
	
	        responseFuture.thenApply(res -> {
	        	var statusCode = res.statusCode();
	            log.info("StatusCode: "+ statusCode);
	            if (statusCode != 200 && statusCode != 201) { // Check other response other than 200 or 201
	            	log.severe("Wrong connection to " + url);
	            }
	            return res;
	        })
	                .thenApply(HttpResponse::body)
	                .thenAccept(log::info)
	                .get();
	        log.info("test");
	        responseFuture.join();
	    	executor.shutdownNow();
        } catch (IllegalArgumentException ex) { // catch IllegalArgumentException thrown by the HttpClient for wrong urls
        	log.severe("Illegal url: " + url);
        	throw ex;
        } catch (NullPointerException ex) { // catch NullPointerException thrown by the HttpClient for null urls
        	log.severe("Null url");
        	throw ex;
        } catch (Exception ex) { // catch any other exception that the server could throw
        	log.severe("Connection error to " + url);
        	throw ex;
        }
    }
}
