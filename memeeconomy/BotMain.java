package me.noelwiz.bots.memeeconomy;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class BotMain extends ListenerAdapter{
	public static JDA api;
	
	public static void main(String[] args) {
		//read file
		String botToken = "";
		try {
			Scanner tokenScanner = new Scanner(new File("bottoken.txt"));
			botToken = tokenScanner.next();
		} catch(FileNotFoundException e) {
			System.out.println(e);
			System.out.println("Please create a file called 'botoken.txt' and put your bot user's token into it");			
		}
		
		
		
		try {
			api = new JDABuilder(AccountType.BOT).setToken(botToken).buildBlocking();
			api.getPresence().setGame(Game.of(Game.GameType.DEFAULT, "Meme-onomics"));
			api.addEventListener(new MemeEconomyCommandInterpreter());
		} catch (LoginException | IllegalArgumentException | InterruptedException e) {
			System.out.println("Failed to connect to the api.");
			e.printStackTrace();
		}
		
		System.out.println("Exiting bot");
	}
}
