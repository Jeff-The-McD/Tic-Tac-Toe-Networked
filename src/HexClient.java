import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


public class HexClient {
	private static String board[][] = {{"1","2","3"},{"4","5","6"},{"7","8","9"}};
	private static Socket socket;
	private static InputStream in;
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

	public static void run(String ip) throws IOException {
		socket = new Socket("localhost", 51453);
		in = socket.getInputStream();
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
				makeMove();
			}
			else if(response.charAt(1) == '8'){
				System.out.println(response.substring(4));
			}

			else if(response.charAt(0) == '1'){
				System.out.println(response.substring(4));
				int index = Character.getNumericValue(response.charAt(response.length()-1));

				board[(index-1)/3][(index-1)%3] = Character.toString(player);
			}
			else if(response.charAt(0) == '2'){
				System.out.println(response.substring(4));
			}
			else if(response.charAt(0) == '3'){
				System.out.println(response.substring(4));
			}
			else if(response.charAt(0) >= '4'){
				System.out.println(response.substring(4));
				break;
			}
		}


		sc.close();
		out.close();
		in.close();
		socket.close();
	}

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
		System.out.println("Please enter which square you want to place your mark. If you would like to quit, enter q.");
		userInput = sc.next();
		if(userInput.equals("q")){
			out.write(Msg.QUIT);
			System.exit(0);
		}
		out.write(Integer.parseInt(userInput, 16));
	}
}

