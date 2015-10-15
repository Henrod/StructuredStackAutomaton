package semanticAnalyser;

public class SemanticAnalyser {
	
	/* boolean indicates if next symbol will be pushed or analyzed in arithmetic expression*/
	private static boolean get_next_symbol = false, start_exp = false;
	private static String partial_operand = "";
	private static int counter = 1, label  = 1;
	private static String[] command;
	private static String variable_first_half_decision = "", 
			variable_second_half_decision = "";
	private static String first_label = "", 
			second_label = "";
	
	public static void generate_assembly(String code, String command) {
		switch(command) {
		case "LET":
			generate_assembly_let(code);
			break;
		case "PRINT":
			if (code.equals(command)) get_next_symbol = true;
			generate_assembly_print(code);
			break;
		case "READ":
			generate_assembly_read(code);
			break;
		case "FUNCTION":
			generate_assembly_function(code);
			break;
		case "IF":
			generate_assembly_if(code);
			break;
		case "ELSE":
			generate_assembly_if(code);
			break;
		default:
			break;
		}
	}
	
	private static void generate_assembly_function(String code) {
		
	}
	
	private static void generate_assembly_read(String code) {
		if (code.equals("END") || code.equals("READ") || code.equals(";") || code.equals("}") || code.equals(",")) 
			return;
		
		Assembly.add_command("SC", "Read", "");
		Assembly.add_command("MM", code, "");
	}
	
	private static void generate_assembly_print(String code) {
		if (code.equals(";") || code.equals("}") || code.equals("END") || code.equals(",")) {
			start_exp = false;
			while (!Stack.isEmpty()) {
				if (Stack.peak()[0].equals("-") || Stack.peak()[0].equals("+")) {
					get_from_stack();
				} else {
					command = semanticAnalyser.Stack.pop();
					Assembly.add_command(command[0], command[1], "");	
				}
			}
			if (code.equals(",")) {
				semanticAnalyser.Stack.push("SC", "Print", "");
				start_exp = true;
			}
		} else if (code.equals("(")) { 
			Assembly.add_command("MM", "TEMP" + counter, "");
			Stack.push(partial_operand, "TEMP" + counter++, "");
			Stack.push("(", "", "");
			get_next_symbol = false;
		} else if (code.equals(")")) {
			if (Stack.peak()[0].equals("(")) {
				Stack.pop();
				get_from_stack();
			} else {
				get_from_stack();
				if (Stack.peak()[0].equals("(")) Stack.pop();
			}
		} else if (start_exp) {
			if (code.equals("*") || code.equals("/")) {
				get_next_symbol = true;
				partial_operand = code;
			} else if (code.equals("+") || code.equals("-")) { 
				while (!Stack.isEmpty() && !(Stack.peak()[0].equals("SC") || Stack.peak()[0].equals("("))) {
					get_from_stack();
				}
				
				Assembly.add_command("MM", "TEMP" + counter, "");
				Stack.push(code, "TEMP" + counter++, "");
				
			} else {
				if (get_next_symbol) {
					get_next_symbol = false;
					Assembly.add_command(partial_operand, code, "");
				} else {
					Assembly.add_command("LD", code, "");
				}
			}
		} else if (get_next_symbol) {
			semanticAnalyser.Stack.push("SC", "Print", "");
			get_next_symbol = false;
			start_exp = true;
		}
	}
	
	private static void generate_assembly_if(String code) {
		switch(code) {
		case "IF":
			Stack.push("IF", "", "");
			start_exp = true;
			break;
		case ">":
			comsume_stack_if(code.equals("IF"));
			Assembly.add_command("MM", "TEMP" + counter, "");
			variable_first_half_decision = "TEMP" + counter++;
			Stack.push("JN", "LABEL" + label++, "");
			break;
		case "==":
			comsume_stack_if(code.equals("IF"));
			get_next_symbol = false;
			partial_operand = code;
			break;
		case "<":
			comsume_stack_if(code.equals("IF"));
			get_next_symbol = false;
			partial_operand = code;
			break;
		default:
			if (code.equals(";") || code.equals("}") || code.equals("END")) {
				start_exp = false;
				while (!Stack.isEmpty()) {
					if (Stack.peak()[0].equals("-") || Stack.peak()[0].equals("+")) {
						get_from_stack();
					} else {
						command = semanticAnalyser.Stack.pop();
						Assembly.add_command(command[0], command[1], "");	
					}
				}
			} else if (code.equals("(")) { 
				Assembly.add_command("MM", "TEMP" + counter, "");
				Stack.push(partial_operand, "TEMP" + counter++, "");
				Stack.push("(", "", "");
				get_next_symbol = false;
			} else if (code.equals(")")) {
				if (Stack.peak()[0].equals("(")) {
					Stack.pop();
					get_from_stack();
				} else {
					get_from_stack();
					if (Stack.peak()[0].equals("(")) Stack.pop();
				}
			} else if (code.equals("THEN")) {
				comsume_stack_if(code.equals("IF"));
				
				Assembly.add_command("MM", "TEMP" + counter, "");
				variable_second_half_decision = "TEMP" + counter++;
				
				Assembly.add_command("LD", variable_first_half_decision, "");
				Assembly.add_command("-", variable_second_half_decision, "");
				
				command = semanticAnalyser.Stack.pop();	//JN or JZ command
				if (command[0].equals("JN") && Stack.isEmpty()) { //meaning operation is ">"
						Assembly.add_command(command[0], command[1], "");
						second_label = command[2];
				} else { //meaning operation is "<" or "=="
						Assembly.add_command(command[0], command[1], "");
						first_label = command[2];
						
						command = semanticAnalyser.Stack.pop();	//JN or JZ command
						Assembly.add_command(command[0], command[1], "");
						second_label = command[2];
						
						Assembly.add_command("", "", first_label);
				}
			} else if (start_exp) {
				if (code.equals("*") || code.equals("/")) {
					get_next_symbol = true;
					partial_operand = code;
				} else if (code.equals("+") || code.equals("-")) { 
					while (!(Stack.peak()[0].equals("MM") || Stack.peak()[0].equals("("))) {
						get_from_stack();
					}
					
					Assembly.add_command("MM", "TEMP" + counter, "");
					Stack.push(code, "TEMP" + counter++, "");
					
				} else {
					if (get_next_symbol) {
						get_next_symbol = false;
						Assembly.add_command(partial_operand, code, "");
					} else {
						Assembly.add_command("LD", code, "");
					}
				}
			} else if (get_next_symbol) {
				semanticAnalyser.Stack.push("MM", code, "");
				get_next_symbol = false;
			}
			break;  
		}
		
		Assembly.print_last();
		Stack.print();
		
	}
	
	private static void comsume_stack_if(boolean is_if) {
		while (!Stack.isEmpty()) {
			if (Stack.peak()[0].equals("JN") || Stack.peak()[0].equals("JZ")) 
				break;
			if (is_if && Stack.peak()[0].equals("IF")) 
				break;
			
			if (Stack.peak()[0].equals("-") || Stack.peak()[0].equals("+")) {
				get_from_stack();
			} else {
				command = semanticAnalyser.Stack.pop();
				Assembly.add_command(command[0], command[1], "");	
			}
			
			if (!Stack.isEmpty() && !is_if && Stack.peak()[0].equals("IF")) {
				Stack.pop();
				break;
			}
		}
	}

	private static void generate_assembly_let(String code) {
		switch(code) {
		case "LET":
			get_next_symbol = true;
			break;
		case "=":
			start_exp = true;
			get_next_symbol = false;
			partial_operand = code;
			break;
		default:
			if (code.equals(";") || code.equals("}") || code.equals("END")) {
				start_exp = false;
				while (!Stack.isEmpty()) {
					if (Stack.peak()[0].equals("IF"))
						break;
					
					if (Stack.peak()[0].equals("-") || Stack.peak()[0].equals("+")) {
						get_from_stack();
					} else {
						command = semanticAnalyser.Stack.pop();
						Assembly.add_command(command[0], command[1], "");	
					}
				}
			} else if (code.equals("(")) { 
				Assembly.add_command("MM", "TEMP" + counter, "");
				Stack.push(partial_operand, "TEMP" + counter++, "");
				Stack.push("(", "", "");
				get_next_symbol = false;
			} else if (code.equals(")")) {
				if (Stack.peak()[0].equals("(")) {
					Stack.pop();
					get_from_stack();
				} else {
					get_from_stack();
					if (Stack.peak()[0].equals("(")) Stack.pop();
				}
			} else if (start_exp) {
				if (code.equals("*") || code.equals("/")) {
					get_next_symbol = true;
					partial_operand = code;
				} else if (code.equals("+") || code.equals("-")) { 
					while (!(Stack.peak()[0].equals("MM") || Stack.peak()[0].equals("("))) {
						get_from_stack();
					}
					
					Assembly.add_command("MM", "TEMP" + counter, "");
					Stack.push(code, "TEMP" + counter++, "");
					
				} else {
					if (get_next_symbol) {
						get_next_symbol = false;
						Assembly.add_command(partial_operand, code, "");
					} else {
						Assembly.add_command("LD", code, "");
					}
				}
			} else if (get_next_symbol) {
				semanticAnalyser.Stack.push("MM", code, "");
				get_next_symbol = false;
			}
			break;  
		}
		
		Assembly.print_last();
		Stack.print();
		
	}
	
	private static void get_from_stack() {
		Assembly.add_command("MM", "TEMP" + counter, "");
		command = Stack.pop();
		Assembly.add_command("LD", command[1], "");
		Assembly.add_command(command[0], "TEMP" + counter++, "");
	}
}
