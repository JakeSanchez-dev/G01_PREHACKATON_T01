package com.prehackaton.eventos;

import javafx.application.Application;

/**
 * Punto de entrada de la aplicación (clase ejecutable).
 *
 * ¿Por qué esta clase NO extiende javafx.application.Application?
 * Porque el JDK se niega a ejecutar directamente una clase que extiende
 * Application cuando JavaFX está en el classpath (dependencias Maven) y no
 * en el module-path, produciendo el error:
 *
 *   "Error: JavaFX runtime components are missing, and are required
 *    to run this application"
 *
 * La ventana JavaFX vive en App.java; desde aquí solo se lanza.
 * Ejecutar "Run 'Main'" en IntelliJ funciona sin configurar nada extra,
 * igual que en consola (java -cp ...) y con Maven (mvn javafx:run).
 */
public class Main {

    public static void main(String[] args) {
        Application.launch(App.class, args);
    }
}
