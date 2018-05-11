public class WordProblem {
    
	// should not be mutable
	private final String description;
	private final int solution;
	private int userAnswer;
	
	public WordProblem(String description, int solution) {
		this.description = description;
		this.solution = solution;
		userAnswer = 0;
	}
	
	public String getDescription() {
		return description;
	}
	
	public int getSolution() {
		return solution;
	}
	
	public int getUserAnswer() {
		return userAnswer;
	}
	
	public void setUserAnswer(int userAnswer) {
		this.userAnswer = userAnswer;
	}
	
}
