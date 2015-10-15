import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import semanticAnalyser.Assembly;
import Components.Automaton;

public class Main {
	
	public static void main(String[] args) throws IOException {
		//By convention, states are always referred in the format: "submachine_id","state_id"
		Automaton automaton = new Automaton();	
		automaton.displayStatus();
		
		BufferedReader wordBuffer = new BufferedReader(new FileReader(new File("WirthGrammarWord.txt")));
		String inputs = ""; //read the sequence of symbols to be analyzes my the machine
		String first = wordBuffer.readLine();
		
		while(first != null){
			inputs += first;
			first = wordBuffer.readLine();
		}
			
		System.out.println("--------Input = " + inputs + "--------\n");
		wordBuffer.close();
		
		//String[] input = inputs.split("//s+");
		//for (String i : input) automaton.analyzeSymbol(i);
		automaton.analyzeSymbol(inputs);
		Assembly.print_assembly();
	}
}
