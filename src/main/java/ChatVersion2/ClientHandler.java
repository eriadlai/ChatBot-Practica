package ChatVersion2;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;

   
    public ClientHandler(Socket socket) throws Exception {
        if (clientHandlers.size() == 4) {
            throw new Exception("No more clients allowed");
        }
       
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUsername = bufferedReader.readLine();
            clientHandlers.add(this);
            broadcastMessage("Server: " + clientUsername + " has entered the chat.");
            
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
         
    }
   public ClientHandler(){
   }
    @Override
    public void run() {

        String messageFromClient;

        while (socket.isConnected()) {
            try {
                messageFromClient = bufferedReader.readLine();
                if(messageFromClient == null){
                    closeEverything(socket, bufferedReader, bufferedWriter);
                    break;
                }else if(messageFromClient.equals("GANADOR-PATATA")){
                    broadcastMessage("Server: " + clientUsername + "ha ganado el juego!");
                    for (ClientHandler clientHandler :
                            clientHandlers) {
                        closeEverything(clientHandler.socket, clientHandler.bufferedReader, clientHandler.bufferedWriter);
                    }
                }
                
                broadcastMessage(messageFromClient);
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            } 
        }
    }
    public void broadcastMessage(String messageToSend) {
        for (ClientHandler clientHandler :
                clientHandlers) {
            try {
                if (clientHandler != this|| clientHandler != null) {
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }
    public void removeClientHandler () {
        clientHandlers.remove(this);
        broadcastMessage("Server: " + clientUsername + " lost the challenge");
    } 
    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClientHandler();
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
}
