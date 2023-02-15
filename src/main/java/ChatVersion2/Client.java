package ChatVersion2;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    static int oResultado;
    static int[] oListadoNumeros = new int[24];
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;

    public Client (Socket socket, String username) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void sendMessage() {
        try {
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()) {
                String messageToSend = scanner.nextLine();
                try{
                    int oIndex = Integer.parseInt(messageToSend);
                    if(oIndex>= 0 && oIndex <= 24){
                        int oValor = oListadoNumeros[oIndex];
                        System.out.println("===INGRESE UNA OPERACION===");                                  
                        System.out.println("SUMA: +");
                        System.out.println("RESTA: -");
                        System.out.println("MULTIPLICACION: *");                                
                        System.out.println("DIVISION: *");
                        messageToSend = scanner.nextLine();
                        switch(messageToSend){
                            case "+":
                                oResultado = oResultado + oValor;
                                break;
                            case "-":
                                oResultado = oResultado - oValor;
                                break;
                            case "*":
                                oResultado = oResultado * oValor;
                                break;
                            case "/":
                                oResultado = oResultado/oValor;
                                break;
                            default:
                                System.out.println("FAVOR DE INGRESAR UNA OPCION VALIDA");
                                break;   
                        }
                        if(oResultado == 100){
                            System.out.println("EL JUGADOR HA GANADO!!");
                        bufferedWriter.write("GANADOR-PATATA");
                        bufferedWriter.newLine();
                        bufferedWriter.flush();
                            }else if(oResultado >200){
                            System.out.println("=======FIN DEL JUEGO======");
                            closeEverything(socket, bufferedReader, bufferedWriter);
                            }
                    }
                    oListadoNumeros[oIndex] = 0;
                }catch(Exception e){
                    System.out.println(e);
                }
                
                bufferedWriter.write(username + ": " + messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void listenForMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String messageFromChat;

                while (socket.isConnected()) {
                    try {
                        messageFromChat = bufferedReader.readLine();
                        int oValor = Integer.parseInt(messageFromChat);
                        int oContador = 25;
                        for(int i=0;i>oListadoNumeros.length;i++){
                            oContador=-1;
                            if(oListadoNumeros[i] == 0){
                            oListadoNumeros[i] = oValor;
                            }
                            if(oContador == 0){
                            System.out.println("=====FIN DEL JUEGO======");
                            closeEverything(socket, bufferedReader, bufferedWriter);
                            }       
                        }
                        System.out.println(messageFromChat);
                    } catch (IOException e) {
                        closeEverything(socket, bufferedReader, bufferedWriter);
                    }
                }
            }
        }).start();
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException{
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your username for the chat: ");
        String username = scanner.nextLine();
        System.out.println("Welcome: " + username);
        Socket socket = new Socket("localhost", 1234);
        Client client = new Client(socket, username);
        client.listenForMessage();
        client.sendMessage();
    }
}
