package View_Controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.collections.ObservableList;
import javafx.scene.input.MouseEvent;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.scene.Scene;
import Model.*;

public class mainController implements Initializable
{
    private Inventory inv;
    private ObservableList<Part> searchPartsList;
    private ObservableList<Product> searchProductsList;
    Stage stage;
    Parent scene;

    public mainController(Inventory inv)
    {
        this.inv = inv;
        this.searchPartsList = javafx.collections.FXCollections.observableArrayList();
        this.searchProductsList = javafx.collections.FXCollections.observableArrayList();
    }

    @FXML private TextField searchPart;
    @FXML private TextField searchProduct;
    @FXML private Label errorLabel;

    @FXML private TableView<Part> partsTable;
    @FXML private TableColumn<Part,Integer> partIDCol;
    @FXML private TableColumn<Part,String> partNameCol;
    @FXML private TableColumn<Part,Integer> partStockCol;
    @FXML private TableColumn<Part,Double> partPriceCol;

    @FXML private TableView<Product> productsTable;
    @FXML private TableColumn<Product, Integer> productIDCol;
    @FXML private TableColumn<Product, String> productNameCol;
    @FXML private TableColumn<Product, Integer> productStockCol;
    @FXML private TableColumn<Product, Double> productPriceCol;

    // Displays all parts in the Parts TableView
    // Displays all products in the Products Tableview
    // Clears the errorLabel of any error messages
    @Override public void initialize(URL location, ResourceBundle resources)
    {
        generatePartsTable(inv.getAllParts());
        generateProductsTable(inv.getAllProducts());
        errorLabel.setText("");
    }

    // Takes a list of parts and displays them in the Parts TableView
    public void generatePartsTable(ObservableList<Part> list){
        partsTable.setItems(list);
        partIDCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        partNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        partStockCol.setCellValueFactory(new PropertyValueFactory<>("stock"));
        partPriceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
    }

    // Takes a list of products and displays them in the Products TableView
    public void generateProductsTable(ObservableList<Product> list){
        productsTable.setItems(list);
        productIDCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        productNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        productStockCol.setCellValueFactory(new PropertyValueFactory<>("stock"));
        productPriceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
    }

    // User can search for parts by either inputting a partID (int)
    // or a partName (a String). The text the user inputs in the search bar is retrieved.
    // In a try block, the user input is treated as the partID and is
    // casted to an integer. The Part with the corresponding partID is found and
    // displayed in the aParts TableView.
    // If the try block fails, the user input is treated as a partName
    // and all the parts that contain the user inputted string are displayed
    // in the Parts TableView.
    // The user must press the 'Search' button with a blank 'Search' TextField
    // for the TableView to display the regular inventory data again.
    public void lookUpPart(MouseEvent event)
    {
        String searchPartText = searchPart.getText();
        searchPartsList.clear();
        if(searchPartText.isEmpty()) { generatePartsTable(inv.getAllParts()); return;}
        try {
            int partId = Integer.parseInt(searchPartText);
            searchPartsList.add(inv.lookupPart(partId));
            generatePartsTable(searchPartsList);
        }
        catch(Exception e) {
            generatePartsTable(inv.lookupPart(searchPartText));
        }
    }

    // User can search for products by either inputting a productID (int)
    // or a productName (a String). The text the user inputs in the search bar is retrieved.
    // In a try block, the user input is treated as the productID and is
    // casted to an integer. The Product with the corresponding productID
    // is found and displayed in the Products TableView.
    // If the try block fails, the user input is treated as a productName
    // and all the products that contain the user inputted string are displayed
    // in the Products TableView.
    // The user must press the 'Search' button with a blank 'Search' TextField
    // for the TableView to display the regular inventory data again.
    public void lookUpProduct(MouseEvent event)
    {
        String searchProductText = searchProduct.getText();
        searchProductsList.clear();
        if(searchProductText.isEmpty()) { generateProductsTable(inv.getAllProducts()); return;}
        try {
            int productId = Integer.parseInt(searchProductText);
            searchProductsList.add(inv.lookupProduct(productId));
            generateProductsTable(searchProductsList);
        }
        catch(Exception e) {
            generateProductsTable(inv.lookupProduct(searchProductText));
        }
    }

    // Takes the part selected in the Parts TableView and
    // deletes it from the Parts observableArrayList.
    // The Parts TableView is updated to reflect the change.
    public void deletePart(MouseEvent event)
    {
        Part selectedPart = partsTable.getSelectionModel().getSelectedItem();
        inv.deletePart(selectedPart);
        generatePartsTable(inv.getAllParts());
    }

    // Takes the product selected in the Products TableView and
    // deletes it from the Products observableArrayList.
    // The Products TableView is updated to reflect the change.
    public void deleteProduct(MouseEvent event)
    {
        Product selectedProduct = productsTable.getSelectionModel().getSelectedItem();
        inv.deleteProduct(selectedProduct);
        generateProductsTable(inv.getAllProducts());
    }

    //Changes the scene to the 'Add Part' scene
    public void goToAddPart(MouseEvent event) throws IOException {
        stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View_Controller/AddPart.fxml"));
        addPartController controller = new addPartController(inv);
        loader.setController(controller);
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    //changes the scene to the 'Add Product' scene
    public void goToAddProduct(MouseEvent event) throws IOException {
        stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View_Controller/AddProduct.fxml"));
        addProductController controller = new addProductController(inv);
        loader.setController(controller);
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    // changes the scene to the 'Modify Part' scene
    // only changes the scene if a part is selected in the TableView
    public void goToModifyPart(MouseEvent event) {
        try{
            Part selectedPart = partsTable.getSelectionModel().getSelectedItem();
            stage = (Stage)((Button)event.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View_Controller/ModifyPart.fxml"));
            modifyPartController controller = new modifyPartController(inv, selectedPart);
            loader.setController(controller);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
        catch(Exception e) { errorLabel.setText("PLEASE SELECT A PART TO MODIFY"); }
    }

    // changes the scene to the 'Modify Product' scene
    // only changes the scene if a product is selected in the TableView
    public void goToModifyProduct(MouseEvent event) throws IOException{
        try{
            Product selectedProduct = productsTable.getSelectionModel().getSelectedItem();
            stage = (Stage)((Button)event.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View_Controller/ModifyProduct.fxml"));
            modifyProductController controller = new modifyProductController(inv, selectedProduct);
            loader.setController(controller);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
        catch(Exception e) { errorLabel.setText("PLEASE SELECT A PRODUCT TO MODIFY"); }
    }

    // exits the application when the 'Exit' button is pressed
    public void exit(MouseEvent event) { System.exit(0); }

    // static function that is called by all 4 other scenes
    // to return to the main scene
    public static void goToMain(MouseEvent event, Stage stage, FXMLLoader loader, Inventory inv) throws IOException {
        mainController controller = new mainController(inv);
        loader.setController(controller);
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    // static function that is used by all 4 other scenes to verify
    // whether values inputted into the TextFields are valid or not
    public static void inputErrorChecking(int error, Label errorLabel)
    {
        switch(error)
        {
            case 0: //id
                errorLabel.setText("PLEASE ENTER AN INTEGER IN THE 'ID' FIELD");
                break;
            case 2: //stock
                errorLabel.setText("PLEASE ENTER AN INTEGER IN THE 'INV' FIELD");
                break;
            case 3: //price
                errorLabel.setText("PLEASE ENTER A DECIMAL NUMBER IN THE 'PRICE' FIELD");
                break;
            case 4: //max
                errorLabel.setText("PLEASE ENTER AN INTEGER IN THE 'MAX' FIELD");
                break;
            case 5: //min
                errorLabel.setText("PLEASE ENTER AN INTEGER IN THE 'MIN' FIELD");
                break;
            case 6: //machine ID
                errorLabel.setText("PLEASE ENTER AN INTEGER IN THE 'MACHINE ID' FIELD");
                break;
        }
    }

}
