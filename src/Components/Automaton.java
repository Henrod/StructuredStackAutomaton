package Components;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import Lists.ProductionLinkedList;
import Lists.StateLinkedList;
import Lists.SubmachineLinkedList;
import Lists.SymbolsLinkedList;


public class Automaton {
	static public SubmachineLinkedList submachineLinkedList;	/* contain all sub machines of the system. */
	
	StateLinkedList stateLinkedList;	/* contains all states of the system */
	
	SymbolsLinkedList symbolsLinkedList; /* contains the alphabet of the language */
	
	ProductionLinkedList productionLinkedList;	/* the production that determines the language */
	
	Stack stack;	/* data structured that holds states of return after calls for sub machine */
	
	boolean accepted;	/*false by default, and true when in an accepting state and the 
						* stack is empty and the input has been all read. */
	
	boolean error;		/* false by default, and true when there is no production to 
						* execute input in the state; machine finalizes when encounters an error */
	
	State current_state; /* state and sub machine current being processed */
	
	boolean show_track;	/* show passengers inside the finite automaton machine (true) or only 
						* the final result (false) */
	
	BufferedReader machineStructure; /* reads the txt file */
	
	public Automaton() throws IOException{
		submachineLinkedList = new SubmachineLinkedList();
		stateLinkedList = new StateLinkedList();
		symbolsLinkedList = new SymbolsLinkedList();
		productionLinkedList = new ProductionLinkedList();
		stack = new Stack(20);
		
		//create structure-------------------------------------------------------------------------
		createMachineStructure();
		
		this.accepted = false;
		this.error = false;
	}
	
	void createMachineStructure() throws IOException{
		machineStructure = new BufferedReader(new FileReader(new File("machine.txt")));
		
		//first, get the states from the first line and create states and submachines
		String[] states = machineStructure.readLine().split("\\s+");
		//the first state is the initial state, by default
		String[] separate = states[0].split(",");
		State newState = new State(Integer.parseInt(separate[1]), Integer.parseInt(separate[0]));
		this.current_state = newState;	//the first state passed is the initial state
		stateLinkedList.insert(newState);
		submachineLinkedList.insert(new Submachine(newState, Integer.parseInt(separate[0])));	//by default, the first state passed is the initial state
		
		//now, insert the others
		createStatesAndSubmachines(states);
		
		//second, read if wants to see track or not
		char want_to_see_track = machineStructure.readLine().charAt(0); 
		if(want_to_see_track == 'y'){
			this.show_track = true;
		} else if(want_to_see_track == 'n') {
			this.show_track = false;
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
			int state_id = Integer.parseInt(split[0].split(",")[1]); //get sub machine and state separated by a collon, so split and get the second one
			int submachine_id = Integer.parseInt(split[0].split(",")[0]); //get sub machine and state separated by a collon, so split and get the first one
			State current_state = new State(state_id, submachine_id);
			
			//the second read, split[1], is a symbol as input OR is the number of the sub machine to call OR is a RETURN
			String symbol_or_submachine = split[1];
			if(symbol_or_submachine.charAt(0) >= '0' && symbol_or_submachine.charAt(0) <= '9'){ //if it is a symbol, then save as next sub machine to call
				Submachine next_submachine = submachineLinkedList.get_submachine((Integer.parseInt(symbol_or_submachine)));

				int next_state_id = Integer.parseInt(split[3].split(",")[1]); //get sub machine and state separated by a collon, so split and get the second one
				int next_submachine_id = Integer.parseInt(split[3].split(",")[0]); //get sub machine and state separated by a collon, so split and get the first one
				
				State next_state = new State(next_state_id, next_submachine_id);
				// call sub machine only happens in a STACK command, then:
				productionLinkedList.insert(new Production(current_state, null, next_state, "STACK", next_submachine));
			} else { //if it is a symbol, save as input
				if(symbol_or_submachine.equals("RETURN")){
					productionLinkedList.insert(new Production(current_state, null, null, "RETURN", null));
				} else{ //then it is a symbol in the alphabet
					input = new Symbol(symbol_or_submachine);
				}
			}
			
			//the third read can be a command STACK or the next state of the transition
			//if STACK, it has already been resolved before
			if(split.length >= 3){
				if(!split[2].equals("STACK")){
					int next_state_id = Integer.parseInt(split[2].split(",")[1]); //get sub machine and state separated by a collon, so split and get the second one
					int next_submachine_id = Integer.parseInt(split[2].split(",")[0]); //get sub machine and state separated by a collon, so split and get the first one
					State next_state = new State(next_state_id, next_submachine_id);
					
					//command is null because transactions inside same sub machine don't need it
					//next sub machine is null because it is the same, only changes when operations is the stack
					productionLinkedList.insert(new Production(current_state, input, next_state, null, null));
				}
			}
			
			prod = machineStructure.readLine();
		}
	}
	
	public void displayStatus(){
		if(show_track)
			System.out.println("Current state of the automaton: " + current_state.submachine_id + "," + current_state.get_state_id() + "\n");
	}
	
	public void analyzeSymbol(String input){
		if(show_track)
			System.out.println("--------Word starting to be analyzed by the machine--------");
		
		int i = 0;
		while(i <= input.length()){
			if(show_track){
				System.out.println("\n---------------------------------------------------------------------");
				System.out.println("Word to be analyzed: " + input.substring(i, input.length()) + 
						" at state " + current_state.submachine_id + "," + current_state.get_state_id());
				stack.displayStatus();
			}
			
			String symbol = "";
			if(i >= input.length()){
				symbol = "E";
			} else {
				symbol = input.substring(i, i+1);
			}
			
			Production production = productionLinkedList.get_production(
					current_state.get_submachine_id(), 
					current_state.get_state_id(),
					new Symbol(symbol));
			
			int j = i + 1;
			while(production == null && j < input.length()){
				symbol = input.substring(i, ++j);
				
				production = productionLinkedList.get_production(
						current_state.get_submachine_id(), 
						current_state.get_state_id(),
						new Symbol(symbol));
			}
			i = j - 1;
			
			if(production != null){
				if(show_track)
					production.displayProduction();
				
				if(production.get_command() != null){	//if it is a transaction inside the same sub machine
					if(production.get_command().equals("STACK")){
						
						stack.push(production.next_state);	//returning state
						
						current_state = production.next_submachine.get_initial_state();
						
					} else if(production.get_command().equals("RETURN")) {	//if it is a RETURN
						
						current_state = stack.pop();
						
						if(current_state == null){ //if current state is null, stack is empty
							if(i >= input.length() - 1){	//is this case, all word has been read already, 
														//then it is accepted by the machine
								System.out.println("\nThe word " + input + " is ACCEPTED by the automaton" +
										", so it belongs to the language.");
								return;
							} else {
								System.out.println("\nThe automaton finished analyzes but the word was NOT " +
										"accpeted, " + input + " does NOT belong to the language");
								return;
							}
							
						}
						//i++;
					}
					
				} else {	//if command is null, then it is a transaction inside the same sub machine
					current_state = production.next_state;
					i++;
				}
				
			} else {
				System.out.println("\nError: there is no transactions for this state. Machine finished and " +
						"word does not belong to the language defined by this machine.");
				return;
			}	
		}
		
		System.out.println("\nThe automaton finished analyzes in a not accepting state. Then, the word was NOT " +
				"accpeted, " + input + " does NOT belong to the language");
		return;
		
	}
}
