var ioc = {
	"serverRunner": {
		"type": "nrt.jetty.ServerRunner",
		"fields": {
			"bindAddress": "0.0.0.0",
			"bindPort": 1920,
			"filters": [{
				"refer": "nutzFilter"
			}]
		}
	},
	"nutzFilter": {
		"type": "org.eclipse.jetty.servlet.FilterHolder",
		"fields": {
			"name": "nutz",
			"filter": {"type": "org.nutz.mvc.NutFilter"},
			"initParameters": {
				"urlPattern": "*.html"
				"modules": "nrt.jetty.web.MainModule"
			}
		}
	}
};