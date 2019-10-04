/**
 * 
 */
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
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

/**
 * @author dppet
 *
 */
class CommandsHandler {
	static HashMap<Guild,Market> marketdict = new HashMap<Guild,Market>(6);
	
	//for checking if a guild has a market already?
	static boolean MarketInitalized(Guild g) {
		return marketdict.containsKey(g);
	}
	
	
	
	@SuppressWarnings("unused")
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
	
	
	
	void GetPrices(Guild g, MessageChannel c) {
		//tell people what the prices are on command
		EmbedBuilder eb = marketEmbed(marketdict.get(g));
		c.sendMessage(eb.build()).queue();
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
	
	//TODO: get prices
	
	//TODO: portfolio	
	
	/*THOUGHT!!! SO KEEP ROUNDS, BUT HAVE SERVER MODES BEHIND THE SECENES
	 * so after x orders are in queue, ends the round + give people a button 
	 * 
	 */
	
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
