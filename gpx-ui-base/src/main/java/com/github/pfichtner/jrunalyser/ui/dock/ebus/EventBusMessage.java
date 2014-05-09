package com.github.pfichtner.jrunalyser.ui.dock.ebus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation for messages send via the message bus.
 * 
 * @author Peter Fichtner
 */
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventBusMessage {
	// marker annotation
}
