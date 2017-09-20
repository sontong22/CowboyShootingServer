
package spaceshootingserver;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SpaceShootingServer extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.setTitle("Space Shooting Server");
       
        stage.setOnCloseRequest(event->System.exit(0));        
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
    
    
}
