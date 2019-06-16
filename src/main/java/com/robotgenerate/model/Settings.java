package com.robotgenerate.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;


@Data
@Builder
public class Settings {

	private List<String> library;

	private List<String> resource;

}
