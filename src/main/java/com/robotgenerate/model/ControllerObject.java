package com.robotgenerate.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;


@Data
@Builder
public class ControllerObject {

	private Class controllerClass;

	private String packageName;

	private List<RequestMethod> requestMethods;
}
