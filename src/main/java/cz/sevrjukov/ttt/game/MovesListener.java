package cz.sevrjukov.ttt.game;

public interface MovesListener {

	void refreshBoard();

	void resign();

	void announceVictory();
}
