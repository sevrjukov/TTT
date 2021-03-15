package cz.sevrjukov.ttt.engine;

import cz.sevrjukov.ttt.board.Board;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoardSequences {

	public static final Map<Integer, List<int[]>> ASSOCIATIVE_INDEXES;
	public static int[][] HORIZONTALS = {
			{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18},
			{19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37},
			{38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56},
			{57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75},
			{76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94},
			{95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113},
			{114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127, 128, 129, 130, 131, 132},
			{133, 134, 135, 136, 137, 138, 139, 140, 141, 142, 143, 144, 145, 146, 147, 148, 149, 150, 151},
			{152, 153, 154, 155, 156, 157, 158, 159, 160, 161, 162, 163, 164, 165, 166, 167, 168, 169, 170},
			{171, 172, 173, 174, 175, 176, 177, 178, 179, 180, 181, 182, 183, 184, 185, 186, 187, 188, 189},
			{190, 191, 192, 193, 194, 195, 196, 197, 198, 199, 200, 201, 202, 203, 204, 205, 206, 207, 208},
			{209, 210, 211, 212, 213, 214, 215, 216, 217, 218, 219, 220, 221, 222, 223, 224, 225, 226, 227},
			{228, 229, 230, 231, 232, 233, 234, 235, 236, 237, 238, 239, 240, 241, 242, 243, 244, 245, 246},
			{247, 248, 249, 250, 251, 252, 253, 254, 255, 256, 257, 258, 259, 260, 261, 262, 263, 264, 265},
			{266, 267, 268, 269, 270, 271, 272, 273, 274, 275, 276, 277, 278, 279, 280, 281, 282, 283, 284},
			{285, 286, 287, 288, 289, 290, 291, 292, 293, 294, 295, 296, 297, 298, 299, 300, 301, 302, 303},
			{304, 305, 306, 307, 308, 309, 310, 311, 312, 313, 314, 315, 316, 317, 318, 319, 320, 321, 322},
			{323, 324, 325, 326, 327, 328, 329, 330, 331, 332, 333, 334, 335, 336, 337, 338, 339, 340, 341},
			{342, 343, 344, 345, 346, 347, 348, 349, 350, 351, 352, 353, 354, 355, 356, 357, 358, 359, 360}
	};
	public static int[][] VERTICALS = {
			{0, 19, 38, 57, 76, 95, 114, 133, 152, 171, 190, 209, 228, 247, 266, 285, 304, 323, 342},
			{1, 20, 39, 58, 77, 96, 115, 134, 153, 172, 191, 210, 229, 248, 267, 286, 305, 324, 343},
			{2, 21, 40, 59, 78, 97, 116, 135, 154, 173, 192, 211, 230, 249, 268, 287, 306, 325, 344},
			{3, 22, 41, 60, 79, 98, 117, 136, 155, 174, 193, 212, 231, 250, 269, 288, 307, 326, 345},
			{4, 23, 42, 61, 80, 99, 118, 137, 156, 175, 194, 213, 232, 251, 270, 289, 308, 327, 346},
			{5, 24, 43, 62, 81, 100, 119, 138, 157, 176, 195, 214, 233, 252, 271, 290, 309, 328, 347},
			{6, 25, 44, 63, 82, 101, 120, 139, 158, 177, 196, 215, 234, 253, 272, 291, 310, 329, 348},
			{7, 26, 45, 64, 83, 102, 121, 140, 159, 178, 197, 216, 235, 254, 273, 292, 311, 330, 349},
			{8, 27, 46, 65, 84, 103, 122, 141, 160, 179, 198, 217, 236, 255, 274, 293, 312, 331, 350},
			{9, 28, 47, 66, 85, 104, 123, 142, 161, 180, 199, 218, 237, 256, 275, 294, 313, 332, 351},
			{10, 29, 48, 67, 86, 105, 124, 143, 162, 181, 200, 219, 238, 257, 276, 295, 314, 333, 352},
			{11, 30, 49, 68, 87, 106, 125, 144, 163, 182, 201, 220, 239, 258, 277, 296, 315, 334, 353},
			{12, 31, 50, 69, 88, 107, 126, 145, 164, 183, 202, 221, 240, 259, 278, 297, 316, 335, 354},
			{13, 32, 51, 70, 89, 108, 127, 146, 165, 184, 203, 222, 241, 260, 279, 298, 317, 336, 355},
			{14, 33, 52, 71, 90, 109, 128, 147, 166, 185, 204, 223, 242, 261, 280, 299, 318, 337, 356},
			{15, 34, 53, 72, 91, 110, 129, 148, 167, 186, 205, 224, 243, 262, 281, 300, 319, 338, 357},
			{16, 35, 54, 73, 92, 111, 130, 149, 168, 187, 206, 225, 244, 263, 282, 301, 320, 339, 358},
			{17, 36, 55, 74, 93, 112, 131, 150, 169, 188, 207, 226, 245, 264, 283, 302, 321, 340, 359},
			{18, 37, 56, 75, 94, 113, 132, 151, 170, 189, 208, 227, 246, 265, 284, 303, 322, 341, 360}
	};
	public static int[][] DIAGONALS_TOP_RIGHT = {
			{76, 58, 40, 22, 4},
			{95, 77, 59, 41, 23, 5},
			{114, 96, 78, 60, 42, 24, 6},
			{133, 115, 97, 79, 61, 43, 25, 7},
			{152, 134, 116, 98, 80, 62, 44, 26, 8},
			{171, 153, 135, 117, 99, 81, 63, 45, 27, 9},
			{190, 172, 154, 136, 118, 100, 82, 64, 46, 28, 10},
			{209, 191, 173, 155, 137, 119, 101, 83, 65, 47, 29, 11},
			{228, 210, 192, 174, 156, 138, 120, 102, 84, 66, 48, 30, 12},
			{247, 229, 211, 193, 175, 157, 139, 121, 103, 85, 67, 49, 31, 13},
			{266, 248, 230, 212, 194, 176, 158, 140, 122, 104, 86, 68, 50, 32, 14},
			{285, 267, 249, 231, 213, 195, 177, 159, 141, 123, 105, 87, 69, 51, 33, 15},
			{304, 286, 268, 250, 232, 214, 196, 178, 160, 142, 124, 106, 88, 70, 52, 34, 16},
			{323, 305, 287, 269, 251, 233, 215, 197, 179, 161, 143, 125, 107, 89, 71, 53, 35, 17},
			{342, 324, 306, 288, 270, 252, 234, 216, 198, 180, 162, 144, 126, 108, 90, 72, 54, 36, 18},
			{343, 325, 307, 289, 271, 253, 235, 217, 199, 181, 163, 145, 127, 109, 91, 73, 55, 37},
			{344, 326, 308, 290, 272, 254, 236, 218, 200, 182, 164, 146, 128, 110, 92, 74, 56},
			{345, 327, 309, 291, 273, 255, 237, 219, 201, 183, 165, 147, 129, 111, 93, 75},
			{346, 328, 310, 292, 274, 256, 238, 220, 202, 184, 166, 148, 130, 112, 94},
			{347, 329, 311, 293, 275, 257, 239, 221, 203, 185, 167, 149, 131, 113},
			{348, 330, 312, 294, 276, 258, 240, 222, 204, 186, 168, 150, 132},
			{349, 331, 313, 295, 277, 259, 241, 223, 205, 187, 169, 151},
			{350, 332, 314, 296, 278, 260, 242, 224, 206, 188, 170},
			{351, 333, 315, 297, 279, 261, 243, 225, 207, 189},
			{352, 334, 316, 298, 280, 262, 244, 226, 208},
			{353, 335, 317, 299, 281, 263, 245, 227},
			{354, 336, 318, 300, 282, 264, 246},
			{355, 337, 319, 301, 283, 265},
			{356, 338, 320, 302, 284}
	};
	public static int[][] DIAGONALS_TOP_LEFT = {
			{266, 286, 306, 326, 346},
			{247, 267, 287, 307, 327, 347},
			{228, 248, 268, 288, 308, 328, 348},
			{209, 229, 249, 269, 289, 309, 329, 349},
			{190, 210, 230, 250, 270, 290, 310, 330, 350},
			{171, 191, 211, 231, 251, 271, 291, 311, 331, 351},
			{152, 172, 192, 212, 232, 252, 272, 292, 312, 332, 352},
			{133, 153, 173, 193, 213, 233, 253, 273, 293, 313, 333, 353},
			{114, 134, 154, 174, 194, 214, 234, 254, 274, 294, 314, 334, 354},
			{95, 115, 135, 155, 175, 195, 215, 235, 255, 275, 295, 315, 335, 355},
			{76, 96, 116, 136, 156, 176, 196, 216, 236, 256, 276, 296, 316, 336, 356},
			{57, 77, 97, 117, 137, 157, 177, 197, 217, 237, 257, 277, 297, 317, 337, 357},
			{38, 58, 78, 98, 118, 138, 158, 178, 198, 218, 238, 258, 278, 298, 318, 338, 358},
			{19, 39, 59, 79, 99, 119, 139, 159, 179, 199, 219, 239, 259, 279, 299, 319, 339, 359},
			{0, 20, 40, 60, 80, 100, 120, 140, 160, 180, 200, 220, 240, 260, 280, 300, 320, 340, 360},
			{1, 21, 41, 61, 81, 101, 121, 141, 161, 181, 201, 221, 241, 261, 281, 301, 321, 341},
			{2, 22, 42, 62, 82, 102, 122, 142, 162, 182, 202, 222, 242, 262, 282, 302, 322},
			{3, 23, 43, 63, 83, 103, 123, 143, 163, 183, 203, 223, 243, 263, 283, 303},
			{4, 24, 44, 64, 84, 104, 124, 144, 164, 184, 204, 224, 244, 264, 284},
			{5, 25, 45, 65, 85, 105, 125, 145, 165, 185, 205, 225, 245, 265},
			{6, 26, 46, 66, 86, 106, 126, 146, 166, 186, 206, 226, 246},
			{7, 27, 47, 67, 87, 107, 127, 147, 167, 187, 207, 227},
			{8, 28, 48, 68, 88, 108, 128, 148, 168, 188, 208},
			{9, 29, 49, 69, 89, 109, 129, 149, 169, 189},
			{10, 30, 50, 70, 90, 110, 130, 150, 170},
			{11, 31, 51, 71, 91, 111, 131, 151},
			{12, 32, 52, 72, 92, 112, 132},
			{13, 33, 53, 73, 93, 113},
			{14, 34, 54, 74, 94}
	};

	static {

		final List<int[]> allSequences = new ArrayList<>();
		Collections.addAll(allSequences, VERTICALS);
		Collections.addAll(allSequences, HORIZONTALS);
		Collections.addAll(allSequences, DIAGONALS_TOP_LEFT);
		Collections.addAll(allSequences, DIAGONALS_TOP_RIGHT);


		/* Calculate associative index, meaning that
		 * each board square is contained in one or many sequences (vertical, horizontal, diagonal).
		 * We can use that to evaluate only sequences which are "activated" - non empty.
		 * Thus we can eliminate evaluating empty sequences and speed up position evaluation.
		 */
		ASSOCIATIVE_INDEXES = new HashMap<>();
		for (int squareNum = 0; squareNum < Board.SIZE; squareNum++) {

			final List<int[]> sequencesForThisSquare = new ArrayList<>();
			ASSOCIATIVE_INDEXES.put(squareNum, sequencesForThisSquare);

			for (int[] sequence : allSequences) {
				if (arrayContains(squareNum, sequence)) {
					sequencesForThisSquare.add(sequence);
				}
			}
		}
	}

	private static boolean arrayContains(int num, int[] arr) {
		for (int i : arr) {
			if (i == num) {
				return true;
			}
		}
		return false;
	}

//	public static void main(String[] args) {
//			ASSOCIATIVE_INDEXES.entrySet().forEach(
//					entry -> {
//						System.out.println(entry.getKey() + "->" + entry.getValue().size());
//						entry.getValue().forEach(
//								line -> {
//									printArray(line);
//								}
//						);
//					}
//			);
//	}
//
//	private static void printArray(int [] arr) {
//		System.out.print("[");
//		for (int i: arr) {
//			System.out.print(i + " ");
//		}
//		System.out.println("]");
//	}

}
