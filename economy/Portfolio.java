package economy;

import economy.Portfolio.PortfolioLinkedList.FolioNode;

public class Portfolio {
	public PortfolioLinkedList MemeList;
	public int InterestPerRound = 0;
	public int value = 0;
	public int Size = 0;
	
	public class PortfolioLinkedList{
		FolioNode head = null;
		
		public PortfolioLinkedList(MemeLinkedList memell) {
			Meme[] memes= memell.toArray();
			for(int i=0; i < memes.length;i++) {				
				addMeme(memes[i]);
			}
			
		}
		
		public class FolioNode{
			Meme data;
			int owned;
			FolioNode next = null;
			
			public FolioNode(Meme m) {
				data = m;
				owned = 0;
			}
			
			public FolioNode(Meme m, FolioNode n) {
				data = m;
				owned = 0;
				next = n;
			}
			
		}
		
		public void addMeme(Meme m) {
			if (head!= null) {
				FolioNode current = head;
				while(current.next != null) {
					current = current.next;
				}
				current.next = new FolioNode(m);
				Size++;
			}
			else {
				head = new FolioNode(m);
				Size++;
			}
		}
		
		public FolioNode search(Meme m) {
			FolioNode current = head;
			while (current != null) {
				if (current.data == m) {
					return current;
				}
				current = current.next;
			}
			System.out.println("failed to find a meme in a user's portfolio");
			return null;
		}
		
		
		
	}	
	
	//constructor
	public Portfolio(MemeLinkedList market) {
		MemeList = new PortfolioLinkedList(market);
	}
	
	//process an order
	public void proccessOrder(Order o) {
		Meme currentMeme = o.m;
		FolioNode currentfolioNode = MemeList.search(currentMeme);		
		//update number of memes owned
		currentfolioNode.owned+=o.Amount;
		//update interest
		InterestPerRound += o.Amount*currentMeme.dividend;	
		value += o.Amount*currentMeme.Price;
	}
	
	public void DevAdd(Meme m, int amount) {
		FolioNode target = MemeList.search(m);
		target.owned += amount;
	}

}


