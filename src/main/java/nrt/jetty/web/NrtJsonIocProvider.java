package nrt.jetty.web;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.Ioc2;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.loader.json.JsonLoader;
import org.nutz.mvc.IocProvider;
import org.nutz.mvc.NutConfig;

public class NrtJsonIocProvider
implements IocProvider {
	private static final Ioc2 rootIoc = new NutIoc(new JsonLoader("NUTZ-IOC/"));

	public Ioc create(NutConfig config, String[] args) {
		return new NutIoc(new JsonLoader(args), rootIoc.getIocContext(), "app");
	}
	
	public static Ioc2 nutzIoc() {
		return rootIoc;
	}
}
