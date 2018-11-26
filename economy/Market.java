package economy;
import net.dv8tion.jda.core.entities.User;

public class Market {
	public PlayerDictionary playerdict;
	public MemeLinkedList memedict;
	public int round = 0;
	public PlayerLinkedList playing;
	public boolean trading = false;
	
	//constructor
	public Market() {
		round = 0;
		playerdict = new PlayerDictionary();
		memedict = new MemeLinkedList();
	}
	
	public void processRound() {
		Player[] toresolve = playing.toArray();
		for(int i = 0;i<toresolve.length;i++) {				
			//process round
			toresolve[i].ResolveRoundOrders();			
		}
		Meme[] memelist = memedict.toArray();
		
		for (int i =0;i<memelist.length;i++) {
			memelist[i].updatePrice();
		}
		
		for(int i = 0;i<toresolve.length;i++) {				
			//process round
			toresolve[i].updateMoney();			
		}
	}
	
	public boolean join(User person) {
		System.out.println("person's id: "+person.getId());		
		Player tojoin = playerdict.search(person.getId());
			
		
		if(tojoin !=null && tojoin.NetWorth!=0) {
			System.out.println("ToJoin: "+tojoin.id);
			return playing.add(tojoin);	
			 
		}
		else {
			assert(tojoin == null);
			//tojoin is null
			//tojoin = new Player(person);
			tojoin = playerdict.add(person,memedict);
			
			System.out.println("ToJoin: "+tojoin.id);
			return playing.add(tojoin);
		}
	}
	
	public boolean startRound() {
		if (!trading) {
			//started round
			trading = true;
			//increment
			round++;
			
			playing = new PlayerLinkedList();
			//retunr succses
			return true;
		} else {
			System.out.println("tried to start a round while trading");
			return false;
		}
	}
	
	public boolean endRound() {
		if(trading) {
			trading = false;
			processRound();
			return true;
		}
		else {
			System.out.println("tried to end a round when one was not going on");
			return false;
		}
		
	}

}
