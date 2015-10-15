package semanticAnalyser;

/*
 * This class has the assembly language to be executed in the MVN
 */

public class Assembly {

	private static Command first = null;
	private static Command last = null; 
	
	public static void add_command(String operator, String operand, String label) {
		if (first == null) {
			first = new Command(operator, operand, label);
			last = first;
		} else {
			Command new_last = new Command(operator, operand, label);
			last.next = new_last;
			last = new_last;
		}
	}
	
	public static void print_assembly() {
		System.out.println("MVN code in Assembly");
		for (Command curr = first; curr != null; curr = curr.next)
			System.out.println(curr.get_label() + "	" + curr.get_operator() + "	" + curr.get_operand());
	}
	
	public static void print_last() {
		if (last != null)
			System.out.println(last.get_label() + "	" + last.get_operator() + "	" + last.get_operand());
	}
	
	private static class Command {
		private String operator, operand, label;
		private Command next;
		
		public Command(String operator, String operand, String label) {
			this.operator = operator;
			this.operand = operand;
			this.label = label;
			this.next = null;
		}
		
		public String get_operator() {return operator;}
		public String  get_operand() {return operand; }
		public String get_label() { return label; }
	}
}
