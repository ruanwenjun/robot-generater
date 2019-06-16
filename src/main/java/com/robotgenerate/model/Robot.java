package com.robotgenerate.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;


@Data
@Builder
public class Robot {

	private Class controllerClass ;

	private Settings settings;

	private Map<String, String> variables;

	private List<Keyword> keywords;

	private List<Task> task;

}
