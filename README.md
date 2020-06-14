# MemeEconomyBot
A Discord Based Stock Market Simulation game.<br>

# Demos
![An Image of a New Round of Meme Trading](https://cdn.discordapp.com/attachments/407351922828378116/721611060137951282/unknown.png) <br>
The bot works on a round system. Every round, people can buy and sell stocks, in this case memes, and at the end of the round, 
prices will change, and players will get dividends from their stocks. The motivation behind the round based system
is that I don't think it would be fun for players to passively earn tons of money while theyre gone 
if the bot has been very active. Returning 50 rounds later to a bunch of money is not fun, unless there was 
enough depth like [Cookie Clicker](https://en.wikipedia.org/wiki/Cookie_Clicker) has.<br>
![Image of A Meme Portfolio](https://media.discordapp.net/attachments/407351922828378116/721609844980842516/unknown.png) <br>
The bot messages every player in the round a summary of their portfolio at the end of every round.<br>
![Buying A Meme](https://cdn.discordapp.com/attachments/407351922828378116/721610575334998036/unknown.png)<br>
A short tag is used to buy stocks because spaces are used to separate command arguments. People can buy and sell stocks at any
point during a round they have entered. 

# Why I did this
I'm currently in the middle of re-writing it to fit into the model view controller (MVC) paradigm and somewhat redesigning the
round based system after testing it and noticing people didn't like ending rounds, so it was left unused. 
Originally, I wanted to make a stock market simulation after my databases class used stock market data for much of the programming assignments, 
and the idea of a "meme economy" was popular at the time. I had made a few discord bots a few years earlier, the Python Maze repository
was one I built in 2017 originally, but didn't finish, so this was my return to discord bots in Java. 
