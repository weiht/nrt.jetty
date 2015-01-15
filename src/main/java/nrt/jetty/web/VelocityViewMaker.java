package nrt.jetty.web;

import org.nutz.ioc.Ioc;
import org.nutz.mvc.View;
import org.nutz.mvc.ViewMaker;

public class VelocityViewMaker
implements ViewMaker {
	public static final String TYPE_NAME = "vm";
	
	public View make(Ioc ioc, String type, String value) {
		if (TYPE_NAME.equalsIgnoreCase(type)) {
			VelocityConfig config = ioc.get(VelocityConfig.class);
			return VelocityView.serve(config, value);
		}
		return null;
	}
}
