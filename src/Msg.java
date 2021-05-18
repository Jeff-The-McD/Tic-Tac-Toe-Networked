public class Msg {
	public static final int LOCATION_MASK = 0x0F;
	public static final int COMMAND_MASK = 0xF0;

	/* the following commands can only be sent from the server to the
	 * client (and not the other way around) unless stated otherwise */
	
	public static final int MOVE = 0x00; /* can be sent by the client */
	public static final int MOVE_CONFIRMED = 0x10;
	public static final int MOVE_FAILURE_OCCUPIED_BY_YOU = 0x20;
	public static final int MOVE_FAILURE_OCCUPIED_BY_OPPONENT = 0x30;
	
	//public static final int YOUR_TURN = 0x50;
	public static final int OPPONENTS_TURN = 0x51;
	
	public static final int YOU_ARE_X = 0x52;
	public static final int YOU_ARE_O = 0x53;
	public static final int VICTORY = 0x54;
	public static final int DEFEAT = 0x55;
	public static final int TIE = 0x56;

	public static final int OPPONENT_CONNECTED = 0x57;
	public static final int OPPONENT_NOT_CONNECTED = 0x58;
	public static final int QUIT = 0x59; /* can be sent by the client */

	public static String serverMsgDescription(int msg) {
		String name = Integer.toHexString(msg) + ": ";
		if (msg >= OPPONENTS_TURN) {
			switch (msg) {
			case OPPONENTS_TURN:
				name += "It is not your turn";
				break;
			case YOU_ARE_X:
				name += "You are X, it is your turn once your opponent connects";
				break;
			case YOU_ARE_O:
				name += "You are O, it is your opponent's turn";
				break;
			case VICTORY:
				name += "You won and the gameboard reset (it is now X's turn)";
				break;
			case DEFEAT:
				name += "You lost and the gameboard reset (it is now X's turn)";
				break;
			case TIE:
				name += "The game has reached a tie and the gameboard reset (it is now X's turn)";
				break;
			case OPPONENT_CONNECTED:
				name += "Your opponent connected";
				break;
			case OPPONENT_NOT_CONNECTED:
				name += "Your opponent is not connected";
				break;
			case QUIT:
				name += "Your opponent has left the game";
				break;
			default:
				name += "Unrecognized message";
			}
		} else {
			int leftHalf = msg & COMMAND_MASK;
			int rightHalf = msg & LOCATION_MASK;

			name = "";
			switch (leftHalf) {
			case MOVE:
				name += "00: Your opponent placed a mark";
				break;
			case MOVE_CONFIRMED:
				name += "10: You placed a mark";
				break;
			case MOVE_FAILURE_OCCUPIED_BY_YOU:
				name += "00: It is still your turn, you already placed a mark";
				break;
			case MOVE_FAILURE_OCCUPIED_BY_OPPONENT:
				name += "00: It is still your turn, your opponent already placed a mark";
				break;
			}
			if (isValidLocation(rightHalf)) {
					name += " at location " + rightHalf;
			} else {
				name += " at the INVALID location " + rightHalf;
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
			return name + "unrecognized message from client";
		if (leftHalf == MOVE && isValidLocation(rightHalf))
			name += " at location " + rightHalf;
		return name;
	}

	
	public static boolean isValidLocation(int msg) {
		return msg > 0 && msg < 10;
	}
}
