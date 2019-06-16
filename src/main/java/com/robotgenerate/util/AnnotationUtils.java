package com.robotgenerate.util;


import org.junit.Assert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class AnnotationUtils {

	public static boolean classContainAnnotation(Class cla, Class annotation) {
		Assert.assertNotNull(cla);
		Assert.assertNotNull(annotation);
		Annotation[] annotations = cla.getAnnotations();
		for (Annotation annotation1 : annotations){
			String name = annotation1.annotationType().getName();
			System.out.println(name);
		}
		return cla.getAnnotation(annotation) != null;
	}

	public static boolean methonContainAnnotation(Class cla, Class annotation) {
		Assert.assertNotNull(cla);
		Assert.assertNotNull(annotation);
		Method[] methods = cla.getMethods();
		for (Method method : methods) {
			if (methonContainAnnotation(method, annotation)) {
				return true;
			}
		}
		return false;
	}

	public static boolean methonContainAnnotation(Method method, Class annotation) {
		Assert.assertNotNull(method);
		Assert.assertNotNull(annotation);
		for (Annotation annotation1 : method.getAnnotations()){
			String name = annotation1.annotationType().getSimpleName();
			System.out.println(name);
		}
		return method.getAnnotation(annotation) != null;
	}
}
