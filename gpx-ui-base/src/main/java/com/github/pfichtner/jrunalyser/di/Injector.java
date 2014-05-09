package com.github.pfichtner.jrunalyser.di;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.google.common.base.Throwables;

public final class Injector {

	private Injector() {
		super();
	}

	// poor man's @Inject ;-)
	public static <T, S> T inject(final T instance, Class<S> clazz, S toInject) {
		try {
			for (PropertyDescriptor pd : Introspector.getBeanInfo(
					instance.getClass()).getPropertyDescriptors()) {
				Method wm = pd.getWriteMethod();
				if (wm != null && wm.isAnnotationPresent(Inject.class)
						&& pd.getPropertyType().equals(clazz)) {
					wm.invoke(instance, toInject);
				}
			}
		} catch (IntrospectionException e) {
			throw Throwables.propagate(e);
		} catch (IllegalArgumentException e) {
			throw Throwables.propagate(e);
		} catch (IllegalAccessException e) {
			throw Throwables.propagate(e);
		} catch (InvocationTargetException e) {
			throw Throwables.propagate(e);
		}
		return instance;
	}

}
