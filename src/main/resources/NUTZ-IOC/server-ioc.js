var ioc = {
	"serverRunner": {
		"type": "nrt.jetty.ServerRunner",
		"fields": {
			"bindAddress": "0.0.0.0",
			"bindPort": 1920,
			"filters": [{
				"refer": "nutzFilter"
//			}],
//			"servlets": [{
//				"refer": "jspServlet"
			}]
		}
	},
	"nutzFilter": {
		"type": "org.eclipse.jetty.servlet.FilterHolder",
		"fields": {
			"name": "nutz",
			"filter": {"type": "org.nutz.mvc.NutFilter"},
			"initParameters": {
				"urlPattern": "*.gsp"
				"modules": "nrt.jetty.web.MainModule"
			}
		}
	},
	"jspServlet": {
		"type": "org.eclipse.jetty.servlet.ServletHolder",
		"fields": {
			"name": "jsp",
			"servlet": {"type": "org.apache.jasper.servlet.JspServlet"},
			"initParameters": {
				"development": "true", /*True to check modification on every request*/
				"checkInterval": "60", /*When development is set to false, use a separate thread to check modifications*/
				"urlPattern": "*.jsp"
			}
		}
	}
};