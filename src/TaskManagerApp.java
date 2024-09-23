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
import javafx.scene.input.KeyCode;
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
        
        
        //edit existing task
        Button edit = new Button("Edit");
        // Stores items and checkBoxes for easier deletion alter
        List<Pair<TaskItem, CheckBox>> taskCheckBoxes = new ArrayList<>(); 
        
        // stores the list of tasks to be displayed
        ObservableList<TaskItem> tasks = FXCollections.observableArrayList();
        ListView<TaskItem> listView = new ListView<>(tasks);
        listView.setEditable(true);
        
        //adds a checkbox next to each listview item
        //listens for changes to the checkbox states and strikes the text when checked
        listView.setCellFactory(lv -> new ListCell<TaskItem>() {
        	private TextField textField;
        	
        	@Override
        	public void startEdit() {
        		super.startEdit();
        		if(textField == null) {
        			textField = new TextField(getItem().getText());
        		}else {
        			textField.setText(getItem().getText());
        		}
        		
        		textField.setOnAction(event -> commitEdit(getItem()));
        		textField.setOnKeyPressed(event->{
        			if(event.getCode() == KeyCode.ESCAPE) {
        				cancelEdit();
        			}
        		});
        		setGraphic(textField); //Turns the text into an editable textfield
        		textField.selectAll(); //Selects all text
        	}
        	
        	
        	@Override
        	public void commitEdit(TaskItem updatedTaskItem) {
        		super.commitEdit(updatedTaskItem);
        		updatedTaskItem.setText(textField.getText());// updates the TaskItems internal text string
        		setGraphic(null); // Switches back to display mode *****
        		updateItem(updatedTaskItem, false); //update
        	}
        	@Override
        	public void cancelEdit() {
        	    super.cancelEdit();
        	    setGraphic(null);
        	    updateItem(getItem(), false); 
        	}
        	
            @Override
            protected void updateItem(TaskItem task, boolean empty) {
                super.updateItem(task, empty);
                if (empty || task == null) { // prevents empty items from being created.
                    setGraphic(null); 
                } else {
                	Text text = new Text(task.getText()); //assign the actual text to the Text class instance.
                    CheckBox checkBox = new CheckBox();
                    
                    checkBox.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
                        task.setIsCompleted(isSelected);
                        text.setStrikethrough(isSelected); // Apply/Remove strikethrough
                    });

	                // Add the pair to the taskCheckBoxes list
	                taskCheckBoxes.add(new Pair<>(task, checkBox));
	                HBox hbox = new HBox(10); // 10 is spacing between CheckBox and Text
	                hbox.getChildren().addAll(checkBox, text);

	                setGraphic(hbox); 
                }
            }
        });


        
        // Defines the buttons action upon click
        addButton.setOnAction(event -> {
            String taskText = input.getText(); // Get the text from the input field
            if(!taskText.isEmpty()) {
            	TaskItem newTask = new TaskItem(taskText);
            	tasks.add(newTask);
            	input.clear();
            }
        });
        
        input.setOnAction(event -> addButton.fire()); //allows the user to press enter instead of pressing the AddButton button.
        
    	List<Pair<TaskItem, CheckBox>> itemsToRemove = new ArrayList<>();
        clearCheckedItems.setOnAction(event -> {
        	//Only erases items if there is atleast one checked item. This prevents clearing the history so undo will work better
            for (Pair<TaskItem, CheckBox> pair : taskCheckBoxes) { 
                if (pair.getValue().isSelected()) {
                	itemsToRemove.clear();
                	break;
                }
            }
        	
            for (Pair<TaskItem, CheckBox> pair : taskCheckBoxes) { 
                if (pair.getValue().isSelected()) {
                    itemsToRemove.add(pair);
                }
            }
            // Remove the checked tasks from both taskCheckBoxes and tasks
            for (Pair<TaskItem, CheckBox> pair : itemsToRemove) {
                taskCheckBoxes.remove(pair);
                tasks.remove(pair.getKey());
            }
        });

        //Restores the most recent delete action
        undoLastDelete.setOnAction(event ->{
        	if(!itemsToRemove.isEmpty()) {
        		for (Pair<TaskItem, CheckBox> pair : itemsToRemove) {
        			tasks.add(pair.getKey());
        		}
        		itemsToRemove.clear();
        	}
        });
        
        edit.setOnAction(event -> {

        	int selectedIndex = listView.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) { // Ensure something is selected
                listView.edit(selectedIndex); // Start editing the selected cell
            }
        	
        });
        
        VBox root = new VBox(10);

        root.getChildren().addAll(input, addButton, listView, clearCheckedItems, undoLastDelete, edit);

        Scene scene = new Scene(root, 300, 250);
        scene.getStylesheets().add(getClass().getResource("/resources/style.css").toExternalForm());
        primaryStage.setTitle("TODO LIST");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}