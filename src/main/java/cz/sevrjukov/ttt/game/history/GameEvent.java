package cz.sevrjukov.ttt.game.history;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class GameEvent {

	private EventType eventType;
	private LocalDateTime eventTime;
	private Player playedBy;
	private int moveSquareNumber;
	private int evaluation;

	public enum EventType {
		MOVE,
		EVALUATION,
		RESIGN,
		VICTORY
	}

	public enum Player {
		HUMAN,
		COMPUTER
	}


}
