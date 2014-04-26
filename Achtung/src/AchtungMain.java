import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletHandler;

public class AchtungMain {

	public static void main(String[] args) throws Exception {
		startWebServer(8881);
	}

	public static void startWebServer(int port) throws Exception {
		org.eclipse.jetty.server.Server server = new org.eclipse.jetty.server.Server(port);
		ResourceHandler resource_handler = new ResourceHandler();
		resource_handler.setDirectoriesListed(true);
		resource_handler.setResourceBase(".");
		ServletHandler servletHandler = new ServletHandler();
		servletHandler.addServletWithMapping(CommandServlet.class, "/command");
		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] { resource_handler, servletHandler });
		server.setHandler(handlers);
		server.start();
		server.join();
	}

}
