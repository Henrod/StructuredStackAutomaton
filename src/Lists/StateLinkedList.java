package Lists;

import Components.State;

public class StateLinkedList {
	State firstState;
	
	//constructor
	public StateLinkedList(){
		firstState = null;
	}
	
	public void insert(State state){
		state.next_state = firstState;
		firstState = state;
	}
	
	public void displayStates(){
		System.out.println("--------States in the system--------");
				
		State current = firstState;
		
		while(current != null){
			System.out.println("	Sub-machine: " + current.get_submachine_id() + " -> State " + current.get_state_id());
			
			current = current.next_state;
		}
		
		System.out.println("\n");
	}
}
