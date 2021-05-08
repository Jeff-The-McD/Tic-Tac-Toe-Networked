import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public class Server {

	public static void main(String[] args) throws IOException {
		try (ServerSocket listener = new ServerSocket(51453)) {
			System.out.println("Tic Tac Toe Server is Running...");
			ExecutorService pool = Executors.newFixedThreadPool(20);
			while (true) {
				Game game = new Game();
				pool.execute(game.new Player(listener.accept(), 'X'));
				pool.execute(game.new Player(listener.accept(), 'O'));
			}
		}
	}
}

class Game {

	// Board cells numbered 0-8, top to bottom, left to right; null if empty
	private Player[] board = new Player[9];

	Player currentPlayer;
	Player XPlayer;

	public boolean hasWinner() {
		return (board[0] != null && board[0] == board[1] && board[0] == board[2])
			|| (board[3] != null && board[3] == board[4] && board[3] == board[5])
			|| (board[6] != null && board[6] == board[7] && board[6] == board[8])
			|| (board[0] != null && board[0] == board[3] && board[0] == board[6])
			|| (board[1] != null && board[1] == board[4] && board[1] == board[7])
			|| (board[2] != null && board[2] == board[5] && board[2] == board[8])
			|| (board[0] != null && board[0] == board[4] && board[0] == board[8])
			|| (board[2] != null && board[2] == board[4] && board[2] == board[6]);
	}

	public boolean isBoardFilledUp() {
		return Arrays.stream(board).allMatch(p -> p != null);
	}
	
	public void printBoard() {
		for (int i = 0; i < board.length; i++) {
			if (i % 3 == 0)
				System.out.println();
			if (board[i] == null)
				System.out.print(i+1 + " ");
			else
				System.out.print(board[i].mark + " ");
		}
		System.out.println();
	}

	public synchronized int move(int location, Player player) {
		if (player != currentPlayer) {
			return Msg.OPPONENTS_TURN;
		} else if (board[location-1] == player) {
			return Msg.MOVE_FAILURE_OCCUPIED_BY_YOU|location;
		} else if (board[location-1] == player.opponent) {
			return Msg.MOVE_FAILURE_OCCUPIED_BY_OPPONENT|location;
		}
		board[location-1] = currentPlayer;
		currentPlayer = currentPlayer.opponent;
		printBoard();
		return Msg.MOVE_CONFIRMED|location;
	}

	public synchronized void resetBoard() {
		for (int i = 0; i < board.length; i++) {
			board[i] = null;
		}
		currentPlayer = XPlayer;
		printBoard();
	}

	class Player implements Runnable {
		private final char mark;
		private Player opponent;
		private Socket socket;
		private InputStream in;
		private OutputStream out;

		public Player(Socket socket, char mark) {
			this.socket = socket;
			this.mark = mark;
		}

		@Override
		public void run() {
			try {
				setup();
				processCommands();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					System.out.println("Player " + mark + " quit");
					if (opponent != null && opponent.out != null) {
						opponent.send(Msg.QUIT);
					} /*else {
						System.out.println("Both players quit");
					}*/
					out.close();
					in.close();
					socket.close();
				} catch (IOException e) {
				}
			}
		}

		private void send(int msg) throws IOException {
			out.write((byte) msg);
			out.flush();
		}
		
		private void setup() throws IOException {
			in = socket.getInputStream();
			out = socket.getOutputStream();
			if (mark == 'X') {
				XPlayer = currentPlayer = this;
				send(Msg.YOU_ARE_X);
			} else {
				opponent = currentPlayer;
				opponent.opponent = this;
				send(Msg.YOU_ARE_O);
				opponent.send(Msg.OPPONENT_CONNECTED);
			}
		}

		private void processCommands() throws IOException {
			int msg;
			while ((msg = in.read()) != Msg.QUIT) {
				if (opponent == null) {
					send(Msg.OPPONENT_NOT_CONNECTED);
					continue;
				}
				if ((msg&Msg.COMMAND_MASK) != Msg.MOVE) {
					System.out.println("Unrecognized response from client"
							+ Integer.toHexString(msg));
					continue;
				}
				int responce = move(msg, this);
				if ((responce&Msg.COMMAND_MASK) == Msg.MOVE_CONFIRMED)
					opponent.send(msg); /* if it was a valid move notify the opponent */
				send(responce);
				if (hasWinner()) {
					send(Msg.VICTORY);
					opponent.send(Msg.DEFEAT);
					resetBoard();
				} else if (isBoardFilledUp()) {
					resetBoard();
					send(Msg.TIE);
					opponent.send(Msg.TIE);
				}
			}
		}
	}
}
