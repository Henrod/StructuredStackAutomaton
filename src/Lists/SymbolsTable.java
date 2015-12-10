package Lists;

public class SymbolsTable {

	private static Node first = null;
	
	public static void add_symbol(String name) {
		Node new_symbol = new Node(name, first);
		first = new_symbol;
	}
	
	public static boolean symbol_exists(String name) {
		System.out.println("A PALAVRA USADA EH " + name);
		print();
		for (Node curr = first; curr != null; curr = curr.next)
			if (curr.name.equals(name))
				return true;
		
		return false;
	}
	
	private static void print() {
		for (Node curr = first; curr != null; curr = curr.next)
			System.out.print(curr.name + " ");
		System.out.println(" ");
	}
	
	private static class Node {
		String name;
		Node next;
		
		public Node(String name, Node next) {
			this.name = name;
			this.next = next;
		}
	}
}
