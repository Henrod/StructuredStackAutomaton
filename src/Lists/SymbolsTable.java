package Lists;

import Components.Automaton;

public class SymbolsTable {

	private static Node first = null;
	
	public static void add_symbol(String name, int scope) {
		Node new_symbol = new Node(name, first, scope);
		first = new_symbol;
		
	}
	
	public static void add_symbol(String name, int scope, String label) {
		Node new_symbol = new Node(name, first, scope, label);
		first = new_symbol;
	}
	
	public static boolean symbol_exists(String name) {
		print();
		
		for (Node curr = first; curr != null; curr = curr.next)
			if (curr.name.equals(name) /*&& curr.scope >= Automaton.scope*/)
				return true;
		
		return false;
	}
	
	public static String get_label_from_name(String name) {
		for (Node curr = first; curr != null; curr = curr.next)
			if (curr.name.equals(name))
				return curr.label;
		return null;
	}
	
	public static void print() {
		//System.out.print("lintin ");
		//for (Node curr = first; curr != null; curr = curr.next)
		//	System.out.print(curr.name + "," + curr.label + " ");
		//System.out.print("scope " + Automaton.scope);
		//System.out.println(" ");
	}
	
	private static class Node {
		String name, label;
		Node next;
		int scope;
		
		public Node(String name, Node next, int scope) {
			this.name = name;
			this.next = next;
			this.label = "";
			this.scope = scope;
		}
		
		public Node(String name, Node next, int scope, String label) {
			this.name = name;
			this.next = next;
			this.label = label;
			this.scope = scope;
		}
	}
}
