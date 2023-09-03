import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.Buffer;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;




public class Main {

    public static List<Specializare> getSpecializari(){
        List<Specializare> specializari = new ArrayList<>();

        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:Date/facultate.db");
            Statement statement = connection.createStatement();
            statement.execute("select * from specializari");

            ResultSet rs = statement.getResultSet();

            while(rs.next()){
                specializari.add(new Specializare(
                      rs.getInt(1),
                      rs.getString(2),
                      rs.getInt(3)
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        return specializari;
    }

    public static List<Inscriere> getInscrieri(){
        List<Inscriere> inscrieri = new ArrayList<>();

        try {
            var in = new BufferedReader(new InputStreamReader(new FileInputStream("Date/inscrieri.txt")));
            inscrieri = in.lines().map(Inscriere::new).collect(Collectors.toList());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        return inscrieri;
    }

    public static void main(String[] args) {
        List<Specializare> specializari = getSpecializari();
        List<Inscriere> inscrieri = getInscrieri();

        System.out.println("\n~~~~~~~ 1 ~~~~~~\n");

        System.out.println("Numar total de locuri disponibile la facultate: " +
                specializari.stream().mapToInt(s -> s.getLocuri()).sum());

        System.out.println("\n~~~~~~~ 2 ~~~~~~");
        System.out.println("Lista specializarilor din facultate:\n");

        specializari.stream().forEach(specializare -> {
            int nrInscrieri = inscrieri.stream().filter(inscriere -> inscriere.getCodSpecializare() == specializare.getCod())
                    .collect(Collectors.toList()).size();

            if (specializare.getLocuri() - nrInscrieri > 0) {
                System.out.println("Cod: " + specializare.getCod() + ", specializarea: " + specializare.getDenumire() +
                        ", are un numar de locuri disponibile de: " + (specializare.getLocuri() - nrInscrieri));
            } else {
                System.out.println("Cod: " + specializare.getCod() + ", specializarea: " + specializare.getDenumire() +
                        ", are un numar de locuri disponibile de: 0");
            }
        });

        System.out.println("\n~~~~~~~ 3 ~~~~~~");
        System.out.println("Crearea uunui fisier json ce contine numarul de inscrieri pentru fiecare specializare si media notelor a candidatilor inscrisi.\n");

        try {
            var out = new PrintWriter("inscriere_specializari.json");
            JSONArray array = new JSONArray();

            specializari.stream().forEach(specializare ->{
                JSONObject obj = new JSONObject();

                long numar = inscrieri.stream().filter(i -> i.getCodSpecializare()==specializare.getCod())
                        .collect(Collectors.toList()).size();

                long suma = inscrieri.stream().filter(i -> i.getCodSpecializare()==specializare.getCod())
                        .collect(Collectors.toList()).stream().mapToInt(insc -> (int)insc.getNota()).sum();

                obj.put("danumire",specializare.getDenumire());
                obj.put("cod_specializare",specializare.getCod());
                obj.put("numar_inscrieri",numar);
                obj.put("medie",suma/numar);

                array.put(obj);

            });
          out.println(array.toString(1));
          out.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Fisierul \"inscrieri_specializari.json\" a fost creat cu succes");

        System.out.println("\n~~~~~~~ 4 ~~~~~~");
        System.out.println("Crearea server si client TCP/IP pentru informarea in legatura cu locurile disponibile\n");

        final int port = 8080;

        try {
            ServerSocket serverSocket = new ServerSocket(port);

            System.out.println("Astept conexiune client");

            while(true){

                Socket socket = serverSocket.accept();
                System.out.println("Client conectat!");

                new Thread(()->{
                    procesareCerere(socket, specializari, inscrieri);
                }).start();


            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void procesareCerere(Socket socket, List<Specializare> specializari, List<Inscriere> inscrieri){

        while(true) {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                String s = in.readLine();

                Specializare specializare = specializari.stream().filter(spec -> spec.getDenumire().equalsIgnoreCase(s)).collect(Collectors.toList())
                        .stream().findFirst().get();

                int nrInscrieri = inscrieri.stream().filter(inscriere -> inscriere.getCodSpecializare() == specializare.getCod())
                        .collect(Collectors.toList()).size();

                out.println(specializare.getLocuri() - nrInscrieri);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}