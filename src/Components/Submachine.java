package Components;

public class Submachine { 
	State initial_state;	//state from where the analysis begin
	int submachine_id;		//number which represents sub machine
	public Submachine next_submachine;	//next for the list 
	
	public Submachine(State initial_state, int submachine_id){
		this.initial_state = initial_state;
		this.submachine_id = submachine_id;
	}
	
	public State get_initial_state(){
		return initial_state;
	}
	public int get_submachine_id(){
		return submachine_id;
	}
}
