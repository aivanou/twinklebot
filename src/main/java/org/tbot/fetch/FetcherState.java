/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tbot.fetch;

/**
 *
 * @author alex
 */
public enum FetcherState {

    Anonym("anonym"),
    Named("named");
    private String name;
    private String state;

    FetcherState(String state) {
        this.state = state;
    }

    public synchronized void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.state;
    }
}
