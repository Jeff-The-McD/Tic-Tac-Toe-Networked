import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;


public class HexClient {

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
		Socket socket = new Socket(ip, 51453);
		InputStream in = socket.getInputStream();
		OutputStream out = socket.getOutputStream();
		Scanner sc = new Scanner(System.in);

		System.out.println("f   = fetch responce from server");
		System.out.println("q   = quit");
		System.out.println("s## = send server the hex \"0x##\"");
		System.out.println("see Msg.java for a list of hex codes to send to the server");
		System.out.print("> ");
		while (sc.hasNext()) {
			String cmd = sc.next();
			if (cmd.equals("q")) {
				out.write(Msg.QUIT);
				break;
			}
			if (cmd.equals("f")) { /* fetch */
				if (in.available() == 0) {
					System.out.println("nothing available");
				} else {
					do {
						int msg = in.read();
						System.out.println(Msg.serverMsgDescription(msg));
					} while (in.available() > 0);
				}
			} else if (cmd.startsWith("s")) { /* send */
				try {
					out.write(Integer.parseInt(cmd.substring(1), 16));
				} catch (Exception e) {
					System.out.println("malformed s command");
				}
			}
			System.out.print("> ");
		}

		sc.close();
		out.close();
		in.close();
		socket.close();
	}

}

