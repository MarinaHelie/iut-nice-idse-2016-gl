package fr.unice.idse.model.save;

import java.util.ArrayList;
import java.util.Map;

import fr.unice.idse.db.DataBaseCard;
import fr.unice.idse.db.DataBaseGame;
import fr.unice.idse.model.Deck;
import fr.unice.idse.model.Game;
import fr.unice.idse.model.card.Card;
import fr.unice.idse.model.player.Player;

public class Load {
	protected DataBaseGame dbg;
	protected DataBaseCard dbc;

	public Load() {
		dbg = new DataBaseGame();
		dbc = new DataBaseCard();
	}
	
	
	public Game load(String gameName) throws Exception{
		Game game = dbg.getGameWithName(gameName);
		
		if(game == null) {
			throw new Exception("ERROR : No game exist with this name");
		}
		
		game.getBoard().getDeck().initDeck();
		
		initPlayer(game);
		initHands(game);
		initLoadStack(game);
		
		return game;
	}
	
	
	private void initLoadStack(Game game){
		String gameName = game.getName();
		
		int gameId = dbg.getIdgameWithName(gameName);
		
		int matchId = dbg.getIdMatchWithGameId(gameId);
		
		ArrayList<Card> listStack = dbg.getStackWithMatchId(matchId);
		
		game.getBoard().getStack().setStack(listStack);

		Deck deck = game.getBoard().getDeck();
		
		for(int i =0; i<= listStack.size();i++){
			deck.removeCard(listStack.get(i)); 
		}
		
	}
	
	private void initHands(Game game){
		int gameId = dbg.getIdgameWithName(game.getGameName());
		int matchId = dbg.getIdMatchWithGameId(gameId);
		
		Map<String, ArrayList<Card>> map = dbg.getLastHandPlayers(matchId);
		
		for (Player player : game.getPlayers()) {
			player.setCards(map.get(player.getName()));
		}
		
	}
	
	private void initPlayer(Game game){
		String gameName = game.getName();
		
		int gameId = dbg.getIdgameWithName(gameName);
		
		ArrayList<Player> listPlayers = dbg.getIdUserAndPositionWithGameId(gameId);
		
		game.setPlayers(listPlayers);
		
		
	}
	
}
