var ioc = {
	"velocityView": {
		"type": "nrt.jetty.web.VelocityView",
		"fields": {
			"ioc": {"refer": "$ioc"}
		}
	}
};