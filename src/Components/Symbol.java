package Components;

/* this class contains all symbols of the vocabulary of the language */
public class Symbol {
	String symbol;
	public Symbol next_symbol;
	
	public Symbol(String symbol){
		this.symbol = symbol;
		this.next_symbol = null;
	}
	
	public String get_symbol(){
		return this.symbol;
	}
}
