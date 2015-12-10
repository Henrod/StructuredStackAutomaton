package semanticAnalyser;

public class Stack {

	private static Level top = null;

	public static String[] pop() {
		String[] top_command = { top.operator, top.operand, top.label };
		top = top.next;

		return top_command;
	}

	public static String[] peak() {
		return new String[] { top.operator, top.operand, top.label };
	}

	public static void push(String operator, String operand, String label) {
		top = new Level(operator, operand, label, top);
	}

	public static boolean isEmpty() {
		return top == null;
	}

	public static void print() {
		System.out.println("_______PILHA_______");
		for (Level curr = top; curr != null; curr = curr.next)
			System.out
					.println("| " + curr.operator + " " + curr.operand + " |");
		System.out.println("___________________");
	}

	public static boolean contains(String word) {
		for (Level curr = top; curr != null; curr = curr.next)
			return curr.operator.equals(word) || curr.operand.equals(word)
					|| curr.label.equals(word);
		return false;
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
