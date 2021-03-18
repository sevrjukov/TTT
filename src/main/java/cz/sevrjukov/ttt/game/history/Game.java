package cz.sevrjukov.ttt.game.history;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class Game {

	private String id;
	private LocalDateTime dateStarted;
	private LocalDateTime dateFinished;
	private List<GameEvent> events;

	public void addEvent(GameEvent event) {
		events.add(event);
	}

	public enum WinningSide {
		HUMAN,
		COMPUTER
	}

}
