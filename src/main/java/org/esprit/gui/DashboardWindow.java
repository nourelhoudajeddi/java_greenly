package org.esprit.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DashboardWindow extends Application {

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.getStyleClass().add("linear-grad");

        Button backOfficeBtn = new Button("Back Office");
        backOfficeBtn.getStyleClass().add("button4");
        backOfficeBtn.setOnAction(e -> {
            new EventManagementApp().start(new Stage());
            primaryStage.close();
        });

        Button frontOfficeBtn = new Button("Front Office");
        frontOfficeBtn.getStyleClass().add("button4");
        frontOfficeBtn.setOnAction(e -> {
            new FrontOfficeWindow().start(new Stage());
            primaryStage.close();
        });

        root.getChildren().addAll(backOfficeBtn, frontOfficeBtn);

        Scene scene = new Scene(root, 400, 300);
        scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());

        primaryStage.setTitle("Event Management System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
