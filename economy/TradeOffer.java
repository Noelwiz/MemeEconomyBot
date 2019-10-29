package economy;

import java.awt.Color;

import net.dv8tion.jda.api.EmbedBuilder;

public class TradeOffer {
	Market market;
	
	Player from;
	Meme meme;
	int amountOfMemeOffered;
	Player to;
	int amountOfCurrencyRequested;
	
	public TradeOffer(Market marketin, Player offerfrom, Player Offerto, String memetag, int memeamount, int moneyamount) {
		//is this necssisary?
		market = marketin;
		
		from = offerfrom;
		to = Offerto;
		
		meme = marketin.MarketSearch(memetag);
		amountOfMemeOffered = memeamount;
		amountOfCurrencyRequested = moneyamount;
	}
	
	public TradeOffer(Market marketin, Player offerfrom, Player Offerto, Meme meme, int memeamount, int moneyamount) {
		//is this necssisary?
		market = marketin;
		
		from = offerfrom;
		to = Offerto;
		
		this.meme = meme;
		amountOfMemeOffered = memeamount;
		amountOfCurrencyRequested = moneyamount;
	}
	
	public String toString() {
		String result;
		result = from.thisuser.getName();
		result += " wants to trade " + amountOfMemeOffered + " of "+result+"'s";
		result += meme.Name+"(s) to ";
		result += to.thisuser.getName() + " for ";
		result += amountOfCurrencyRequested + " from "+ to.thisuser.getName();
		
		return result;
	}
	
	public boolean canAfford() {
		boolean hasenoughofMeme = false;
		boolean hasenoughofMoney = false;
		
		//TODO: FIX trading a negative number/checking a negative number of memes and probably money
		if(amountOfMemeOffered >= 0) {
			//giveing away meme
			if(from.folio.search(meme).owned>amountOfMemeOffered) {
				//has enough of the meme to trade
				hasenoughofMeme = true;
			}
		} else {
			//asking for meme
			if(to.folio.search(meme).owned>(amountOfMemeOffered * -1)) {
				//has enough of the meme to trade
				hasenoughofMeme = true;
			}
		}
		
		if(amountOfCurrencyRequested <= 0) {
			//giveing away money
			if(to.Money + to.getTotalCost() > (amountOfCurrencyRequested  * -1) ) {
				//has enough of the meme to trade
				hasenoughofMoney = true;
			}
		} else {
			//asking for money
			if(to.Money + to.getTotalCost() > amountOfCurrencyRequested) {
				//has enough of the meme to trade
				hasenoughofMoney = true;
			}
		}
		
		
		return (hasenoughofMoney && hasenoughofMeme);
	}
		
	
	
	
	public EmbedBuilder offerEmbed() {
		EmbedBuilder eb = new EmbedBuilder();
		String toname = to.thisuser.getName();
		String fromname = from.thisuser.getName();
		eb.setColor(Color.PINK);
		eb.setTitle("Trade Offer for"+toname);
		eb.setAuthor(fromname);
		
		if(amountOfCurrencyRequested < 0) {
			//giving money
			if(amountOfMemeOffered > 0) {
				//asking for meme
				eb.addField(fromname+" Will give:", "$"+(-1*amountOfCurrencyRequested)+
						"\nAnd Receave "+amountOfMemeOffered+" of "+meme.Name, false);
				
				eb.addField(toname+" Will receave:", "$"+(-1*amountOfCurrencyRequested)+
						"\nAnd give "+amountOfMemeOffered+" of "+meme.Name, false);
			} else if(amountOfMemeOffered < 0) {
				//giving meme
				eb.addField(fromname+" Will give:", (-1 * amountOfMemeOffered)+" of "+meme.Name +
						"\nand  "+"$"+(-1*amountOfCurrencyRequested), false);
				
				eb.addField(toname+" Will receave:",(-1 * amountOfMemeOffered)+" of "+meme.Name +
						"\nand  "+"$"+(-1*amountOfCurrencyRequested), false);
			} else {
				//gifting money for no memes
				eb.addField(fromname+" Will give:", amountOfMemeOffered+" of "+meme.Name +
						"as a gift", false);
				
				eb.addField(toname+" Will receave:", amountOfMemeOffered+" of "+meme.Name +
						"as a gift", false);
			}
		} else if (amountOfCurrencyRequested > 0) {
			//asking for money
			if(amountOfMemeOffered > 0) {
				//asking for memes
				eb.addField(fromname+" Will receave:", amountOfMemeOffered+" of "+meme.Name +
						"\nand  "+"$"+amountOfCurrencyRequested, false);
				
				eb.addField(toname+" Will give:", amountOfMemeOffered+" of "+meme.Name +
						"\nand  "+"$"+amountOfCurrencyRequested, false);
				
			} else if(amountOfMemeOffered < 0) {
				//giving memes
				eb.addField(fromname+" Will give:",(-1 * amountOfMemeOffered) + " of " + meme.Name
						+ "\nand recieve" +"$"+amountOfCurrencyRequested, false);
				
				eb.addField(toname+" Will receave:",(-1 * amountOfMemeOffered)+" of "+meme.Name +
						"\nand give  "+"$"+amountOfCurrencyRequested, false);
				
			} else {
				//asking for a gift of money
				eb.addField(fromname+" Will receave:", amountOfCurrencyRequested +
						"as a gift", false);
				
				eb.addField(toname+" Will give:", amountOfCurrencyRequested+
						"as a gift", false);
			}
		}
		else {
			//money = 0
			if(amountOfMemeOffered > 0) {
				//asking for memes
				eb.addField(fromname+" Will give:", amountOfMemeOffered+" of "+meme.Name +
						"as a gift", false);
				
				eb.addField(toname+" Will receave:", amountOfMemeOffered+" of "+meme.Name +
						"as a gift", false);
				
			} else if(amountOfMemeOffered < 0) {
				//giving memes
				eb.addField(fromname+" Will receave:", amountOfMemeOffered+" of "+meme.Name +
						"as a gift", false);
				
				eb.addField(toname+" Will give:", amountOfMemeOffered+" of "+meme.Name +
						"as a gift", false);		
				
			} else {
				//asking for nothing
				eb.addField("Nothing Happens","You win nothing, you loose nothing, good day sir", false);
			}
		}
		

		return eb;
	}
	
	

}
