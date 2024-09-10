
public class TaskItem {
	private String text;
	private Boolean isCompleted;
	private int priority;
	
	public TaskItem(String text) {
		this.text = text;
		this.isCompleted = false;
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public Boolean getIsCompleted() {
		return isCompleted;
	}
	
	public int getpriority() {
		return priority;
	}
	
	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	public void setIsCompleted(Boolean isCompleted) {
		this.isCompleted = isCompleted;
	}
	
	public void completeTask() {
		this.isCompleted = !this.isCompleted;
	}
}