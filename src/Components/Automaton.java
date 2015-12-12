package Components;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import semanticAnalyzer.SemanticAnalyzer;
import Lists.ProductionLinkedList;
import Lists.StateLinkedList;
import Lists.SubmachineLinkedList;
import Lists.SymbolsLinkedList;

public class Automaton {
	static public SubmachineLinkedList submachineLinkedList;	/* contain all sub machines of the system. */
	
	private static StateLinkedList stateLinkedList;	/* contains all states of the system */
	
	private static SymbolsLinkedList symbolsLinkedList; /* contains the alphabet of the language */
	
	private static ProductionLinkedList productionLinkedList;	/* the production that determines the language */
	
	private static Stack stack;	/* data structured that holds states of return after calls for sub machine */
	
	private static State current_state; /* state and sub machine current being processed */
	
	private static boolean show_track;	/* show passengers inside the finite automaton machine (true) or only 
						* the final result (false) */
	
	private static BufferedReader machineStructure; /* reads the text file */
	
	private static String curr_command = "";
	
	public static boolean ERROR = false;
	
	public static int scope = 0;
	
	public Automaton() throws IOException{
		submachineLinkedList = new SubmachineLinkedList();
		stateLinkedList = new StateLinkedList();
		symbolsLinkedList = new SymbolsLinkedList();
		productionLinkedList = new ProductionLinkedList();
		stack = new Stack(20);
		
		//create structure-------------------------------------------------------------------------
		createMachineStructure();
	}
	
	void createMachineStructure() throws IOException{
		machineStructure = new BufferedReader(new FileReader(new File("WirthGrammarMachine.txt")));
		
		//first, get the states from the first line and create states and sub machines
		String[] states = machineStructure.readLine().split("\\s+");
		//the first state is the initial state, by default
		String[] separate = states[0].split(",");
		State newState = new State(Integer.parseInt(separate[1]), Integer.parseInt(separate[0]));
		current_state = newState;	//the first state passed is the initial state
		stateLinkedList.insert(newState);
		submachineLinkedList.insert(new Submachine(newState, Integer.parseInt(separate[0])));	//by default, the first state passed is the initial state
		
		//now, insert the others
		createStatesAndSubmachines(states);
		
		//second, read if wants to see track or not
		char want_to_see_track = machineStructure.readLine().charAt(0); 
		if(want_to_see_track == 'y'){
			show_track = true;
		} else if(want_to_see_track == 'n') {
			show_track = false;
		}
		
		//third, read input symbols
		String[] alphabet = machineStructure.readLine().split("\\s+");
		for(String symbol : alphabet){
			
			symbolsLinkedList.insert(new Symbol(symbol));
			
		}
		symbolsLinkedList.displayAlphabet();
		
		//fourth, get the production until the end of file
		get_productions();
		productionLinkedList.displayProductions();
	}
	
	void createStatesAndSubmachines(String[] states){
		for(int i = 1; i < states.length; i++){
			String stateNumber = states[i];
			String[] separate = stateNumber.split(","); //separate[0] is the sub machine and separate[1] is the state inside it
			
			//create state
			State newState = new State(Integer.parseInt(separate[1]), Integer.parseInt(separate[0]));
			
			//add it into the state linked list
			stateLinkedList.insert(newState);
			
			//verify if sub machine has already been created
			if(!submachineLinkedList.search(Integer.parseInt(separate[0]))){ //verify if this sub machine is already in
			
				submachineLinkedList.insert(new Submachine(newState, Integer.parseInt(separate[0])));	//by default, the first state passed is the initial state
				
			}
			
		}
		stateLinkedList.displayStates();
		submachineLinkedList.displaySubmachines();
	}
	
	public void get_productions() throws IOException{
		String prod = machineStructure.readLine();

		while(prod != null){
			String[] split = prod.split("\\s+");
			Symbol input = null;
			//the first read, split[0], is always current_state
			int state_id = Integer.parseInt(split[0].split(",")[1]); //get sub machine and state separated by a colon, so split and get the second one
			int submachine_id = Integer.parseInt(split[0].split(",")[0]); //get sub machine and state separated by a colon, so split and get the first one
			
			State current_state = stateLinkedList.getState(state_id, submachine_id);
			//the second read, split[1], is a symbol as input OR is the number of the sub machine to call OR is a RETURN
			String symbol_or_submachine = split[1];
			if(prod.contains("STACK")){ //if has STACK, then save as next sub machine to call
				Submachine next_submachine = submachineLinkedList.get_submachine((Integer.parseInt(symbol_or_submachine)));

				int next_state_id = Integer.parseInt(split[3].split(",")[1]); //get sub machine and state separated by a colon, 
																				//so split and get the second one
				int next_submachine_id = Integer.parseInt(split[3].split(",")[0]); //get sub machine and state separated by a colon, 
																					//so split and get the first one
				State next_state = new State(next_state_id, next_submachine_id);
				// call sub machine only happens in a STACK command, then:
				productionLinkedList.insert(new Production(current_state, null, next_state, "STACK", next_submachine));
			} else { //if it is a symbol, save as input
				//if is a RETURN, search if production already exists or insert
				if(symbol_or_submachine.equals("RETURN")){
					
					current_state.finalState = true;
					productionLinkedList.insert(new Production(current_state, null, null, "RETURN", null));
					
				} else{ //then it is a symbol in the alphabet
					input = new Symbol(symbol_or_submachine);
				}
			}
			//the third read can be a command STACK or the next state of the transition
			//if STACK, it has already been resolved before
			if(split.length >= 3){
				if(!split[2].equals("STACK")){
					int next_state_id = Integer.parseInt(split[2].split(",")[1]); //get sub machine and state separated by a colon, 
																					//so split and get the second one
					int next_submachine_id = Integer.parseInt(split[2].split(",")[0]); //get sub machine and state separated by a colon, 
																						//so split and get the first one
					State next_state = new State(next_state_id, next_submachine_id);
					
					//command is null because transactions inside same sub machine don't need it
					//next sub machine is null because it is the same, only changes when operations is the stack
					productionLinkedList.insert(new Production(current_state, input, next_state, null, null));
				}
			} prod = machineStructure.readLine();
		}
	}
	
	public void displayStatus(){
		if(show_track)
			System.out.println("Current state of the automaton: " + current_state.submachine_id + "," + current_state.get_state_id() + "\n");
	}
	
	public void analyzeSymbol(String input){
		Production production = null;
		
		if(show_track)
			System.out.println("--------Word starting to be analyzed by the machine--------");
		
		int i = 0;
		while(i <= input.length()){
			if(show_track){
				System.out.println("\n---------------------------------------------------------------------");
				System.out.println("Word to be analyzed: \"" + input.substring(i, input.length()) + 
						"\" at state " + current_state.submachine_id + 
						"," + current_state.get_state_id());
				stack.displayStatus();
			}
			
			// search for a production STACK or RETURN.
			production = productionLinkedList.get_production(
					current_state.get_submachine_id(), 
					current_state.get_state_id(),
					null);
			
			String symbol = "";
			int const_i = i;
			
			// if there is no STACK, get next symbol of input.
			int j = i;
			if (production == null) {
				while (j < input.length()) {
					if (input.charAt(i) == ' ' || input.charAt(i) == '	') j = ++i;
					else if (input.charAt(j) == ' ' || input.charAt(i) == '	') break;
					else j++;
				}
				symbol = input.substring(i, j);
			} else if (production.command.equals("RETURN")) { //if it got an RETURN, check if there is no other transaction
				while (j < input.length()) {
					if (input.charAt(j) == ' ' || input.charAt(i) == '	') break;
					else j++;
				}
				symbol = input.substring(i, j);
			}
			
			// search for a transaction with reserved word
			production = productionLinkedList.get_production(
					current_state.get_submachine_id(), 
					current_state.get_state_id(),
					symbol);
			
			// if still has no transactions, search for one with the current character
			if (production == null && symbol != null) {
				
				if (i >= input.length()) symbol = null; 
				else symbol = String.valueOf(input.charAt(i++));
				
				production = productionLinkedList.get_production(
						current_state.get_submachine_id(), 
						current_state.get_state_id(),
						symbol);
				
				if (production == null) { //if still null, search for a RETURN production
					production = productionLinkedList.get_production(
							current_state.get_submachine_id(), 
							current_state.get_state_id(),
							null);
					symbol = null;
					
				}
			} else {
				i = j;
			}
			
			System.out.println("Atom to be analyed: " + symbol); 
			
			if (production != null){
				if(production.command != null)
					if(production.command.equals("RETURN"))
						i = const_i;
				
				current_state = production.state;
				
				if(show_track)
					production.displayProduction();
				
				if(production.get_command() != null){	//if it is a transaction inside the same sub machine
					if(production.get_command().equals("STACK")){
						stack.push(production.next_state);	//returning state
						current_state = production.next_submachine.get_initial_state();
					} 
					//if it is a RETURN and the word is finished
					else if(production.state.finalState//production.get_command().equals("RETURN")
							&& symbol == null) {	
						current_state = stack.pop();
						
						if(current_state == null){ //if current state is null, stack is empty
							if(i >= input.length() - 1){	//is this case, all word has been read already, 
														//then it is accepted by the machine
								System.out.println("\nThe word \"" + input + "\" is ACCEPTED by the automaton" +
										", so it belongs to the language.");
								return;
							} else {
								System.out.println("\nThe automaton finished analyzes but the word was NOT " +
										"accpeted, \"" + input + "\" does NOT belong to the language");
								ERROR = true;
								return;
							}
							
						}
					}
					
				} else {	//if command is null, then it is a transaction inside the same sub machine
					curr_command = get_command(production.get_input().get_symbol());
					SemanticAnalyzer.generate_assembly(production.get_input().get_symbol(), curr_command);
					current_state = production.next_state;
				}
				
			} if (production == null) {
				System.out.println("\nError: there is no transactions for this state. Machine finished and " +
						"word does not belong to the language defined by this machine.");
				System.out.print("Probably the following are missing: ");
				
				productionLinkedList.printTrasactions(current_state.state_id, current_state.submachine_id);
				while (productionLinkedList.get_production(current_state.submachine_id, 
						current_state.state_id, null) != null) 
					if (productionLinkedList.get_production(current_state.submachine_id, 
							current_state.state_id, null).state.finalState) {
						current_state = stack.pop();
						productionLinkedList.printTrasactions(current_state.state_id, current_state.submachine_id);
					}
				ERROR = true;
			}
			
			if (ERROR) {
				System.out.println("OCORREU UM ERRO!!!!!");
				return; 
			}
		}
		
		if (production.state.finalState) {
			System.out.println("\nThe word \"" + input + "\" is ACCEPTED by the automaton" +
					", so it belongs to the language.");
			return;
		} else {
			System.out.println("\nThe automaton finished analyzes in a not accepting state. Then, the word was NOT " +
					"accpeted, \"" + input + "\" does NOT belong to the language");
			ERROR = true;
		}
	}
	
	private static String get_command(String command) {
		switch(command) {
		case "LET":
			command = "LET";
			break;
		case "PRINT":
			command = "PRINT";
			break;
		case "READ":
			command = "READ";
			break;
		case "IF":
			command = "IF";
			break;
		case "ELSE":
			command = "ELSE";
			break;
		case "FUNCTION":
			command = "FUNCTION";
			break;
		case "INT":
			if 		(curr_command.equals("FUNCTION")) command = "FUNCTION";
			else if (curr_command.equals("DECLARE")) command = "DECLARE";
			else command = "INT";
			break;
		case "CALL":
			command = "CALL";
			break;
		case "WHILE":
			command = "WHILE";
			break;
		case "GIVE":
			command = "FUNCTION";
			break;
		case "DECLARE":
			command = "DECLARE";
			break;
		case ";":
			if (curr_command.equals(("DECLARE"))) command = ";";
			else command = curr_command;
			break;
		default:
			command = curr_command;
			break;
		}
		return command;
	}
}
