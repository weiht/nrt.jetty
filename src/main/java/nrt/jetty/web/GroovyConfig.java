package nrt.jetty.web;

import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.nutz.ioc.Ioc2;
import org.nutz.json.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GroovyConfig {
	private static final Logger logger = LoggerFactory.getLogger(GroovyConfig.class);
	
	public static final String SYS_PROP_GROOVY_SCRIPT_PATHS = "nrt.groovy.classpath";
	public static final String KEY_GROOVY_CONFIG = "groovyConfig";
	public static final Object KEY_FORWARD_TO = "forwardTo";
	public static final Object KEY_SKIP_VIEW = "skipView";
	public static final Object KEY_JSON_RESULT = "__json__";
	
	public static final String[] YES_VALUES = {
		"y", "yes", "true", "t"
	};
	
	public static final String[] NO_VALUES = {
		"n", "no", "false", "f"
	};
	
	private String resourceLocation = VelocityConfig.DEFAULT_RESOURCE_LOCATION;
	private String[] groovyClasspaths = new String[0];
	private Binding rootBinding = new Binding();
	private GroovyScriptEngine engine;
	private Ioc2 ioc;
	private boolean devMode;
	
	private void loadGroovyClasspaths() {
		List<String> classpaths = new ArrayList<String>();
		ClassLoader loader = getClass().getClassLoader();
		if (loader instanceof URLClassLoader) {
			@SuppressWarnings("resource")
			URLClassLoader uloader = (URLClassLoader)loader;
			for (URL u: uloader.getURLs()) {
				logger.trace("Groovy classpath from jvm: {}", u);
				classpaths.add(u.getPath());
			}
		}
		String paths = System.getProperty(SYS_PROP_GROOVY_SCRIPT_PATHS);
		if (paths != null) {
			for (String p: paths.split(File.pathSeparator)) {
				p = p.trim();
				if (!p.isEmpty()) {
					File f = new File(p);
					if (f.exists() && f.isDirectory()) {
						logger.trace("Groovy classpath: {}", f);
						classpaths.add(f.toString());
					}
				}
			}
		}
		logger.trace("GroovyRunner: classpaths: {}", classpaths);
		groovyClasspaths = classpaths.toArray(groovyClasspaths);
	}
	
	private void initRootBinding() {
		rootBinding.setVariable(VelocityConfig.KEY_IOC, ioc);
		if (System.getProperty(VelocityConfig.SYS_PROP_DEV_MODE) != null) {
			rootBinding.setVariable(VelocityConfig.KEY_DEV_MODE, true);
			devMode = true;
		}
		rootBinding.setVariable(KEY_GROOVY_CONFIG, this);
	}
	
	private void ensureEngine() throws IOException {
		if (engine == null) {
			loadGroovyClasspaths();
			initRootBinding();
			engine = new GroovyScriptEngine(groovyClasspaths, getClass().getClassLoader());
		}
	}

	public GroovyScriptEngine getEngine() {
		try {
			ensureEngine();
			return engine;
		} catch (IOException e) {
			return null;
		}
	}
	
	public Binding getBinding() {
		try {
			ensureEngine();
			return new Binding(rootBinding.getVariables());
		} catch (IOException e) {
			return null;
		}
	}
	
	public static boolean viewSkipped(Object param) {
		if (param == null) return false;
		if (param instanceof Boolean) return (Boolean)param;
		String sval = param.toString();
		return isYes(sval);
	}

	private static boolean inStrArr(String[] sarr, String sval) {
		String s = sval.toLowerCase();
		for (String y: sarr) {
			if (y.equals(s)) return true;
		}
		return false;
	}
	
	public static boolean isYes(String sval) {
		if (sval == null) return false;
		return inStrArr(YES_VALUES, sval);
	}
	
	public static boolean isNo(String sval) {
		if (sval == null) return false;
		return inStrArr(NO_VALUES, sval);
	}

	public boolean preRender(Map<String, Object> result, Writer w) {
		if (result == null) return false;
		Object o = result.get(KEY_SKIP_VIEW);
		if (viewSkipped(o))
			return true;
		o = result.get(KEY_JSON_RESULT);
		if (o != null) {
			logger.trace("Json result: {}", o);
			Json.toJson(w, o);
			return true;
		}
		return false;
	}

	public String calcForwardPath(String path, String fwd) {
		if (fwd.startsWith("/")) {
			return fwd;
		}
		//TODO 解析./和../等。
		int ix = path.lastIndexOf("/");
		if (ix < 1) {
			return fwd;
		} else {
			return path.substring(0, ix + 1) + fwd;
		}
	}

	public void setResourceLocation(String resourceLocation) {
		this.resourceLocation = resourceLocation;
	}

	public void setIoc(Ioc2 ioc) {
		this.ioc = ioc;
	}

	public String getResourceLocation() {
		return resourceLocation;
	}

	public boolean isDevMode() {
		return devMode;
	}
}
