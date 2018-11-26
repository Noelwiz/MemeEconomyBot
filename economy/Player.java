package economy;

import java.awt.Color;
import java.util.Random;

import economy.Portfolio.PortfolioLinkedList.FolioNode;
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
	
	public OrderQueue orders = new OrderQueue();
	
	public Player(User person, MemeLinkedList memes) {
		System.out.println("making new player object with "+person.getName());		
		// TODO Auto-generated constructor stub
		id = person.getId();
		thisuser = person;
		//CREATE PORTFOLIO
		folio = new Portfolio(memes);
		Random rng = new Random();
		Meme[] memearray = memes.toArray();
		for(int i=0; i<memearray.length;i++) {
			if(rng.nextInt()%2==1) {
				System.out.println("adding a meme to a portfolio");
				folio.DevAdd(memearray[i],rng.nextInt(10));
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
		while(!orders.isempty()) {
			Order current = orders.dequeue();
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
	
	public boolean buyMeme(Meme m,int num) {
		//check if player has enough money
		if((orders.totalcost+(-1*m.Price)*num)>(-1*Money)) {
			//if so add to order queue 
			Order o = new Order(m, num);
			
			orders.enqueue(o);
			
			//return if it was succsesfull.	
			return true;
		}
		else return false;
	}
	
	public boolean sellMeme(Meme m,int num) {
		//check if player has enough of that meme
		if(folio.MemeList.search(m).owned>num) {
			//if so add to order queue
			Order o = new Order(m,-1*num);
			
			orders.enqueue(o);
			
			//return if sucseasfull
			return true;
			
		}
		else {
			return false;
		}
	}
	
	public boolean cancelOrder(Meme m, int num) {
		//search for order then delete's it	
		return orders.deleteOrder(m,num);
	}
	
	public void clearOrders() {
		//replace the order queue with a new one
		orders = new OrderQueue();
	}
	
	public void calculateNetWorth() {
		int total = folio.value;
		total+= Money;
		NetWorth = total;
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
		FolioNode current = folio.MemeList.head;
		while (current!=null) {
			eb.addField(current.data.Name, "Tag:"+current.data.Tag+"\n Owned: "+current.owned+"\nDividened: "+current.data.dividend, true);
			current = current.next;
		}
		return eb;
	}
}
