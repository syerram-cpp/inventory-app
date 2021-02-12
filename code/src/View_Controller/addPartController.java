package View_Controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.scene.control.RadioButton;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;

import javafx.scene.input.MouseEvent;
import javafx.scene.Parent;
import javafx.stage.Stage;
import Model.*;

public class addPartController implements Initializable {

    Inventory inv;
    Stage stage;
    Parent scene;
    public addPartController(Inventory inv) { this.inv = inv; }

    @FXML private RadioButton inHouseButton;
    @FXML private RadioButton outsourcedButton;
    @FXML private TextField idText;
    @FXML private TextField nameText;
    @FXML private TextField stockText;
    @FXML private TextField priceText;
    @FXML private TextField maxText;
    @FXML private TextField minText;
    @FXML private TextField machineIdText;
    @FXML private Label machineIdLabel;
    @FXML private Label errorLabel;

    // selects the the In-House button
    // clears the errorLabel of any error messages
    @Override public void initialize(URL location, ResourceBundle resources)
    {
        inHouseButton.fire();
        errorLabel.setText("");
    }

    //changes companyID to machineID when the In-House button is pressed
    public void inHouseButtonPressed(MouseEvent event) throws IOException
    {
        machineIdLabel.setText("Machine ID");
        machineIdText.setPromptText("Machine ID");
    }

    //changes machineID to companyID when the Outsourced button is pressed
    public void outsourcedButtonPressed(MouseEvent event) throws IOException
    {
        machineIdLabel.setText("Company Name");
        machineIdText.setPromptText("Company Name");
    }

    // Tries to convert text from each TextField to Part attributes
    // An integer called error keeps track of each conversion.
    // If a conversion fails, the integer 'error' is input into a switch/case
    // and a corresponding error message is printed to the application screen.
    // If all conversions succeed, a part is created and added to the inventory
    // and the scene is changed to the Main Scene.
    public void savePart(MouseEvent event) throws IOException
    {
        int error = 0;
        try{
            int id = Integer.parseInt(idText.getText());

            String name = nameText.getText();
            if(name.contentEquals("")){ errorLabel.setText("PLEASE ENTER A PART NAME"); return;}

            error = 2;
            int stock = Integer.parseInt(stockText.getText());

            error = 3;
            double price = Double.parseDouble(priceText.getText());

            error = 4;
            int max = Integer.parseInt(maxText.getText());

            error = 5;
            int min = Integer.parseInt(minText.getText());

            if (inHouseButton.isSelected()) {
                error = 6;
                int machineId = Integer.parseInt(machineIdText.getText());
                Part inHousePart = new InHouse(id, name, price, stock, max, min, machineId);
                inv.addPart(inHousePart);
            }
            else {
                String companyName = machineIdText.getText();
                if(companyName.isEmpty()){ errorLabel.setText("PLEASE ENTER A COMPANY NAME"); return; }
                Part outsourcedPart = new Outsourced(id, name, price, stock, max, min, companyName);
                inv.addPart(outsourcedPart);
            }
            goToMain(event);
        }
        catch(Exception e) {
            mainController.inputErrorChecking(error, errorLabel);
        }
    }

    // Changes the scene to the Main Scene.
    // Happens when the 'Cancel' button is pressed.
    // Can happen when the 'Save' button is pressed if
    // a part is also created successfully.
    public void goToMain(MouseEvent event) throws IOException {
        stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View_Controller/Main.fxml"));
        mainController.goToMain(event, stage, loader, inv);
    }

}
