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

public class modifyPartController implements Initializable {

    Inventory inv;
    Part selectedPart;
    int index;
    Stage stage;
    Parent scene;

    public modifyPartController(Inventory inv, Part selectedPart)
    {
        this.inv = inv;
        this.selectedPart = selectedPart;
        for(int i=0; i<inv.getAllParts().size(); i++)
        {
            if(inv.getAllParts().get(i) == selectedPart)
            {
                index = i;
                break;
            }
        }
    }

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

    // Sets all TextFields according to the selectedPart's attributes
    // Clears the errorLabel of any error messages
    @Override public void initialize(URL location, ResourceBundle resources)
    {
        idText.setText(Integer.toString(selectedPart.getId()));
        nameText.setText(selectedPart.getName());
        stockText.setText(Integer.toString(selectedPart.getStock()));
        priceText.setText(Double.toString(selectedPart.getPrice()));
        maxText.setText(Integer.toString(selectedPart.getMax()));
        minText.setText(Integer.toString(selectedPart.getMin()));

        if(selectedPart instanceof InHouse) {
            inHouseButton.fire();
            machineIdText.setText(Integer.toString(((InHouse) selectedPart).getMachineID())); }
        else {
            outsourcedButton.fire();
            machineIdLabel.setText("Company ID");
            machineIdText.setPromptText("Company ID");
            machineIdText.setText(((Outsourced)selectedPart).getCompanyName()); }

        errorLabel.setText("");
    }

    //changes companyID to machineID when the In-House button is pressed
    public void inHouseButtonPressed(MouseEvent event) throws IOException
    {
        machineIdLabel.setText("Machine ID");
        machineIdText.setPromptText("Machine ID");
        machineIdText.setText("");
    }

    //changes machineID to companyID when the Outsourced button is pressed
    public void outsourcedButtonPressed(MouseEvent event) throws IOException
    {
        machineIdLabel.setText("Company Name");
        machineIdText.setPromptText("Company Name");
        machineIdText.setText("");
    }

    // Tries to convert text from each TextField to Part attributes
    // An integer called error keeps track of each conversion.
    // If a conversion fails, the integer 'error' is input into a switch/case
    // and a corresponding error message is printed to the application screen.
    // If all conversions succeed, the part is updated in the inventory
    // and the scene is changed to the Main Scene.
    public void savePart(MouseEvent event) throws IOException {
        int error = 0;
        try{
            int id = Integer.parseInt(idText.getText());

            String name = nameText.getText();
            if(name.isEmpty()){ errorLabel.setText("PLEASE ENTER A PART NAME"); return;}
            
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
                inv.updatePart(index, inHousePart);
            }
            else {
                String companyName = machineIdText.getText();
                if(companyName.isEmpty()){ errorLabel.setText("PLEASE ENTER A COMPANY NAME"); return; }
                Part outsourcedPart = new Outsourced(id, name, price, stock, max, min, companyName);
                inv.updatePart(index, outsourcedPart);
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
    // a part is also updated successfully.
    public void goToMain(MouseEvent event) throws IOException {
        stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View_Controller/Main.fxml"));
        mainController.goToMain(event, stage, loader, inv);
    }
}
