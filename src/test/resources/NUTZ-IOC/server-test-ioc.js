var ioc = {
	"filterList": {
		"type": "java.util.List",
		"factory": "nrt.jetty.NutzHelperFactory#aliasFor",
		"args": [[{
			"refer": "nutzFilter"
		}]]
	},
	"servletList": {
		"type": "java.util.List",
		"factory": "nrt.jetty.NutzHelperFactory#aliasFor",
		"args": [[{
			"refer": "staticServlet"
		}]]
	}
};