package me.noelwiz.bots.memeeconomy;


import java.util.List;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/***
 * Takes in the discord api events and sends them to the handler
 * 
 * Basically the controller part of modle view controller 
 * @author dppet
 *
 */
public class MemeEconomyCommandInterpreter extends ListenerAdapter {
	private static String trigger = "$";
	private CommandsHandler handler = new CommandsHandler();
	
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {	
		//every time a message is sent from a server, an event will be receved here
		Message incommingMessage = event.getMessage();
		
		String[] arguments = incommingMessage.getContentDisplay().split(" ");
		String command = arguments[0];
		
		
		if(! command.startsWith(trigger)) {
			//return now if not present
			System.out.println("hey, no trigger");
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
			//get the detailed information about a meme
			case"memeinfo":
				String targetmeme = arguments[1];
				handler.GetMemeInfo(incommingMessage.getGuild(), incommingMessage.getChannel(), targetmeme);
				break;
			//see your meme portfolio
			case"portfolio":
				//only send the persons portfolio who did the command
				handler.PMPortfolio(incommingMessage.getGuild(), incommingMessage.getAuthor());
				break;			
			//buy a meme
			//TODO: Update help command for this
			case "buy":
				if (arguments.length<=2) {
					handler.ErrorNotEnoughArgsResponse(incommingMessage.getChannel());
				}else if(arguments.length>3) {
					//player attempted to specify the number of memes to sell
					int amount = 0;
					try {
						amount = Integer.parseInt(arguments[2]);
					}
					catch (NumberFormatException e) {
						handler.ErrorArgumentNotAIntResponse(incommingMessage.getChannel(), 2);
						return;
					}
					handler.BuyMeme(incommingMessage.getGuild(), incommingMessage.getChannel(), incommingMessage.getAuthor(), arguments[1], amount);
				} else {
					//player said didn't give an amount of the meme to sell
					handler.BuyMeme(incommingMessage.getGuild(), incommingMessage.getChannel(), incommingMessage.getAuthor(), arguments[1], 1);
				}
				break;
			//sell a meme
			case "sell":
				if (arguments.length<=2) {
					handler.ErrorNotEnoughArgsResponse(incommingMessage.getChannel());
				}else if(arguments.length>3){
					//player attempted to specify the number of memes to sell
					int amount = 0;
					try {
						amount = Integer.parseInt(arguments[2]);
					}
					catch (NumberFormatException e) {
						handler.ErrorArgumentNotAIntResponse(incommingMessage.getChannel(), 2);
						return;
					}
					handler.SellMeme(incommingMessage.getGuild(), incommingMessage.getChannel(), incommingMessage.getAuthor(), arguments[1], amount);
				} else {
					//player said didn't give an amount of the meme to sell
					handler.SellMeme(incommingMessage.getGuild(), incommingMessage.getChannel(), incommingMessage.getAuthor(), arguments[1], 1);
				}
				break;
			//TODO: change this commands name in help from givemeorderqueue
			//print the pending orders for a user
			case"checkorders":
				handler.CheckOrders(incommingMessage.getGuild(), incommingMessage.getChannel(), incommingMessage.getAuthor());
				break;
			//cancel an order for a meme	
			case "cancelorder":
				if (arguments.length<=2) {
					handler.ErrorNotEnoughArgsResponse(incommingMessage.getChannel());
				}else if(true) {
					int amount = 0; //TODO: FIGURE OUT WHY THIS IS HERE BECAUSE IT SHOULDN'T BE SO SOMETHING ELSE IS PROBABLY BROKEN
					try {
						amount = Integer.parseInt(arguments[2]);
					}
					catch (NumberFormatException e) {
						handler.ErrorArgumentNotAIntResponse(incommingMessage.getChannel(), 2);
						return;
					}
				}
				break;											
			//TODO: ADD an s to this command in help
			//delete all current orders
			case"clearorders":
				handler.ClearOrders(incommingMessage.getGuild(), incommingMessage.getChannel(), incommingMessage.getAuthor());
				break;
			//TODO: add these to help
			//TODO: add protection against cheating by clearing order queues
			//ask to trade one persons memes for another's money
			case "trade":
				//   0     1              2                          3
				//$trade [tag] [amount from otherplayer] [amount of $ to otherplayer] @otherplayer
				List<Member> other = incommingMessage.getMentionedMembers();
				User otherUser = other.get(0).getUser(); 
				if(otherUser == null) {	
					handler.ErrorNoUserMentionedResponse(incommingMessage.getChannel(), 5);
					return;
				}	
				int moneytotrade;
				int nummemes;
				//check arg 2
				try {
					nummemes = Integer.parseInt(arguments[2]);
				}
				catch (NumberFormatException e) {
					handler.ErrorArgumentNotAIntResponse(incommingMessage.getChannel(), 2);
					return;
				}
				//check arg 3
				try {
					moneytotrade = Integer.parseInt(arguments[3]);
				}
				catch (NumberFormatException e) {
					handler.ErrorArgumentNotAIntResponse(incommingMessage.getChannel(), 3);
					return;
				}	
				handler.Trade(incommingMessage.getGuild(), incommingMessage.getChannel(), incommingMessage.getAuthor(), otherUser, arguments[1], nummemes, moneytotrade);
				break;				
			//TODO Change name in help
			//accept a trade from another user
			case "accepttrade":
				List<Member> mentionedusers = incommingMessage.getMentionedMembers();
				User offerFrom = mentionedusers.get(0).getUser(); 
				if(offerFrom == null) {	
					handler.ErrorNoUserMentionedResponse(incommingMessage.getChannel(), 2);
					return;
				}	
				handler.AcceptTrade(incommingMessage.getGuild(), incommingMessage.getChannel(), incommingMessage.getAuthor(), offerFrom);
				break;				
			//TODO: add a decline offer command
			//TODO: ADD that command to help
			//declines an offer from another user
			case"declineoffer":
				System.out.println("this is not yet implemented, decline offer");
				break;	
			//Cancel an offer you made to a user
			case "canceloffer":
				//List<Member> mentionedusers = incommingMessage.getMentionedMembers();
				User DecOfferFrom = incommingMessage.getMentionedMembers().get(0).getUser(); 
				if(DecOfferFrom == null) {	
					handler.ErrorNoUserMentionedResponse(incommingMessage.getChannel(), 2);
					return;
				}	
				handler.AcceptTrade(incommingMessage.getGuild(), incommingMessage.getChannel(), incommingMessage.getAuthor(), DecOfferFrom);
				break;	
			//print a leaderboard of money
			case "leaderboard":
				handler.Leaderboard(incommingMessage.getGuild(), incommingMessage.getChannel());
				break;
			//display commands
			case"help":				
				handler.Help(incommingMessage.getChannel());
				break;
			//TODO: Write
			case "playmusic":
				//respond with command not yet implemented
				incommingMessage.getChannel().sendMessage("Not Implemented for this release").queue();
				break;
			//TODO: WRITE
			case "pausemusic":
				//respond with command not yet implemented
				incommingMessage.getChannel().sendMessage("Not Implemented for this release").queue();
				break;	
			//error
			default:
				incommingMessage.getChannel().sendMessage("Unknown Command").queue();
				break;
		}

	}

}
