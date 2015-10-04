package Lists;

import Components.Automaton;
import Components.Production;

public class ProductionLinkedList {
	Production firstProduction;
	
	//constructor
	public ProductionLinkedList(){
		firstProduction = null;
	}
	
	public void insert(Production production){
		production.next_production = firstProduction;
		firstProduction = production;
	}
	
	public void displayProductions(){
		System.out.println("--------Productions in the machine--------");
		for(Production current = firstProduction; current != null; current = current.next_production){
			if(current.get_command() == null){	
				System.out.println("	Current state: " + current.get_current_state().get_submachine_id() + "," + current.get_current_state().get_state_id() + 
								"	|	Input symbol:  "  + current.get_input().get_symbol() +
								"	|	Next state: " + current.get_next_state().get_submachine_id() + "," + current.get_next_state().get_state_id() + 
								"	|	Command: ---" + 
								" 	| Final: " + current.get_current_state().finalState);
			} else if(current.get_command().equals("RETURN")){	
				System.out.println("	Current state: " + current.get_current_state().get_submachine_id() + "," + current.get_current_state().get_state_id() +
								"	|	Input symbol: ---" + 
								"	|	Next state: ---" +   
								"	|	Command: RETURN" + 
								" 	| Final: " + current.get_current_state().finalState);
			} else if(current.get_input() == null){	
				System.out.println("	Current state: " + current.get_current_state().get_submachine_id() + "," + current.get_current_state().get_state_id() +
								"	|	Input symbol: ---" + 
								"	|	Next state: " + current.get_next_submachine_id() + "," + Automaton.submachineLinkedList.get_submachine(current.get_next_submachine_id()).get_initial_state().get_state_id() +  
								"	|	Command: " + current.get_command() + 
								" 	| Final: " + current.get_current_state().finalState);
			} else if(current.get_next_state() == null){	
				System.out.println("	Current state: " + current.get_current_state().get_submachine_id() + "," + current.get_current_state().get_state_id() +
								"	|	Input symbol:  " + current.get_input().get_symbol() +  
								"	|	Next state: ---" + 
								"	|	Command: " + current.get_command() + 
								" 	| Final: " + current.get_current_state().finalState);
			}
		}
		
		System.out.println("\n");
	}
	
	public Production get_production(int submachine_id, int state_id, String symbol){
		for(Production current = firstProduction; current != null; current = current.next_production){
			if (symbol == null && current.get_current_state().get_submachine_id() == submachine_id
					&& current.get_current_state().get_state_id() == state_id
					&& current.get_current_state().finalState) { //empty transaction
				return current;
			}
			//must match state, sub machine and symbol to be the right production
			else if (current.get_input() != null) {
			if(current.get_current_state().get_submachine_id() == submachine_id
				&& current.get_current_state().get_state_id() == state_id
				&& current.get_input().get_symbol().equals(symbol)
				&& current.get_command() == null)
					return current;
			} else {	//in case of STACK, when there is no symbol, only next sub machine id
				if(current.get_current_state().get_submachine_id() == submachine_id
				&& current.get_current_state().get_state_id() == state_id
				&& !current.get_current_state().finalState)
					return current;
			}
			
		}
		
		return null;
		
	}
}
