/**
 * Copyright (c) 2012-2014 Axelor. All Rights Reserved.
 *
 * The contents of this file are subject to the Common Public
 * Attribution License Version 1.0 (the “License”); you may not use
 * this file except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://license.axelor.com/.
 *
 * The License is based on the Mozilla Public License Version 1.1 but
 * Sections 14 and 15 have been added to cover use of software over a
 * computer network and provide for limited attribution for the
 * Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 *
 * Software distributed under the License is distributed on an “AS IS”
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 *
 * The Original Code is part of "Axelor Business Suite", developed by
 * Axelor exclusively.
 *
 * The Original Developer is the Initial Developer. The Initial Developer of
 * the Original Code is Axelor.
 *
 * All portions of the code written by Axelor are
 * Copyright (c) 2012-2014 Axelor. All Rights Reserved.
 */
package com.axelor.web.internal;

import java.net.MalformedURLException;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import com.axelor.app.AppSettings;
import com.axelor.auth.AuthUtils;
import com.axelor.auth.db.Group;
import com.axelor.auth.db.User;
import com.axelor.common.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public final class AppInfo {

	private static final String APPLICATION_JS = "js/application.js";
	private static final String APPLICATION_JS_MIN = "js/application.min.js";
	
	private static final String APPLICATION_CSS = "css/application.css";
	private static final String APPLICATION_CSS_MIN = "css/application.min.css";

	private static final String APPLICATION_LANG_JS = "js/i18n/%s.js";
	
	private static final Locale DEFAULT_LOCALE = new Locale("en");
	
	private static final AppSettings settings = AppSettings.get();

	public static String asJson() {

		final Map<String, Object> map = Maps.newHashMap();
		try {
			User user = AuthUtils.getUser();
			Group group = user.getGroup();

			map.put("user.name", user.getName());
			map.put("user.login", user.getCode());

			if (group != null) {
				map.put("user.navigator", group.getNavigation());
			}
			map.put("user.lang", user.getLanguage());
			map.put("user.action", user.getAction().getName());
		} catch (Exception e){
		}

		map.put("application.name", settings.get("application.name"));
		map.put("application.description", settings.get("application.description"));
		map.put("application.version", settings.get("application.version"));
		map.put("application.mode", settings.get("application.mode"));
		
		map.put("help.location", settings.get("help.location"));
		map.put("sdk.version", settings.get("sdk.version"));
		map.put("file.upload.size", settings.get("file.upload.size"));

		for (String key : settings.getProperties().stringPropertyNames()) {
			if (key.startsWith("view.")) {
				Object value = settings.get(key);
				if ("true".equals(value) || "false".equals(value)) {
					value = Boolean.parseBoolean(value.toString());
				}
				map.put(key, value);
			}
		}

		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(map);
		} catch (Exception e) {
		}
		return "{}";
	}

	private static String getUserLanguage() {
		final User user = AuthUtils.getUser();
		if (user == null) {
			return null;
		}
		return user.getLanguage();
	}

	public static String getLangJS(HttpServletRequest request, ServletContext context){

		Locale locale = null;
		String language = getUserLanguage();
		
		if (!StringUtils.isBlank(language)) {
			locale = toLocale(language);
		}
		if (locale == null) {
			locale = request.getLocale();
		}
		if (locale == null) {
			locale = toLocale(settings.get("application.locale", DEFAULT_LOCALE.getLanguage()));
		}

		for(String lang : Lists.newArrayList(toLanguage(locale, false), toLanguage(locale, true))) {
			if (checkResources(context, "/js/i18n/" + lang + ".js")) {
				language = lang;
				break;
			}
		}
		
		if (language == null) {
			language = DEFAULT_LOCALE.getLanguage();
		}
		
		return String.format(APPLICATION_LANG_JS, language);
	}

	public static String getAppJS(ServletContext context) {
		if (settings.isProduction() && checkResources(context, "/" + APPLICATION_JS_MIN)) {
			return APPLICATION_JS_MIN;
		}
		return APPLICATION_JS;
	}
	
	public static String getAppCSS(ServletContext context) {
		if (settings.isProduction() && checkResources(context, APPLICATION_CSS_MIN)) {
			return APPLICATION_CSS_MIN;
		}
		return APPLICATION_CSS;
	}

	private static String toLanguage(Locale locale, boolean minimize) {
		final String lang = locale.getLanguage().toLowerCase();
		if (minimize || StringUtils.isBlank(locale.getCountry())) {
			return lang;
		}
		return lang + "_" + locale.getCountry().toUpperCase();
	}

	private static Locale toLocale(String language) {
	    final String parts[] = language.split("_", -1);
	    if (parts.length == 1) {
	    	return new Locale(parts[0].toLowerCase());
	    }
	    return new Locale(parts[0].toLowerCase(), parts[1].toUpperCase());
	}

	private static boolean checkResources(ServletContext context, String resourcesPath) {
		try {
			return context.getResource(resourcesPath) != null;
		} catch (MalformedURLException e) {
			return false;
		}
	}
}