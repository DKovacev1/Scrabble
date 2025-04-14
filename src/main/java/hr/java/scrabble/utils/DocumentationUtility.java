package hr.java.scrabble.utils;

import hr.java.scrabble.game.GameConstants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.stream.Collectors;

public class DocumentationUtility {

    private DocumentationUtility(){}

    public static void generateDocumentation() {
        try(FileOutputStream fileOutputStream = new FileOutputStream(GameConstants.DOCUMENTATION_PATH_AND_FILE)){
            String topOfDocument = """
                                    <!DOCTYPE html>
                                    <html>
                                    <head>
                                    <title>Documentation</title>
                                    </head>
                                    <body>
                                    <h1>List of classes used for scrabble game:</h1>
                                    """;

            String bottomOfDocument = """
                                    </body>
                                    </html>
                                    """;

            StringBuilder documentBuilder = new StringBuilder();
            documentBuilder.append(topOfDocument);

            File javaFiles = new File(GameConstants.JAVA_FILE_PATH);
            Files.walk(javaFiles.toPath())
                    .filter(path -> path.getFileName().toString().contains(".java") && !path.getFileName().toString().equals("module-info.java"))
                    .forEach(path -> {
                        String className = path.toString().replace("src\\main\\java\\", "");
                        className = className.replace("\\", ".");
                        className = className.substring(0, className.length() - 5);
                        System.out.println(className);
                        try {
                            Class<?> aClass = Class.forName(className);
                            documentBuilder.append(getDataForClass(aClass));
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    });

            documentBuilder.append(bottomOfDocument);

            fileOutputStream.write(documentBuilder.toString().getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static String getDataForClass(Class<?> aClass) {
        StringBuilder classDataBuilder = new StringBuilder();
        classDataBuilder.append("<h3>" + aClass.getSimpleName() + "</h3>\n");
        classDataBuilder.append("<p> - package: "+ aClass.getPackageName() + " </p>");

        //atributi
        classDataBuilder.append("<p> - fields: </p>");
        classDataBuilder.append("<ul>");
        classDataBuilder.append(
                Arrays.stream(aClass.getDeclaredFields())
                        .map(field ->
                                "<li>" + getFieldData(field) + "</li>")
                        .collect(Collectors.joining())
        );
        classDataBuilder.append("</ul>");

        //konstruktori
        classDataBuilder.append("<p> - constructors: </p>");
        classDataBuilder.append("<ul>");
        classDataBuilder.append(
                Arrays.stream(aClass.getDeclaredConstructors())
                        .map(constructor ->
                                "<li>" + getConstructorData(constructor) + "</li>")
                        .collect(Collectors.joining())
        );
        classDataBuilder.append("</ul>");

        //metode
        classDataBuilder.append("<p> - methods: </p>");
        classDataBuilder.append("<ul>");
        classDataBuilder.append(
                Arrays.stream(aClass.getDeclaredMethods())
                        .map(method ->
                                "<li>" + getMethodData(method) + "</li>")
                        .collect(Collectors.joining())
        );
        classDataBuilder.append("</ul>");


        return classDataBuilder.toString();
    }

    private static String getConstructorData(Constructor<?> constructor) {
        StringBuilder constructorData = new StringBuilder();
        constructorData.append("<p> constructor modifiers: " + getModifiers(constructor.getModifiers()) + "</p>");
        constructorData.append("<p> constructor name: " + constructor.getName() + "</p>");
        constructorData.append("<p> constructor parameters: " + getParametersData(constructor.getParameters()) + "</p>");

        return constructorData.toString();
    }

    private static String getParametersData(Parameter[] parameters) {
        StringBuilder methodData = new StringBuilder();
        methodData.append("<ul>" +
                Arrays.stream(parameters).map(parameter -> "<li>"
                        + "<p> parameter type: " + parameter.getType() + " </p>"
                        + "<p> parameter name: " + parameter.getName() + " </p>"
                        + "</li>")
                        .collect(Collectors.joining())
                + "</ul>");

        return methodData.toString();
    }

    private static String getMethodData(Method method) {
        StringBuilder methodData = new StringBuilder();
        methodData.append("<p> method modifiers: " + getModifiers(method.getModifiers()) + "</p>");
        methodData.append("<p> method name: " + method.getName() + "</p>");
        methodData.append("<p> method parameters: " + getParametersData(method.getParameters()) + "</p>");
        methodData.append("<p> method return type: " + method.getReturnType() + "</p>");

        return methodData.toString();
    }

    private static String getFieldData(Field field) {
        StringBuilder fieldData = new StringBuilder();
        fieldData.append("<p> field modifiers: " + getModifiers(field.getModifiers()) + "</p>");
        fieldData.append("<p> field name: " + field.getName() + "</p>");
        fieldData.append("<p> field type: " + field.getType() + "</p>");

        return fieldData.toString();
    }

    private static String getModifiers(int modifiers){
        StringBuilder modifiersSb = new StringBuilder();

        if (Modifier.isPublic(modifiers))
            modifiersSb.append("public ");
        if (Modifier.isPrivate(modifiers))
            modifiersSb.append("private ");
        if (Modifier.isProtected(modifiers))
            modifiersSb.append("protected ");
        if (Modifier.isStatic(modifiers))
            modifiersSb.append("static ");
        if (Modifier.isFinal(modifiers))
            modifiersSb.append("final ");
        if (Modifier.isTransient(modifiers))
            modifiersSb.append("transient ");
        if (Modifier.isVolatile(modifiers))
            modifiersSb.append("volatile ");

        return modifiersSb.toString();
    }

}