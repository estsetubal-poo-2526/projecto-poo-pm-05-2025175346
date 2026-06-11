package org.example;

import javafx.application.Application;
import org.example.View.App;

public class Main {
    public static void main(String[] args) {
        // Esta linha inicia o ciclo de vida do JavaFX e vai chamar o método start() da classe App
        Application.launch(App.class, args);
    }
}
