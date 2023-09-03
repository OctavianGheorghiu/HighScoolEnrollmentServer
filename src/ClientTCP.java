import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.SQLOutput;
import java.util.Scanner;

public class ClientTCP {

    public static void main(String[] args){


        try {
            Socket socket = new Socket("127.0.0.1",8080);
            System.out.println("Conexiune realizata cu succes!");

            var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            var out = new PrintWriter(socket.getOutputStream(),true);
            Scanner scanner = new Scanner(System.in);

            while(true){
                System.out.println("Introdu specializarea: ");
                String s = scanner.nextLine();
                out.println(s);
                System.out.println("[RASPUNS SERVER -> numar locuri disponibile]: "+in.readLine());
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
