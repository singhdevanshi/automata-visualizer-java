package com.devanshi.automatavisualizer.view;

import com.devanshi.automatavisualizer.model.DFA;
import com.devanshi.automatavisualizer.util.DFAVisualizer;
import com.devanshi.automatavisualizer.util.LanguageParser;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main extends Application {

    private ImageView dfaImageView;
    private TextField patternField;
    private ComboBox<String> patternTypeComboBox;
    private TextField testInputField;
    private Label resultLabel;
    private DFA currentDfa;
    private Path tempImagePath;

    @Override
    public void start(Stage primaryStage) {
        try {
            // Create a temporary file for storing the DFA image
            tempImagePath = Files.createTempFile("dfa_", ".png");
            tempImagePath.toFile().deleteOnExit(); // Ensure it gets deleted after execution
        } catch (IOException e) {
            showError("Initialization Error", "Failed to create temporary file: " + e.getMessage());
            return;
        }

        BorderPane root = new BorderPane();
        
        // Controls section
        VBox controls = new VBox(10);
        controls.setPadding(new Insets(10));
        
        Label patternLabel = new Label("Pattern:");
        patternField = new TextField();
        
        Label typeLabel = new Label("Pattern Type:");
        patternTypeComboBox = new ComboBox<>();
        patternTypeComboBox.getItems().addAll("Ends With", "Contains");
        patternTypeComboBox.setValue("Ends With");
        
        Button generateButton = new Button("Generate DFA");
        generateButton.setOnAction(e -> generateDFA());
        
        Label testLabel = new Label("Test String:");
        testInputField = new TextField();
        
        Button testButton = new Button("Test Input");
        testButton.setOnAction(e -> testInput());
        
        resultLabel = new Label();
        
        controls.getChildren().addAll(patternLabel, patternField, typeLabel, 
                                    patternTypeComboBox, generateButton,
                                    testLabel, testInputField, testButton, resultLabel);
        
        // Display section
        dfaImageView = new ImageView();
        dfaImageView.setPreserveRatio(true);
        dfaImageView.setFitWidth(700);
        
        // Layout
        root.setLeft(controls);
        root.setCenter(dfaImageView);
        
        Scene scene = new Scene(root, 1000, 600);
        primaryStage.setTitle("Automata Visualizer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private void generateDFA() {
        String pattern = patternField.getText().trim();
        if (pattern.isEmpty()) {
            showError("Input Error", "Please enter a valid pattern.");
            return;
        }
        
        try {
            // Generate DFA based on selected type
            if (patternTypeComboBox.getValue().equals("Ends With")) {
                currentDfa = LanguageParser.createEndsWith(pattern);
            } else {
                currentDfa = LanguageParser.createContains(pattern);
            }
            
            // Visualize DFA and save it as an image
            DFAVisualizer.visualize(currentDfa, tempImagePath.toString());

            // Display DFA image
            dfaImageView.setImage(new Image(tempImagePath.toUri().toString()));
            
            resultLabel.setText("DFA generated successfully.");
        } catch (Exception e) {
            showError("Generation Error", "Failed to generate DFA: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void testInput() {
        if (currentDfa == null) {
            showError("Test Error", "Please generate a DFA first.");
            return;
        }
        
        String input = testInputField.getText().trim();
        if (input.isEmpty()) {
            showError("Input Error", "Please enter a string to test.");
            return;
        }

        try {
            boolean accepted = currentDfa.accepts(input);
            resultLabel.setText("Input \"" + input + "\" is " + 
                               (accepted ? "ACCEPTED" : "REJECTED") + " by the DFA");
        } catch (Exception e) {
            showError("Test Error", "Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void stop() {
        // Clean up temporary file
        try {
            Files.deleteIfExists(tempImagePath);
        } catch (IOException e) {
            System.err.println("Failed to delete temporary file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
