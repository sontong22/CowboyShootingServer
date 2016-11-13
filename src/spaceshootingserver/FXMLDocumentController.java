
package spaceshootingserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

public class FXMLDocumentController implements Initializable {
    
    @FXML
    private TextArea textArea;    
    
    private int playerId = 0;    
    private ObservableList<GameRecord> recordList;              
            
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        recordList = FXCollections.synchronizedObservableList(FXCollections.observableList(new ArrayList<>()));        
        
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
    private ObservableList<GameRecord> recordList;         
    private TextArea textArea;    
    private Player player;
    private int recordId;
    
    public HandleAPlayer(Socket socket,ObservableList<GameRecord> recordList, int playerId, TextArea textArea) {
      this.socket = socket;
      this.recordList = recordList;       
      this.textArea = textArea;      
      player = new Player(playerId);
      recordId = (playerId+1)/2;
      
      // if playerId is an odd number, create a new GameRecord for recordList with index = (i+1) / 2 and put him into it
      if(playerId%2 == 1){
          this.recordList.add(recordId, new GameRecord());
          this.recordList.get(recordId).addPlayer(player);
      // if playerId is an even number, put the player into the GameRord for recordList with index = i / 2
      } else {
          this.recordList.get(recordId).addPlayer(player);
      }
      
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
                  break;
              }
              case GET_START_GAME: {             
                  outputToClient.println(recordList.get(recordId).getState());
                  outputToClient.flush();                  
                  break;
              }
              case SEND_MOVE: {
                  int kindOfMove = Integer.parseInt(inputFromClient.readLine());                                    
                  recordList.get(recordId).addMove(player.getPlayerId(), kindOfMove);                  
                  break;
              }
              case GET_MOVE_COUNT: {                  
                  outputToClient.println(recordList.get(recordId).getMoveCount());
                  outputToClient.flush();
                  break;
              }
              case GET_MOVE: {
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

 