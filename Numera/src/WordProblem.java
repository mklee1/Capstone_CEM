import java.util.Set;

public class WordProblem {
    
	// should not be mutable
	private final String description;
	private final Set<String> solutionSet;
	
	public WordProblem(String description, Set<String> solutionSet) {
		this.description = description;
		this.solutionSet = solutionSet;
	}
	
	public String getDescription() {
		return description;
	}
	
	public boolean containsSolution(String solution) {
		return solutionSet.contains(solution);
	}
	
}
