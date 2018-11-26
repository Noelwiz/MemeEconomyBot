package economy;

public class OrderQueue {
	Node head = null;
	int totalcost = 0;
	
	private class Node{
		Order data;
		Node next;
		
		public Node(Order o){
			data = o;
			next = null;
		}
		
		public Node(Order o, Node nxt){
			data = o;
			next = nxt;
		}
		
		public String toString() {
			return data.toString();
		}
		
	}
	
	public void enqueue(Order o) {
		if(head!=null) {
			Node current = head;
			while (current.next != null) {
				current = current.next;
			}
			current.next = new Node(o);
			totalcost-=o.MoneyChange;
		} else {
			head = new Node(o);
		}
	}
	
	public Order dequeue() {
		Node oldhead = head;
		head = head.next;
		totalcost -= oldhead.data.MoneyChange;
		return oldhead.data;
	}
	
	public boolean deleteOrder(Meme meme, int numofmeme) {
		Node current = head;
		Node previous = null;
		while (current!=null) {
			if(current.data.m == meme && current.data.Amount == numofmeme) {
				if (current == head) {
					head = head.next;
				} else if ( previous != null) {
					previous.next = current.next;
				}
				totalcost -= current.data.MoneyChange;
				return true;
			}
			else {
				previous = current;
				current = current.next;
			}
		}
		return false;
		
	}
	
	public boolean isempty() {
		return(head==null);
	}
	
	public String toString() {
		String result = " ";
		Node current = head;
		while (current != null) {
			result+=current.toString();
			current = current.next;
		}
		return result;
	}
}
