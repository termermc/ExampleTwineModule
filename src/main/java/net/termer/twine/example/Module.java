package net.termer.twine.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.termer.twine.Twine;
import net.termer.twine.modules.TwineModule;

// Static import so we can use ServerManager methods without typing ServerManager.<method name>
import static net.termer.twine.ServerManager.*;

public class Module implements TwineModule {
	// Module logger
	// Using a logger like this is preferred over System.out, as it outputs more specified messages on who's sending the message 
	private final Logger logger = LoggerFactory.getLogger(Module.class);
	
	// This method is called when the module is run; it's essentially the main method for your module
	public void initialize() {
		logger.info("Setting up routes...");
		
		// Simple route at /hello
		// "*" specifies this will be applied for any domain
		get("/hello", "*", r -> {
			// Writes the response "Hello world" and ends the response
			r.response().end("Hello world");
		});
		
		// More complicated route with a route parameter
		// Visiting /hello/John will output "Hello John"
		get("/hello/:name", "*", r -> {
			// Writes a response using the route param
			r.response().end("Hello "+r.pathParam("name"));
		});
		
		// Route demonstrating query parameters
		get("/product", "*", r -> {
			// Check if "name" query param is present
			if(r.request().getParam("name") == null) {
				// Did not include the parameter
				r.response().end("Please specify a product name (apple, banana, orange) with the \"name\" query parameter.");
			} else {
				String param = r.request().getParam("name");
				
				// Display a different response based on name
				switch(param) {
				case "apple":
					r.response().end("Apple!");
					break;
				case "banana":
					r.response().end("Banana!!");
					break;
				case "orange":
					r.response().end("Orange!!!");
					break;
				default:
					r.response().end("\""+param+"\" is not a product. (Specify apple, banana, or orange).");
				}
			}
		});
		
		// Simple route that shuts down the server (Never use this for a real module, haha!)
		get("/bye", "*", r -> {
			r.response().end("BYE!");
			Twine.shutdown();
		});
		
		/* Example of a POST handler */
		// Serve basic HTML form
		get("/form", "*", r -> {
			// Set content type
			r.response().putHeader("Content-Type", "text/html");
			
			// Respond with a basic HTML form
			r.response().end(
				"<form method=\"POST\">\n" + 
				"    <p>Enter a message:</p>\n" + 
				"    <input type=\"text\" name=\"message\"/>\n" + 
				"    <br>\n" + 
				"    <input type=\"submit\" value=\"Send\"/>\n" + 
				"</form>"
			);
		});
		// Now handle a POST to /form
		post("/form", "*", r -> {
			// Always check if the message is present
			if(r.request().getParam("message") == null) {
				// Message is not present
				r.response().end("You didn't send any message");
			} else {
				// Respond with message
				r.response().end("You said: "+r.request().getParam("message"));
			}
		});
		
		/* Handling all requests to a domain */
		// You can use the `handler` method to handle all request, regardless of method.
		// `handler` can also handle all paths if you leave out the path in the method call.
		// This makes it well suited to serving a 404 page, or something like that.
		// Handlers are called in the order they're registered, for all catch-all handlers should be declared last.
		handler("*", r -> {
			// Set the status to 404
			r.response().setStatusCode(404);
			
			// Send not found message
			r.response().end("404, not found... :((((");
		});
		
		// Since Twine uses Eclipse Vert.x, you are free to use any Vert.x feature you want within Twine.
		// For more on using Vert.x, you definitely should go read the documentation on their website (https:/vertx.io/)
	}
	
	// Returns the module name
	public String name() {
		return "ExampleTwineModule";
	}
	
	// The priority for the module to be loaded
	public Priority priority() {
		return Priority.MEDIUM;
	}
	
	// Method called on server shutdown
	public void shutdown() {
		logger.info("Shutting down ExampleTwineModule...");
	}
	
	// The Twine version this module was built for
	public String twineVersion() {
		// Compatible with 1.0 and any version after it
		return "1.0+";
	}
}