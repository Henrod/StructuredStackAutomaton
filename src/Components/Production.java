package Components;

public class Production {
	State state; 		/* indicates from where this production starts */
	Symbol input;		/*the symbol read in this state*/
	State next_state; 	/* can be the next state in the same sub machine, or the state which will be
	 				  	* pulled in the stack to go to another sub machine */
	String command; 	/* this command can be STACK, to pull in the stack the next state
						* or RETURN, to go back to the state on the top of the stack */
	Submachine next_submachine; 		/* the next sub machine to go when a command STACK is taken */
	public Production next_production; /* next production of the list */
	
	public Production(State state, Symbol input, State next_state, String command, Submachine next_submachine){
		this.state = state;
		this.input = input;
		this.next_state = next_state;
		this.command = command;
		this.next_submachine = next_submachine;
		
		next_production = null;
	}
	
	public State get_current_state(){
		return state;
	}
	
	public Symbol get_input(){
		return input;
	}
	
	public State get_next_state(){
		return next_state;
	}
	
	public String get_command(){
		return command;
	}
	
	public int get_next_submachine_id(){
		return next_submachine.get_submachine_id();
	}
	
	public void displayProduction(){
		if(command == null){	
			System.out.println("Current state: " + state.get_submachine_id() + "," + state.get_state_id() + 
							" | Input symbol: "  + input.get_symbol() +
							" | Next state: " + state.get_submachine_id() + "," + next_state.get_state_id() + 
							" | Command: ---");
		} else if(command.equals("RETURN")){	
			System.out.println("Current state: " + state.get_submachine_id() + "," + state.get_state_id() + 
					" | Input symbol: --- "  +
					" | Next state: --- " +  
					" | Command: RETURN");
		} else if(input == null){	
			System.out.println("Current state: " + state.get_submachine_id() + "," + state.get_state_id() +
							" | Input symbol: ---" + 
							" | Next state: " + next_submachine.submachine_id + "," + Automaton.submachineLinkedList.get_submachine(next_submachine.get_submachine_id()).get_initial_state().get_state_id() +  
							" | Command: " + command);
		} else if(next_state == null){	
			System.out.println("Current state: " + state.get_submachine_id() + "," + state.get_state_id() +
							" | Input symbol: " + input.get_symbol() +  
							" | Next state: ---" + 
							" | Command: " + command);
		}
	}
	
}
