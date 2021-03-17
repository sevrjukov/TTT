package cz.sevrjukov.ttt.game;

public interface GameEventListener {

	void refreshBoard();

	void resign();

	void announceVictory();

	void printInfo(String info);
}
