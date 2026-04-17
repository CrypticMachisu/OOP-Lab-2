import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {

        //step 1: creat label Name
        Text text1 = new Text("Name:");

        //step 2: creat label Rgistered
        Text text2 = new Text("Registered:");

        //step 3: Creat Text Filed for name
        TextField textField1 = new TextField();

        //step 4: creat Combo Box for Registerd
        ComboBox comboBox = new ComboBox();

        //step 5: Creat Buttons
        Button button1 = new Button("Save");
        Button button2 = new Button("Remove");

        //step 6: Creating a Grid Pane and Import relevant Classes
        GridPane gridPane = new GridPane();

        //step 7: Set up size for the pane
        gridPane.setMinSize(600, 400);

        //step 8: Set padding
        gridPane.setPadding(new Insets(10, 10, 10, 10));

        //step 9: Set the vertical and horizontal gaps between the columns
        gridPane.setVgap(10);
        gridPane.setHgap(10);

        //step 10: Set the Grid alignment
        gridPane.setAlignment(Pos.CENTER);

        //step 11: Arrange all the nodes in the grid
        gridPane.add(text1, 0, 0);
        gridPane.add(textField1, 1, 0);
        gridPane.add(button1, 1, 1);

        gridPane.add(text2, 0, 2);
        gridPane.add(comboBox, 1, 2);

        gridPane.add(button2, 1, 3);

        //step 12: Style nodes be creative and add more styles
        button1.setStyle("-fx-background-color: darkslateblue; -fx-text-fill: white; -fx-font-size:13pt;");
        button2.setStyle("-fx-background-color: darkslateblue; -fx-text-fill: white; -fx-font-size:13pt;");

        text1.setStyle("-fx-font: normal bold 20px 'serif' ");
        text2.setStyle("-fx-font: normal bold 20px 'serif' ");
        gridPane.setStyle("-fx-background-color: BEIGE;");

        //Creating a scene object
        Scene scene = new Scene(gridPane);

        //Setting title to the Stage
        stage.setTitle("Movie Library System");

        //Adding scene to the stage
        stage.setScene(scene);

        //Displaying the contents of the stage
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
