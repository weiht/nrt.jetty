var ioc = {
	"serverRunner": {
		"type": "nrt.jetty.ServerRunner",
		"fields": {
			"bindAddress": "0.0.0.0",
			"bindPort": 1920,
			"filterList": {"refer": "filterList"},
			"servletList": {"refer": "servletList"}
		}
	},
	"nutzFilter": {
		"type": "org.eclipse.jetty.servlet.FilterHolder",
		"fields": {
			"name": "nutz",
			"filter": {"type": "org.nutz.mvc.NutFilter"},
			"initParameters": {
				"urlPattern": "*.html",
				"modules": "nrt.jetty.web.MainModule"
			}
		}
	},
	"defaultFilter": {
		"type": "org.eclipse.jetty.servlet.FilterHolder",
		"fields": {
			"name": "defaultFilter",
			"filter": {"type": "nrt.jetty.web.DefaultFilter"},
			"initParameters": {
				"urlPattern": "/*"
			}
		}
	},
	"staticServlet": {
		"type": "org.eclipse.jetty.servlet.ServletHolder",
		"fields": {
			"name": "staticServlet",
			"servlet": {"type": "nrt.jetty.web.StaticServlet"},
			"initParameters": {
				"urlPattern": "/s/*"
			}
		}
	}
};