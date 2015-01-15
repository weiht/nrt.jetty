var ioc = {
	"velocityView": {
		"type": "nrt.jetty.web.VelocityView",
		"fields": {
			"config": {"refer": "velocityConfig"}
		}
	},
	"velocityConfig": {
		"type": "nrt.jetty.web.VelocityConfig",
		"fields": {
			"ioc": {"refer": "$ioc"}
		}
	}
};