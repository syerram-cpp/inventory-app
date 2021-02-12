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
import Model.*;

public class modifyProductController implements Initializable {

    Inventory inv;
    Product selectedProduct;
    ObservableList<Part> searchPartsList;
    Stage stage;
    Parent scene;

    public modifyProductController(Inventory inv, Product selectedProduct)
    {
        this.inv = inv;
        this.selectedProduct = selectedProduct;
        this.searchPartsList = javafx.collections.FXCollections.observableArrayList();
    }

    @FXML private TextField idText;
    @FXML private TextField nameText;
    @FXML private TextField stockText;
    @FXML private TextField priceText;
    @FXML private TextField maxText;
    @FXML private TextField minText;
    @FXML private TextField searchAllPartsText;
    @FXML private Label errorLabel;

    @FXML private TableView<Part> allPartsTable;
    @FXML private TableColumn<Part, Integer> partIdCol;
    @FXML private TableColumn<Part, String> partNameCol;
    @FXML private TableColumn<Part, Integer> partStockCol;
    @FXML private TableColumn<Part, Double> partPriceCol;

    @FXML private TableView<Part> associatedPartsTable;
    @FXML private TableColumn<Part, Integer> assPartIdCol;
    @FXML private TableColumn<Part, String> assPartNameCol;
    @FXML private TableColumn<Part, Integer> assPartStockCol;
    @FXML private TableColumn<Part, Double> assPartPriceCol;

    // Sets all TextFields according to the selectedProduct
    // Makes the allParts TableView display all parts
    // Makes the associatedParts TableView display all associatedParts
    // Clears the errorLabel of any error messages
    @Override public void initialize(URL location, ResourceBundle resources)
    {
        idText.setText(Integer.toString(selectedProduct.getId()));
        idText.setDisable(true);
        nameText.setText(selectedProduct.getName());
        stockText.setText(Integer.toString(selectedProduct.getStock()));
        priceText.setText((Double.toString(selectedProduct.getPrice())));
        minText.setText(Integer.toString(selectedProduct.getMin()));
        maxText.setText(Integer.toString(selectedProduct.getMax()));

        generateAllPartsTable(inv.getAllParts());
        generateAssociatedPartsTable();
        errorLabel.setText("");
    }

    // Takes a list of parts and displays them in the allParts TableView
    public void generateAllPartsTable(ObservableList<Part> list){
        allPartsTable.setItems(list);
        partIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        partNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        partStockCol.setCellValueFactory(new PropertyValueFactory<>("stock"));
        partPriceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
    }

    // Displays all AssociatedParts of the selectedProduct in the associatedParts TableView
    public void generateAssociatedPartsTable(){
        associatedPartsTable.setItems(selectedProduct.getAllAssociatedParts());
        assPartIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        assPartNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        assPartStockCol.setCellValueFactory(new PropertyValueFactory<>("stock"));
        assPartPriceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
    }

    // Takes the part selected in the allParts TableView and
    // adds it to the associatedParts observableArrayList and
    // associatedParts TableView is updated to reflect the change.
    @FXML void addPartToAssParts(MouseEvent event)
    {
        Part selectedPart = allPartsTable.getSelectionModel().getSelectedItem();
        selectedProduct.addAssociatedPart(selectedPart);
        generateAssociatedPartsTable();
    }

    // Takes the part selected in the associatedParts TableView and
    // deletes it from the associatedParts ArrayList and the
    // associatedParts TableView is updated to reflect the change.
    @FXML void deletePartFromAssociatedParts(MouseEvent event)
    {
        Part selectedPart = associatedPartsTable.getSelectionModel().getSelectedItem();
        selectedProduct.deleteAssociatedPart(selectedPart);
        generateAssociatedPartsTable();
    }

    // Tries to convert text from each TextField to Product attributes
    // An integer called error keeps track of each conversion.
    // If a conversion fails, the integer, 'error', is input into a switch/case
    // and a corresponding error message is printed to the application screen.
    // If all conversions succeed, the product along with its associatedParts
    // is updated into the inventory and the scene is changed to the Main Scene.
    @FXML void saveProduct(MouseEvent event) throws IOException
    {
        // checks to see if the product has >= 1 part
        // If the product is not in compliance, an error message is printed to the application screen.
        if(selectedProduct.getAllAssociatedParts().isEmpty()) { errorLabel.setText("PRODUCT MUST HAVE AT LEAST ONE PART");return; }

        int error = 0;
        try
        {
            selectedProduct.setId(Integer.parseInt(idText.getText()));

            if(nameText.getText().isEmpty()){ errorLabel.setText("PLEASE ENTER A PRODUCT NAME");return; }
            selectedProduct.setName(nameText.getText());

            error = 2;
            selectedProduct.setStock(Integer.parseInt(stockText.getText()));

            error = 3;
            // checks to see if the price of the product is >= the cost of its associated parts
            // If the price is not in compliance, an error message is printed to the application screen.
            double price = Double.parseDouble(priceText.getText());
            double cost = 0.0;
            for (int i=0; i<selectedProduct.getAllAssociatedParts().size(); i++) {
                cost += selectedProduct.getAllAssociatedParts().get(i).getPrice(); }
            if (price < cost){ errorLabel.setText("PRICE OF A PRODUCT CANNOT BE LESS THAN THE COST OF ITS ASSOCIATED PARTS"); return;}
            selectedProduct.setPrice(price);

            error = 4;
            selectedProduct.setMax(Integer.parseInt(maxText.getText()));

            error = 5;
            selectedProduct.setMin(Integer.parseInt(minText.getText()));
            goToMain(event);
        }
        catch(Exception e)
        {
            mainController.inputErrorChecking(error, errorLabel);
        }
    }

    // User can search for parts by either inputting a partID (int)
    // or a partName (a String). The text the user inputs in the search bar is retrieved.
    // In a try block, the user input is treated as the partID and is
    // casted to an integer. The corresponding Part is found and displayed in the allParts TableView.
    // If the try block fails, the user input is treated as a partName
    // and all the parts that contain the user inputted string are displayed
    // in the allParts TableView.
    // The user must press the 'Search' button with an blank 'Search' TextField
    // for the TableView to display the regular inventory data again.
    @FXML void searchAllParts(MouseEvent event)
    {
        String searchPartText = searchAllPartsText.getText();
        searchPartsList.clear();
        if(searchPartText.isEmpty()) { generateAllPartsTable(inv.getAllParts()); return;}
        try {
            int partId = Integer.parseInt(searchPartText);
            searchPartsList.add(inv.lookupPart(partId));
            generateAllPartsTable(searchPartsList);
        }
        catch(Exception e) {
            generateAllPartsTable(inv.lookupPart(searchPartText));
        }
    }

    // Changes the scene to the Main Scene.
    // Happens when the 'Cancel' button is pressed.
    // Can happen when the 'Save' button is pressed
    // if a product is updated successfully.
    @FXML void goToMain(MouseEvent event) throws IOException
    {
        stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View_Controller/Main.fxml"));
        mainController.goToMain(event, stage, loader, inv);
    }
}
