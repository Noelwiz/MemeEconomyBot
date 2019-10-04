package me.noelwiz.bots.memeeconomy;

import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import economy.Market;
import economy.Meme;
import economy.Player;
import economy.TradeOffer;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class MemeEconomyCommandInterpreter extends net.dv8tion.jda.core.hooks.ListenerAdapter {
	private static String trigger = "$";
	private CommandsHandler handler = new CommandsHandler();
	
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {	
		//every time a message is sent from a server, an event will be receved here
		Message incommingMessage = event.getMessage();
		String command = incommingMessage.toString().split(" ")[0];
		
		if(command.startsWith(trigger)) {
			//return now if not present
			return;
		}	
		
		System.out.println(command);		
		
		//cut out the trigger character(s) to make the following more readable
		command = command.substring(trigger.length());
		
		switch(command.toLowerCase()) {
			//set this up
			case"initialize":
				handler.Initalize(incommingMessage.getGuild(),incommingMessage);
				break;
			//start the trading round					
			case "startround":
				handler.StartRound(incommingMessage.getGuild(), incommingMessage.getChannel());
				break;
			//end round of trading
			case "endround":
				handler.EndRound(incommingMessage.getGuild(), incommingMessage.getChannel());
				break;	
			//join this round of trading	
			case "join":
				handler.JoinRound(incommingMessage.getGuild(),incommingMessage);
				break;
			//get prices of memes
			case "getprices":
					handler.GetPrices(incommingMessage.getGuild(), incommingMessage.getChannel());
					break;
			//see your meme portfolio
			case"portfolio":
				//only send the persons portfolio who did the command
				currentMarket.PlayerDict.get(incommingMessage.getAuthor().getId()).pmPortfolio();
				break;
				
			case"memeinfo":
				//show info about a meme
				//example: `$memeinfo pepe` 2nd argument is the name or tag of a meme
				String targetmeme = command[1];
				m =  currentMarket.MarketSearch(targetmeme);
				if (m!=null) {
					EmbedBuilder em = m.memeEmbed();
					incommingMessage.getChannel().sendMessage(em.build()).queue();
				} 
				else {
					incommingMessage.getChannel().sendMessage("Meme not found\nexample: `$memeinfo pepe` in which the argument is the name or tag, pepe in this case, of a meme").queue();
				}
				break;
				
			//TODO: Update help command for this
			case "buy":
				//buy a meme
				if(currentMarket.trading) {
					Player p = currentMarket.PlayingSearch(incommingMessage.getAuthor().getId());
					if(p==null) {
						incommingMessage.getChannel().sendMessage("You haven't joined this round of trading").queue();
						return;
					}
					//command[0] = sell, command[1] = amount, command[2] = meme
					if (command.length<=2) {
						incommingMessage.getChannel().sendMessage("Error: Not enough arguments").queue();
					}				
					m =  currentMarket.MarketSearch(command[1]);
					if (m!=null) {
						int amount = 0;
						
						if(command.length >= 3) {
							try {
								amount = Integer.parseInt(command[2]);
							}
							catch (NumberFormatException e) {
								incommingMessage.getChannel().sendMessage("Error: argument 2 was not a positive intiger").queue();
								return;
							}	
							if(p.buyMeme(m, amount)) {
								//sucsesfully bought the meme
								incommingMessage.getChannel().sendMessage("Placed an order for, "+amount+" "+m.Name+"s").queue();
							}
							else {
								//tell player it failed
								incommingMessage.getChannel().sendMessage("Failed to buy meme").queue();
							}
						} else if(p.buyMeme(m)){
							incommingMessage.getChannel().sendMessage("Placed an order for 1 "+m.Name).queue();
						}
						else {
							//tell player it failed
							incommingMessage.getChannel().sendMessage("Failed to buy meme").queue();
						}
					} 
					else {
						incommingMessage.getChannel().sendMessage("Meme not found\nexample: `$buy pepe 1` in which the argument is the name or tag, pepe in this case, of a meme").queue();
					}
				} 
				else {
					incommingMessage.getChannel().sendMessage("Problem: No Round Started").queue();				
					if (currentMarket.startRound()) {
						incommingMessage.getChannel().sendMessage("Starting a new round\ntype $join to join this round of trading").queue();
						eb = marketEmbed(currentMarket);				
						//send embed
						incommingMessage.getChannel().sendMessage(eb.build()).queue();
						incommingMessage.getChannel().sendMessage(currentMarket.LeaderBoardToPrint()).queue();
					}
					else {
						incommingMessage.getChannel().sendMessage("Type $StartRound to start a round, failed to automatically do so").queue();	
					}
				}
				break;
			
			case "sell":
				//sell a meme
				if(currentMarket.trading) {
					Player p = currentMarket.PlayingSearch(incommingMessage.getAuthor().getId());
					if(p==null) {
						incommingMessage.getChannel().sendMessage("You haven't joined this round of trading").queue();
						return;
					}
					if (command.length<=2) {
						incommingMessage.getChannel().sendMessage("Error: Not enough arguments").queue();
					}				
					m =  currentMarket.MarketSearch(command[1]);
					if (m!=null) {
						if (command.length>=3) {
							int amount = 0;
							try {
								amount = Integer.parseInt(command[2]);
							}
							catch (NumberFormatException e) {
								incommingMessage.getChannel().sendMessage("Error: argument 2 was not a positive intiger").queue();
								return;
							}
							if(p.sellMeme(m, amount)) {
								//sucsesfully bought the meme
								incommingMessage.getChannel().sendMessage("Placed an order to sell, "+amount+" of "+m.Name+"(s)").queue();
							}
							else {
								//tell player it failed
								incommingMessage.getChannel().sendMessage("Failed to sell meme").queue();
							}
						} else if (p.sellMeme(m)) {
							incommingMessage.getChannel().sendMessage("Placed an order to sell, 1 "+m.Name).queue();
						}
						else {
							//tell player it failed
							incommingMessage.getChannel().sendMessage("Failed to sell meme").queue();
						}
					} 
					else {
						incommingMessage.getChannel().sendMessage("Meme not found\nexample: `$sell pepe 1` in which the argument is the name or tag, pepe in this case, of a meme").queue();
					}
				} 
				else {
					incommingMessage.getChannel().sendMessage("Problem: No Round Started").queue();				
					if (currentMarket.startRound()) {
						incommingMessage.getChannel().sendMessage("Starting a new round\ntype $join to join this round of trading").queue();
						eb = marketEmbed(currentMarket);				
						//send embed
						incommingMessage.getChannel().sendMessage(eb.build()).queue();
						incommingMessage.getChannel().sendMessage(currentMarket.LeaderBoardToPrint()).queue();
					}
					else {
						incommingMessage.getChannel().sendMessage("Type $StartRound to start a round, failed to automatically do so").queue();	
					}
				}
				break;
			
			case "cancelorder":
				//sell a meme
				if(currentMarket.trading) {
					Player p = currentMarket.PlayingSearch(incommingMessage.getAuthor().getId());
					if(p==null) {
						incommingMessage.getChannel().sendMessage("You haven't joined this round of trading").queue();
						return;
					}
					if (command.length<2) {
						incommingMessage.getChannel().sendMessage("Error: Not enough arguments").queue();
					}				
					m =  currentMarket.MarketSearch(command[1]);
					if (m!=null) {
						int amount = 0;
						try {
							amount = Integer.parseInt(command[2]);
						}
						catch (NumberFormatException e) {
							incommingMessage.getChannel().sendMessage("Error: argument 2 was not a positive or negative intiger").queue();
							return;
						}
						if(p.cancelOrder(m, amount)) {
							//sucsesfully bought the meme
							incommingMessage.getChannel().sendMessage("Canceled an order for, "+amount+" of "+m.Name+"s").queue();
						}
						else {
							//tell player it failed
							incommingMessage.getChannel().sendMessage("Failed to cancel, order not found.").queue();
						}
					} 
					else {
						incommingMessage.getChannel().sendMessage("Meme not found\nexample: `$buy pepe 1` in which the argument is the name or tag, pepe in this case, of a meme").queue();
					}
				} 
				else {
					incommingMessage.getChannel().sendMessage("Trading not currently active, no orders").queue();
				}
				break;
				
			case"clearorder":
				//sell a meme
				if(currentMarket.trading) {
					Player p = currentMarket.PlayingSearch(incommingMessage.getAuthor().getId());
					if(p!= null) {
						p.clearOrders();
						incommingMessage.getChannel().sendMessage("Order queue cleared").queue();
					}else {
						incommingMessage.getChannel().sendMessage("You haven't joined this round of trading").queue();
					}
				} else {
					incommingMessage.getChannel().sendMessage("Trading not currently active").queue();
				}
				break;
				
			case"givemequeue":
				//pm the player their order queue.
				Player p = currentMarket.PlayingSearch(incommingMessage.getAuthor().getId());
				if(p!= null) {
					//send the player their queue
					incommingMessage.getAuthor().openPrivateChannel().queue((channel) ->
			        {
			            channel.sendMessage(p.OrderQueueToSTring()).queue();
			        });
				}else {
					incommingMessage.getChannel().sendMessage("You haven't joined this round of trading").queue();
				}
				break;
			
			case"help":
				//display commands
				incommingMessage.getChannel().sendMessage("All commands are triggered with `$` and to refernce a meme, use the meme's tag, as the name only works if it  has no spaces.\n`$initialize` Sends the start messag.\n`$startround` starts a new round of trading\n`$endround` ends a round of trading\n`$join` joins the current round of trading - so you will be paied interest and can buy/sell memes\n`$portfolio` privately messages you your portfolio of memes\n`$memeinfo [meme tag]` sends an embed with info about a meme including it's dividend % \n`$buy [tag] [amount]` buy `amount` of specified meme, or 1 if no amount is specified\n`$sell [tag] [amount]` sells the amount of that meme, or 1 if no amount is specified\n`$help` sends this wall  of text\n`$trade [tag] [amount] [amount of $] @otherplayer` offer to trade an amount of memes for the $amount of money from you to them **both numbers can be negitive so sign matters** NYI\n`$playmusic` plays the theme song. NYI\n`$stopmusic` stops the meme economy theme song. :( NYI.\n`$cancelorder [tag] [amount]` stops an order from this round\n`$clearorders` clears your orders for this round\n`$givemequeue` sends you your order queue in text\n`$getprices` sends the current market to chat").queue();
				break;
			
			//TODO: add these to help
			//TODO: add protection against cheating by clearing order queues
			case "trade":
				if(currentMarket.trading) {
					Player offerfrom = currentMarket.PlayingSearch(person.getId());
					
					if(offerfrom != null) {
						//start copy pasta for code reuse
						//   0     1              2                          3
						//$trade [tag] [amount from otherplayer] [amount of $ to otherplayer] @otherplayer
						List<Member> other = incommingMessage.getMentionedMembers();
						Player offerto = currentMarket.PlayerDict.get(other.get(0).getUser().getId());
						if(offerto == null) {
							incommingMessage.getChannel().sendMessage("Error: the other person has never played the bot").queue();
							return;
						}
						
						int moneytotrade ;
						int nummemes;
						try {
							nummemes = Integer.parseInt(command[2]);
							moneytotrade = Integer.parseInt(command[3]);
						}
						catch (NumberFormatException e) {
							incommingMessage.getChannel().sendMessage("Error: argument 2 or 3 was not an intiger").queue();
							return;
						}
						
						TradeOffer newoffer = new TradeOffer(currentMarket, offerfrom, offerto, command[1], nummemes,  moneytotrade);
						//TODO: maybe make this an embed?
						if(currentMarket.AddOffer(newoffer)) {
							incommingMessage.getChannel().sendMessage("Trade offer: "+ newoffer.toString()).queue();
						} else {
							incommingMessage.getChannel().sendMessage("Failed to send trade offer, one of you can't afford it, or doesn't have enough "+ command[1]+"s").queue();
						}
						//end og
						
					}
					else {
						if (currentMarket.join(incommingMessage.getAuthor())) {
							incommingMessage.getChannel().sendMessage("You have not joined this round; Adding you to this round of trading.").queue();
							//insert code here again
							List<Member> other = incommingMessage.getMentionedMembers();
							Player offerto = currentMarket.PlayerDict.get(other.get(0).getUser().getId());
							if(offerto == null) {
								incommingMessage.getChannel().sendMessage("Error: the other person has never played the bot").queue();
								return;
							}
							
							int moneytotrade ;
							int nummemes;
							try {
								nummemes = Integer.parseInt(command[2]);
								moneytotrade = Integer.parseInt(command[3]);
							}
							catch (NumberFormatException e) {
								incommingMessage.getChannel().sendMessage("Error: argument 2 or 3 was not an intiger").queue();
								return;
							}
							
							TradeOffer newoffer = new TradeOffer(currentMarket, offerfrom, offerto, command[1], nummemes,  moneytotrade);
						
							if(currentMarket.AddOffer(newoffer)) {
								//incommingMessage.getChannel().sendMessage("Trade offer: "+ newoffer.toString()).queue();
								incommingMessage.getChannel().sendMessage(newoffer.offerEmbed().build()).queue();
							} else {
								incommingMessage.getChannel().sendMessage("Failed to send trade offer, one of you can't afford it, or doesn't have enough "+ command[1]+"s").queue();
							}
							//end pasta
						
						}
						else {
							incommingMessage.getChannel().sendMessage("Failed to Add you to this round of trading.").queue();
						}
					}				
				} else {
					incommingMessage.getChannel().sendMessage("No active trading round.\nType $StartRound to start a round.").queue();
				}
				break;
			
			case "acceptoffer":
				if(currentMarket.trading) {
					//$acceptoffer @offerfrom
					List<Member> other = incommingMessage.getMentionedMembers();
					
					TradeOffer offertoaccept = currentMarket.searchOffers(other.get(0).getUser(), person);
					if(offertoaccept!= null) {
						if(currentMarket.AcceptOffer(offertoaccept)) {
							incommingMessage.getChannel().sendMessage("Accepted: "+offertoaccept.toString()).queue();
						}
						
					}else {
						incommingMessage.getChannel().sendMessage("Could not find a trade offer from that person to you.").queue();
					}
					
				} else {
					incommingMessage.getChannel().sendMessage("No active trading round.\nType $StartRound to start a round.").queue();
				}
				break;
				
			case "canceloffer":
				if(currentMarket.trading) {
					//$canceloffer @offerfrom
					List<Member> other = incommingMessage.getMentionedMembers();
					
					TradeOffer offertocancel = currentMarket.searchOffers(person, other.get(0).getUser());
					if(offertocancel!= null) {
						if(currentMarket.CancelOffer(offertocancel)) {
							incommingMessage.getChannel().sendMessage("Canciled: "+offertocancel.toString()).queue();
						}
						
					}else {
						incommingMessage.getChannel().sendMessage("Could not find a trade offer from that person to you.").queue();
					}
					
				} else {
					incommingMessage.getChannel().sendMessage("No active trading round.\nType $StartRound to start a round.").queue();
				}
				break;
			
			case "playmusic":
				//respond with command not yet implemented
				incommingMessage.getChannel().sendMessage("Not Implemented for this release").queue();
				break;
			
			case "pausemusic":
				//respond with command not yet implemented
				incommingMessage.getChannel().sendMessage("Not Implemented for this release").queue();
				break;
				
			case "leaderboard":
				incommingMessage.getChannel().sendMessage(currentMarket.LeaderBoardToPrint()).queue();
				break;
				
			default:
				incommingMessage.getChannel().sendMessage("Unknown Command").queue();
				break;
		}

	}

}
