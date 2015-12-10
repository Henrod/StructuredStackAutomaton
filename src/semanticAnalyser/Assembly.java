package semanticAnalyser;

import Components.Automaton;
import Lists.SymbolsTable;

/*
 * This class has the assembly language to be executed in the MVN
 */

public class Assembly {

	private static Command first = null;
	private static Command last = null;

	public static void add_command(String operator, String operand, String label) {
		if (first == null || last == null) {
			first = new Command(operator, operand, label);
			last = first;
		} else {
			Command new_last = new Command(operator, operand, label);
			last.next = new_last;
			last = new_last;
		}
	}

	public static void add_constant(String name, int value) {
		SymbolsTable.add_symbol(name, Automaton.scope);
		
		Command new_first = new Command("K", "/" + convert_to_hexa(value), name);
		new_first.next = first;
		first = new_first;
		
		if (last == null) last = first;
	}

	private static String convert_to_hexa(int value) {
		String hexa_number = "";

		for (int i = 0; i < 4; i++) {
			int rest = value % 16;
			value /= 16;
			
			switch(rest) {
			case 10:
				hexa_number = "A" + hexa_number;
			case 11:
				hexa_number = "B" + hexa_number;
			case 12:
				hexa_number = "C" + hexa_number;
			case 13:
				hexa_number = "D" + hexa_number;
			case 14:
				hexa_number = "E" + hexa_number;
			case 15:
				hexa_number = "F" + hexa_number;
			default:
				hexa_number = rest + hexa_number;
			}
		}

		return hexa_number;
	}

	public static void print_assembly() {
		Command curr = first;

		System.out.println("MVN code in Assembly");
		System.out.println("	@	/0000");
		System.out.println("	JP	BEGIN");

		for (; curr != null; curr = curr.next) {
			if (curr.operator.equals("K")) {
				System.out.println(curr.get_label() + "	" + curr.get_operator()
						+ "	" + curr.get_operand());
			} else {
				System.out.println("BEGIN	" + curr.get_operator() + "	"
						+ curr.get_operand());
				curr = curr.next;
				break;
			}
		}

		for (; curr != null; curr = curr.next)
			System.out.println(curr.get_label() + "	" + curr.get_operator()
					+ "	" + curr.get_operand());
	}

	public static void print_last() {
		if (last != null)
			System.out.println(last.get_label() + "	" + last.get_operator()
					+ "	" + last.get_operand());
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

		public String get_operator() {
			return operator;
		}

		public String get_operand() {
			return operand;
		}

		public String get_label() {
			return label;
		}
	}
}
