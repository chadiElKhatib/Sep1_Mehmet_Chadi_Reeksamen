package kloeverly;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import kloeverly.data.AppData;
import kloeverly.data.WebDataManager;

import java.io.IOException;


public class KloeverlyApp extends Application
{
  private static Stage stage;

  @Override
  public void start(Stage primaryStage)
  {
    AppData.hentData();

    WebDataManager.opdaterHjemmeside();

    stage = primaryStage;

    stage.setMinWidth(900);
    stage.setMinHeight(600);

    stage.setTitle("Kløverly");

    skiftScene("MainView.fxml");

    stage.show();
  }

  public static void skiftScene(String fxmlFil)
  {
    try
    {
      FXMLLoader loader = new FXMLLoader(
          KloeverlyApp.class.getResource("/kloeverly/" + fxmlFil)
      );

      Parent root = loader.load();

      Scene scene = new Scene(root, 1000, 650);

      scene.getStylesheets().add(
          KloeverlyApp.class
              .getResource("/kloeverly/style.css")
              .toExternalForm()
      );

      stage.setScene(scene);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
}