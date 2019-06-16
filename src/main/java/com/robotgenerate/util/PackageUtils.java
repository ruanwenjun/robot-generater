package com.robotgenerate.util;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PackageUtils {

	private static String classEnd = ".class";

	/**
	 * get all class full name under the package
	 *
	 * @param packageName like com.xx.xx
	 * @return empty if there is no java class under the given package
	 */
	public static List<Class> getAllClassNames(String packageName) {
		String dirPath = packageName.replaceAll("\\.", "/");
		URL resource = PackageUtils.class.getClassLoader().getResource(dirPath);
		if (resource == null) {
			return new ArrayList<>();
		}
		String dir = resource.getFile();
		File file = new File(dir);
		if (!file.exists()) {
			return new ArrayList<>();
		}
		return getAllClassFromFile(packageName, file);
	}

	/**
	 * @param packageName
	 * @param file
	 * @return
	 */
	private static List<Class> getAllClassFromFile(String packageName, File file) {
		List<Class> classList = new ArrayList<>();
		if (!file.exists()) {
			return classList;
		}
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			if (null == files) {
				return classList;
			}
			Arrays.stream(files)
				.forEach(childFile -> {
					if (childFile.isDirectory()) {
						classList.addAll(getAllClassFromFile(packageName + "." + childFile.getName(), childFile));
					} else if (childFile.isFile() && childFile.getName().endsWith(classEnd)) {
						classList.addAll(getAllClassFromFile(packageName, childFile));
					}
				});
		} else if (file.isFile() && file.getName().endsWith(classEnd)) {
			try {
				String className = packageName + "." + file.getName();
				Class<?> aClass = Class.forName(className.replace(classEnd, ""));
				classList.add(aClass);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return classList;
	}

}
