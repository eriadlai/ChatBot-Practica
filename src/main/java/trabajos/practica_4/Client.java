package trabajos.practica_4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
   static int oResultado;
   static int[] oListadoNumeros = new int[24];
    public static void main(String[] args) {
        final Socket clientSocket;
        final BufferedReader in;
        final PrintWriter out;
        final Scanner sc = new Scanner(System.in);
        
        try {
            clientSocket = new Socket("192.168.56.1", 5000);
            out = new PrintWriter(clientSocket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            
            Thread sender = new Thread(new Runnable(){
                String msg;
                @Override
                public void run() {
                    while(true) {
                        msg = sc.nextLine();
                      try{
                          int oIndex  = Integer.parseInt(msg);
                       
                           if(oIndex >=0 && oIndex <=24){
                                  int oValor = oListadoNumeros[oIndex];
                                  System.out.println("===INGRESE UNA OPERACION===");                                  
                                  System.out.println("SUMA: +");
                                  System.out.println("RESTA: -");
                                  System.out.println("MULTIPLICACION: *");                                
                                  System.out.println("DIVISION: *");
                                  msg = sc.nextLine();
                                    switch(msg){
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
                                    out.close();
                                     clientSocket.close();
                                    }else if(oResultado >200){
                                    System.out.println("=======FIN DEL JUEGO======");
                                       out.close();
                                       clientSocket.close();
                                    }
                                }
                           oListadoNumeros[oIndex] = 0;
                           System.out.println(oResultado);
                      }catch(Exception e){
                          System.out.println("INGRESE NUMEROS VALIDOS");
                              }
                        out.println(msg);
                        out.flush();
                    }
                }
            });
            sender.start();
        
            Thread receiver = new Thread(new Runnable() {
                String msg;
                @Override
                public void run() {
                    try {
                        msg = in.readLine();
                        while (msg != null) {
                            System.out.println("Server: " + msg);
                            msg = in.readLine();
                           try{
                               int oValor = Integer.parseInt(msg);
                               System.out.println(oValor);
                               int oContador = 25;
                               for(int i=0;i>oListadoNumeros.length;i++){
                                   oContador=-1;
                                   if(oListadoNumeros[i] == 0){
                                   oListadoNumeros[i] = oValor;
                                   }
                                   if(oContador == 0){
                                       System.out.println("=====FIN DEL JUEGO======");
                                       out.close();
                                       clientSocket.close();
                                   }
                                   
                               }
                           }catch(Exception e){
                               System.out.println("PROBLEMA EN GUARDAR EL NUMERO");
                           }
                            for(int i =0; i<oListadoNumeros.length;i++){
                                System.out.println("[ "+i+"]:"+ oListadoNumeros[i]);
                            }
                        }
                        System.out.println("Server out of Service");
                        out.close();
                        clientSocket.close();
                    } catch(IOException e) {
                        e.printStackTrace();
                    }
                } 
            });
            receiver.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
