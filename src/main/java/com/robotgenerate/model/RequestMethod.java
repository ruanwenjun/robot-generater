package com.robotgenerate.model;


import lombok.Data;

import java.lang.reflect.Parameter;


@Data
public class RequestMethod {

	private String methodName;

	private RequestType requestType;

	private String requestUri;

	private Parameter[] parameters;

}
