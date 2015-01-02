package nrt.jetty;

import java.net.InetSocketAddress;
import java.util.EnumSet;

import javax.servlet.DispatcherType;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * 运行Jetty服务。
 * @author weiht
 *
 */
public class ServerRunner implements Runnable {
	private static final String DEFAULT_BIND_ADDRESS = "127.0.0.1";
	private static final short DEFAULT_BIND_PORT = 5432;
	
	private String bindAddress = DEFAULT_BIND_ADDRESS;
	private short bindPort = DEFAULT_BIND_PORT;
	private Server server;
	private FilterHolder[] filters;
	private ServletHolder[] servlets;

	private Runnable stopper = new Runnable() {
		public void run() {
			doStop();
		}
	};

	public void run() {
		try {
			doStart();
		} catch (InterruptedException e) {
			doStop();
		} catch (Exception e) {
			doStop();
		}
	}

	private synchronized void doStop() {
		if (server != null) {
			server = null;
			try {
				server.stop();
			} catch (Exception e) {
			}
		}
	}

	private void doStart() throws Exception, InterruptedException {
		synchronized(this) {
			if (server != null) return;
		}
		createServer();
		server.join();
	}

	private void createServer() throws Exception {
		InetSocketAddress addr = new InetSocketAddress(bindAddress, bindPort);
		Server svr = new Server(addr);
		ServletContextHandler handler = new ServletContextHandler();
		addHolders(handler);handler.setClassLoader(getClass().getClassLoader());
		svr.setHandler(handler);
		svr.start();
		server = svr;
	}

	private void addHolders(ServletContextHandler handler) {
		EnumSet<DispatcherType> dispatches = EnumSet.allOf(DispatcherType.class);
		if (filters != null && filters.length > 0) {
			for (FilterHolder f: filters) {
				String up = f.getInitParameter("urlPattern");
				if (up == null || (up = up.trim()).isEmpty()) {
					handler.addFilter(f, "/" +  f.getName() + "/*", dispatches);
				} else {
					for (String p: up.split("[\\n,\\,\\;]")) {
						if (!(p = p.trim()).isEmpty()) {
							handler.addFilter(f, p, dispatches);
						}
					}
				}
			}
		}
		if (servlets != null && servlets.length > 0) {
			for (ServletHolder s: servlets) {
				String up = s.getInitParameter("urlPattern");
				if (up == null || (up = up.trim()).isEmpty()) {
					handler.addServlet(s, "/" +  s.getName() + "/*");
				} else {
					for (String p: up.split("[\\n,\\,\\;]")) {
						if (!(p = p.trim()).isEmpty()) {
							handler.addServlet(s, p);
						}
					}
				}
			}
		}
	}

	public void setBindAddress(String bindAddress) {
		this.bindAddress = bindAddress;
	}

	public void setBindPort(short bindPort) {
		this.bindPort = bindPort;
	}

	public void setFilters(FilterHolder[] filters) {
		this.filters = filters;
	}

	public void setServlets(ServletHolder[] servlets) {
		this.servlets = servlets;
	}

	public Runnable getStopper() {
		return stopper;
	}
}
