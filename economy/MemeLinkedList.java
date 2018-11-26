package economy;

public class MemeLinkedList {
	private Node head = null;
	private int size = 0;
	
	private class Node{
		Meme data = null;
		Node next = null;
		
		public Node(Meme m) {
			data = m;
		}
		
		//constructor to create a new verson of a meme
		
	}
	
	public MemeLinkedList() {
		this.add(MemeReference.datboi);
		this.add(MemeReference.pepe);
		this.add(MemeReference.tidepods);
		this.add(MemeReference.meta);
		this.add(MemeReference.adviceanimals);
		this.add(MemeReference.memeeconomy);
		
	}
	
	public void add(Meme m) {
		if (head!= null) {
			Node current = head;
			while(current.next != null) {
				current = current.next;
			}
			current.next = new Node(m);
			size++;
		}
		else {
			head = new Node(m);
			size++;
		}
	}
	//public void remove(Meme m)
	//public void remove(String m)
	
	public Meme search(String name) {
		Node current = head;
		while (current != null) {
			if (current.data.Name != null &&  (current.data.Name.equalsIgnoreCase(name) || current.data.Tag.equalsIgnoreCase(name))) {
				return current.data;
			}
			current = current.next;
		}
		System.out.println("failed to find:"+name);
		return null;
	}
	
	public Meme search(Meme m) {
		Node current = head;
		while (current!= null) {
			if (current.data != null &&  current.data == m) {
				return current.data;
			}
			current = current.next;
		}
		System.out.println("failed to find:"+m.Name);
		return null;
	}
	
	public Meme[] toArray() {
		Meme[] result= new Meme[size];
		Node current = head;
		for (int i=0; i<size;i++) {
			result[i] = current.data;
			current = current.next;			
		}
		return result;
	}
}
