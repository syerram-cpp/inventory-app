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

public class addProductController implements Initializable {

    Inventory inv;
    Stage stage;
    ObservableList<Part> associatedParts;
    ObservableList<Part> searchPartsList;
    Parent scene;

    public addProductController(Inventory inv)
    {
        this.inv = inv;
        this.associatedParts = javafx.collections.FXCollections.observableArrayList();
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

    // Displays all parts in the allPartsTableView
    // Clears the errorLabel of any error messages
    @Override public void initialize(URL location, ResourceBundle resources)
    {
        generateAllPartsTable(inv.getAllParts());
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
    // Displays the associatedParts
    // in the associatedParts TableView
    public void generateAssociatedPartsTable(){
        associatedPartsTable.setItems(associatedParts);
        assPartIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        assPartNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        assPartStockCol.setCellValueFactory(new PropertyValueFactory<>("stock"));
        assPartPriceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
    }

    // Takes the part selected in the allParts TableView and
    // adds it to the associatedParts ArrayList and the
    // associatedParts TableView is updated to reflect the change.
    @FXML void addPartToAssociatedParts(MouseEvent event)
    {
        Part selectedPart = allPartsTable.getSelectionModel().getSelectedItem();
        associatedParts.add(selectedPart);
        generateAssociatedPartsTable();
    }

    // Takes the part selected in the associatedParts TableView and
    // deletes it from the associatedParts ArrayList and the
    // associatedParts TableView is updated to reflect the change.
    @FXML void deletePartFromAssociatedParts(MouseEvent event)
    {
        Part selectedPart = allPartsTable.getSelectionModel().getSelectedItem();
        associatedParts.remove(selectedPart);
        generateAssociatedPartsTable();
    }

    // Tries to convert text from each TextField to Product attributes
    // An integer called error keeps track of each conversion.
    // If a conversion fails, the integer, 'error', is input into a switch/case
    // and a corresponding error message is printed to the application screen.
    // If all conversions succeed, the product is created along with its
    // associatedParts observableArrayList and added to the inventory
    // and the scene is changed to the Main Scene.
    @FXML void saveProductAndAssociatedParts(MouseEvent event) throws IOException
    {
        if(associatedParts.isEmpty()) { errorLabel.setText("PRODUCT MUST HAVE AT LEAST ONE PART");return; }

        int error = 0;
        try
        {
            int id = Integer.parseInt(idText.getText());

            if(nameText.getText().isEmpty()){ errorLabel.setText("PLEASE ENTER A PRODUCT NAME");return; }
            String name = nameText.getText();

            error = 2;
            int stock = Integer.parseInt(stockText.getText());

            error = 3;
            double price = Double.parseDouble(priceText.getText());

            // checks to see if the price of the product is >= the cost of its associated parts
            // If the price is not in compliance, an error message is printed to the application screen.
            double cost = 0.0;
            for (int i=0; i<associatedParts.size(); i++) {
                cost += associatedParts.get(i).getPrice(); }
            if (price < cost){ errorLabel.setText("PRICE OF A PRODUCT CANNOT BE LESS THAN THE COST OF ITS ASSOCIATED PARTS"); return;}

            error = 4;
            int max = Integer.parseInt(maxText.getText());

            error = 5;
            int min = Integer.parseInt(minText.getText());

            if (min>max) { errorLabel.setText("MINIMUM INVENTORY LEVEL CANNOT BE GREATER THAN MAXIMUM INVENTORY LEVEL");}

            Product product = new Product(id, name, price, stock, min, max);
            for (int i=0; i<associatedParts.size(); i++)
            {
                product.addAssociatedPart(associatedParts.get(i));
            }
            inv.addProduct(product);
            onActionGoToMain(event);
        }
        catch(Exception e)
        {
            mainController.inputErrorChecking(error, errorLabel);
        }
    }

    // User can search for parts by either inputting a partID (int)
    // or a partName (a String). The text the user inputs in the search bar is retrieved.
    // In a try block, the user input is treated as the partID and is
    // casted to an integer. The Part with the corresponding partID
    // is found and displayed in the allParts TableView.
    // If the try block fails, the user input is treated as a partName
    // and all the parts that contain the user inputted string are displayed
    // in the allParts TableView.
    // The user must press the 'Search' button with a blank 'Search' TextField
    // for the TableView to display the regular inventory data again.
    @FXML void searchAllPartsButton(MouseEvent event)
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
    // if a product is created successfully.
    public void onActionGoToMain(MouseEvent event) throws IOException {
        stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View_Controller/Main.fxml"));
        mainController.goToMain(event, stage, loader, inv);
    }
}
