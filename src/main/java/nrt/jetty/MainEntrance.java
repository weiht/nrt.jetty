package nrt.jetty;

import nrt.jetty.web.NrtJsonIocProvider;

public class MainEntrance {
	public static void main(String[] args) {
		ServerRunner runner = NrtJsonIocProvider.nutzIoc().get(ServerRunner.class, "serverRunner");
		runner.run();
	}
}
