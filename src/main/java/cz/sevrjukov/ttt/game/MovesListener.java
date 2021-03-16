package cz.sevrjukov.ttt.game;

public interface MovesListener {

	void displayMove(int sq, int side);

	void resign();

	void announceVictory();
}
