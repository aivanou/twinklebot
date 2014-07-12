/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tbot.parse;

/**
 *
 * @author alex
 */
public class ParserException extends Exception {

    private String message;

    public ParserException(Exception ex) {
        super(ex);
        this.message = ex.getMessage();
    }

    public ParserException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}
