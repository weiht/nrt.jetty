package nrt.jetty.velocity;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import nrt.jetty.web.VelocityView;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BundleDirective
extends Directive {
	private static final Logger logger = LoggerFactory.getLogger(BundleDirective.class);
	
	public static final String BUNDLE_KEY = "bundle";
	private static ClassLoader bundleClassLoader;
	private VelocityView viewConfig;

	@Override
	public String getName() {
		return "bundle";
	}

	@Override
	public int getType() {
		return LINE;
	}
	
	private boolean isEmpty(String str) {
		return str == null || (str = str.trim()).isEmpty();
	}

	@Override
	public boolean render(InternalContextAdapter ctx, Writer w, Node n)
			throws IOException, ResourceNotFoundException, ParseErrorException,
			MethodInvocationException {
		this.viewConfig = (VelocityView) ctx.get(VelocityView.KEY_VIEW_CONFIG);
		String bundleName = getBundleName(ctx, n);
		if (isEmpty(bundleName)) {
			logger.trace("No bundle name specified.");
			return false;
		}
		Locale lc = getLocaleName(ctx);
		logger.trace("Locale for resource bundle {}: {}", bundleName, lc);
		File[] repos = getRepos(ctx);
		logger.trace("Repos for resources: {}", (Object)repos);
		try {
			loadBundle(ctx, bundleName, lc, repos);
			return true;
		} catch (MissingResourceException e) {
			logger.warn("", e);
			return false;
		}
	}

	private void loadBundle(InternalContextAdapter ctx, String bundleName,
			Locale lc, File[] repos) {
		if (repos == null || repos.length < 1) {
			loadClasspathBundles(ctx, bundleName, lc);
		} else {
			loadMixedBundles(ctx, bundleName, lc, repos);
		}
	}

	private void loadClasspathBundles(InternalContextAdapter ctx,
			String bundleName, Locale lc) {
		ResourceBundle bundle = ResourceBundle.getBundle(viewConfig.getResourceLocation() + "." + bundleName, lc);
		ctx.put(BUNDLE_KEY, bundle);
	}

	private void loadMixedBundles(InternalContextAdapter ctx,
			String bundleName, Locale lc, File[] repos) {
		ClassLoader cloader = ensureLoader(ctx, repos);
		ResourceBundle bundle = ResourceBundle.getBundle(bundleName, lc, cloader);
		ctx.put(BUNDLE_KEY, bundle);
	}
	
	private ClassLoader ensureLoader(InternalContextAdapter ctx, File[] repos) {
		if (isDevMode(ctx)) return createLoader(repos);
		if (bundleClassLoader == null) {
			bundleClassLoader = createLoader(repos);
		}
		return bundleClassLoader;
	}

	private boolean isDevMode(InternalContextAdapter ctx) {
		return ctx.get(VelocityView.KEY_DEV_MODE) != null;
	}

	private ClassLoader createLoader(File[] repos) {
		// Only one class loader will be retained.
		URL[] urls = reposToUrls(repos);
		return new URLClassLoader(urls, getClass().getClassLoader());
	}

	private URL[] reposToUrls(File[] repos) {
		URL[] result = new URL[repos.length];
		for (int i = 0; i < result.length; i ++) {
			try {
				result[i] = repos[i].toURI().toURL();
			} catch (MalformedURLException e) {
				//Does nothing
			}
		}
		return result;
	}

	private Locale getLocaleName(InternalContextAdapter ctx) {
		HttpServletRequest request = (HttpServletRequest) ctx.get(VelocityView.KEY_REQUEST);
		if (request == null) return Locale.getDefault();
		return request.getLocale();
	}

	private File[] getRepos(InternalContextAdapter ctx) {
		return (File[]) ctx.get(VelocityView.KEY_REPO_DIRS);
	}

	private String getBundleName(InternalContextAdapter ctx, Node n) {
		//TODO Retrieve bundle to current template.
		if (n.jjtGetNumChildren() < 1) {
			// 获取默认资源
			String bundleName = pathToBundleName((String) ctx.get(VelocityView.KEY_PATH));
			logger.trace("No bundle name specified in directive. Using full path: {}", bundleName);
			return bundleName;
		} else {
			SimpleNode nbundle = (SimpleNode) n.jjtGetChild(0);
			String bundleName = (String) nbundle.value(ctx);
			logger.trace("Bundle name specified in directive: {}", bundleName);
			return bundleName;
		}
	}

	private String pathToBundleName(String bundleName) {
		//最后一段路径中不允许出现点

		int ix = bundleName.lastIndexOf('/');
		if (ix >= 0) {
			ix = bundleName.indexOf('.', ix);
		}
		String result = ix > 0 ? bundleName.substring(0, ix) : bundleName;
		logger.trace("Translated bundle name: {}", result);
		if (result.charAt(0) == '/') return result.substring(1);
		return result;
	}
}
