package economy;

import net.dv8tion.jda.core.entities.User;

public class PlayerLinkedList {
	private Node head = null;
	public int size = 0;
	
	private class Node{
		Player data = null;
		Node next = null;
		
		public Node(Player p) {
			data = p;
		}
		
		//constructor to create a new verson of a meme
		
	}
	
	public PlayerLinkedList(Player p) {
		head = new Node(p);
	}
	
	public PlayerLinkedList() {
		head = null;
	}
	
	public boolean add(Player p) {
		if (head!= null) {
			Node current = head;
			while( current.next != null){
				if (current.data.id.equals(p.id)){
					return false;
				}
				current = current.next;
			}
			current.next = new Node(p);
			size++;
			return true;
		}
		else {
			head = new Node(p);
			
			size++;
			return true;
		}
	}
	//public void remove(Meme m)
	//public void remove(String m)
	
	public Player search(String id) {
		Node current = head;
		while (current!= null) {
			if (current.data.id != null &&  current.data.id.equals(id)) {
				return current.data;
			}
			current = current.next;
		}
		System.out.println("failed to find user in current players");
		return null;
	}
	
	public Player search(Player p) {
		Node current = head;
		while (current!= null) {
			if (current.data != null &&  current.data.id.equals(p.id)) {
				return current.data;
			}
			current = current.next;
		}
		System.out.println("failed to find:" + p.thisuser.getName());
		return null;
	}
	
	public Player[] toArray() {
		Player[] result= new Player[size];
		Node current = head;
		for (int i=0; i<size;i++) {
			result[i] = current.data;
			current = current.next;			
		}
		return result;
	}
}
