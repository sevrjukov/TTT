package cz.sevrjukov.ttt.game.history;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GameHistory {

	private List<Game> gamesList = new ArrayList<>();
}
