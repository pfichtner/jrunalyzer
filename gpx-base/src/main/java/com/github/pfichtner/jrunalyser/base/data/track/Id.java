package com.github.pfichtner.jrunalyser.base.data.track;

import javax.annotation.concurrent.Immutable;

/**
 * Id implementations must implement {@link #hashCode()} and
 * {@link #equals(Object)} to identify Tracks uniquely. This implementations
 * must also be immutable.
 * 
 * @author Peter Fichtner
 */
@Immutable
public interface Id {
	// marker interface
}