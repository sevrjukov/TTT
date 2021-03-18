package cz.sevrjukov.ttt.game.history;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class GameEvent {

	private EventType eventType;

	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime eventTime;

	private Player playedBy;
	private int moveSquareNumber;
	private int evaluation;

	public enum EventType {
		MOVE,
		RESIGN,
		VICTORY
	}

	public enum Player {
		HUMAN,
		COMPUTER
	}


}
