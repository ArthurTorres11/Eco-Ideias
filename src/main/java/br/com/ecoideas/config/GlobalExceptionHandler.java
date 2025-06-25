// src/main/java/br/com/ecoideas/config/GlobalExceptionHandler.java

package br.com.ecoideas.config;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public String handleRuntimeException(RuntimeException ex, Model model) {
        System.err.println("Erro de negócio capturado: " + ex.getMessage());

        model.addAttribute("errorMessage", ex.getMessage());

        return "error/error-page";
    }

    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception ex, Model model) {
        System.err.println("Erro inesperado capturado: " + ex.getMessage());

        model.addAttribute("errorMessage", "Ocorreu um erro inesperado. Por favor, tente novamente mais tarde.");

        return "error/error-page";
    }
}