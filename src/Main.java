import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import Components.Automaton;

public class Main {
	
	static String input;	/* set of symbols to verify if it belongs to the machine language or not */
	
	public static void main(String[] args) throws IOException {
		
		//By convention, states are always referred in the formart: "submachine_id","state_id"
		Automaton automaton = new Automaton();	
		automaton.displayStatus();
		
		BufferedReader wordBuffer = new BufferedReader(new FileReader(new File("word.txt")));
		String input = wordBuffer.readLine();	//read the sequence of symbols to be analyzes my the machine
		System.out.println("--------Input = " + input + "--------\n");
		
		automaton.analyzeSymbol(input);
	}
}
