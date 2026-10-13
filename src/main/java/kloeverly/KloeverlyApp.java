package kloeverly;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class KloeverlyApp extends Application
{
  @Override
  public void start(Stage stage)
  {
    Label label = new Label("Kløverly – opsætning virker");
    stage.setScene(new Scene(new StackPane(label), 400, 200));
    stage.setTitle("Kløverly");
    stage.show();
  }
}