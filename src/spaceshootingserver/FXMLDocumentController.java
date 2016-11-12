
package spaceshootingserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.Scanner;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLDocumentController implements Initializable {
    
    @FXML
    private ListView list;
    @FXML
    private TextArea textArea;
    @FXML
    private TextField newRoomName;
    
    private int clientNo = 0;    
    private ObservableList<GameRoom> roomList;          
        
    @FXML
    private void addRoom(ActionEvent event){
        String roomName = newRoomName.getText();
        if(!roomName.isEmpty()){
            GameRoom room = new GameRoom(roomName);
            roomList.add(room);           
            newRoomName.clear();
        }
    }
    
    @FXML
    private void removeRoom(ActionEvent event){
        GameRoom chosenRoom = (GameRoom) list.getSelectionModel().getSelectedItem();
        chosenRoom.waitForNoClients();
                
        // Delete the text file of the removed ChatRoom
        try {
            File file = new File(chosenRoom.getRoomName() + ".txt");

            if (file.delete()){
                roomList.remove(chosenRoom);                
                System.out.println(file.getName() + " is deleted!");
            }
            else 
                System.out.println("Delete operation is failed.");            
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                    new Thread(new HandleAClient(socket, roomList, textArea)).start();                    
                }
            } catch (IOException ex) {
                System.err.println(ex);
            }
        }).start();
                
    }
}

class HandleAClient implements Runnable, interaction.InteractionConstants {
    private Socket socket; // A connected socket    
    private ObservableList<GameRoom> roomList; // Reference to shared list of transcript         
    private TextArea textArea;
    private int roomId;
    private String handle;    
    

    public HandleAClient(Socket socket,ObservableList<GameRoom> roomList, TextArea textArea) {
      this.socket = socket;
      this.roomList = roomList;
      this.textArea = textArea;
      roomId = -1;
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
              case GET_ROOM: {
                  int n = roomList.size();
                  outputToClient.println(n);                                   
                  
                  for(int i = 0; i < n; i++){
                      outputToClient.println(i);
                      outputToClient.println(roomList.get(i).getRoomName());
                  }
                  
                  outputToClient.flush();
                  break;
              }              
              case SEND_ROOM: {
                  roomId = Integer.parseInt(inputFromClient.readLine());
                  roomList.get(roomId).playerEntered();
                  System.out.println("roomId: "+roomId+" name:"+roomList.get(roomId).toString());
                  System.out.println("clientEntered. numOfClient: "+roomList.get(roomId).getNumOfPlayer());
                  break;
              }
              
              
              case GET_ROOM_COUNT: {
                  outputToClient.println(roomList.size());
                  outputToClient.flush();
                  break;
              }              
              
              
              case SEND_EXIT_ROOM: {                  
                  roomList.get(roomId).playerExited();
                  System.out.println("roomId: "+roomId+" name:"+roomList.get(roomId).toString());
                  System.out.println("clientExited. numOfClient: "+roomList.get(roomId).getNumOfPlayer());
                  break;
              }
              case SEND_NAME: {
                  handle = inputFromClient.readLine();
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

 