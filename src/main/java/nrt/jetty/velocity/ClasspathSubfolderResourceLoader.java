package nrt.jetty.velocity;

import java.io.InputStream;

import nrt.jetty.web.VelocityConfig;

import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

public class ClasspathSubfolderResourceLoader
extends ClasspathResourceLoader {
	@Override
	public InputStream getResourceStream(String name)
			throws ResourceNotFoundException {
		//TODO Make it configurable.
		return super.getResourceStream(VelocityConfig.DEFAULT_RESOURCE_LOCATION + name);
	}
}
