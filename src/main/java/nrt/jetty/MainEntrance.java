package nrt.jetty;

import nrt.jetty.web.NrtJsonIocProvider;

public class MainEntrance {
	private static final String CMD_ARG_RUNNER_ID = "-runner-id";
	private static final String DEFAULT_SERVER_RUNNER_ID = "serverRunner";
	private static final String KEY_SERVER_RUNNER_ID = "server.runner.id";

	public static void main(String[] args) {
		ServerRunner runner = NrtJsonIocProvider.nutzIoc().get(ServerRunner.class, guessRunnerId(args));
		runner.run();
	}
	
	private ServerRunner serverRunner;
	
	public void init(String[] args) {
		String runnerId = guessRunnerId(args);
		serverRunner = NrtJsonIocProvider.nutzIoc().get(null, runnerId);
	}

	private static String guessRunnerId(String[] args) {
		String runnerId = null;
		for (int i = args.length - 2; i >= 0; i --) {
			if (args[i].equals(CMD_ARG_RUNNER_ID)) {
				runnerId = args[i + 1];
			}
		}
		if (runnerId == null || runnerId.isEmpty()) {
			runnerId = System.getProperty(KEY_SERVER_RUNNER_ID);
		}
		if (runnerId == null || runnerId.isEmpty()) {
			runnerId = System.getenv(KEY_SERVER_RUNNER_ID);
		}
		if (runnerId == null || runnerId.isEmpty()) {
			runnerId = DEFAULT_SERVER_RUNNER_ID;
		}
		return runnerId;
	}

	public void start() {
		if (serverRunner != null) {
			serverRunner.run();
		}
	}

	public void stop() {
		if (serverRunner != null) {
			serverRunner.getStopper().run();
		}
	}

	public void destroy() {
		NrtJsonIocProvider.nutzIoc().depose();
	}
}
