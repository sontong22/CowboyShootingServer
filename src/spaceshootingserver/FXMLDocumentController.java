
package spaceshootingserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

public class FXMLDocumentController implements Initializable  {
    
    @FXML
    private TextArea textArea;    
    
    private int playerId = 0;    
    private List<GameRecord> recordList;              
            
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        recordList = Collections.synchronizedList(new ArrayList<>()); 
        
        // A thread that handles the connection with users
        new Thread(() -> {
            try {
                // Create a server socket
                ServerSocket serverSocket = new ServerSocket(8000);

                while (true) {
                    // Listen for a new connection request
                    Socket socket = serverSocket.accept();
                    
                    // Increment playerId
                    playerId++;
                    
                    Platform.runLater(() -> {
                        // Display the client number
                        textArea.appendText("Starting thread for client " + playerId
                                + " at " + new Date() + '\n');
                    });

                    // Create and start a new thread for the connection                    
                    new Thread(new HandleAPlayer(socket, recordList, playerId, textArea)).start();                    
                }
            } catch (IOException ex) {
                System.err.println(ex);
            }
        }).start();                
    }
}

class HandleAPlayer implements Runnable, interaction.InteractionConstants {
    private Socket socket; // A connected socket    
    private List<GameRecord> recordList;         
    private TextArea textArea;    
    private Player player;
    private int recordId;
    
    public HandleAPlayer(Socket socket, List<GameRecord> recordList, int playerId, TextArea textArea) {
      this.socket = socket;
      this.recordList = recordList;       
      this.textArea = textArea;      
      player = new Player(playerId);      
    }    
    
    public void run() {
      try {
        // Create reading and writing streams
        BufferedReader inputFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter outputToClient = new PrintWriter(socket.getOutputStream());

        // Continuously serve the client
        while (true) {
          // Receive request code from the client
          int request = Integer.parseInt(inputFromClient.readLine());
          // Process request
          switch(request) {       
              case SEND_NAME: {
                  player.setPlayerName(inputFromClient.readLine());                  
                  outputToClient.println(player.getPlayerId());
                  outputToClient.flush();            
                  
                  // if playerId is an odd number, create a new GameRecord and put him into it
                  if (player.getPlayerId() % 2 == 1) {
                      recordId = player.getPlayerId() / 2;
                      recordList.add(new GameRecord());
                  // if playerId is an even number, put the player into the GameRord for recordList with index = i / 2 - 1
                  } else {
                      recordId = player.getPlayerId() / 2 - 1;
                  }

                  recordList.get(recordId).addPlayer(player);
                  
                  break;
              }
              case GET_OPPONENT_ID: {
                  int n = recordList.size();
                  int opponentId = -1;
                  
                  for(int i = 0; i < n; i++){
                      opponentId = recordList.get(recordId).getPlayerId(i);
                      if(opponentId != player.getPlayerId())
                          outputToClient.println(opponentId);
                  }
                  
                  outputToClient.flush();
                  break;
              }
              case GET_START_GAME: {             
                  outputToClient.println(recordList.get(recordId).getState());
                  outputToClient.flush();                  
                  break;
              }
              
              
              case SEND_COWBOY_MOVE: {                                                  
                  int x = Integer.parseInt(inputFromClient.readLine());                                    
                  int y = Integer.parseInt(inputFromClient.readLine());                                    
                  Movement move = new Movement(player.getPlayerId(), x, y);
                  recordList.get(recordId).addCowboyMove(move);                  
                  break;
              }
              case GET_COWBOY_MOVE_COUNT: {                  
                  outputToClient.println(recordList.get(recordId).getCowboyMoveCount());
                  outputToClient.flush();
                  break;
              }
              case GET_COWBOY_MOVE: {
                  int n = Integer.parseInt(inputFromClient.readLine());
                  outputToClient.println(recordList.get(recordId).getCowboyMove(n).playerId);
                  outputToClient.println(recordList.get(recordId).getCowboyMove(n).x);
                  outputToClient.println(recordList.get(recordId).getCowboyMove(n).y);
                  outputToClient.flush();
                  break;
              }
              case SEND_MISSILE_MOVE: {                  
                  int x = Integer.parseInt(inputFromClient.readLine());                                    
                  int y = Integer.parseInt(inputFromClient.readLine());                                    
                  Movement move = new Movement(player.getPlayerId(), x, y);
                  recordList.get(recordId).addMissileMove(move); 
                  break;
              }
              case GET_MISSILE_MOVE_COUNT: {
                  outputToClient.println(recordList.get(recordId).getMissileMoveCount());
                  outputToClient.flush();
                  break;
              }
              case GET_MISSILE_MOVE: {                  
                  int n = Integer.parseInt(inputFromClient.readLine());
                  outputToClient.println(recordList.get(recordId).getMissileMove(n).playerId);
                  outputToClient.println(recordList.get(recordId).getMissileMove(n).x);
                  outputToClient.println(recordList.get(recordId).getMissileMove(n).y);
                  outputToClient.flush();
                  break;
              }
          }
        }
      }
      catch(IOException ex) {
          Platform.runLater(()->textArea.appendText("Exception in client thread: "+ex.toString()+"\n"));
      }
    }
}

 