package com.devanshi.automatavisualizer.util;

import com.devanshi.automatavisualizer.model.DFA;
import com.devanshi.automatavisualizer.model.State;
import guru.nidi.graphviz.attribute.*;
import guru.nidi.graphviz.engine.*;
import guru.nidi.graphviz.model.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static guru.nidi.graphviz.model.Factory.*;

public class DFAVisualizer {
    public static void visualize(DFA dfa, String outputFilePath) throws IOException {
        MutableGraph graph = mutGraph("dfa").setDirected(true)
                .graphAttrs().add(Rank.dir(Rank.RankDir.LEFT_TO_RIGHT))
                .graphAttrs().add("dpi", "100")
                .nodeAttrs().add(Shape.CIRCLE, Font.size(12));

        // Store nodes to prevent duplicate creation
        Map<String, MutableNode> nodes = new HashMap<>();

        // Add all states as nodes
        for (State state : dfa.getStates()) {
            MutableNode node = mutNode(state.getName());
            if (state.isAccepting()) {
                node.add(Shape.DOUBLE_CIRCLE);
            }
            if (state.isInitial()) {
                node.add(Style.FILLED, Color.LIGHTBLUE);
            }
            nodes.put(state.getName(), node);
            graph.add(node);  // ✅ Ensure nodes are added properly
        }

        // Add all transitions as edges
        for (State state : dfa.getStates()) {
            MutableNode sourceNode = nodes.get(state.getName());
            for (char symbol : dfa.getAlphabet()) {
                State target = state.getTransition(symbol);
                if (target != null) {
                    MutableNode targetNode = nodes.get(target.getName());
                    sourceNode.addLink(to(targetNode).with(Label.of(String.valueOf(symbol))));
                }
            }
        }

        // Generate and save the visualization
        Graphviz.fromGraph(graph)
                .width(800)
                .render(Format.PNG)
                .toFile(new File(outputFilePath));
    }
}