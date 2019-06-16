package com.robotgenerate.util;


import org.apache.commons.lang3.StringUtils;


public class KeywordUtils {

	/**
	 * alias, url, headers
	 */
	private static String CREATE_SESSION = "Create Session    %s    %s    %s";

	/**
	 * alias, uri, data, files
	 */
	private static String POST_REQUEST = "Post    Request    %s    %s    %s    %s";

	/**
	 * alias, uri, params
	 */
	private static String GET_REQUEST = "Get Request    %s    %s    %s";


	public static String createSession(String alias, String server, String headers) {
		return String.format(CREATE_SESSION, alias, server, StringUtils.isBlank(headers) ? "" : "headers=" + headers);
	}

	public static String postRequest(String alias, String uri, String data, String files) {
		if (!StringUtils.isBlank(data)) {
			data = "data=" + data;
		} else {
			data = "";
		}
		if (!StringUtils.isBlank(files)) {
			files = "files=" + files;
		} else {
			files = "";
		}
		return String.format(POST_REQUEST, alias, uri, data, files);
	}

	public static String getRequest(String alias, String uri, String params) {
		if (!StringUtils.isBlank(params)) {
			params = "params=" + params;
		} else {
			params = "";
		}
		return String.format(GET_REQUEST, alias, uri, params);
	}
}
