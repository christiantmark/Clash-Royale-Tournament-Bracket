import java.util.*;
import java.io.*;
import java.util.concurrent.ThreadLocalRandom;

public class ClashTournamentController {
	
	static Scanner keys = new Scanner (System.in);
	
	static String gamemode;
	// player related array lists
	static ArrayList<ClashPlayer> players = new ArrayList<ClashPlayer>();
	static ArrayList<ClashMatchup> matchups = new ArrayList<ClashMatchup>();
	// deck related array lists
	static ArrayList<ClashCard> cards = new ArrayList<ClashCard>();
	static ArrayList<ClashDeck> classicDecks = new ArrayList<ClashDeck>();
	// constants
	static final int NUM_OF_DECK_CARDS = 8;
	static final int MAX_NUM_LOSS = 2;
	static final String MIRROR_MODE = "mirror";
	static final String CLASSIC_MODE = "classic";
	static final String REGULAR_MODE = "regular";
	static final String DRAFT_MODE = "draft";
	
	public static void main(String[] args)
	{		
		// get initial list of players
		getPlayers();
		// get game mode for tournament
		saveGameMode();
		// get all clash cards
		readClashCards();
		// get all classic decks
		readClassicDecks();
		
		while (players.size() > 1) {
			System.out.println();
			System.out.println("Start of new round of tournament");
			
			runTournamentBracket();
		}
		
		// print out winner and congratz message
		showWinnerMessage();
	}
	
	// runs all game mode methods, runs one round of the tournament bracket
	public static void runTournamentBracket()
	{
		getMatchup();
		
		if (gamemode.equals(MIRROR_MODE))
		{
			showAllMirrorDecks();
		}
		else if (gamemode.equals(CLASSIC_MODE))
		{
			showAllClassicDecks();
		} 
		else if (gamemode.equals(DRAFT_MODE))
		{
			showDraftOptions();
		} 
		else if (gamemode.equals(REGULAR_MODE)) 
		{
			System.out.println("Players please prepare your own decks!");
		}
		
		updateLossCounter();		
	}

	// get the player in the players array
	// print their name and winning message
	public static void showWinnerMessage() 
	{
		if (players.size() == 1)
		{
			System.out.println("Congratulations! " + players.get(0).id + " has won the tournament!");
		}
	}
	
	// ask user to enter name
	public static void getPlayers()
	{
		String response;
		System.out.println("Who are these players? (Enter names separated by comma with no space)");
		response = keys.nextLine();
		
		// take list of player names from user input
		String [] nameArray = response.split(",");
		
		// go through name array and add player to global list
		for (String name: nameArray) {
			ClashPlayer player = new ClashPlayer(name);
			players.add(player);
		}
	}
	
	// ask user for game mode
	public static void saveGameMode()
	{
		String response;
		System.out.println("Which game mode do you want to play?");
		System.out.println("Game modes available are: Regular, Classic, Mirror, Draft");
		response = keys.nextLine().toLowerCase();
		
		while (!response.equals(MIRROR_MODE) && !response.equals(CLASSIC_MODE) 
				&& !response.equals(REGULAR_MODE) && !response.equals(DRAFT_MODE)) {
			System.out.println("Invalid - please type in " + MIRROR_MODE + " or " + CLASSIC_MODE + " or " + DRAFT_MODE + " or "+ REGULAR_MODE);
			System.out.println("Please enter a game mode again");
			response = keys.nextLine().toLowerCase();
		}
		
		gamemode = response;
	}
	
	// makes all of the player matchups
	public static void getMatchup()
	{
		matchups.clear();
		
		// randomize player order
		Collections.shuffle(players);
		
		// make matchup from 2-player pairs
		int lastIndexInbound = players.size() - 1; 
		for (int i = 0; i < players.size() - 1; i+=2) {
			int player1Index = i;
			int player2Index = i + 1;
			
			if (player2Index <= lastIndexInbound) {
				ClashPlayer player1 = players.get(player1Index);
				ClashPlayer player2 = players.get(player2Index);
			
				ClashMatchup matchup = new ClashMatchup(player1, player2);
				matchups.add(matchup);
			}
		}
	}
	
	// records when a player loses, if a player has 2 losses then they will be removed from the tournament
	public static void updateLossCounter()
	{
		for (ClashMatchup match: matchups)
		{
			String response;
			System.out.println(match.player1.id + " v.s " +  match.player2.id);
			System.out.println("Who won? Enter '1' for left side player and '2' for right side player");
			response = keys.nextLine();
			
			while (!response.equals("1") && !response.equals("2")) {
				System.out.println("Invalid input. Please type in 1 or 2");
				response = keys.nextLine();
			}
			
			if (response.equals("1"))
			{
				match.player2.incrementLossCount();
				System.out.println(match.player2.id + "'s loss counter has been updated.");
			}
			else if (response.equals("2"))
			{
				match.player1.incrementLossCount();
				System.out.println(match.player1.id + "'s loss counter has been updated.");
			}
		}
		
		ArrayList<ClashPlayer> playersLeft = new ArrayList<ClashPlayer>();
		for (ClashPlayer player: players) {
			if (player.lossCount < MAX_NUM_LOSS)
			{
				// how to safely remove an element of the array while looping through array
				playersLeft.add(player);
			} else {
				System.out.println(player.id + " is out of the tournament! Better luck next time :)");
			}
		}
		
		players = playersLeft;
	}
	
	// reads all the Clash cards from the text file
	public static void readClashCards()
	{		 
		 BufferedReader reader = null;
		 try {
			 // initializing the text file 
		     File cardFile = new File("ClashCardList.txt");
		     reader = new BufferedReader(new FileReader(cardFile));

		     // reading each line of text file until no more line
		     String line;
		     while ((line = reader.readLine()) != null) {
		    	 	// each line is a card name, make clash card object from the name 
		         ClashCard card = new ClashCard(line);
		         cards.add(card);
		     }

		 } catch (IOException e) {
			 // debug error
		     e.printStackTrace();
		 } finally {
			 // close the file reader
		     try {
		         reader.close();
		     } catch (IOException e) {
		         e.printStackTrace();
		     }
		 }
		 
		 System.out.println();
//		 System.out.println("Printing out all cards:");
//		 for (ClashCard card: cards)
//		 {
//			 System.out.println(card.id);
//		 }
	}
	
	// shows all mirror decks available for each matchup
	public static void showAllMirrorDecks() {
		for (ClashMatchup matchup: matchups)
		{
			// print out the matchup
			System.out.println(matchup.player1.id + " v.s " + matchup.player2.id);
			System.out.println("These are the cards in your mirror deck:");
		
			// calling makeMirrorDeck to make a mirror deck
			ClashDeck mirrorDeck = makeMirrorDeck();
			// print out the content of the mirror deck
			for (ClashCard card: mirrorDeck.cards)
			{
				System.out.println(card.id);
			}
			System.out.println();
		}
	}
	
	// makes a single mirror deck
	public static ClashDeck makeMirrorDeck()
	{
		//shuffles cards in the array list
		Collections.shuffle(cards);
		
		// make a new empty list that stores ClashCards
		ArrayList<ClashCard> listOfCards = new ArrayList<ClashCard>();
		// fill in the list with 8 cards from the randomized array
		for (int i = 0; i < NUM_OF_DECK_CARDS; i++) {
			ClashCard card = cards.get(i);
			listOfCards.add(card);
		}
		
		ClashDeck mirrorDeck = new ClashDeck(listOfCards);
		return mirrorDeck;
	}
	
	//reads the classic decks from the text file
	public static void readClassicDecks()
	{
		BufferedReader reader = null;
		 try 
		 {
			 // initializing the text file 
		     File cardFile = new File("ClashClassicDeckList.txt");
		     reader = new BufferedReader(new FileReader(cardFile));

		     // make a new empty list that stores ClashCards
		 	ArrayList<ClashCard> listOfCardsRead = new ArrayList<ClashCard>();
		 	
		     String line;
		     while ((line = reader.readLine()) != null) 
		     {
		    	 	if (line.equals("END_OF_DECK")) 
		    	 	{		 
		    	 		// copy value of cards in current listOfCards
		    	 		ArrayList<ClashCard> deckCards = new ArrayList<ClashCard>();
		    	 		for (ClashCard card: listOfCardsRead) {
		    	 			deckCards.add(card);
		    	 		}
		    	 		// make deck using these cards
		    	 		ClashDeck deck = new ClashDeck(deckCards);
		    	 		
		    	 		// add deck to global list of classic decks
		    	 		classicDecks.add(deck);
		    	 		// clear existing list of cards read
		    	 		listOfCardsRead.clear();
		    	 	}
		    	 	else 
		    	 	{
		    	 		// read card and add to listOfCards
		    	 		ClashCard card = new ClashCard(line);
		    	 		listOfCardsRead.add(card);
		    	 	} 
		     }
		     
/*		     System.out.println("Here are the classic decks:");
		     for (ClashDeck deck: classicDecks)
		     {
		    	 	for (ClashCard card: deck.cards)
		    	 	{
		    	 		System.out.println(card.id);
		    	 	}
		    	 	System.out.println();
		     } 
*/  
		 } catch (IOException e) {
			 // debug error
		     e.printStackTrace();
		 } finally {
			 // close the file reader
		     try {
		         reader.close();
		     } catch (IOException e) {
		         e.printStackTrace();
		     }
		 }
		 
		 System.out.println();
	}
	
	// prints out all the classic decks
	public static void showAllClassicDecks()
	{
		for (ClashPlayer player: players)
		{
			System.out.println(player.id + ", these are the cards in your Classic Deck: ");
			int randomIndex = ThreadLocalRandom.current().nextInt(0, classicDecks.size());
			ClashDeck deck = classicDecks.get(randomIndex);
			for (ClashCard card: deck.cards)
			{
				System.out.println(card.id);
			}
			System.out.println();
		}
	}
	
	public static void showDraftOptions() {
		for (ClashMatchup matchup:matchups)
		{
			showMatchupDraftOptions(matchup);
		}
	}

	public static void showMatchupDraftOptions(ClashMatchup matchup)
	{
		Collections.shuffle(cards);
		
		// player1's deck: what player1 chose + what player2 didn't choose
		ArrayList<Integer> player1CardNumbers = new ArrayList<Integer>();
		// player2's deck: what player2 chose + what player1 didn't choose
		ArrayList<Integer> player2CardNumbers = new ArrayList<Integer>();
		
		
		// ==== Player 1 =====
		// get draft options for player 1
		Map<Integer, ClashCard> indexToPlayerCards1 = new HashMap<Integer, ClashCard>();
		for (int i = 0; i < NUM_OF_DECK_CARDS; i++) {
			ClashCard card = cards.get(i);
			int cardNumber = i + 1;
			System.out.println(cardNumber + " " + card.id);
			
			indexToPlayerCards1.put(cardNumber, card);	
		}
		
		// get player 1's response and check if response is valid 
		ArrayList<String> cardNumberStrResponseArray = new ArrayList<String>(); 
		boolean isValid = getDraftResponse(matchup.player1.id, indexToPlayerCards1.keySet(), cardNumberStrResponseArray);
		
		while(!isValid) {
			System.out.println("Invalid response. Please try again.");
			cardNumberStrResponseArray.clear();
			isValid = getDraftResponse(matchup.player1.id, indexToPlayerCards1.keySet(), cardNumberStrResponseArray);
		}
		
		// store player 1's choices and populate half of player 2's cards 
		for (String numberStr: cardNumberStrResponseArray) {
			Integer number = Integer.parseInt(numberStr);			
			player1CardNumbers.add(number);
		}
		
		for (Integer key: indexToPlayerCards1.keySet()) {
			if (!player1CardNumbers.contains(key))
			{
				player2CardNumbers.add(key);
			}
		}
		
		// ==== Player 2 =====
		// get draft options for player 2
		Map<Integer, ClashCard> indexToPlayerCards2 = new HashMap<Integer, ClashCard>();
		for (int i = NUM_OF_DECK_CARDS; i < NUM_OF_DECK_CARDS * 2; i++) {
			ClashCard card = cards.get(i);
			int cardNumber = i+1;
			System.out.println(cardNumber + " " + card.id);
			
			indexToPlayerCards2.put(cardNumber, card);
			
		}
		
		// get player 2's response and check if response is valid 
		ArrayList<String> cardNumberStrResponseArray2 = new ArrayList<String>(); 
		boolean isValid2 = getDraftResponse(matchup.player2.id, indexToPlayerCards2.keySet(), cardNumberStrResponseArray2);
		
		while(!isValid2) {
			System.out.println("Invalid response. Please try again.");
			cardNumberStrResponseArray2.clear();
			isValid2 = getDraftResponse(matchup.player2.id, indexToPlayerCards2.keySet(), cardNumberStrResponseArray2);
		}
	
		// store player 2's choices and populate half of player 1's cards 
		for (String numberStr: cardNumberStrResponseArray2) {
			Integer number = Integer.parseInt(numberStr);			
			player2CardNumbers.add(number);
		}
		
		for (Integer key: indexToPlayerCards2.keySet()) {
			if (!player2CardNumbers.contains(key))
			{
				player1CardNumbers.add(key);
			}
		}
		
		// printing out decks
		Map<Integer, ClashCard> indexToPlayersCardsCombined = new HashMap <Integer, ClashCard>();
		indexToPlayersCardsCombined.putAll(indexToPlayerCards1);
		indexToPlayersCardsCombined.putAll(indexToPlayerCards2);
		
		System.out.println();
		System.out.println("This is " + matchup.player1.id + "'s deck: ");
		for (Integer cardNumber: player1CardNumbers)
		{
			ClashCard card = indexToPlayersCardsCombined.get(cardNumber);
			System.out.println(card.id);
		}
		System.out.println();
		System.out.println("This is " + matchup.player2.id + "'s deck: ");
		for (Integer cardNumber: player2CardNumbers)
		{
			ClashCard card = indexToPlayersCardsCombined.get(cardNumber);
			System.out.println(card.id);
		}
		System.out.println();
	}
	
	public static boolean getDraftResponse(String name, Set<Integer> validCardNumbers, ArrayList<String> responseArray) 
	{
		// player 1 prompt
		System.out.println(name + ", choose 4 of the " + NUM_OF_DECK_CARDS + " cards:");
		System.out.println("Please enter the number on the left hand side separated by commas with no space");
		
		String response;
		response = keys.nextLine();
		response = response.replaceAll("\\s",""); // remove white space 
		String [] cardNumberStrArray = response.split(",");
		
		// populate response back
		responseArray.addAll(Arrays.asList(cardNumberStrArray));
		
		boolean isValid = true;
		// filter out repeat entries 
		Set<String> uniqueCardNumberStrSet = new HashSet<String>(Arrays.asList(cardNumberStrArray));
		
		// check if user entered the correct number of card numbers
		if (uniqueCardNumberStrSet.size() != validCardNumbers.size() / 2) {
			return false;
		}
		
		// check if numbers entered are in the range of numbers given
		for (String str: cardNumberStrArray)
		{
			Integer number = Integer.parseInt(str);
			if (!validCardNumbers.contains(number))
			{
				isValid = false;
			} 
		}
		
		return isValid;
	}
}