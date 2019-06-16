package com.robotgenerate.model;

import lombok.Data;

import java.util.Map;


@Data

public class RobotMethod {


	private RequestType requestType;

	private String path;

	private Map<String, Object> param;


}
