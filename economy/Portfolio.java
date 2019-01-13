package economy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;



public class Portfolio {
	//TODO: implement the old functionality with new linked lists
	public ArrayList<FolioNode> MemeList = new ArrayList<FolioNode>();
	
	public int InterestPerRound = 0;
	public int value = 0;
	public int Size = 0;
	
	
	public class FolioNode{
		Meme data;
		int owned;
		
		public FolioNode(Meme m) {
			data = m;
			owned = 0;
		}	
	}
	
	public void addMeme(Meme m) {
		MemeList.add(new FolioNode(m));	
	}		
		

	public FolioNode search(Meme m) {
		FolioNode current;
		ListIterator<FolioNode> iter = MemeList.listIterator();
		while (iter.hasNext()) {
			current = iter.next();
			if (current.data == m) {
				return current;
			}
		}
		System.out.println("failed to find a meme in a user's portfolio");
		return null;
	}
	
	//constructor
	//makes a porfolio and adds every meme to the list
	public Portfolio(ArrayList<Meme> MarketMemeList) {
		this.MemeList = new ArrayList<FolioNode>(); 
		
		Iterator<Meme> iter = MarketMemeList.iterator();
		while (iter.hasNext()) {
			this.MemeList.add( new FolioNode(iter.next()) );
		}
	}
	
	//process an order
	public void proccessOrder(Order o) {
		Meme currentMeme = o.m;
		FolioNode currentfolioNode = search(currentMeme);		
		//update number of memes owned
		currentfolioNode.owned+=o.Amount;
		//update interest
		InterestPerRound += o.Amount*currentMeme.dividend;	
		value += o.Amount*currentMeme.Price;
	}
	
	public void DevAdd(Meme m, int amount) {
		FolioNode target = search(m);
		target.owned += amount;
		value += m.Price * amount;
	}

}


