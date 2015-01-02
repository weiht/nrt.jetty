package nrt.jetty.web.modules;

import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

public class HomeModule {
	@At("/index")
	@Ok("nsp:/index")
	public String index() {
		return "world";
	}
}
