package Components;

public class State {
	public int state_id; 	//identification of the state inside the sub machine
	public int submachine_id; //identification of the sub machine inside the sub machine
	public boolean finalState;	//tells if this is state is final or not
	public State next_state; //link for the next state on the linked list
	
	public State(int state_id, int submachine_id){
		this.finalState = false;
		this.state_id = state_id;
		this.submachine_id = submachine_id;
		this.next_state = null;
	}
	
	public int get_submachine_id(){
		return this.submachine_id;
	}
	
	public int get_state_id(){
		return this.state_id;
	}
}
