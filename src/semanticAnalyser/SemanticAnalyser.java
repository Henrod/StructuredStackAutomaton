package semanticAnalyser;

import Components.Automaton;
import Lists.SymbolsTable;

public class SemanticAnalyser {
	
	/* boolean indicates if next symbol will be pushed or analyzed in arithmetic expression*/
	private static boolean get_next_symbol = false, start_exp = false;
	private static String partial_operand = "";
	private static int counter = 1, label  = 1;
	private static String[] command;
	private static String variable_first_half_decision = "", 
			variable_second_half_decision = "";
	private static String first_label = "", 
			second_label = "",
			third_label = "";
	
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
			
			if (!second_label.equals("")) {
				Assembly.add_command("", "", second_label);
				second_label = "";
			}
			break;
		case "INT":
			generate_assembly_int(code);
			break;
		case "WHILE":
			generate_assembly_while(code);
			break;
		default:
			break;
		}
	}
	
	private static void generate_assembly_while(String code) {
		switch(code) {
		case "WHILE":
			Assembly.add_command("", "", "LABEL" + label);
			Stack.push("WHILE", "LABEL" + label++, "LABEL" + label);
			start_exp = true;
			break;
		case ">":
			
			Assembly.add_command("MM", "TEMP" + counter, "");
			Assembly.add_constant("TEMP" + counter, 0);
			variable_first_half_decision = "TEMP" + counter++;
			Stack.push("JN", "LABEL" + label++, "");
			break;
		case "==":
			 
			get_next_symbol = false;
			partial_operand = code;
			Assembly.add_command("MM", "TEMP" + counter, "");
			Assembly.add_constant("TEMP" + counter, 0);
			variable_first_half_decision = "TEMP" + counter++;
			Stack.push("JP", "LABEL" + (label+1), "");
			Stack.push("JZ", "LABEL" + label++, "");
			label++;
			
			break;
		case "<":
			 
			get_next_symbol = false;
			partial_operand = code;
			Assembly.add_command("MM", "TEMP" + counter, "");
			Assembly.add_constant("TEMP" + counter, 0);
			variable_first_half_decision = "TEMP" + counter++;
			Stack.push("JP", "LABEL" + (label+1), "");
			Stack.push("JN", "LABEL" + label++, "");
			label++;
			
			break;
		default:
			if (code.equals(";") || code.equals("}") || code.equals("END")) {
				start_exp = false;
				while (!Stack.isEmpty()) {
					if (Stack.peak()[0].equals("-") || Stack.peak()[0].equals("+")) {
						get_from_stack();
					} else if (Stack.peak()[0].equals("WHILE")) {
						break;
					} else {
						command = Stack.pop();
						Assembly.add_command(command[0], command[1], "");	
					}
				}
				
			} else if (code.equals("(")) { 
				Assembly.add_command("MM", "TEMP" + counter, "");
				Assembly.add_constant("TEMP" + counter, 0);
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
			} else if (code.equals("{")) {
				start_exp = false;
				 
				
				Assembly.add_command("MM", "TEMP" + counter, "");
				Assembly.add_constant("TEMP" + counter, 0);
				variable_second_half_decision = "TEMP" + counter++;
				
				Assembly.add_command("LD", variable_first_half_decision, "");
				Assembly.add_command("-", variable_second_half_decision, "");
				
				command = Stack.pop();	//JN or JZ command
				if (command[0].equals("JN") && !Stack.peak()[0].equals("JP")) { //meaning operation is ">"
						Assembly.add_command(command[0], command[1], "");
						second_label = command[1];
				} else { //meaning operation is "<" or "=="
						Assembly.add_command(command[0], command[1], "");
						first_label = command[1];				
						command = Stack.pop();	//JN or JZ command
						Assembly.add_command(command[0], command[1], "");
						second_label = command[1];
						
						command = Stack.pop();
						Stack.push(command[0], command[1], second_label);
						
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
					Assembly.add_constant("TEMP" + counter, 0);
					Stack.push(code, "TEMP" + counter++, "");
					
				} else {
					if (get_next_symbol) {
						get_next_symbol = false;
						Automaton.ERROR = SymbolsTable.symbol_exists(code);
						Assembly.add_command(partial_operand, code, "");
					} else {
						Automaton.ERROR = !SymbolsTable.symbol_exists(code);
						
						if (code.matches("-?\\d+(\\.\\d+)?")) {
							if (!SymbolsTable.symbol_exists(code)) {
								SymbolsTable.add_symbol(code);
								Assembly.add_constant(convert_to_number(code), Integer.parseInt(code));
							}
							
							code = convert_to_number(code);
							Automaton.ERROR = false;
						}
						
						Assembly.add_command("LD", code, "");
					}
				}
			} else if (get_next_symbol) {
				Automaton.ERROR = !SymbolsTable.symbol_exists(code);
				Stack.push("MM", code, "");
				get_next_symbol = false;
			}
			break;  
		}
		
		Assembly.print_last();
		Stack.print();
		
	}
	
	private static void generate_assembly_int(String code) {
		switch(code) {
		case "INT":
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
					if (Stack.peak()[0].equals("IF") || Stack.peak()[0].equals("WHILE"))
						break;
					
					if (Stack.peak()[0].equals("-") || Stack.peak()[0].equals("+")) {
						get_from_stack();
					} else {
						command = semanticAnalyser.Stack.pop();
						Assembly.add_command(command[0], command[1], "");	
					}
				}
				
				if (code.equals("}")) {
					if (Stack.peak()[0].equals("WHILE")) {
						command = Stack.pop();
						third_label = command[2];
						Assembly.add_command("JP", command[1], "");
					}
					
					Assembly.add_command("", "", third_label);
					third_label = "";
				}
				
			} else if (code.equals("(")) { 
				Assembly.add_command("MM", "TEMP" + counter, "");
				Assembly.add_constant("TEMP" + counter, 0);
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
					Assembly.add_constant("TEMP" + counter, 0);
					Stack.push(code, "TEMP" + counter++, "");
					
				} else {
					if (get_next_symbol) {
						get_next_symbol = false;
						Automaton.ERROR = SymbolsTable.symbol_exists(code);
						Assembly.add_command(partial_operand, code, "");
					} else {	
						Automaton.ERROR = !SymbolsTable.symbol_exists(code);
						
						if (Automaton.ERROR && code.matches("-?\\d+(\\.\\d+)?")) {
							Assembly.add_constant(convert_to_number(code), Integer.parseInt(code));
							
							code = convert_to_number(code);
							SymbolsTable.add_symbol(code);
							Automaton.ERROR = false;
						}
						
						if (code.matches("-?\\d+(\\.\\d+)?"))
							Assembly.add_command("LD", convert_to_number(code), "");
						else 
							Assembly.add_command("LD", code, "");
					}
				}
			} else if (get_next_symbol) {
				Automaton.ERROR = SymbolsTable.symbol_exists(code);
				
				Assembly.add_constant(code, 0);
				Stack.push("MM", code, "");
				get_next_symbol = false;
			}
			break;  
		}
		
		Assembly.print_last();
		Stack.print();
	}
	
	
	
	private static void generate_assembly_if(String code) {
		switch(code) {
		case "IF":
			Stack.push("IF", "", "");
			start_exp = true;
			break;
		case "ELSE":
			comsume_stack_if(code.equals("ELSE"));
			start_exp = false;
			
			third_label = "LABEL" + label++;
			Assembly.add_command("JP", third_label, "");
			break;
		case ">":
			comsume_stack_if(code.equals("ELSE"));
			Assembly.add_command("MM", "TEMP" + counter, "");
			Assembly.add_constant("TEMP" + counter, 0);
			variable_first_half_decision = "TEMP" + counter++;
			Stack.push("JN", "LABEL" + label++, "");
			break;
		case "==":
			comsume_stack_if(code.equals("ELSE"));
			get_next_symbol = false;
			partial_operand = code;
			Assembly.add_command("MM", "TEMP" + counter, "");
			Assembly.add_constant("TEMP" + counter, 0);
			variable_first_half_decision = "TEMP" + counter++;
			Stack.push("JP", "LABEL" + (label+1), "");
			Stack.push("JZ", "LABEL" + label++, "");
			label++;
			
			break;
		case "<":
			comsume_stack_if(code.equals("ELSE"));
			get_next_symbol = false;
			partial_operand = code;
			Assembly.add_command("MM", "TEMP" + counter, "");
			Assembly.add_constant("TEMP" + counter, 0);
			variable_first_half_decision = "TEMP" + counter++;
			Stack.push("JP", "LABEL" + (label+1), "");
			Stack.push("JN", "LABEL" + label++, "");
			label++;
			
			break;
		default:
			if (code.equals(";") || code.equals("}") || code.equals("END")) {
				start_exp = false;
				while (!Stack.isEmpty()) {
					if (Stack.peak()[0].equals("-") || Stack.peak()[0].equals("+")) {
						get_from_stack();
					} else if (Stack.peak()[0].equals("WHILE")) { 
						break;
					} else {
						command = semanticAnalyser.Stack.pop();
						Assembly.add_command(command[0], command[1], "");	
					}
				}
				
				if (code.equals("}")) {
					Assembly.add_command("", "", third_label);
					third_label = "";
				}
				
			} else if (code.equals("(")) { 
				Assembly.add_command("MM", "TEMP" + counter, "");
				Assembly.add_constant("TEMP" + counter, 0);
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
				start_exp = false;
				comsume_stack_if(code.equals("ELSE"));
				
				Assembly.add_command("MM", "TEMP" + counter, "");
				Assembly.add_constant("TEMP" + counter, 0);
				variable_second_half_decision = "TEMP" + counter++;
				
				Assembly.add_command("LD", variable_first_half_decision, "");
				Assembly.add_command("-", variable_second_half_decision, "");
				
				command = Stack.pop();	//JN or JZ command
				if (command[0].equals("JN") && !Stack.peak()[0].equals("JP")) { //meaning operation is ">"
						Assembly.add_command(command[0], command[1], "");
						second_label = command[1];
						System.out.println("SEGUNDO LABEL EH " + second_label);
				} else { //meaning operation is "<" or "=="
						Assembly.add_command(command[0], command[1], "");
						first_label = command[1];				
						command = Stack.pop();	//JN or JZ command
						Assembly.add_command(command[0], command[1], "");
						second_label = command[1];
						
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
					Assembly.add_constant("TEMP" + counter, 0);
					Stack.push(code, "TEMP" + counter++, "");
					
				} else {
					if (get_next_symbol) {
						get_next_symbol = false;
						Automaton.ERROR = SymbolsTable.symbol_exists(code);
						Assembly.add_command(partial_operand, code, "");
					} else {
						Automaton.ERROR = !SymbolsTable.symbol_exists(code);
						
						System.out.println("DEU MATCH????? " + code.matches("-?\\d+(\\.\\d+)?"));
						
						
						Assembly.add_command("LD", code, "");
					}
				}
			} else if (get_next_symbol) {
				Automaton.ERROR = !SymbolsTable.symbol_exists(code);
				Stack.push("MM", code, "");
				get_next_symbol = false;
			}
			break;  
		}
		
		Assembly.print_last();
		Stack.print();
		
	}
	private static void generate_assembly_function(String code) {
		
	}
	
	private static void generate_assembly_read(String code) {
		if (code.equals("END") || code.equals("READ") || code.equals(";") || code.equals(",")) {
			return;
		}
		
		if (code.equals("}")) {
			if (Stack.peak()[0].equals("WHILE")) {
				command = Stack.pop();
				third_label = command[2];
				Assembly.add_command("JP", command[1], "");
			}
			
			Assembly.add_command("", "", third_label);
			third_label = "";
			
			return;
		}
		
		
		Assembly.add_command("SC", "Read", "");
		
		Automaton.ERROR = !SymbolsTable.symbol_exists(code);
		Assembly.add_command("MM", code, "");
	}
	
	private static void generate_assembly_print(String code) {
		if (code.equals(";") || code.equals("}") || code.equals("END") || code.equals(",")) {	
			start_exp = false;
			while (!Stack.isEmpty()) {
				if (Stack.peak()[0].equals("IF") || Stack.peak()[0].equals("WHILE"))
					break;
				
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
			
			if (code.equals("}")) {
				if (Stack.peak()[0].equals("WHILE")) {
					command = Stack.pop();
					third_label = command[2];
					Assembly.add_command("JP", command[1], "");
				}
				
				Assembly.add_command("", "", third_label);
				third_label = "";
			}
			
		} else if (code.equals("(")) { 
			Assembly.add_command("MM", "TEMP" + counter, "");
			Assembly.add_constant("TEMP" + counter, 0);
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
				Assembly.add_constant("TEMP" + counter, 0);
				Stack.push(code, "TEMP" + counter++, "");
				
			} else {
				if (get_next_symbol) {
					get_next_symbol = false;
					Automaton.ERROR = SymbolsTable.symbol_exists(code);
					Assembly.add_command(partial_operand, code, "");
				} else {
					Automaton.ERROR = !SymbolsTable.symbol_exists(code);
					Assembly.add_command("LD", code, "");
				}
			}
		} else if (get_next_symbol) {
			Stack.push("SC", "Print", "");
			get_next_symbol = false;
			start_exp = true;
		}
	}
	
	
	private static void comsume_stack_if(boolean is_else) {
		while (!Stack.isEmpty()) {
			if (Stack.peak()[0].equals("JN") || Stack.peak()[0].equals("JZ")) 
				break;
			if (is_else && Stack.peak()[0].equals("IF")) {
				Stack.pop();
				break;
			}
			
			if (Stack.peak()[0].equals("-") || Stack.peak()[0].equals("+")) {
				get_from_stack();
			} else if (!Stack.peak()[0].equals("IF")){
				command = Stack.pop();
				Assembly.add_command(command[0], command[1], "");	
			} else {
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
					if (Stack.peak()[0].equals("IF") || Stack.peak()[0].equals("WHILE"))
						break;
					
					if (Stack.peak()[0].equals("-") || Stack.peak()[0].equals("+")) {
						get_from_stack();
					} else {
						command = semanticAnalyser.Stack.pop();
						Assembly.add_command(command[0], command[1], "");	
					}
				}
				
				 if (code.equals("}")) {
					if (!Stack.isEmpty() && Stack.peak()[0].equals("WHILE")) {
						command = Stack.pop();
						third_label = command[2];
						Assembly.add_command("JP", command[1], "");
					}
					
					Assembly.add_command("", "", third_label);
					third_label = "";
				 }
				
			} else if (code.equals("(")) { 
				Assembly.add_command("MM", "TEMP" + counter, "");
				Assembly.add_constant("TEMP" + counter, 0);
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
					Assembly.add_constant("TEMP" + counter, 0);
					Stack.push(code, "TEMP" + counter++, "");
					
				} else {
					if (get_next_symbol) {
						get_next_symbol = false;
						Automaton.ERROR = !SymbolsTable.symbol_exists(code);
						Assembly.add_command(partial_operand, code, "");
					} else {
						Automaton.ERROR = !SymbolsTable.symbol_exists(code);
						
						if (Automaton.ERROR && code.matches("-?\\d+(\\.\\d+)?")) {
							Assembly.add_constant(convert_to_number(code), Integer.parseInt(code));
							
							code = convert_to_number(code);
							SymbolsTable.add_symbol(code);
							Automaton.ERROR = false;
						}
						if (code.matches("-?\\d+(\\.\\d+)?"))
							Assembly.add_command("LD", convert_to_number(code), "");
						else 
							Assembly.add_command("LD", code, "");
					}
				}
			} else if (get_next_symbol) {
				Automaton.ERROR = !SymbolsTable.symbol_exists(code);
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
		Assembly.add_constant("TEMP" + counter, 0);
		command = Stack.pop();
		Assembly.add_command("LD", command[1], "");
		Assembly.add_command(command[0], "TEMP" + counter++, "");
	}
	
	private static String convert_to_number(String number) {
		switch(Integer.parseInt(number)) {
		case -1:
			return "MUM";
		case 0:
			return "ZERO";
		case 1:
			return "UM";
		case 2:
			return "DOIS";
		case 3:
			return "TRES";
		case 4:
			return "QUATRO";
		case 5:
			return "CINCO";
		case 6:
			return "SEIS";
		case 7:
			return "SETE";
		case 8:
			return "OITO";
		case 9:
			return "NOVE";
		case 10:
			return "DEZ";
		default:
			return number;
		}
	}
}
