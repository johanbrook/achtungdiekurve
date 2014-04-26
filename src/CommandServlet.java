

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.Player;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketFactory;

public class CommandServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9198308277858976063L;
	private WebSocketFactory _wsFactory;

	/* ------------------------------------------------------------ */
	/**
	 * Initialise the servlet by creating the WebSocketFactory.
	 */
	@Override
	public void init() throws ServletException {

		// Create and configure WS factory
		_wsFactory = new WebSocketFactory(new WebSocketFactory.Acceptor() {
			public boolean checkOrigin(HttpServletRequest request, String origin) {
				// Allow all origins
				return true;
			}

			public WebSocket doWebSocketConnect(HttpServletRequest request, String protocol) {
				return new Player();
			}
		});
		_wsFactory.setBufferSize(4096);
		_wsFactory.setMaxIdleTime(Integer.MAX_VALUE);
	}

	/* ------------------------------------------------------------ */
	/**
	 * Handle the handshake GET request.
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// If the WebSocket factory accepts the connection, then return
		if (_wsFactory.acceptWebSocket(request, response))
			return;
		// Otherwise send an HTTP error.
		response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Websocket only");
	}

}
