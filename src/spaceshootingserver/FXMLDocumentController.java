
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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

public class FXMLDocumentController implements Initializable {
    
    @FXML
    private ListView list;
    @FXML
    private TextArea textArea;    
    
    private int clientNo = 0;    
    private ObservableList<GameRoom> roomList;          
            
    @FXML
    private void removeRoom(ActionEvent event) {
        GameRoom chosenRoom = (GameRoom) list.getSelectionModel().getSelectedItem();
        chosenRoom.waitForNoClients();
        roomList.remove(chosenRoom);

    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        roomList = FXCollections.synchronizedObservableList(FXCollections.observableList(new ArrayList<>()));

        list.setItems(roomList);
        
        // A thread that handles the connection with users
        new Thread(() -> {
            try {
                // Create a server socket
                ServerSocket serverSocket = new ServerSocket(8000);

                while (true) {
                    // Listen for a new connection request
                    Socket socket = serverSocket.accept();
                    // Increment clientNo
                    clientNo++;
                    Platform.runLater(() -> {
                        // Display the client number
                        textArea.appendText("Starting thread for client " + clientNo
                                + " at " + new Date() + '\n');
                    });

                    // Create and start a new thread for the connection                    
                    new Thread(new HandleAPlayer(socket, roomList, textArea)).start();                    
                }
            } catch (IOException ex) {
                System.err.println(ex);
            }
        }).start();                
    }
}

class HandleAPlayer implements Runnable, interaction.InteractionConstants {
    private Socket socket; // A connected socket    
    private ObservableList<GameRoom> roomList; // Reference to shared list of transcript         
    private TextArea textArea;
    private int roomId;    
    private String playerName;
    // This is a 2-player game so the creator of the room will have id: 1
    // and the one who joins the room will have id: 2
    private int playerId;    
    
    
    public HandleAPlayer(Socket socket,ObservableList<GameRoom> roomList, TextArea textArea) {
      this.socket = socket;
      this.roomList = roomList;            
      this.textArea = textArea;
      roomId = -1;
      playerId = -1;
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
                  playerName = inputFromClient.readLine();
                  break;
              }
              case GET_USER_ID: {
                  break;
              }
              case SEND_START_GAME: {
                  roomList.get(roomId).startGame();
                  break;
              }
              case GET_START_GAME: {                                  
                  if (roomId < roomList.size()) {
                      outputToClient.println(roomList.get(roomId).getGameState());
                      outputToClient.flush();
                  } else{                      
                      outputToClient.println(0);
                      outputToClient.flush();
                  }
                  break;
              }
              case SEND_MOVE: {
                  int kindOfMove = Integer.parseInt(inputFromClient.readLine());                                    
                  roomList.get(roomId).addMove(playerId, kindOfMove);                  
                  break;
              }
              case GET_MOVE_COUNT: {
                  if (roomId < roomList.size()) {
                      outputToClient.println(roomList.get(roomId).getMoveCount());
                      outputToClient.flush();
                  } else{
                      outputToClient.println(0);
                      outputToClient.flush();
                  }
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

 