package nrt.jetty.web;

import org.nutz.mvc.annotation.IocBy;
import org.nutz.mvc.annotation.Modules;
import org.nutz.mvc.annotation.Views;

@Modules(packages={"org.necros.web.modules", "nrt.jetty.web.modules"})
@IocBy(type=NrtJsonIocProvider.class, args={
	"NUTZ-MVC"
})
@Views({ClasspathJspViewMaker.class})
public class MainModule {

}
