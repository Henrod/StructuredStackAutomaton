package Lists;

import Components.Submachine;

public class SubmachineLinkedList {
	Submachine firstSubmachine;
	
	public SubmachineLinkedList() {
		firstSubmachine = null;
	}
	
	public void insert(Submachine submachine){
		submachine.next_submachine = firstSubmachine;
		firstSubmachine = submachine;
	}
	
	//return true if submachine is already inserted, false if not
	public boolean search(int submachine_id){
		Submachine current = firstSubmachine;
		while(current != null){
			if(current.get_submachine_id() == submachine_id)
				return true;
			
			current = current.next_submachine;
		}
		
		return false;
	}
	
	//return sub machine from the list by its ID
	public Submachine get_submachine(int submachine_id){
		Submachine current = firstSubmachine;
		while(current != null){
			if(current.get_submachine_id() == submachine_id)
				return current;
			
			current = current.next_submachine;
		}
		
		return null;
	}
	
	public void displaySubmachines(){
		System.out.println("--------Initial states from submachines--------");
		
		Submachine current = firstSubmachine;
		
		while(current != null){
			System.out.println("	Submachine " + current.get_submachine_id() + " -> Initial state: " + current.get_initial_state().get_state_id());
			
			current = current.next_submachine;
		}
		
		System.out.println("\n");
	}
}
