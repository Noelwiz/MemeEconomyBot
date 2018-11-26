package economy;

public class Order {
	Meme m;
	double MoneyChange;
	int Amount;
	
	public Order(Meme m, int num) {
		this.m=m;
		Amount = num;
		MoneyChange = num*(m.Price*-1);
	}
	
	public Order(Meme meme, int numofmeme, int moneyexhanged) {
		//for trading
		m = meme;
		Amount = numofmeme;
		//don't forget this isn't an absolute value
		MoneyChange = moneyexhanged;
	}
	
	public String toString() {
		if (Amount>0) {
			return new String("Buying "+Amount+" of "+m.Name+" for "+MoneyChange);
		} else {
			return new String("Selling "+Amount+" of "+m.Name+" for "+MoneyChange);
		}
	}
}
