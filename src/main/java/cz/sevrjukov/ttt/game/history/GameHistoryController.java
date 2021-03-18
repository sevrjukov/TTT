package cz.sevrjukov.ttt.game.history;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.sevrjukov.ttt.board.Board;
import cz.sevrjukov.ttt.game.history.GameEvent.EventType;
import cz.sevrjukov.ttt.game.history.GameEvent.Player;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

public class GameHistoryController {

	private GameHistory gameHistory = new GameHistory();

	private static Player getPlayer(int player) {
		if (player == Board.HUMAN) {
			return Player.HUMAN;
		} else {
			return Player.COMPUTER;
		}
	}

	public void newGame() {
		final Game newGame = Game
				.builder()
				.dateStarted(LocalDateTime.now())
				.events(new ArrayList<>())
				.id(UUID.randomUUID().toString())
				.build();
		gameHistory.getGamesList().add(newGame);
	}

	public Game getLastGame() {
		var gamesList = gameHistory.getGamesList();
		if (!gamesList.isEmpty()) {
			return gamesList.get(gamesList.size() - 1);
		} else {
			return null;
		}
	}

	public void recordMove(int squareNum, int player) {
		var event = GameEvent.builder()
				.eventTime(LocalDateTime.now())
				.eventType(EventType.MOVE)
				.moveSquareNumber(squareNum)
				.playedBy(getPlayer(player))
				.build();
		getLastGame().addEvent(event);
	}

	public void recordMove(int squareNum, int player, int evaluation) {
		var event = GameEvent.builder()
				.eventTime(LocalDateTime.now())
				.eventType(EventType.MOVE)
				.moveSquareNumber(squareNum)
				.evaluation(evaluation)
				.playedBy(getPlayer(player))
				.build();
		getLastGame().addEvent(event);
	}

	public void recordVictory(int player) {
		var event = GameEvent.builder()
				.eventTime(LocalDateTime.now())
				.eventType(EventType.VICTORY)
				.playedBy(getPlayer(player))
				.build();
		getLastGame().addEvent(event);
		getLastGame().setDateFinished(LocalDateTime.now());
	}

	public void recordResign(int player) {
		var event = GameEvent.builder()
				.eventTime(LocalDateTime.now())
				.eventType(EventType.RESIGN)
				.playedBy(getPlayer(player))
				.build();
		getLastGame().addEvent(event);
		getLastGame().setDateFinished(LocalDateTime.now());
	}

	public String exportToJson() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
		return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(gameHistory);
	}

}
