package Main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import Model.*;
import View_Controller.mainController;

import java.io.IOException;

public class Main extends Application {

    public static void main(String[] args)
    {
        launch(args);
    }

    // Creates inventory and adds sample data to it
    // Inputs the inventory into the mainController
    // Scene is changed to the 'Main' scene
    @Override public void start(Stage stage) throws Exception
    {
        Inventory inv = new Inventory();
        addTestData(inv);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View_Controller/Main.fxml"));
        mainController controller = new mainController(inv);
        loader.setController(controller);
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    // adds sample parts and products to the sample inventory
    public void addTestData(Inventory inv)
    {
        //create in-house parts
        Part part1 = new InHouse(1, "inHouse1", 1.99, 10, 10, 100, 101);
        Part part2 = new InHouse(2, "inHouse2", 2.00, 10, 9, 101, 102);
        Part part3 = new InHouse(3, "inHouse3", 3.99, 10, 8, 102, 103);
        //create outsourced parts
        Part part4 = new Outsourced(4, "outsourced1", 4.00, 10, 7, 103, "A");
        Part part5 = new Outsourced(5, "outsourced2", 5.99, 10, 6, 104, "B");
        Part part6 = new Outsourced(6, "outsourced3", 6.00, 10, 5, 105, "C");
        //create products
        Product product1 = new Product(7, "Product1", 6.00, 11, 4, 101);
        Product product2 = new Product(8, "Product2", 7.00, 11, 4, 101);
        Product product3 = new Product(9, "Product3", 12.00, 11, 4, 101);
        //add parts to products
        product1.addAssociatedPart(part1);
        product1.addAssociatedPart(part3);
        product2.addAssociatedPart(part2);
        product2.addAssociatedPart(part4);
        product3.addAssociatedPart(part5);
        product3.addAssociatedPart(part6);
        //add parts to inventory
        inv.addPart(part1);
        inv.addPart(part2);
        inv.addPart(part3);
        inv.addPart(part4);
        inv.addPart(part5);
        inv.addPart(part6);
        //add products to inventory
        inv.addProduct(product1);
        inv.addProduct(product2);
        inv.addProduct(product3);
    }

}
