package Components;

public class State {
	int state_id; 	//identification of the state inside the sub machine
	int submachine_id; //identification of the sub machine inside the sub machine
	public State next_state; //link for the next state on the linked list
	
	public State(int state_id, int submachine_id){
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
