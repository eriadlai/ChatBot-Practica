package ChatVersion2;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class Server {
    private ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer() {
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                System.out.println("A new client has connected...");
                ClientHandler clientHandler = new ClientHandler(socket);
                Thread thread = new Thread(clientHandler);
                thread.start();
                if(ClientHandler.clientHandlers.size()==4){
                    startGame();
                }
            }
        } catch (IOException e) {
            closeServerSocket();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void startGame(){
          while(true){
           try {
                Thread.sleep(5*1000);
                ClientHandler game = new ClientHandler();
                game.broadcastMessage(sendNumbers()+"");
                if(ClientHandler.clientHandlers.size()==1){
                    game.broadcastMessage("==GANASTE EL JUEGO!!");
                    System.out.println("FIN DEL JUEGO");
                    break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                    }
            }          
        }
    public void closeServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public int sendNumbers(){
        Random rand = new Random(); 
        int int_random = rand.nextInt(11,19); 
        return int_random;
    }
    public static void main(String[] args) throws IOException{
        ServerSocket serverSocket = new ServerSocket(1234);
        Server server = new Server(serverSocket);
        server.startServer();
        
    }

}
