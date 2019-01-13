package economy;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import economy.Portfolio.FolioNode;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.User;

public class Player {
	String id;
	User thisuser = null;
	
	Portfolio folio;
	int Money = 0;
	int NetWorth = 0;
	
	public Queue<Order> orders = new LinkedList<Order>();
	
	private double totalcost = 0;
	
	public Player(User person, ArrayList<Meme> memeList) {
		System.out.println("making new player object with "+person.getName());		
		id = person.getId();
		thisuser = person;
		//CREATE PORTFOLIO
		folio = new Portfolio(memeList);
		Random rng = new Random();
		
		Iterator<Meme> memestorng = memeList.iterator();
		Meme current;
		
		 while(memestorng.hasNext()) {
			current = memestorng.next();
			if(rng.nextInt()%2==1) {
				System.out.println("adding a meme to a portfolio");
				folio.DevAdd(current,rng.nextInt(10));
			}
		}
		 
		calculateNetWorth();
		
		if (NetWorth < 250) {
			Money = 250-NetWorth;
		}
		
		System.out.println("created player, now pming portfolio");
		System.out.println(folio.toString());
		pmPortfolio();
		
	}
	
	public Player() {
		System.out.println("null constructor called on player");
	}

	
	public void ResolveRoundOrders() {
		//process order queue
		while(orders.peek() != null) {
			Order current = orders.remove();
			folio.proccessOrder(current);	
			Money += current.MoneyChange;
		}
		
		//pay player interest
		Money += (int) folio.InterestPerRound;
	}
	
	public void updateMoney() {
			//update net worth
			calculateNetWorth();
			//pm the player their stuff as an embed
			pmPortfolio();
		}
	
	public void pmPortfolio() {
		thisuser.openPrivateChannel().queue((channel) ->
        {
            channel.sendMessage(PortfolioEmbed().build()).queue();
        });
	}
	
	//adds the order from a trade offer to this player
	public void queueTradeOrder(Order o) {
		orders.add(o);
		totalcost+= o.MoneyChange;
		return;
	}
	
	public boolean buyMeme(Meme m,int num) {
		//check if player has enough money
		//   money, more negative than total cost of everything
		//so mathy, multiply by -1, swap the < to a > so if money > total cost 
		if(num > 0 && (-1*Money) < (totalcost +(-1*m.Price)*num)) {
			//if so add to order queue 
			Order o = new Order(m, num);
			
			orders.add(o);
			
			//return if it was succsesfull.	
			totalcost -= (m.Price * num);
			return true;
		}
		else return false;
	}
	
	public boolean buyMeme(Meme m) {
		//check if player has enough money
		//   money, more negative than total cost of everything
		//so mathy, multiply by -1, swap the < to a > so if money > total cost 
		if((-1*Money) < (totalcost +(-1*m.Price))) {
			//if so add to order queue 
			Order o = new Order(m, 1);
			
			orders.add(o);
			
			//return if it was succsesfull.	
			totalcost -= (m.Price);
			return true;
		}
		else return false;
	}
	
	public boolean sellMeme(Meme m,int num) {
		//check if player has enough of that meme
		if(num > 0 && folio.search(m).owned>=num) {
			//if so add to order queue
			Order o = new Order(m,-1*num);
			orders.add(o);
			totalcost += o.MoneyChange;
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean sellMeme(Meme m) {
		//check if player has enough of that meme
		if(folio.search(m).owned>= 1) {
			//if so add to order queue
			Order o = new Order(m,-1);
			orders.add(o);
			totalcost += o.MoneyChange;

			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean cancelOrder(Meme m, int num) {
		//search for order then delete's it	
		Iterator<Order> orderit = orders.iterator();
		Order current;
		while(orderit.hasNext()) {
			current = orderit.next();
			if(current.m.equals(m) && current.Amount == num) {				
				orders.remove(current);
				totalcost -= current.MoneyChange;
				return true;
			}
		}
		
		System.out.println("No order to delete for "+ num +" "+m.toString() + "s" );
		return false;
	}
	
	public void clearOrders() {
		//replace the order queue with a new one
		orders.clear();
		totalcost = 0;
	}
	
	public String OrderQueueToSTring() {
		String result = " ";
		
		Iterator<Order> orderit = orders.iterator();
		Order current;
		while(orderit.hasNext()) {
			current = orderit.next();
			result+=current.toString();
		}
		
		return result;
	}
	
	public void calculateNetWorth() {
		int total = folio.value;
		total 	+= Money;
		NetWorth = total;
	}
	
	public int getTotalCost() {
		return (int) this.totalcost;
	}
	
	public EmbedBuilder PortfolioEmbed() {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.YELLOW);
		eb.setTitle(thisuser.getName());
		//get basic stats
		eb.addField("Interest","$"+folio.InterestPerRound, false);
		eb.addField("Portfolio value","$"+folio.value, false);
		eb.addField("Net Worth","$"+NetWorth, false);
		eb.addField("Money","$"+Money, false);
		//GO THROUGH meme by meme
		
		Iterator<FolioNode> folioitter = folio.MemeList.iterator();
		
		FolioNode current;
		while (folioitter.hasNext()) {
			current = folioitter.next();
			eb.addField(current.data.Name, "Tag:"+current.data.Tag+"\n Owned: "+current.owned+"\nDividened: "+current.data.dividend, true);
		}
		return eb;
	}
}
