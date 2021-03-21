package cz.sevrjukov.ttt.game.history;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import static cz.sevrjukov.ttt.util.Versions.ENGINE_VERSION;
import static cz.sevrjukov.ttt.util.Versions.PROGRAM_VERSION;

@Data
public class GameHistory {

	private String programVersion = PROGRAM_VERSION;
	private String engineVersion = ENGINE_VERSION;
	private List<Game> gamesList = new ArrayList<>();
}
