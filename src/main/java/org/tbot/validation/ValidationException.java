/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tbot.validation;

/**
 *
 * @author alex
 */
public class ValidationException extends Exception {

    private String message;

    public ValidationException(Exception ex) {
        super(ex);
        this.message = ex.getMessage();
    }

    public ValidationException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}
