package com.kc.controller;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialogs;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.kc.constant.CommonConstants;
import com.kc.dao.CustomersDAO;
import com.kc.dao.EnquiryDAO;
import com.kc.dao.ServiceDAO;
import com.kc.dao.StatusReminderDAO;
import com.kc.model.CustomersVO;
import com.kc.model.EnquiryViewVO;
import com.kc.model.ReminderVO;
import com.kc.util.DateUtil;
import com.kc.util.QuotationUtil;
import com.kc.util.Validation;

public class ReminderController implements Initializable {
	
	private static final Logger LOG = LogManager.getLogger(ReminderController.class);
	
		EnquiryDAO enquiryDAO;
		CustomersDAO customersDAO;
		ServiceDAO serviceDAO;
		Validation validate;
		StatusReminderDAO statusReminderDAO;
		String startDate;
		String endDate;
	
		public ReminderController() {
		enquiryDAO = new EnquiryDAO();
		customersDAO = new CustomersDAO();
		serviceDAO = new ServiceDAO();
		validate = new Validation();
		statusReminderDAO = new StatusReminderDAO();
		}
	
		@FXML
	    private ComboBox<String> referenceCombo;

	    @FXML
	    private Button createReminder;

	    @FXML
	    private ComboBox<Integer> frequencyCombo;

	    @FXML
	    private TextArea message;

	    @FXML
	    private ComboBox<String> modifyCombo;

	    @FXML
	    private Button modifyReminder;

	    @FXML
	    private ComboBox<String> monthCombo;

	    @FXML
	    private TextField receiver;

	    @FXML
	    private ComboBox<Integer> reminderCombo;

	    @FXML
	    private Button search;

	    @FXML
	    private TextField sender;

	    @FXML
	    private Label sentReminder;

	    @FXML
	    private TextField subject;

	    @FXML
	    private ComboBox<String> autoReminderCombo;

	    @FXML
	    private ComboBox<String> yearCombo;
	    
	    @FXML
	    private ComboBox<String> actionCombo;
	    
	    @FXML
	    private HBox autoReminderHBox;
	    
	    @FXML
	    private VBox autoReminderVBox;
	    
	    @FXML
	    private GridPane emailGrid;
	    
	    @FXML
	    private HBox referenceHBox;

	    private ObservableList<String> monthList = FXCollections.observableArrayList();
		private ObservableList<String> yearList = FXCollections.observableArrayList();
		private ObservableList<ReminderVO> reminderList = FXCollections.observableArrayList();
		private ObservableList<CustomersVO> customerList = FXCollections.observableArrayList();
		private ObservableList<String> refList = FXCollections.observableArrayList();
		SimpleDateFormat formatter = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
		private EnquiryViewVO enquiryViewVO = new EnquiryViewVO();
	    
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try
		{
		
			monthList.addAll(Arrays.asList(CommonConstants.MONTHS.split(",")));
			yearList.addAll(Arrays.asList(CommonConstants.YEARS.split(",")));
			monthCombo.setItems(monthList);
			yearCombo.setItems(yearList);
			search.setOnAction(new EventHandler<ActionEvent>() {
				
				@Override
				public void handle(ActionEvent event) {
					startDate = "01/" + QuotationUtil.monthDigitFromString(monthCombo.getSelectionModel().getSelectedItem()) + "/" + yearCombo.getSelectionModel().getSelectedItem();
					endDate = "31/" + QuotationUtil.monthDigitFromString(monthCombo.getSelectionModel().getSelectedItem()) + "/" + yearCombo.getSelectionModel().getSelectedItem();
					if(monthCombo.getSelectionModel().getSelectedIndex()==-1||yearCombo.getSelectionModel().getSelectedIndex()==-1)
					{
						Dialogs.showInformationDialog(LoginController.primaryStage,CommonConstants.SELECT_MONTH_YEAR);
					}
					
				}
			});
			
			actionCombo.valueProperty().addListener(new ChangeListener<String>() {

				@Override
				public void changed(
						ObservableValue<? extends String> observable,
						String oldValue, String newValue) {
					try
					{
						if(actionCombo.getSelectionModel().getSelectedItem().equalsIgnoreCase("Create Reminder"))
						{
							refList = statusReminderDAO.getCreateReminders(startDate,endDate);
						}
						else if (actionCombo.getSelectionModel().getSelectedItem().equalsIgnoreCase("Modify Reminder"))
						{
							refList = statusReminderDAO.getModifyReminders(startDate,endDate);
						}
						if(refList.isEmpty())
						{
							Dialogs.showInformationDialog(LoginController.primaryStage, CommonConstants.NO_ENQUIRY);
						}
						else
						{
							referenceCombo.setItems(refList);
							referenceHBox.setVisible(true);
							autoReminderHBox.setVisible(true);
						}
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			autoReminderCombo.valueProperty().addListener(new ChangeListener<String>() {

				@Override
				public void changed(
						ObservableValue<? extends String> observable,
						String oldValue, String newValue) {
					if(newValue.equalsIgnoreCase("ON"))
					{
						autoReminderVBox.setVisible(true);
						emailGrid.setVisible(true);
					}
					else if(newValue.equalsIgnoreCase("OFF"))
					{
						autoReminderVBox.setVisible(false);
						emailGrid.setVisible(true);
					}
						
				}
				
			});
			referenceCombo.valueProperty().addListener(new ChangeListener<String>() {

				@Override
				public void changed(
						ObservableValue<? extends String> observable,
						String oldValue, String newValue) {
					
					if(actionCombo.getSelectionModel().getSelectedItem().equalsIgnoreCase("Modify Reminder"))
					{
						try
						{
							reminderList=statusReminderDAO.getReminderDetails();
							for(ReminderVO reminderVO :reminderList)
							{
								if(newValue.equals(reminderVO.getReferenceNo()))
								{
									fillDetails(reminderVO);
								}
							}
						}
						catch (Exception e) {
							e.printStackTrace();
						}
					}
					
				}
			});
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void sendMail()
	{
		if(actionCombo.getSelectionModel().getSelectedItem().equalsIgnoreCase("Create Reminder"))
		{
			ReminderVO reminderVO = new ReminderVO();
			reminderVO.setReferenceNo(referenceCombo.getSelectionModel().getSelectedItem());
			reminderVO.setLastSent(formatter.format(new Date()));
			reminderVO.setStatus(autoReminderCombo.getSelectionModel().getSelectedItem());
			reminderVO.setSubject(subject.getText());
			reminderVO.setEmailMessage(message.getText());
			reminderVO.setReciever(receiver.getText());
			if(autoReminderCombo.getSelectionModel().getSelectedItem().equalsIgnoreCase("ON"))
			{
				reminderVO.setFrequency(frequencyCombo.getSelectionModel().getSelectedItem());
				reminderVO.setTotalReminder(reminderCombo.getSelectionModel().getSelectedItem());
				reminderVO.setNextSend(formatter.format(DateUtil.addDays(new Date(), frequencyCombo.getSelectionModel().getSelectedItem())));
			}
			else if(autoReminderCombo.getSelectionModel().getSelectedItem().equalsIgnoreCase("OFF"))
			{
				reminderVO.setFrequency(0);
				reminderVO.setTotalReminder(1);
				reminderVO.setNextSend(CommonConstants.NA);
			}
			
			statusReminderDAO.createReminder(reminderVO);
		}
		else if (actionCombo.getSelectionModel().getSelectedItem().equalsIgnoreCase("Modify Reminder"))
		{
			ReminderVO reminderVO = new ReminderVO();
			reminderVO.setLastSent(formatter.format(new Date()));
			reminderVO.setStatus(autoReminderCombo.getSelectionModel().getSelectedItem());
			reminderVO.setSubject(subject.getText());
			reminderVO.setEmailMessage(message.getText());
			reminderVO.setReciever(receiver.getText());
			if(autoReminderCombo.getSelectionModel().getSelectedItem().equalsIgnoreCase("ON"))
			{
				reminderVO.setFrequency(frequencyCombo.getSelectionModel().getSelectedItem());
				reminderVO.setTotalReminder(reminderCombo.getSelectionModel().getSelectedItem());
				reminderVO.setNextSend(formatter.format(DateUtil.addDays(new Date(), frequencyCombo.getSelectionModel().getSelectedItem())));
			}
			else if(autoReminderCombo.getSelectionModel().getSelectedItem().equalsIgnoreCase("OFF"))
			{
				reminderVO.setFrequency(0);
				reminderVO.setTotalReminder(1);
				reminderVO.setNextSend(CommonConstants.NA);
			}
			statusReminderDAO.UpdateReminder(reminderVO,referenceCombo.getSelectionModel().getSelectedItem());
			
		}
	}
	public void stopReminder()
	{
		
	}
	public void fillDetails(ReminderVO reminderVO)
	{
		subject.setText(reminderVO.getSubject());
		receiver.setText(reminderVO.getReciever());
		message.setText(reminderVO.getEmailMessage());
		if(reminderVO.getStatus().equalsIgnoreCase("ON"))
		{
			autoReminderCombo.getSelectionModel().selectFirst();
			
			for(int i=1; i<=10;i++)
			{
				if(reminderVO.getTotalReminder()==i)
				{
					reminderCombo.getSelectionModel().select(i-1);
				}
				if(reminderVO.getFrequency()==i)
				{
					frequencyCombo.getSelectionModel().select(i-1);
				}
			}
			
		}
		else if(reminderVO.getStatus().equalsIgnoreCase("OFF"))
		{
			autoReminderCombo.getSelectionModel().selectLast();
			reminderCombo.getSelectionModel().clearSelection();
			frequencyCombo.getSelectionModel().clearSelection();
		}
		
	}
}