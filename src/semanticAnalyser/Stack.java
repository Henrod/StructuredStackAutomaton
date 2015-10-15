package semanticAnalyser;

public class Stack {
	
	private static Level top = null;
	
	public static String[] pop() {
		String[] top_command = {top.operator, top.operand, top.label};
		top = top.next;
		
		return top_command;
	}
	
	public static String[] peak() {
		return new String[] {top.operator, top.operand, top.label};
	}
	
	public static void push(String operator, String operand, String label) {
		top = new Level(operator, operand, label, top);
	}
	
	public static boolean isEmpty() {
		return top == null;
	}
	
	public static void print() {
		for (Level curr = top; curr != null; curr = curr.next)
			System.out.println("___________" + curr.operator + " " + curr.operand + "___________");
	}
	
	private static class Level {
		private String operator, operand, label;
		private Level next;
		
		public Level(String operator, String operand, String label, Level next) {
			this.operator = operator;
			this.operand = operand;
			this.label = label;
			this.next = next;
		}
	}
}