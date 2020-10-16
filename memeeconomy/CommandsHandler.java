/**
 * 
 */
package me.noelwiz.bots.memeeconomy;

import java.awt.Color;
import java.util.HashMap;

import economy.Market;
import economy.Meme;
import economy.Player;
import economy.TradeOffer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

/**
 * @author dppet
 * 
 * bridges from discord over to the meme economy
 * 
 * basically the view part of Modle View Controller
 *
 */
class CommandsHandler {
	static HashMap<Guild,Market> marketdict = new HashMap<Guild,Market>(7);
	
	//for checking if a guild has a market already?
	static boolean MarketInitalized(Guild g) {
		return marketdict.containsKey(g);
	}
	
	
	
	void Initalize(Guild g, Message m){
		if(MarketInitalized(g)) {
			m.getChannel().sendMessage("This market has already been initalized, congratualations!.").queue();
		}
		m.getChannel().sendMessage("Welcome traders to the newest and best cryptocurrency, **MEMES**! \nIn order to view the commands, type $help all commands are not case sensitive, and are triggered by the $ sign because this is a serious meme economy!").queue();
		
		Market currentMarket = new Market();
		//TODO: check if there's a save file for the server, if so load it, if not, don't
		//		this should handle server level info, aka memes, and market info
		//		market should handle player level info.
		
		//if there is no save
		if(true) {
			m.getChannel().sendMessage("It appears as though this server is new to the very serious"
					+ "Meme economy. If you would like to use the defaults, type $configure defaults"
					+ "otherwise, ").queue(); //TODO: Give the other option, eg make new memes and stuff?
		} else {
			m.getChannel().sendMessage("Give me a minute to load up the save I found . . .").queue();
			
			//TODO:save loading stuff
			
			m.getChannel().sendMessage(". . . Save Loaded!");
			
			//TODO: send leaderboard stuff
		}
	}	
	
	
	void StartRound(Guild g, MessageChannel c) {
		if(MarketInitalized(g)) {
			Market current = marketdict.get(g);
			if (current.startRound()) {
				c.sendMessage("A new round of trading has been started\ntype $join to join this round of trading").queue();
				EmbedBuilder eb = marketEmbed(current);				
				c.sendMessage(eb.build()).queue();
				c.sendMessage(current.LeaderBoardToPrint()).queue();
			}
			else {
				c.sendMessage("Error: Round already in progress.").queue();				
			}
		} else {
			c.sendMessage("Error: market not initalized.").queue();
		}		
	}	
	
	
	void EndRound(Guild g, MessageChannel c) {
		if(!MarketInitalized(g)) {
			c.sendMessage("Error: market not initalized.").queue();
			return;
		}	
		Market currentMarket = marketdict.get(g);
		
		//end the trading round
		if(currentMarket.endRound()) {
			c.sendMessage("Round ended, proccessing orders").queue();
			c.sendMessage(currentMarket.LeaderBoardToPrint()).queue();
		}
		else {
			c.sendMessage("Problem: No Round Started").queue();				
			if (currentMarket.startRound()) {
				c.sendMessage("Starting a new round\ntype $join to join this round of trading").queue();
				EmbedBuilder eb = marketEmbed(currentMarket);				
				//send embed
				c.sendMessage(eb.build()).queue();
				c.sendMessage(currentMarket.LeaderBoardToPrint()).queue();
			}
			else {
				c.sendMessage("Type $StartRound to start a round, failed to automatically do so").queue();	
			}
		}
	}
	
	
	void JoinRound(Guild g, Message m) {
		//alternativly creates a new addition to the player dictionary in the market class if they have not traded before
		MessageChannel c = m.getChannel();
		if(!MarketInitalized(g)) {
			c.sendMessage("Error: market not initalized.").queue();
			return;
		}	
		Market currentMarket = marketdict.get(g);
		if (currentMarket.join(m.getAuthor())) {
			c.sendMessage("you joined this round of trading").queue();
		}
		else {
			c.sendMessage("failed to join").queue();
		}
	}

	
	void GetPrices(Guild g, MessageChannel c) {
		//tell people what the prices are on command
		EmbedBuilder eb = marketEmbed(marketdict.get(g));
		c.sendMessage(eb.build()).queue();
	}
	
	
	void GetMemeInfo(Guild g, MessageChannel c, String tag) {
		//show info about a meme
		//example: `$memeinfo pepe` 2nd argument is the name or tag of a meme
		//tag maybe null
		Meme m =  marketdict.get(g).MarketSearch(tag);
		if (m!=null) {
			EmbedBuilder em = Meme.memeEmbed(m);
			c.sendMessage(em.build()).queue();
		} 
		else {
			c.sendMessage("Meme not found\nexample: `$memeinfo pepe` in which the argument is the name or tag, pepe in this case, of a meme").queue();
		}
	}

	
	void PMPortfolio(Guild g, User u) {
		;
		u.openPrivateChannel().queue((channel) ->
        {
            channel.sendMessage(Player.PortfolioEmbed
            		(
            				marketdict.get(g).PlayerDict.get(u.getId())
            		).build()).queue();
        });
	}
	
	
	//TODO: FIX BUYING (might be in economy logic)
	void BuyMeme(Guild g, MessageChannel c, User u, String meme, int amount) {
		Market currentMarket = marketdict.get(g);
		Meme m = null;
		if(currentMarket.trading) {
			Player p = currentMarket.PlayingSearch(u.getId());
			if(p==null) {
				c.sendMessage("You haven't joined this round of trading").queue();
				return;
			}				
			m =  currentMarket.MarketSearch(meme);
			if (m!=null) {			
				if(p.buyMeme(m, amount)) {
					//sucsesfully bought the meme
					c.sendMessage("Placed an order for, "+amount+" "+m.Name+"s").queue();
				}
				else {
					//tell player it failed
					c.sendMessage("Failed to buy meme").queue();
				}
			}
			else {
				c.sendMessage("Meme not found\nexample: `$buy pepe 1` in which the argument is the name or tag, pepe in this case, of a meme").queue();
			}
		} 
		else {
			c.sendMessage("Problem: No Round Started").queue();				
			if (currentMarket.startRound()) {
				c.sendMessage("Starting a new round\ntype $join to join this round of trading").queue();
				EmbedBuilder eb = marketEmbed(currentMarket);				
				//send embed
				c.sendMessage(eb.build()).queue();
				c.sendMessage(currentMarket.LeaderBoardToPrint()).queue();
			}
			else {
				c.sendMessage("Type $StartRound to start a round, failed to automatically do so").queue();	
			}
		}
	}
	

	void SellMeme(Guild g, MessageChannel c, User u, String meme, int amount) {
		Market currentMarket = marketdict.get(g);
		Meme m = null;
		if(currentMarket.trading) {
			Player p = currentMarket.PlayingSearch(u.getId());
			if(p==null) {
				c.sendMessage("You haven't joined this round of trading").queue();
				return;
			}					
			m =  currentMarket.MarketSearch(meme);
			if (m!=null) {	
				if(p.sellMeme(m, amount)) {
					//sucsesfully bought the meme
					c.sendMessage("Placed an order to sell, "+amount+" of "+m.Name+"(s)").queue();
				}
				else {
					//tell player it failed
					c.sendMessage("Failed to sell meme").queue();
				}
				
			} 
			else {
				c.sendMessage("Meme not found\nexample: `$sell pepe 1` in which the argument is the name or tag, pepe in this case, of a meme").queue();
			}
		} 
		else {
			c.sendMessage("Problem: No Round Started").queue();				
			if (currentMarket.startRound()) {
				c.sendMessage("Starting a new round\ntype $join to join this round of trading").queue();
				EmbedBuilder eb = marketEmbed(currentMarket);				
				//send embed
				c.sendMessage(eb.build()).queue();
				c.sendMessage(currentMarket.LeaderBoardToPrint()).queue();
			}
			else {
				c.sendMessage("Type $StartRound to start a round, failed to automatically do so").queue();	
			}
		}
	}
	
	
	void CheckOrders(Guild g, MessageChannel c, User u) {
		Market currentMarket = marketdict.get(g);
		//pm the player their order queue.
		Player p = currentMarket.PlayingSearch(u.getId());
		if(p!= null) {
			//send the player their queue
			u.openPrivateChannel().queue((channel) ->
	        {
	            channel.sendMessage(p.OrderQueueToSTring()).queue();
	        });
		}else {
			//TODO: maybe make this a pm, and eliminate the Message Channel Argument
			c.sendMessage("You haven't joined this round of trading").queue();
		}
	}
	
	
	void CancelOrder(Guild g, MessageChannel c, User u, String meme, int amount) {
		Market currentMarket = marketdict.get(g);
		Meme m = null;
		if(currentMarket.trading) {
			Player p = currentMarket.PlayingSearch(u.getId());
			if(p==null) {
				c.sendMessage("You haven't joined this round of trading").queue();
				return;
			}				
			m =  currentMarket.MarketSearch(meme);
			if (m!=null) {
				if(p.cancelOrder(m, amount)) {
					//sucsesfully bought the meme
					c.sendMessage("Canceled an order for "+amount+" "+m.Name+"s.").queue();
				}
				else {
					//tell player it failed
					c.sendMessage("Failed to cancel, order not found.").queue();
				}
			} 
			else {
				c.sendMessage("Meme not found\nexample: `$buy pepe 1` in which pepe is the meme's tag, pepe in this case").queue();
			}
		} 
		else {
			c.sendMessage("Trading not currently active, no orders").queue();
		}
	}
	
	
	void ClearOrders(Guild g, MessageChannel c, User u) {
		Market currentMarket = marketdict.get(g);
		if(currentMarket.trading) {
			Player p = currentMarket.PlayingSearch(u.getId());
			if(p!= null) {
				p.clearOrders(); //TODO: make sure this doesn't clear out trades!
				c.sendMessage("Order queue cleared").queue();
			}else {
				c.sendMessage("You haven't joined this round of trading").queue();
			}
		} else {
			c.sendMessage("Trading not currently active").queue();
		}
	}
	
	
	void Trade(Guild g, MessageChannel c, User u, User other, String meme, int nummemes, int moneytotrade) {
		Market currentMarket = marketdict.get(g);
		if(currentMarket.trading) {			
			if (currentMarket.join(u)) { //NOTE: auto joins current round
				c.sendMessage("You have not joined this round; Adding you to this round of trading.").queue();
			}			
			Player offerfrom = currentMarket.PlayingSearch(u.getId());			
			Player offerto = currentMarket.PlayerDict.get(other.getId());
			//TODO: May have to add other person to the current round of trading.
			if(offerto == null) {
				c.sendMessage("Error: the other person has never played the bot").queue();
				return;
			}
				
			TradeOffer newoffer = new TradeOffer(currentMarket, offerfrom, offerto, meme, nummemes,  moneytotrade);
			//TODO: maybe make this an embed?
			if(currentMarket.AddOffer(newoffer)) {
				c.sendMessage("Trade offer: "+ newoffer.toString()).queue();
			} else {
				c.sendMessage("Failed to send trade offer, one of you can't afford it, or doesn't have enough "+ meme+"s").queue();
			}
				
		} 
		else {
			c.sendMessage("Problem: No Round Started").queue();				
			if (currentMarket.startRound()) {
				c.sendMessage("Starting a new round\ntype $join to join this round of trading").queue();
				EmbedBuilder eb = marketEmbed(currentMarket);				
				//send embed
				c.sendMessage(eb.build()).queue();
				c.sendMessage(currentMarket.LeaderBoardToPrint()).queue();
			}
			else {
				c.sendMessage("Type $StartRound to start a round, failed to automatically do so").queue();	
			}
		}
	}
	
	
	//TODO: make sure we considered what happens if you have multiple offers from a user.
	void AcceptTrade(Guild g, MessageChannel c, User u, User otheruser) {
		Market currentMarket = marketdict.get(g);
		if(currentMarket.trading) {
			//$acceptoffer @offerfrom			
			TradeOffer offertoaccept = currentMarket.searchOffers(otheruser, u);
			if(offertoaccept!= null) {
				if(currentMarket.AcceptOffer(offertoaccept)) {
					c.sendMessage("Accepted: "+offertoaccept.toString()).queue();
				}				
			}else {
				c.sendMessage("Could not find a trade offer from that person to you.").queue();
			}			
		} else {
			c.sendMessage("No active trading round.\nType $StartRound to start a round.").queue();
		}
	}
	
	
	//TODO: WRITE THIS
	void DeclineOffer() {
		//declines a trade offer from someone
	}
	
	
	void CancelOffer(Guild g, MessageChannel c, User u, User otheruser) {
		Market currentMarket = marketdict.get(g);
		if(currentMarket.trading) {
			//$canceloffer @offerfrom			
			TradeOffer offertocancel = currentMarket.searchOffers(u, otheruser);
			if(offertocancel!= null) {
				if(currentMarket.CancelOffer(offertocancel)) {
					c.sendMessage("Canciled: "+offertocancel.toString()).queue();
				}				
			}else {
				c.sendMessage("Could not find a trade offer from that person to you.").queue();
			}			
		} else {
			c.sendMessage("No active trading round.\nType $StartRound to start a round.").queue();
		}
	}
	
	
	void Leaderboard(Guild g, MessageChannel c) {
		Market currentMarket = marketdict.get(g);
		c.sendMessage(currentMarket.LeaderBoardToPrint()).queue();
	}
	
	
	//TODO: maybe just make this a text document so it's easier to work with.
	void Help(MessageChannel c) {
		c.sendMessage("All commands are triggered with `$` and to refernce a meme, use the meme's tag, as the name only works if it  has no spaces.\n`$initialize` Sends the start messag.\n`$startround` starts a new round of trading\n`$endround` ends a round of trading\n`$join` joins the current round of trading - so you will be paied interest and can buy/sell memes\n`$portfolio` privately messages you your portfolio of memes\n`$memeinfo [meme tag]` sends an embed with info about a meme including it's dividend % \n`$buy [tag] [amount]` buy `amount` of specified meme, or 1 if no amount is specified\n`$sell [tag] [amount]` sells the amount of that meme, or 1 if no amount is specified\n`$help` sends this wall  of text\n`$trade [tag] [amount] [amount of $] @otherplayer` offer to trade an amount of memes for the $amount of money from you to them **both numbers can be negitive so sign matters** NYI\n`$playmusic` plays the theme song. NYI\n`$stopmusic` stops the meme economy theme song. :( NYI.\n`$cancelorder [tag] [amount]` stops an order from this round\n`$clearorders` clears your orders for this round\n`$givemequeue` sends you your order queue in text\n`$getprices` sends the current market to chat").queue();
	}
	
	
	
	
	
	//TODO: COSIDER vvvvv
	//ALSO consider implicit round joining rather than making users join explicitly
	
	/*THOUGHT!!! SO KEEP ROUNDS, BUT HAVE SERVER MODES BEHIND THE SECENES
	 * so after x orders are in queue, ends the round + give people a button 
	 * 
	 */
	
	protected void ErrorNotEnoughArgsResponse(MessageChannel c) {
		c.sendMessage("Error: Not enough arguments.").queue();
	}
	
	protected void ErrorArgumentNotAIntResponse(MessageChannel c, int argumentposition) {
		c.sendMessage("Error: argument " + argumentposition + " was not an intiger.").queue();
	}
	
	protected void ErrorNoUserMentionedResponse(MessageChannel c, int argumentposition) {
		c.sendMessage("Error: you forgot to mention someone, use the '@' symbol followed by their name."
				+ "The mention should come as the "+argumentposition+"th thing in the message.").queue();
	}
	
	private EmbedBuilder marketEmbed(Market m) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.ORANGE);
		eb.setTitle("Current Market");
		eb.addField("Round",Integer.toString( m.round), false);
		String tags = "tags: ";
		
		Meme current;
		
		for(int i=0;i<m.MemeList.size();i++) {
			current = m.MemeList.get(i);
			eb.addField(current.Name, "Current Price:"+current.Price+"\nDividend: "+current.dividend, true);
			tags+=(", "+current.Tag);
		}
		
		eb.addField("Stock Tags",tags, false);
		return eb;
	}
	

}
