package nrt.jetty;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class NutzHelperFactory {
	public static Object methodCall(Object bean, String method, Object[] args)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		for (Method m: bean.getClass().getMethods()) {
			if (m.getName().equals(method)) {
				if (argsMatch(m.getParameterTypes(), args)) {
					m.invoke(bean, args);
					break;
				}
			}
		}
		return bean;
	}

	private static boolean argsMatch(Class<?>[] parameterTypes, Object[] args) {
		if (parameterTypes.length != args.length) return false;
		for (int i = 0; i < parameterTypes.length; i ++) {
			if (!argMatch(parameterTypes[i], args[i])) {
				return false;
			}
		}
		return true;
	}

	private static boolean argMatch(Class<?> argType, Object object) {
		if (object == null) {
			if (argType.isPrimitive()) return false;
			else return true;
		}
		Class<?> clazz = object.getClass();
		return argType.isAssignableFrom(clazz);
	}
}
