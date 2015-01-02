package nrt.jetty.web;

import org.nutz.ioc.Ioc;
import org.nutz.mvc.View;
import org.nutz.mvc.ViewMaker;

public class VelocityViewMaker
implements ViewMaker {
	public static final String TYPE_NAME = "vm";
	private static VelocityView __def_view;
	
	private static VelocityView defaultView() {
		if (__def_view == null) {
			__def_view = new VelocityView();
		}
		return __def_view;
	}
	
	public View make(Ioc ioc, String type, String value) {
		if (TYPE_NAME.equalsIgnoreCase(type)) {
			VelocityView view = ioc.get(VelocityView.class);
			if (view == null) {
				view = defaultView();
			}
			return view.serve(value);
		}
		return null;
	}
}
