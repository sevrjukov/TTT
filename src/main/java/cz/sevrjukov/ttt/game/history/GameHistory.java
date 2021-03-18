package cz.sevrjukov.ttt.game.history;

import cz.sevrjukov.ttt.board.Board;
import cz.sevrjukov.ttt.game.history.GameEvent.EventType;
import cz.sevrjukov.ttt.game.history.GameEvent.Player;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GameHistory {

	private List<Game> gamesList = new ArrayList<>();

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
				.id(UUID.randomUUID().toString())
				.build();
		gamesList.add(newGame);
	}

	public Game getLastGame() {
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

	public void recordEvaluation(int evaluation) {
		var event = GameEvent.builder()
				.eventTime(LocalDateTime.now())
				.eventType(EventType.EVALUATION)
				.evaluation(evaluation)
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
				.eventType(EventType.VICTORY)
				.playedBy(getPlayer(player))
				.build();
		getLastGame().addEvent(event);
		getLastGame().setDateFinished(LocalDateTime.now());
	}

}
