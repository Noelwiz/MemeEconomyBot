package economy;

import java.util.ArrayList;
import java.util.List;

public class MemeReference {
	public static Meme owo = new Meme("owo","What's This?", 2, 0.9);
	public static Meme datboi = new Meme("datboi","Dat Boi", 5, 0.7);
	public static Meme pepe = new Meme("pepe","Pepe the frog", 10, 0.65);
	public static Meme tidepods = new Meme("tide","Tide Pods", 25, .6);
	public static Meme adviceanimals = new Meme("anim","Advice Animals", 50, .5);
	public static Meme meta = new Meme("meta","Meta", 75, .4);
	public static Meme UwwwU = new Meme("uwu","UwwwU", 100, .3);
	public static Meme memeeconomy = new Meme("memeec","Meme Economy", 200, .2);
	public static Meme phones = new Meme("phone","Don't You Have Phones?", 500, .1);
	public static Meme TodHoward = new Meme("Todd","ToddHoward", 1000, .01);
	
	
	
	public static ArrayList<Meme> memes = new ArrayList<Meme>(){
		/**
		 * I have no clue what this is
		 */
		private static final long serialVersionUID = 1L;

		{
			add(MemeReference.datboi);
			add(MemeReference.pepe);
			add(MemeReference.tidepods);
			add(MemeReference.adviceanimals);
			add(MemeReference.memeeconomy);
			add(MemeReference.meta);
			add(MemeReference.UwwwU);
			add(MemeReference.phones);
			add(MemeReference.owo);
			add(MemeReference.TodHoward);
		}
	};

}
	