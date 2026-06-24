package timertaskserver.workserver.service;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ClasspathDirectoryClassesFinder {

    public static List<Class<?>> findClassesInClasspathDirectory(String directoryPath) {
        List<Class<?>> classes = new ArrayList<>();
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            URL resource = classLoader.getResource(directoryPath);

            if (resource != null) {
                File directory = new File(resource.toURI());
                findClassesInDirectory(classes, directory, directoryPath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classes;
    }

    private static void findClassesInDirectory(List<Class<?>> classes, File directory, String basePath) {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles((file) -> file.getName().endsWith(".class") || file.isDirectory());
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        findClassesInDirectory(classes, file, basePath + "/" + file.getName());
                    } else {
                        String className = basePath + "/" + file.getName().replace(".class", "");
                        className = className.replace('/', '.').replace('\\', '.');
                        try {
                            Class<?> clazz = Class.forName(className);
                            classes.add(clazz);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        // 指定目录路径，例如 "com/example/mypackage"
        String directoryPath = "timertaskserver/workserver/service/TimerTask";
        List<Class<?>> classes = findClassesInClasspathDirectory(directoryPath);
        for (Class<?> clazz : classes) {
            System.out.println(clazz.getName());
        }
    }
}
