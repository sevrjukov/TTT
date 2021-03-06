package cz.sevrjukov.ttt.game;

public interface GameEventListener {

	void refreshBoard();

	void resign(int player);

	void announceVictory();

	void announceDefeat();

	void printEvaluationInfo(String info);

	void printGameInfo(String info);

}
