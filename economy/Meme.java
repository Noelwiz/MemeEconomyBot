package economy;

import java.awt.Color;
import java.util.Random;

import net.dv8tion.jda.core.EmbedBuilder;

public class Meme {
	public String Tag;
	public String Name;
	public int Price;
	public int dividend;
	double DividendPrecent;
	private Random rng = new Random();
	final double PRICESCALAR = rng.nextDouble();
	int count = 0;

	public Meme(String tag, String name, int startprice, double dividendpercent ) {
		Tag = tag;
		Name = name;
		Price = startprice;
		DividendPrecent = dividendpercent;
		dividend = (int) (DividendPrecent*Price);
	}
	
	public void updatePrice() {
		//implement new price calulation function, * PRICESCALAR
		double pricechange = rng.nextDouble();
		if(pricechange%.02 != 0) {
			pricechange=pricechange*-1;
		}
		Price = (int) ((Price * (pricechange+PRICESCALAR))+ Price);
		
		if (Price <= 1) {
			Price = 6;
		}
		dividend = (int) (DividendPrecent*Price);
	}
	
	public EmbedBuilder memeEmbed() {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.CYAN);
		eb.setTitle(Name);
		eb.addField("Tag",Tag, false);
		eb.addField("Price", "$"+Price, true);	
		eb.addField("Dividend Info", "Dividend: $"+dividend+"\nDividend Precent: "+(100*DividendPrecent), true);
		return eb;
	}
}