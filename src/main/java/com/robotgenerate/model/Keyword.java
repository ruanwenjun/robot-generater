package com.robotgenerate.model;

import lombok.Data;

import java.util.List;


@Data
public class Keyword {

	private String name;

	private List<String> arguments;

	private String returnValue;

	private List<String> content;

}
