import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


public class HexClient {
	private static String[][] board = {{"1","2","3"},{"4","5","6"},{"7","8","9"}};
	private static OutputStream out;
	private static Scanner sc;
	private static char player;
	private static char opponent;

	public static void main(String[] args) {
		try {
			if (args.length != 1) {
				run("localhost");
			} else {
				run(args[0]);
			}
		} catch (IOException e) {
			if (args.length > 0)
				System.err.println("Failled to connect to " + args[0] + " at port: 51453");
			else
				System.err.println("Failled to connect to localhost at port: 51453");
		}
	}
	/* establishes the socket connection to the server and sends the moves accordingly
	 * @param: ip the address to which to connect the socket to
	 * @return: void
	 * */

	public static void run(String ip) throws IOException {
		Socket socket = new Socket("localhost", 51453);
		InputStream in = socket.getInputStream();
		out = socket.getOutputStream();
		sc = new Scanner(System.in);
		int msg = 0;
		System.out.println("Welcome to Tic Tac Toe!!" +
				"\nFollow the instructions.");

		while(true){
			msg = in.read();

			String response = Msg.serverMsgDescription(msg);
			if(response.charAt(0) == '5' && response.charAt(1) == '0'){
				System.out.println(response.substring(4));
			}
			else if(response.charAt(1) == '1'){
				System.out.println(response.substring(4));
			}
			else if(response.charAt(1) == '2'){
				player = 'X';
				opponent = 'O';
				System.out.println(response.substring(4));
			}
			else if(response.charAt(1) == '3'){
				player = 'O';
				opponent = 'X';
				System.out.println(response.substring(4));
			}
			else if(response.charAt(1) == '7'){
				System.out.println(response.substring(4));
				makeMove();
			}
			else if(response.charAt(0) == '0'){
				System.out.println(response.substring(4));
				int index = Character.getNumericValue(response.charAt(response.length()-1));

				board[(index-1)/3][(index-1)%3] = Character.toString(opponent);
				if(hasWinner())
					continue;
				makeMove();
			}
			else if(response.charAt(1) == '8'){
				System.out.println(response.substring(4));
			}

			else if(response.charAt(0) == '1'){
				System.out.println(response.substring(4));
				int index = Character.getNumericValue(response.charAt(response.length()-1));

				board[(index-1)/3][(index-1)%3] = Character.toString(player);
				System.out.println("Please wait for your opponent to finish their turn.\n\n");
			}
			else if(response.charAt(0) == '2'){
				System.out.println(response.substring(4));
			}
			else if(response.charAt(0) == '3'){
				System.out.println(response.substring(4));
			}
			else if(response.charAt(0) >= '4'){
				System.out.println(response.substring(4));
				System.out.println("Would you like to play again? (y/n)");
				String userInput = sc.next();
				if(userInput.equals("y")) {
					resetBoard();
					if(player=='X')
						makeMove();
				}
				else {
					out.write(Msg.QUIT);
					break;
				}
			}
		}

		out.flush();
		sc.close();
		out.close();
		in.close();
		socket.close();
	}
	
	/* the helper function that determines what moves are and aren't legal
	 * @param: none
	 * @return: void
	 * */

	public static void makeMove() throws IOException {
		String userInput;
		System.out.println("It is your turn to make a move");
		for(int i=0; i<3; i++)
			for(int j=0;j<3; j++){
				if(j!=2)
					System.out.print(board[i][j] + "|");
				else
					System.out.println(board[i][j]);
			}
		System.out.println("Please enter which square you would like to place your mark at.");
		userInput = sc.next();
		out.write(Integer.parseInt(userInput, 16));
	}
	
	/* helps clear the board
	 * @param: none
	 * @return: void
	 * */    
	public static void resetBoard(){
		for(int i=0; i<3; i++)
			for(int j=0; j<3; j++)
				board[i][j] = Integer.toString((j+1)+(3*i));
	}
	
    
	
	/* determines the winner with the typical rules of a tic-tac-toe game.
	 * @param: none
	 * @return: none
	 * */
	public static boolean hasWinner() {
		return (board[0][0] != null && board[0][0].equals(board[0][1]) && board[0][0].equals(board[0][2]))
				|| (board[1][0] != null && board[1][0].equals(board[1][1]) && board[1][0].equals(board[1][2]))
				|| (board[2][0] != null && board[2][0].equals(board[2][1]) && board[2][0].equals(board[2][2]))
				|| (board[0][0] != null && board[0][0].equals(board[1][0]) && board[0][0].equals(board[2][0]))
				|| (board[0][1] != null && board[0][1].equals(board[1][1]) && board[0][1].equals(board[2][1]))
				|| (board[0][2] != null && board[0][2].equals(board[1][2]) && board[0][2].equals(board[2][2]))
				|| (board[0][0] != null && board[0][0].equals(board[1][1]) && board[0][0].equals(board[2][2]))
				|| (board[0][2] != null && board[0][2].equals(board[1][1]) && board[0][2].equals(board[2][0]));
	}
}

