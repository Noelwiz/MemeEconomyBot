package economy;

import net.dv8tion.jda.core.entities.User;

public class PlayerDictionary {
	public Player[] dictionary = new Player[61];
	private int NumPlayers = 0;
	
	public Player search(String id) {
		//search for a player
		int start = (int)((Long.parseLong(id)/2)%60);
		if (dictionary[start] == null) {
			return null;
		} 
		
		if(dictionary[start].id.equals(id)) {
			return dictionary[start];
		} else {
			int current = (start+1)%60;
			while(current!=start) {
				if (dictionary[current] == null) {
					return null;
				}
				else if(dictionary[start].id.equals(id)) {
					return dictionary[current];
				}
				current = (current+1)%60;
			}
		}
		//null if not in dictionary
		System.out.println("failed to find a player in playerdictionary");
		return null;
	}
	
	public Player add(User person, MemeLinkedList memeset) {
		//Player newplayer;
		int start = (int)((person.getIdLong()/2)%60);
		if(dictionary[start] == null) {
			dictionary[start] =  new Player(person, memeset);
			NumPlayers++;
			return dictionary[start];
		}
		else {
			int current = (start+1)%60;
			while(current!=start) {
				
				if (dictionary[current] == null) {
					dictionary[current] = new Player(person, memeset);
					NumPlayers++;
					return dictionary[current];
				}
				else if (dictionary[current].id.equals(person.getAvatarId())) {
					//NumPlayers++;
					return dictionary[current];					
				} else {
					current = (current+1)%60;
				}			
			}
			if (current == start) {
				System.out.println("No Room for players");
				return null;
			}
			return null;
			
			
		}
		
	}
	
	public String LeaderBoardToPrint() {
		Player[] sortedBoard = GetLeaderBoard();
		sortedBoard = sortLeaderBoard(sortedBoard);
		
		String Leaderboard = "Leader Board: \n";
		
		for (int i=0; i < sortedBoard.length; i++) {
			if(sortedBoard[i]!=null && sortedBoard[i].id !=null) {
				Leaderboard += "#"+(i+1)+": "+sortedBoard[i].thisuser.getName()+" with $"+sortedBoard[i].NetWorth;
			} else {
				System.out.println("error, some of the players in sortedBoard are null, index: "+i);
			}
		}
		
		return Leaderboard;
		
	}
	
	
	private Player[] GetLeaderBoard() {
		Player[] leaderboard = new Player[NumPlayers];
		int current = 0;
		for(int i=0; i<61;i++) {
			if (dictionary[i] != null && dictionary[i].id != null) {
				leaderboard[current] = dictionary[i];
				current++;
				
				//if we've filled up the dictionary, return, slight optimiazation
				if(current>NumPlayers) {
					return leaderboard;
				}
				
			}
		}
		return leaderboard;
	}
	
	private Player[] sortLeaderBoard(Player[] board) {
		//Player[] sortedBoard = board.clone();
		
		//sortedBoard[0]=board[0];
		
		for (int i = 1; i < board.length; i++) {
			Player playerToInsert = board[i];
			//save this value
			int toInsert = board[i].NetWorth;
			
			//end in text book
			int index = i-1;
			//begin = 0;
			
			//make room for new entery
			while ( index>= 0 &&   board[index-1].NetWorth < toInsert) {
				board[index+1] = board[index];
				index--;
			}
			
			board[index+1] = playerToInsert;
			
		}
		
		
		return board;
	}
	
//	private Long keyFunct(Player p) {
//		Long id = Long.parseLong(p.thisuser.getId());
//		int key =  (id%60);
//		return key;
//	}
//	
//	private Long keyFunct(User u) {
//		Long id = Long.parseLong(u.getId());
//		int key = (int) (id%60);
//		return key;		
//	}
//	
//	private Long keyFunct(String id) {
//		Long id1 = new Long(id);
//		int key =  id1%60;
//		return key;		
//	}
}
