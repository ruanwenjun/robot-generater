package com.robotgenerate.core;

import com.robotgenerate.model.*;
import com.robotgenerate.model.RequestMethod;
import com.robotgenerate.util.AnnotationUtils;
import com.robotgenerate.util.KeywordUtils;
import com.robotgenerate.util.PackageUtils;
import org.junit.Assert;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;


public class RobotGenerater {

	private static String SETTINGS = "************************************ Settings *****************************************\n";

	private static String VARIABLES = "************************************* Variables ***************************************\n";

	private static String KEYWORDS = "************************************* Keywords *****************************************\n";

	private static String TASKS = "******************************************** Tasks **********************************************\n";

	public List<Robot> generate(String packageName) {
		List<Class> allClass = PackageUtils.getAllClassNames(packageName);
		return allClass.stream().filter(cla -> AnnotationUtils.classContainAnnotation(cla, RestController.class)
			|| AnnotationUtils.classContainAnnotation(cla, Controller.class)
			|| AnnotationUtils.methonContainAnnotation(cla, GetMapping.class)
			|| AnnotationUtils.methonContainAnnotation(cla, PostMapping.class)
			|| AnnotationUtils.methonContainAnnotation(cla, PutMapping.class)
			|| AnnotationUtils.methonContainAnnotation(cla, DeleteMapping.class))
			.map(this::toControllerObject)
			.map(this::toRobot)
			.collect(Collectors.toList());
	}

	/**
	 * transform the cla to ControllerObject
	 *
	 * @param cla
	 * @return
	 */
	private ControllerObject toControllerObject(Class cla) {
		Assert.assertNotNull(cla);
		Method[] methods = cla.getMethods();
		List<RequestMethod> requestMethods = Arrays.stream(methods)
			.filter(method -> AnnotationUtils.methonContainAnnotation(method, GetMapping.class)
				|| AnnotationUtils.methonContainAnnotation(method, PostMapping.class)
				|| AnnotationUtils.methonContainAnnotation(method, PutMapping.class)
				|| AnnotationUtils.methonContainAnnotation(method, DeleteMapping.class))
			.map(method -> {
				RequestMethod requestMethod = new RequestMethod();
				requestMethod.setMethodName(method.getName());
				if (AnnotationUtils.methonContainAnnotation(method, GetMapping.class)) {
					requestMethod.setRequestType(RequestType.GET);
					GetMapping getMapping = method.getAnnotation(GetMapping.class);
					requestMethod.setRequestUri(getMapping.value()[0]);
				} else if (AnnotationUtils.methonContainAnnotation(method, PostMapping.class)) {
					requestMethod.setRequestType(RequestType.POST);
					PostMapping postMapping = method.getAnnotation(PostMapping.class);
					requestMethod.setRequestUri(postMapping.value()[0]);
				} else if (AnnotationUtils.methonContainAnnotation(method, PutMapping.class)) {
					requestMethod.setRequestType(RequestType.PUT);
					PutMapping putMapping = method.getAnnotation(PutMapping.class);
					requestMethod.setRequestUri(putMapping.value()[0]);
				} else {
					requestMethod.setRequestType(RequestType.DELETE);
					DeleteMapping deleteMapping = method.getAnnotation(DeleteMapping.class);
					requestMethod.setRequestUri(deleteMapping.value()[0]);
				}
				requestMethod.setParameters(method.getParameters());
				return requestMethod;
			}).collect(Collectors.toList());

		return ControllerObject.builder()
			.className(cla.getSimpleName())
			.requestMethods(requestMethods)
			.packageName(cla.getPackage().getName())
			.build();
	}


	private Robot toRobot(ControllerObject controllerObject) {
		Settings settings = Settings.builder()
			.library(Arrays.asList("RequestsLibrary", "Collections", "String"))
			.build();
		HashMap<String, String> variables = new HashMap<>();
		String server = String.format("${%s}", controllerObject.getClassName().toUpperCase());
		variables.put(server, "localhost:8080");
		List<Keyword> keywords = controllerObject.getRequestMethods().stream()
			.map(method -> {
				Keyword keyword = new Keyword();
				keyword.setName(method.getMethodName());
				keyword.setReturnValue("${resp}");
				List<String> arguments = Arrays.stream(method.getParameters())
					.map(Parameter::getName)
					.collect(Collectors.toList());
				keyword.setArguments(arguments);
				List<String> content = new ArrayList<>();
				content.add(KeywordUtils.createSession(controllerObject.getClassName(), server, ""));
				switch (method.getRequestType()) {
					case GET:
						KeywordUtils.getRequest(controllerObject.getClassName(), server, "");
						break;
					case PUT:
						break;
					case HEAD:
						break;
					case POST:
						KeywordUtils.postRequest(controllerObject.getClassName(), server, "", "");
						break;
					case DELETE:
						break;
					default:
						break;
				}
				keyword.setContent(content);
				return keyword;
			}).collect(Collectors.toList());
		return Robot.builder()
			.controller(controllerObject.getClassName())
			.settings(settings)
			.variables(variables)
			.keywords(keywords)
			.task(new ArrayList<>())
			.build();
	}

	public static void main(String[] args) {
		RobotGenerater robotGenerater = new RobotGenerater();
		List<Robot> robots = robotGenerater.generate("com.robotgenerate");

		robots.forEach(robot -> {
			File file = new File("/Users/weruan/Project/testrobot/" + robot.getController() + ".robot");
			try (FileOutputStream fileOutputStream = new FileOutputStream(file);) {
				fileOutputStream.write(SETTINGS.getBytes());
				Settings settings = robot.getSettings();
				settings.getLibrary().forEach(library -> {
					try {
						fileOutputStream.write(String.format("Library    %s\n", library).getBytes());
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
				fileOutputStream.write(VARIABLES.getBytes());
				robot.getVariables().forEach((k, v) -> {
					try {
						fileOutputStream.write(String.format("%s    %s\n", k, v).getBytes());
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
				fileOutputStream.write(KEYWORDS.getBytes());
				robot.getKeywords().forEach(keyword -> {
					try {
						fileOutputStream.write(keyword.getName().getBytes());
						fileOutputStream.write("\n".getBytes());
						fileOutputStream.write("\t[Arguments]".getBytes());
						fileOutputStream.write("\n".getBytes());
						fileOutputStream.write(String.format("\t[Return]    %s", keyword.getReturnValue()).getBytes());
						fileOutputStream.write("\n".getBytes());
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
				fileOutputStream.write(TASKS.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		});


	}

}
