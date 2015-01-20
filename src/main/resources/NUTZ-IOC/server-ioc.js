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
				"urlPattern": "*.shtml",
				"modules": "nrt.jetty.web.MainModule"
			}
		}
	},
	"rsFilter": {
		"type": "org.eclipse.jetty.servlet.FilterHolder",
		"fields": {
			"name": "rs",
			"filter": {"type": "org.nutz.mvc.NutFilter"},
			"initParameters": {
				"urlPattern": "/rs/*",
				"modules": "nrt.jetty.rs.MainModule"
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
	"groovyServlet": {
		"type": "org.eclipse.jetty.servlet.ServletHolder",
		"fields": {
			"name": "groovyServlet",
			"servlet": {
				"type": "nrt.jetty.web.GroovyVelocityServlet",
				"fields": {
					"groovyConfig": {"refer": "groovyConfig"},
					"velocityConfig": {"refer": "velocityConfig"}
				}
			},
			"initParameters": {
				"urlPattern": "*.html"
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
	},
	"velocityConfig": {
		"type": "nrt.jetty.web.VelocityConfig",
		"fields": {
			"ioc": {"refer": "$ioc"}
		}
	},
	"groovyConfig": {
		"type": "nrt.jetty.web.GroovyConfig",
		"fields": {
			"ioc": {"refer": "$ioc"}
		}
	}
};