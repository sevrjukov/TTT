package cz.sevrjukov.ttt.gui;

import cz.sevrjukov.ttt.board.Move;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BoardModel {

	List<Move> movesList = new ArrayList<>();
}
