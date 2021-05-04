public class Msg {
	public static final int LOCATION_MASK = 0x0F;
	public static final int COMMAND_MASK = 0xF0;
	public static final int NULL_MSG = 0x00;

	public static final int MOVE = 0x00;
	public static final int VICTORY = 0x10;
	public static final int DEFEAT = 0x20;
	public static final int TIE = 0x30;
	public static final int WRONG_TURN = 0x40;
	public static final int CELL_OCCUPIED = 0x50;
	public static final int YOU_ARE_X = 0x60;
	public static final int YOU_ARE_O = 0x70;
	public static final int OPPONENT_CONNECTED = 0x80;
	public static final int QUIT = 0x90;

	public static String serverMsgDescription(int msg) {
		String name = Integer.toHexString(msg) + ": ";
		int leftHalf = msg & COMMAND_MASK;
		int rightHalf = msg & LOCATION_MASK;
		switch (leftHalf) {
			case MOVE:
				name += "Your opponent placed a mark";
			case VICTORY:
				name += "Player X has won";
				break;
			case DEFEAT:
				name += "You lost";
				break;
			case TIE:
				name += "The game has reached a tie";
				break;
			case WRONG_TURN:
				name += "It is not your turn. Undo the move you made";
				break;
			case CELL_OCCUPIED:
				name += "The location you selected is occupied. Undo the move you made";
				break;
			case YOU_ARE_X:
				name += "You are X";
				break;
			case YOU_ARE_O:
				name += "You are O";
				break;
			case OPPONENT_CONNECTED:
				name += "Your opponent connected";
				break;
			case QUIT:
				name += "Your opponent has left the game";
				break;
			default:
				name += "unrecognized message";
		}
		if (isValidLocation(rightHalf)) {
			switch (leftHalf) {
				case VICTORY:
				case DEFEAT:
				case TIE:
					name += " after your opponent placed a mark at " + rightHalf;
					break;
				case MOVE:
				case WRONG_TURN:
				case CELL_OCCUPIED:
					name += " at location " + rightHalf;
					break;
				default:
					name += " with an erronious location set to " + rightHalf;
			}
		}
		return name;
	}

	public static String clientMsgDescription(int msg) {
		String name = Integer.toHexString(msg) + ": ";
		int leftHalf = msg & COMMAND_MASK;
		int rightHalf = msg & LOCATION_MASK;
		if (leftHalf == MOVE)
			name += "Client placed a mark";
		else if (leftHalf == QUIT)
			name += "Client left the game";
		else
			name += "unrecognized message";
		if (isValidLocation(rightHalf)) {
			if (leftHalf == MOVE)
				name += " at location " + rightHalf;
			else
				name += " with an erronious location set to " + rightHalf;
		}
		return name;
	}

	
	public static boolean isValidLocation(int msg) {
		return msg > 0 && msg < 10;
	}
}
