package cz.sevrjukov.ttt.engine;

import lombok.Getter;

@Getter
public class VictoryFound extends RuntimeException {

	int player;

	public VictoryFound(int player) {
		super();
		this.player = player;
	}
}
