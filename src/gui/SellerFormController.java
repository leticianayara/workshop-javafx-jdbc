package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.SellerService;

public class SellerFormController implements Initializable{

	private Seller entity;
	
	private SellerService service;
	
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();
	
	@FXML
	private TextField txtId; 
	
	@FXML
	private TextField txtName;
	
	@FXML
	private TextField txtEmail;
	
	@FXML
	private TextField txtBirthDate;
	
	@FXML
	private TextField txtBaseSalary;
	
	@FXML
	private Button btnSave;
	
	@FXML
	private Button btnCancel;
	
	@FXML
	private Label lblErrorName;
	
	@FXML
	private Label lblErrorEmail;
	
	@FXML
	private Label lblErrorBirthDate;
	
	@FXML
	private Label lblErrorBaseSalary;
	
	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}
	
	@FXML
	public void onBtnSaveAction(ActionEvent event) {
		if(entity == null) {
			throw new IllegalStateException("Entity was null");
		}  		
		if(service == null) {
			throw new IllegalStateException("Service was null");
		}
		try {
			entity = getFormData();
			service.saveOrUpdate(entity);
			notifyDataChangeListener();
			Utils.currentStage(event).close();
		}catch (DbException e) {
			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		}catch (ValidationException e) {
			setErrorMessages(e.getErrors());
		}
	}
	
	private void notifyDataChangeListener() {
		for(DataChangeListener listener : dataChangeListeners) {
			listener.onDanaChanged();
		}
		
	}

	private Seller getFormData() {
		Seller obj = new Seller();
		
		ValidationException exception = new ValidationException("Validation error");
		
		obj.setId(Utils.tryParseToInt(txtId.getText()));
		
		if(txtName.getText() == null || txtName.getText().trim().equals("")) {
			exception.addErrors("name", "Field can´t be empty or null");
		}
		obj.setName(txtName.getText());
		
		if(txtEmail.getText() == null || txtEmail.getText().trim().equals("")) {
			exception.addErrors("email", "Field can´t be empty or null");
		} else if ( !txtEmail.getText().contains("@")) {
			exception.addErrors("email", "This is not a email");
		}
		obj.setEmail(txtEmail.getText());

		obj.setBirthDate(Utils.tryParseToDate(txtBirthDate.getText()));;
		obj.setBaseSalary(Utils.tryParseToDouble(txtBaseSalary.getText()));
		
		if(exception.getErrors().size() > 0) {
			throw exception;
		}
		
		return obj;
	}

	@FXML
	public void onBtnCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializNode();
	}
	
	public void setEntity(Seller entity) {
		this.entity = entity;
	}
	
	public void setService(SellerService service) {
		this.service = service;
	}

	public void updateFormData() {
		if(entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		
	//	SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
		txtEmail.setText(entity.getEmail());
		//txtBirthDate.setText(sdf.format(entity.getBirthDate()));
		//txtBaseSalary.setText(String.valueOf(entity.getBaseSalary()));
	}
	

	private void initializNode() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 30);
		Constraints.setTextFieldDouble(txtBaseSalary);
	}
	
	private void setErrorMessages(Map<String,String> errors) {
		Set<String> fields = errors.keySet();
		
		if(fields.contains("name")) {
			lblErrorName.setText(errors.get("name"));
		}
		
		if(fields.contains("email")) {
			lblErrorEmail.setText(errors.get("email"));
		}
		
		if(fields.contains("birthDate")) {
			lblErrorBirthDate.setText(errors.get("birthDate"));
		}
		
		if(fields.contains("baseSalary")) {
			lblErrorBaseSalary.setText(errors.get("baseSalary"));
		}
	}
}
