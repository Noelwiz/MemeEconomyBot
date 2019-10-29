package me.noelwiz.bots.memeeconomy;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.RichPresence;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

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
			api = new JDABuilder(AccountType.BOT).setToken(botToken).build();
			api.getPresence().setActivity(Activity.playing("Meme-o-nomics"));
			api.addEventListener(new MemeEconomyCommandInterpreter());
		} catch (LoginException | IllegalArgumentException  e) {
			System.out.println("Failed to connect to the api.");
			e.printStackTrace();
		}
		
		System.out.println("Exiting bot");
	}
}
