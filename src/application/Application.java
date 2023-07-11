package application;

import java.util.ArrayList;
import java.util.List;

import datamodel.Customer;
/**
 * Runnable application class for the
 * <code>
 * <a href="{@docRoot}/index.html">{@value application.package_info#RootName}</a>.
 * </code>
 * 
 * @version <code style=color:green>{@value application.package_info#Version}</code>
 * @author <code style=color:blue>{@value application.package_info#Author}</code>
 */

public class Application {
	private final String appName;

	/**
	 * Public main() function.
	 * 
	 * @param args arguments passed from command line.
	 */
	public static void main(String[] args) {
		var appInstance = new Application();
		appInstance.run();	
	}	
	
	

	/**
	 * Private constructor to initialize local attributes.
	 */
	private Application() {
		this.appName = package_info.RootName + ": " + this.getClass().getSimpleName();
	}

	/**
	 * Private method that runs with application instance.
	 */
	private void run() {
		System.out.println(this.appName);
	}
}
