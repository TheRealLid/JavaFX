import javafx.scene.control.TextField;

import java.util.ArrayList;

import java.util.List;


import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;

import javafx.stage.Stage;
import javafx.util.Pair;
import javafx.scene.layout.VBox;
import javafx.scene.control.ListCell;


public class JavaFX extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Text field for user input
        TextField input = new TextField();
        input.setPromptText("Enter a task");

        // Create a button that the user will click to submit their input
        Button addButton = new Button("Add Task");       
        
        // Creates a button that the user will click to remove all check marked items
        Button clearCheckedItems = new Button("Clear Checked Tasks");
        
        //undo button for the most recent delete action
        Button undoLastDelete = new Button("Undo");
        
        // Stores items and checkBoxes for easier deletion alter
        List<Pair<String, CheckBox>> taskCheckBoxes = new ArrayList<>(); 
        
        // stores the list of tasks to be displayed
        ObservableList<String> tasks = FXCollections.observableArrayList();
        ListView<String> listView = new ListView<>(tasks);
        
        //adds a checkbox next to each listview item
        listView.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { // prevents empty items from being created.
                    setGraphic(null); 
                } else {
                    CheckBox checkBox = new CheckBox(item);
                    taskCheckBoxes.add(new Pair<>(item, checkBox));
                    setGraphic(checkBox);  
                }
            }
        });


        
        // Defines the buttons action upon click
        addButton.setOnAction(event -> {
            String task = input.getText(); // Get the text from the input field
            if(!task.isEmpty()) {
            	tasks.add(task);
            	input.clear();
            }
        });
    	List<Pair<String, CheckBox>> itemsToRemove = new ArrayList<>();
        clearCheckedItems.setOnAction(event -> {
        	itemsToRemove.clear();
        	

            for (Pair<String, CheckBox> pair : taskCheckBoxes) { 
                if (pair.getValue().isSelected()) {
                    itemsToRemove.add(pair);
                }
            }
            // Remove the checked tasks from both taskCheckBoxes and tasks
            for (Pair<String, CheckBox> pair : itemsToRemove) {
                taskCheckBoxes.remove(pair);
                tasks.remove(pair.getKey());
            }
        });

        //Restores the most recent delete action
        undoLastDelete.setOnAction(event ->{
        	if(!itemsToRemove.isEmpty()) {
        		for (Pair<String, CheckBox> pair : itemsToRemove) {
        			System.out.println(pair.getKey());
        			taskCheckBoxes.add(pair);
        			tasks.add(pair.getKey());
        		}
        		itemsToRemove.clear();
        	}
        });
        
        VBox root = new VBox(10);

        root.getChildren().addAll(input, addButton, listView, clearCheckedItems, undoLastDelete);

        Scene scene = new Scene(root, 300, 250);
        scene.getStylesheets().add(getClass().getResource("/resources/style.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}