package com.github.pfichtner.jrunalyser.ui.base.i18n;

import org.jdesktop.application.ResourceMap;

import com.dteoh.treasuremap.ResourceMaps;

public class I18N {

	public static class Builder {

		private ResourceMaps resourceMaps;

		public Builder(Class<?> clazz) {
			this.resourceMaps = new ResourceMaps(clazz);
		}

		public Builder withParent(I18N parent) {
			this.resourceMaps.withParent(parent.resourceMap);
			return this;
		}

		public I18N build() {
			return new I18N(this.resourceMaps.build());
		}

	}

	private final ResourceMap resourceMap;

	private I18N(ResourceMap resourceMap) {
		this.resourceMap = resourceMap;
	}

	public String getText(String key, Object... args) {
		return this.resourceMap.getString(key, args);
	}

	public String getText(Enum<?> enumType, Object type) {
		return getText(enumType.getDeclaringClass().getName() + '.'
				+ enumType.name() + '$' + type);
	}

	public static Builder builder(Class<?> clazz) {
		return new Builder(clazz);
	}

}
