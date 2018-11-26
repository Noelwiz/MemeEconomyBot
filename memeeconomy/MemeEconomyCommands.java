package me.noelwiz.bots.memeeconomy;

import java.awt.Color;

import economy.Market;
import economy.Meme;
import economy.Player;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class MemeEconomyCommands extends net.dv8tion.jda.core.hooks.ListenerAdapter {
	public static String trigger = "$";
	public static MarketDictionary marketdict= new MarketDictionary();
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		
		//every time a message is sent from a server, an event will be receved here
		Message incommingMessage = event.getMessage();
		String command[] = incommingMessage.getContentDisplay().split(" ");
		System.out.println(incommingMessage.getContentDisplay());
		
		User person = incommingMessage.getAuthor();
		
		//checking for trigger symbol
		if(!command[0].startsWith(trigger)) {
			//return now if not present
			return;
		}	

		
		System.out.println(command[0]);
		
		Market currentMarket = marketdict.search(incommingMessage.getGuild());
		
		command[0] = command[0].substring(trigger.length());
		
		if(command[0].equalsIgnoreCase("initialize")) {
			//set this up
			incommingMessage.getChannel().sendMessage("Welcome traders to the newest and best cryptocurrency, **MEMES**! \nIn order to view the commands, type $help all commands are not case sensitive, and are triggered by the $ sign because this is a serious meme economy!").queue();
			//add this server to the market dictionary
			//marketdict.search(incommingMessage.getGuild());
		}
		else if(command[0].equalsIgnoreCase("startround")) {
			//start the trading round
			if (currentMarket.startRound()) {
				incommingMessage.getChannel().sendMessage("A new round of trading has been started\ntype $join to join this round of trading").queue();
				EmbedBuilder eb = marketEmbed(currentMarket);				
				//send embed
				incommingMessage.getChannel().sendMessage(eb.build()).queue();
				incommingMessage.getChannel().sendMessage(currentMarket.playerdict.LeaderBoardToPrint()).queue();
			}
			else {
				incommingMessage.getChannel().sendMessage("Error: Rount already in progress?").queue();				
			}
		}
		else if(command[0].equalsIgnoreCase("getprices")) {
			//tell people what the prices are on command
			EmbedBuilder eb = marketEmbed(currentMarket);				
			//send embed
			incommingMessage.getChannel().sendMessage(eb.build()).queue();
		}
		else if(command[0].equalsIgnoreCase("endRound")) {
			//end the trading round
			if(currentMarket.endRound()) {
				incommingMessage.getChannel().sendMessage("Round ended, proccessing orders").queue();
				incommingMessage.getChannel().sendMessage(currentMarket.playerdict.LeaderBoardToPrint()).queue();
			}
			else {
				incommingMessage.getChannel().sendMessage("Error: No Round Started?").queue();				
			}
		}
		else if(command[0].equalsIgnoreCase("join")) {
			//join this round of trading, so people don't passively gain money forever
			//alternativly creates a new addition to the player dictionary in the market class if they have not traded before
			if (currentMarket.join(incommingMessage.getAuthor())) {
				incommingMessage.getChannel().sendMessage("you joined this round of trading ~~if this were bug free~~").queue();
			}
			else {
				incommingMessage.getChannel().sendMessage("failed to join").queue();
			}
		}
		else if(command[0].equalsIgnoreCase("portfolio")) {
			//only send the persons portfolio who did the command
			currentMarket.playerdict.search(incommingMessage.getAuthor().getId()).pmPortfolio();
		}
		else if(command[0].equalsIgnoreCase("memeinfo")) {
			//show info about a meme
			//example: `$memeinfo pepe` 2nd argument is the name or tag of a meme
			String targetmeme = command[1];
			Meme meme =  currentMarket.memedict.search(targetmeme);
			if (meme!=null) {
				EmbedBuilder em = meme.memeEmbed();
				incommingMessage.getChannel().sendMessage(em.build()).queue();
			} 
			else {
				incommingMessage.getChannel().sendMessage("Meme not found\nexample: `$memeinfo pepe` in which the argument is the name or tag, pepe in this case, of a meme").queue();
			}
		}
		else if(command[0].equalsIgnoreCase("buy")) {
			//buy a meme
			if(currentMarket.trading) {
				Player p = currentMarket.playing.search(incommingMessage.getAuthor().getId());
				if(p==null) {
					incommingMessage.getChannel().sendMessage("You haven't joined this round of trading").queue();
					return;
				}
				if (command.length<2) {
					incommingMessage.getChannel().sendMessage("Error: Not enough arguments").queue();
				}				
				Meme meme =  currentMarket.memedict.search(command[1]);
				if (meme!=null) {
					int amount = 0;
					try {
						amount = Integer.parseInt(command[2]);
					}
					catch (NumberFormatException e) {
						incommingMessage.getChannel().sendMessage("Error: argument 2 was not a positive or negative intiger").queue();
						return;
					}
					if(p.buyMeme(meme, amount)) {
						//sucsesfully bought the meme
						incommingMessage.getChannel().sendMessage("Theoreticlly, you just placed an order for, "+amount+" "+meme.Name+"s").queue();
					}
					else {
						//tell player it failed
						incommingMessage.getChannel().sendMessage("/shrug failed to buy meme, idk").queue();
					}
				} 
				else {
					incommingMessage.getChannel().sendMessage("Meme not found\nexample: `$buy pepe 1` in which the argument is the name or tag, pepe in this case, of a meme").queue();
				}
			} 
			else {
				//not currently tradeing
				incommingMessage.getChannel().sendMessage("Error: No active trading round").queue();
			}
		}
		else if(command[0].equalsIgnoreCase("sell")) {
			//sell a meme
			if(currentMarket.trading) {
				Player p = currentMarket.playing.search(incommingMessage.getAuthor().getId());
				if(p==null) {
					incommingMessage.getChannel().sendMessage("You haven't joined this round of trading").queue();
					return;
				}
				if (command.length<2) {
					incommingMessage.getChannel().sendMessage("Error: Not enough arguments").queue();
				}				
				Meme meme =  currentMarket.memedict.search(command[1]);
				if (meme!=null) {
					int amount = 0;
					try {
						amount = Integer.parseInt(command[2]);
					}
					catch (NumberFormatException e) {
						incommingMessage.getChannel().sendMessage("Error: argument 2 was not a positive or negative intiger").queue();
						return;
					}
					if(p.sellMeme(meme, amount)) {
						//sucsesfully bought the meme
						incommingMessage.getChannel().sendMessage("Theoreticlly, you just placed an order to sell, "+amount+" "+meme.Name+"(s)").queue();
					}
					else {
						//tell player it failed
						incommingMessage.getChannel().sendMessage("/shrug failed to buy meme, idk").queue();
					}
				} 
				else {
					incommingMessage.getChannel().sendMessage("Meme not found\nexample: `$buy pepe 1` in which the argument is the name or tag, pepe in this case, of a meme").queue();
				}
			} 
			else {
				//not currently tradeing
				incommingMessage.getChannel().sendMessage("Error: No active trading round").queue();
			}
		}
		else if(command[0].equalsIgnoreCase("cancilorder")) {
			//sell a meme
			if(currentMarket.trading) {
				Player p = currentMarket.playing.search(incommingMessage.getAuthor().getId());
				if(p==null) {
					incommingMessage.getChannel().sendMessage("You haven't joined this round of trading").queue();
					return;
				}
				if (command.length<2) {
					incommingMessage.getChannel().sendMessage("Error: Not enough arguments").queue();
				}				
				Meme meme =  currentMarket.memedict.search(command[1]);
				if (meme!=null) {
					int amount = 0;
					try {
						amount = Integer.parseInt(command[2]);
					}
					catch (NumberFormatException e) {
						incommingMessage.getChannel().sendMessage("Error: argument 2 was not a positive or negative intiger").queue();
						return;
					}
					if(p.cancelOrder(meme, amount)) {
						//sucsesfully bought the meme
						incommingMessage.getChannel().sendMessage("Theoreticlly, you just canciled an order for, "+amount+" "+meme.Name+"s").queue();
					}
					else {
						//tell player it failed
						incommingMessage.getChannel().sendMessage("/shrug failed to cancil, idk, probably not found").queue();
					}
				} 
				else {
					incommingMessage.getChannel().sendMessage("Meme not found\nexample: `$buy pepe 1` in which the argument is the name or tag, pepe in this case, of a meme").queue();
				}
			} 
			else {
				//not currently tradeing
				incommingMessage.getChannel().sendMessage("Error: No active trading round").queue();
			}
		}
		else if(command[0].equalsIgnoreCase("clearorder")) {
			//sell a meme
			if(currentMarket.trading) {
				Player p = currentMarket.playing.search(incommingMessage.getAuthor().getId());
				if(p!= null) {
					p.clearOrders();
					incommingMessage.getChannel().sendMessage("Order queue cleared").queue();
				}else {
					incommingMessage.getChannel().sendMessage("You haven't joined this round of trading").queue();
				}
			} else {
				incommingMessage.getChannel().sendMessage("Trading not currently active").queue();
			}
		}
		else if(command[0].equalsIgnoreCase("givemequeue")) {
			//pm the player their order queue.
			Player p = currentMarket.playing.search(incommingMessage.getAuthor().getId());
			if(p!= null) {
				//send the player their queue
				incommingMessage.getAuthor().openPrivateChannel().queue((channel) ->
		        {
		            channel.sendMessage(p.orders.toString()).queue();
		        });
			}else {
				incommingMessage.getChannel().sendMessage("You haven't joined this round of trading").queue();
			}
		}
		else if(command[0].equalsIgnoreCase("help")) {
			//display commands
			incommingMessage.getChannel().sendMessage("All commands are triggered with `$` **It should be noted that if there are any spaces in a name, you must use the meme's tag, these spaces will also break other things, so don't, just don't**\n`$initialize` intializes the bot for the server, actually entirely unsessicary.\n`$startround` starts a new round of trading\n`$endround` ends a round of trading\n`$join` joins the current round of trading - so you will be paied interest and can buy/sell memes\n`$portfolio` privately messages you your portfolio of memes\n`$memeinfo [meme tag]` sends an embed with info about a meme including it's dividends and stuff\n`$buy [meme name or tag] [amount]` buy `amount` of specified meme\n`$sell [tag] [amount]` sells the amount of that meme\n`$help` sends this wall  of text\n`$trade [meme tag] [amount of meme] [amount of $]` offer to trade an amount of memes for the $amount of money from you to them **both numbers can be - so sign matters** this is not implemented for jam at all\n`$playmusic` plays the currently nonexistant meme economy theme song. obviously not implemented.\n`$pausemusic` stops the currently nonexistant meme economy theme song. :( obviously not implemented.\n`$cancilorder [meme tag] [amount]` deletes an order from your queue this round\n`$clearorders` clears your orders for this round\n`$givemequeue` sends you your order queue maybe\n`$getprices` sends the current market to chat").queue();
		}
		else if(command[0].equalsIgnoreCase("trade")) {
			//respond with command not yet implemented
			incommingMessage.getChannel().sendMessage("Not Implemented for this release").queue();
		}
		else if(command[0].equalsIgnoreCase("playmusic")) {
			//respond with command not yet implemented
			incommingMessage.getChannel().sendMessage("Not Implemented for this release").queue();
		}
		else if(command[0].equalsIgnoreCase("pausemusic")) {
			//respond with command not yet implemented
			incommingMessage.getChannel().sendMessage("Not Implemented for this release").queue();
		}
		else if(command[0].equalsIgnoreCase("leaderboard")) {
			incommingMessage.getChannel().sendMessage(currentMarket.playerdict.LeaderBoardToPrint()).queue();
		}

	}
	
	private EmbedBuilder marketEmbed(Market m) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.ORANGE);
		eb.setTitle("Current Market");
		Meme[] memearray= m.memedict.toArray();
		eb.addField("Round",Integer.toString( m.round), false);
		String tags = "tags: ";
		for(int i=0;i<memearray.length;i++) {
			eb.addField(memearray[i].Name, "Current Price:"+memearray[i].Price+"\nDividend: "+memearray[i].dividend, true);
			tags+=", "+memearray[i].Tag;
		}
		eb.addField("Stock Tags",tags, false);
		return eb;
	}

}
