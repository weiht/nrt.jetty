package nrt.jetty.rs;

import nrt.jetty.web.NrtJsonIocProvider;

import org.nutz.mvc.annotation.IocBy;
import org.nutz.mvc.annotation.Modules;

@Modules(packages={"org.necros.rs.modules", "nrt.jetty.rs.modules"})
@IocBy(type=NrtJsonIocProvider.class, args={
	"NUTZ-MVC"
})
public class MainModule {

}
