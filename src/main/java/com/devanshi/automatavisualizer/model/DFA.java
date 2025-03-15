package com.devanshi.automatavisualizer.model;

import java.util.HashSet;
import java.util.Set;

public class DFA {
    private Set<State> states;
    private Set<Character> alphabet;
    private State initialState;

    public DFA() {
        this.states = new HashSet<>();
        this.alphabet = new HashSet<>();
    }

    public void addState(State state) {
        states.add(state);
        if (state.isInitial()) {
            this.initialState = state;
        }
    }

    public void addSymbol(char symbol) {
        alphabet.add(symbol);
    }

    public boolean accepts(String input) {
        if (initialState == null) {
            throw new IllegalStateException("DFA has no initial state");
        }

        State currentState = initialState;
        for (char c : input.toCharArray()) {
            if (!alphabet.contains(c)) {
                return false; // Reject if the input contains an invalid symbol
            }

            State nextState = currentState.getTransition(c);
            if (nextState == null) {
                return false; // Reject if there is no valid transition
            }

            currentState = nextState;
        }

        return currentState.isAccepting(); // Accept if end state is a final state
    }

    public Set<State> getStates() {
        return states;
    }

    public Set<Character> getAlphabet() {
        return alphabet;
    }

    public State getInitialState() {
        return initialState;
    }
}
