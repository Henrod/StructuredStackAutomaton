package Lists;

import Components.Symbol;

public class SymbolsLinkedList {
	Symbol firstSymbol;
	
	public SymbolsLinkedList() {
		firstSymbol = null;
	}
	
	public void insert(Symbol symbol){
		symbol.next_symbol = firstSymbol;
		firstSymbol = symbol;
	}
	
	//search if the list contains this word
	public boolean search(String word){
		Symbol current = firstSymbol;
		while(current != null){
			if(current.get_symbol() == word)
				return true;
			
			current = current.next_symbol;
		}
		
		return false;
	}
	
	//display all alphabet of the language
	public void displayAlphabet(){
		
		System.out.println("--------Alphabet of the System--------");
		
		String symbols = "	";
		
		for(Symbol current = firstSymbol; current != null; current = current.next_symbol){
			symbols += current.get_symbol() + " ";
		}
		
		System.out.println(symbols);
		System.out.println("\n");
	}
}
