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
			}
			run(args[0]);
		} catch (IOException e) {
			System.err.println("Failled to connect to " + args[0] + " at port:" + 51453);
		}
	}

	public static void run(String ip) throws IOException {
		Socket socket = new Socket(ip, 51453);
		InputStream in = socket.getInputStream();
		OutputStream out = socket.getOutputStream();
		Scanner sc = new Scanner(System.in);

		System.out.println("f   = fetch responce from server");
		System.out.println("s## = send server hex \"0x##\"");
		System.out.print("> ");
		while (sc.hasNext()) {
			String cmd = sc.next();
			if (cmd.equals("f")) { /* fetch */
				if (in.available() > 0) {
					int msg = in.read();
					System.out.println(Msg.serverMsgDescription(msg));
				} else {
					System.out.println("nothing available");
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

