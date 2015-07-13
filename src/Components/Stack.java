package Components;

/* this class holds the state of return when another sub machine is called */
public class Stack {
	
	State[] returning_states;//holds the states which will be returned in a pop
	int stack_pointer; 		//indicates the position of the stack
	int stack_size;			//the maximum size
	
	public Stack(int size){
		
		returning_states = new State[size];	//initiates stack with size
		stack_pointer = -1;		//represents empty stack
		stack_size = size;
		
	}
	
	/* add state to the top of the stack */
	public void push(State state){
		if(stack_pointer + 1 < stack_size){	//verify if there is enough space on the stack
			
			returning_states[++stack_pointer] = state;
			
		} else {
			
			System.out.println("\nNot enough space on the stack. Word could not be decided.");
			return;
			
		}
		
	}
	
	/* removes state from the top of the list */
	public State pop(){
		
		if(stack_pointer >= 0){
			return returning_states[stack_pointer--];	//return and decrement one position
		}
		
		System.out.println("\nNo states on the stack, returning null.");
		return null;
		
	}
	
	public boolean isEmpty(){
		return stack_pointer == -1;	//indicates if stack is empty
	}
	
	public void displayStatus(){
		System.out.println("\n----Stack's contents----");
		for(int i = stack_pointer; i >= 0; i--){
			System.out.println("	" + returning_states[i].submachine_id + "," + returning_states[i].get_state_id());
		}
		System.out.println("------------------------\n");
	}
	
}
