package me.noelwiz.bots.memeeconomy;

import economy.Market;
import net.dv8tion.jda.core.entities.*;

public class MarketDictionary {	
	private final int DICTSIZE = 14;
	public Node[] Dictionary = new Node[DICTSIZE];
	
	private class Node{
		Market Data= null;
		Guild g = null;
		
		public Node(Guild g) {
			Data = new Market();
			this.g=g;
		}
		
	}
	public MarketDictionary() {
		Dictionary  = new Node[DICTSIZE];
	}
	
	public Market search(Guild g) {
		System.out.println("Market dict searching for: "+g.getId());
		
		long longstart = keyFunct(g);
		int start = (int)(longstart%(DICTSIZE-1));
		
		System.out.println("Market dict searching from: "+start);
		if (Dictionary[start] == null) {
			Dictionary[start]= new Node(g);
			return Dictionary[start].Data;
		} 
		else if(Dictionary[start].g.getId().equals(g.getId())) {
			return Dictionary[start].Data;
		} else {
			int current = (start+1)%(DICTSIZE-1);
			while(current!=start) {
				System.out.println("market dict curently at"+current);
				if (Dictionary[current] == null) {
					Dictionary[current]= new Node(g);
					return Dictionary[current].Data;
				}
				else if(Dictionary[start].g.getId().equals(g.getId())) {
					return Dictionary[current].Data;
				}
				current = (current+1)%(DICTSIZE-1);
			}
		}
		System.out.println("Something bad happened in the Market Dictonary Search");
		return null;
	}
	
	private Long keyFunct(Guild guild) {
		long key= guild.getIdLong();
		return key;
	}
	
	private Long keyFunct(String id) {
		Long key= Long.parseLong(id);
		return key;
	}
}
