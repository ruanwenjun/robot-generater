package com.robotgenerate.core;

import com.robotgenerate.model.*;
import com.robotgenerate.model.RequestMethod;
import com.robotgenerate.util.AnnotationUtils;
import com.robotgenerate.util.KeywordUtils;
import com.robotgenerate.util.PackageUtils;
import org.junit.Assert;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;


public class RobotGenerater {

    /**
     * resolve the given package, find spring controller, generate robot class
     *
     * @param packageName
     * @return
     */
    private List<Robot> generate(String packageName) {
        List<Class> allClass = PackageUtils.getAllClassNames(packageName);
        return allClass.stream().filter(cla ->
            AnnotationUtils.classContainAnnotation(cla, RestController.class)
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
     * transform the ControllerClass to ControllerObject
     *
     * @param controllerClass
     * @return
     */
    private ControllerObject toControllerObject(Class controllerClass) {
        Assert.assertNotNull(controllerClass);
        Method[] methods = controllerClass.getMethods();
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
            .controllerClass(controllerClass)
            .requestMethods(requestMethods)
            .packageName(controllerClass.getPackage().getName())
            .build();
    }


    /**
     * generate robot
     *
     * @param controllerObject
     * @return
     */
    private Robot toRobot(ControllerObject controllerObject) {
        Settings settings = Settings.builder()
            .library(Arrays.asList("RequestsLibrary", "Collections", "String"))
            .build();
        HashMap<String, String> variables = new HashMap<>();
        variables.put("${SessionServer}", "localhost:8080");
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
                content.add(KeywordUtils.createSession(controllerObject.getClass().getSimpleName(), "${SessionServer}", ""));
                switch (method.getRequestType()) {
                    case GET:
                        KeywordUtils.getRequest(controllerObject.getClass().getSimpleName(), "${SessionServer}", "");
                        break;
                    case PUT:
                        break;
                    case HEAD:
                        break;
                    case POST:
                        KeywordUtils.postRequest(controllerObject.getClass().getSimpleName(), "${SessionServer}", "", "");
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
            .controllerClass(controllerObject.getControllerClass())
            .settings(settings)
            .variables(variables)
            .keywords(keywords)
            .task(new ArrayList<>())
            .build();
    }

    public void run(String pack, String dir) {
        List<Robot> robots = generate(pack);
        RobotWriter.generateRobotFile(robots, dir);
    }

    public static void main(String[] args) {
        RobotGenerater robotGenerater = new RobotGenerater();
        robotGenerater.run("com.robotgenerate", robotGenerater.getClass().getResource("/").toString().substring(5));
    }

}
