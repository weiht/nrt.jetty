package nrt.jetty.web;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.nutz.ioc.Ioc2;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.View;
import org.nutz.resource.NutResource;
import org.nutz.resource.Scans;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VelocityView
implements View {
	private static final Logger logger = LoggerFactory.getLogger(VelocityView.class);
	
	public static final String DEFAULT_CONFIG_LOCATION = "NUTZ-MVC";
	public static final String DEFAULT_RESOURCE_LOCATION = "NUTZ-RES";
	public static final String DEFAULT_CONFIG_FILE = "velocity.properties";
	public static final String CONFIG_VALUE_SEPARATOR = ",";
	public static final String RES_LOADER_KEY = "resource.loader";
	public static final String ENCODING_KEY = "input.encoding";
	public static final String DEFAULT_ENCODING = "UTF-8";
	public static final String INCLUDE_HANDLER_KEY = "eventhandler.include.class";
	public static final String INCLUDE_HANDLER_VALUE = "org.apache.velocity.app.event.implement.IncludeRelativePath";
	public static final String FILE_LOADER = "file";
	public static final String LOADER_CLASS_KEY = ".resource.loader.class";
	public static final String FILE_LOADER_PATH_KEY = ".resource.loader.path";
	public static final String FILE_LOADER_NAME = "org.apache.velocity.runtime.resource.loader.FileResourceLoader";
	public static final String SYS_PROP_VELOCITY_TEMPLATE_PATHS = "nrt.velocity.tplpath";
	public static final String KEY_IOC = "ioc";
	public static final String KEY_PATH = "path";
	public static final String KEY_CONTEXT_PATH = "contextPath";
	public static final String KEY_REQUEST_URI = "requestUri";
	public static final String KEY_REQUEST = "request";
	public static final String KEY_RESPONSE = "response";
	public static final String KEY_RESULT = "obj";

	private static final String[] MERGIBLE_CONFIG_KEYS = {
		"userdirective", RES_LOADER_KEY
	};
	
	private VelocityView parent;
	private String configLocation = DEFAULT_CONFIG_LOCATION, configFile = DEFAULT_CONFIG_FILE;
	private String resourceLocation = DEFAULT_RESOURCE_LOCATION;
	private Properties config;
	private VelocityEngine engine;
	private Context rootContext;
	private Ioc2 ioc;
	
	private String path;
	
	public VelocityView() {
	}
	
	public VelocityView(VelocityView parent) {
		this.parent = parent;
	}
	
	public void init() {
		ensureEngine();
	}
	
	public void dispose() {
		//
	}

	private void ensureEngine() {
		if (engine == null) {
			loadConfig();
			logger.trace("Velocity config initialized: {}", config);
			engine = new VelocityEngine(config);
			initContext();
		}
	}
	
	private void initContext() {
		rootContext = new VelocityContext();
		rootContext.put(KEY_IOC, ioc);
	}

	private void loadConfig() {
		config = new Properties();
		initDefaultConfig();
		for (NutResource res: Scans.me().scan(configLocation, configFile)) {
			try {
				doLoadConfig(res);
			} catch (IOException e) {
				logger.warn("Error loading velocity config file.", e);
			}
		}
	}

	private void initDefaultConfig() {
		config.put(INCLUDE_HANDLER_KEY, INCLUDE_HANDLER_VALUE);
		initFileResourceLoader();
	}

	private void initFileResourceLoader() {
		String paths = System.getProperty(SYS_PROP_VELOCITY_TEMPLATE_PATHS);
		if (paths == null) return;
		StringBuilder buff = new StringBuilder();
		for (String p: paths.split(File.pathSeparator)) {
			p = p.trim();
			if (!p.isEmpty()) {
				if (buff.length() > 0) {
					buff.append(CONFIG_VALUE_SEPARATOR);
				}
				buff.append(p);
			}
		}
		config.put(FILE_LOADER + FILE_LOADER_PATH_KEY, buff.toString());
		config.put(INCLUDE_HANDLER_KEY, INCLUDE_HANDLER_VALUE);
		config.put(FILE_LOADER + LOADER_CLASS_KEY, FILE_LOADER_NAME);
		config.put(RES_LOADER_KEY, FILE_LOADER);
	}

	private void doLoadConfig(NutResource res) throws IOException {
		Reader r = res.getReader();
		if (r == null) return;
		Properties props = new Properties();
		try {
			props.load(r);
			mergeToConfig(props);
		} finally {
			closeReader(r);
		}
	}

	private void mergeToConfig(Properties props) {
		for (Object ok: props.keySet()) {
			String k = (String)ok;
			String v = props.getProperty(k);
			if (v == null || (v = v.trim()).isEmpty()) continue;

			if (isMergibleConfig(k)) {
				String ov = config.getProperty(k);
				if (ov == null || (ov = ov.trim()).isEmpty()) {
					config.setProperty(k, v);
				} else {
					config.setProperty(k, ov + CONFIG_VALUE_SEPARATOR + v);
				}
			} else {
				config.setProperty(k, v);
			}
		}
	}

	private boolean isMergibleConfig(String k) {
		for (String sk: MERGIBLE_CONFIG_KEYS) {
			if (sk.equals(k)) return true;
		}
		return false;
	}

	public void render(HttpServletRequest req, HttpServletResponse resp,
			Object obj) throws Throwable {
		VelocityEngine ve = getEngine();
		Context ctx = getContext(req, resp, obj);
		Writer w = getWriter(resp);
		String fn = evaluateFileName();
		try {
			ve.mergeTemplate(fn, parent.config.getProperty(ENCODING_KEY, DEFAULT_ENCODING), ctx, w);
		} finally {
			closeWriter(w);
		}
	}
	
	private String evaluateFileName() {
		String result = parent.resourceLocation;
		if (result == null || result.isEmpty()) result = "/";
		if (!result.endsWith("/")) result += "/";
		result += path;
		return result;
	}

	private void closeReader(Reader r) {
		try {
			r.close();
		} catch (Exception e) {
			//
		}
	}

	private void closeWriter(Writer w) {
		try {
			w.close();
		} catch (Exception e) {
			//
		}
	}

	private VelocityEngine getEngine() {
		if (parent == null) throw new RuntimeException("Velocity view has not been properly configured.");
		if (parent.engine == null) throw new RuntimeException("Velocity engine has not been initialized.");
		return parent.engine;
	}

	private Writer getWriter(HttpServletResponse resp) throws IOException {
		try {
			return resp.getWriter();
		} catch (IOException e) {
			logger.info("Error retrieving response's writer. Try to retrieve output stream.");
			return new OutputStreamWriter(resp.getOutputStream());
		}
	}

	private Context getContext(HttpServletRequest req,
			HttpServletResponse resp, Object obj) {
		Context ctx = new VelocityContext(parent.rootContext);
		ctx.put(KEY_PATH, path);
		ctx.put(KEY_REQUEST, req);
		ctx.put(KEY_RESPONSE, resp);
		ctx.put(KEY_RESULT, obj);
		ctx.put(KEY_CONTEXT_PATH, req.getContextPath());
		ctx.put(KEY_REQUEST_URI, Mvcs.getRequestPath(req));
		return ctx;
	}

	public VelocityView serve(String path) {
		ensureEngine();
		VelocityView view = new VelocityView(this);
		view.setPath(path);
		return view;
	}
	
	public void setConfigFile(String configFile) {
		this.configFile = configFile;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setResourceLocation(String resourceLocation) {
		this.resourceLocation = resourceLocation;
	}
}
