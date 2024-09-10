//JavaFX imports
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Pair;

//Util Imports
import java.util.ArrayList;
import java.util.List;

public class TaskManagerApp extends Application {

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
        List<Pair<Text, CheckBox>> taskCheckBoxes = new ArrayList<>(); 
        
        // stores the list of tasks to be displayed
        ObservableList<String> tasks = FXCollections.observableArrayList();
        ListView<String> listView = new ListView<>(tasks);
        
        //adds a checkbox next to each listview item
        //listens for changes to the checkbox states and strikes the text when checked
        listView.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { // prevents empty items from being created.
                    setGraphic(null); 
                } else {
                	Text text = new Text(item); //assign the actual text to the Text class instance.
                    CheckBox checkBox = new CheckBox();
	                checkBox.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
	                    if (isSelected) {
	                        text.setStrikethrough(true); // Apply strikethrough
	                    } else {
	                        text.setStrikethrough(false); // Remove strikethrough
	                    }
	                });
	                // Add the pair to the taskCheckBoxes list
	                taskCheckBoxes.add(new Pair<>(text, checkBox));
	                HBox hbox = new HBox(10); // 10 is spacing between CheckBox and Text
	                hbox.getChildren().addAll(checkBox, text);

	                setGraphic(hbox); 
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
    	List<Pair<Text, CheckBox>> itemsToRemove = new ArrayList<>();
        clearCheckedItems.setOnAction(event -> {
        	//Only erases items if there is atleast one checked item. This prevents clearing the history so undo will work better
            for (Pair<Text, CheckBox> pair : taskCheckBoxes) { 
                if (pair.getValue().isSelected()) {
                	itemsToRemove.clear();
                	break;
                }
            }
        	
            for (Pair<Text, CheckBox> pair : taskCheckBoxes) { 
                if (pair.getValue().isSelected()) {
                    itemsToRemove.add(pair);
                }
            }
            // Remove the checked tasks from both taskCheckBoxes and tasks
            for (Pair<Text, CheckBox> pair : itemsToRemove) {
                taskCheckBoxes.remove(pair);
                tasks.remove(pair.getKey().getText());
            }
        });

        //Restores the most recent delete action
        undoLastDelete.setOnAction(event ->{
        	if(!itemsToRemove.isEmpty()) {
        		for (Pair<Text, CheckBox> pair : itemsToRemove) {
        			tasks.add(pair.getKey().getText());
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