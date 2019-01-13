package economy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import net.dv8tion.jda.core.entities.User;

public class Market {
	public HashMap<String, Player> PlayerDict;
	public ArrayList<Meme> MemeList;
	public int round = 0;
	public ArrayList<Player> Playing;
	public boolean trading = false;
	public ArrayList<TradeOffer> TradeOffers;
	
	//constructor
	public Market() {
		round = 0;
		PlayerDict = new HashMap<String,Player>(61);
		MemeList = new ArrayList<Meme>(); //new MemeLinkedList();
		MemeList.addAll(MemeReference.memes);
		TradeOffers = new ArrayList<TradeOffer>();
	}
	
	public void processRound() {
		Iterator<Player> playeritr = Playing.iterator();
		while(playeritr.hasNext()) {	
			playeritr.next().ResolveRoundOrders();		
		}
		
		Meme current;
		Iterator<Meme> memeitr = MemeList.iterator();
		while (memeitr.hasNext()) {
			current = memeitr.next();
			current.updatePrice();
		}
		
		
		playeritr = Playing.iterator();
		while(playeritr.hasNext()) {	
			playeritr.next().updateMoney();		
		}
	}
	
	public boolean join(User person) {
		System.out.println("person's id: "+person.getId());		
		Player tojoin = this.PlayerDict.get(person.getId());
			
		
		if(tojoin !=null && tojoin.NetWorth!=0) {
			System.out.println("ToJoin: "+tojoin.id);
			return Playing.add(tojoin);	
			 
		}
		else {
			assert(tojoin == null);
			//tojoin is null
			tojoin = new Player(person, MemeList);
			PlayerDict.put(person.getId(), tojoin);
			
			System.out.println("ToJoin: "+tojoin.id);
			return Playing.add(tojoin);
		}
	}
	
	public boolean startRound() {
		if (!trading) {
			//started round
			trading = true;
			//increment
			round++;
			
			Playing = new ArrayList<Player>(5);
			TradeOffers = new ArrayList<TradeOffer>(2);
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
	
	public Meme MarketSearch(String memename) {
		Meme current;
		for(int i = 0; i < MemeList.size(); i++) {
			current = MemeList.get(i);
			if(current.Name.equals(memename) || current.Tag.equals(memename) ){
				return current;
			}
		}
		
		System.out.println("Market Search failt to find meme: "+memename);
		return null;
	}
	
	public Player PlayingSearch(String authorid) {
		Player current;
		for(int i = 0; i < Playing.size(); i++) {
			current = Playing.get(i);
			if(current.id.equals(authorid)){
				return current;
			}
		}
		
		System.out.println("Playing Search failt to find player: "+authorid);
		return null;
	}
	
	
	public String LeaderBoardToPrint() {
		Player[] sortedBoard = GetLeaderBoard();
		
		String Leaderboard = "Leader Board: \n";
		
		for (int i=0; i < sortedBoard.length; i++) {
			if(sortedBoard[i]!=null && sortedBoard[i].id !=null) {
				Leaderboard += "#"+(i+1)+": "+sortedBoard[i].thisuser.getName()+" with $"+sortedBoard[i].NetWorth;
			} else {
				System.out.println("error, some of the players in sortedBoard are null, index: "+i);
			}
		}
		
		return Leaderboard;
		
	}
	
	
	private Player[] GetLeaderBoard() {
		Player[] leaderboard = new Player[PlayerDict.size()];
		
		ArrayList<Player> players = new ArrayList<Player>(PlayerDict.values());
		players.trimToSize();
		players.toArray(leaderboard);
		
		leaderboard = sortLeaderBoard(leaderboard);
		//TODO: maybe try and use this
		//leaderboard = players.OrderBy(p => Player.getNetWorth(p)).ToArray();
		return leaderboard;
	}
	
	
	private boolean WorthMore(Player p1, Player p2) {
		if(p1.NetWorth < p2.NetWorth) {
			return true;
		}
		return false;
	}
	
	
	
	//TODO: check this
	private Player[] sortLeaderBoard(Player[] board) {		
		Player playerToInsert;
		Player temp;
		int insertindex;
	
		for(int j = 0; j < board.length - 1; j++) {
			insertindex = j;
			playerToInsert = board[j];	
			for (int i = j+1; i < board.length; i++) {
				if(WorthMore(playerToInsert, board[i])) {
					insertindex = i;
					playerToInsert = board[i];
				}
			}
			//swap j and insertindex
			temp = board[j];
			board[j] =  playerToInsert;
			board[insertindex] = temp;	
		}
		return board;
	}
	
	
	//TODO: this checks if the offer is completeable, then adds it to the queue if so
	public boolean AddOffer(TradeOffer offer) {
		boolean affordable = offer.canAfford();
		if(affordable) {
			//add to the thing of offers
			TradeOffers.add(offer);
		}	
		return affordable;
	}
	
	//offer from should be the taged user, offerto should be the person saying it
	public TradeOffer searchOffers(User offerfrom, User offerto) {
		Iterator<TradeOffer> offers = TradeOffers.iterator();
		boolean found = false;
		TradeOffer current = null;
		while(offers.hasNext() && !found) {
			current = offers.next();
			if(current.to.id.equals(current.from.id)) {
				found = true;
			}
		}
		
		
		System.out.println("Failed to find a trade offer");
		return current;
	}
	
	//TODO: make this check if an offer is still acceptable, then add the orders to the orderqueue
	//maybe add a flag to orders to make them un deleteable if from a trade?
	//maybe a class that extends offer?
	public boolean AcceptOffer(TradeOffer offer) {
		boolean affordable = offer.canAfford();
		if(affordable) {
			//Order(Meme meme, int numofmeme, int moneyexhanged)
			Order toOdrer = new Order(offer.meme, -1 * offer.amountOfMemeOffered, offer.amountOfCurrencyRequested);
			offer.to.queueTradeOrder(toOdrer);
			
			Order fromOrder = new Order(offer.meme,  offer.amountOfMemeOffered, -1 * offer.amountOfCurrencyRequested);
			offer.from.queueTradeOrder(fromOrder);
		}	
		return affordable;
	}

	public boolean CancelOffer(TradeOffer offertocancel) {
		return TradeOffers.remove(offertocancel);
	}

}
