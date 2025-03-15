package com.devanshi.automatavisualizer.util;

import com.devanshi.automatavisualizer.model.DFA;
import com.devanshi.automatavisualizer.model.State;

public class LanguageParser {

    /**
     * Creates a simple DFA that accepts strings ending with the given suffix.
     */
    public static DFA createEndsWith(String suffix) {
        DFA dfa = new DFA();

        // Add all symbols to alphabet
        for (char c : suffix.toCharArray()) {
            dfa.addSymbol(c);
        }

        // Create states
        State[] states = new State[suffix.length() + 1];
        for (int i = 0; i <= suffix.length(); i++) {
            boolean isAccepting = (i == suffix.length());
            boolean isInitial = (i == 0);
            states[i] = new State("q" + i, isInitial, isAccepting);
            dfa.addState(states[i]);
        }

        // Add transitions
        for (int i = 0; i < suffix.length(); i++) {
            char symbol = suffix.charAt(i);

            for (int j = 0; j < suffix.length(); j++) { // Ensuring j doesn't go beyond valid range
                if (suffix.charAt(j) == symbol && j + 1 <= suffix.length()) {
                    states[j].addTransition(symbol, states[j + 1]); // Avoid out-of-bounds access
                } else {
                    // Compute longest suffix-prefix fallback
                    int backTo = 0;
                    for (int k = j; k > 0; k--) {
                        if (suffix.substring(0, k).equals(suffix.substring(j - k + 1, j + 1))) {
                            backTo = k;
                            break;
                        }
                    }
                    states[j].addTransition(symbol, states[backTo]);
                }
            }
        }

        return dfa;
    }

    /**
     * Creates a simple DFA that accepts strings containing the given substring.
     */
    public static DFA createContains(String substring) {
        DFA dfa = new DFA();

        // Add all symbols to alphabet
        for (char c : substring.toCharArray()) {
            dfa.addSymbol(c);
        }

        // Create states
        State[] states = new State[substring.length() + 1];
        for (int i = 0; i <= substring.length(); i++) {
            boolean isAccepting = (i == substring.length());
            boolean isInitial = (i == 0);
            states[i] = new State("q" + i, isInitial, isAccepting);
            dfa.addState(states[i]);
        }

        // Add transitions
        for (int i = 0; i < substring.length(); i++) {
            for (char s : dfa.getAlphabet()) {
                if (substring.charAt(i) == s && i + 1 <= substring.length()) {
                    states[i].addTransition(s, states[i + 1]); // Ensure valid index access
                } else {
                    // Compute fallback state
                    int backTo = 0;
                    for (int k = 1; k < i; k++) {
                        if (substring.substring(0, k).equals(substring.substring(i - k, i))) {
                            backTo = k;
                        }
                    }

                    if (backTo < substring.length() && substring.charAt(backTo) == s) {
                        states[i].addTransition(s, states[backTo + 1]); // Ensure no out-of-bounds access
                    } else {
                        states[i].addTransition(s, states[0]);
                    }
                }
            }
        }

        // From accepting state, stay there for all inputs
        for (char symbol : dfa.getAlphabet()) {
            states[substring.length()].addTransition(symbol, states[substring.length()]);
        }

        return dfa;
    }
}
