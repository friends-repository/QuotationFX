package com.kc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import com.kc.model.CustomersVO;
import com.kc.model.CustomersVO;
import com.kc.util.DBConnector;

public class CustomersDAO {
	private Connection conn = null;
	private PreparedStatement preparedStatement = null;
	private Statement statement = null;
	private ResultSet resultSet = null;
	

	public void saveCustomer(CustomersVO customersVO) throws Exception
	{
		
		try
		{
			conn = DBConnector.getConnection();
			preparedStatement = conn.prepareStatement("INSERT INTO customers(customername,companyname,address,city,state,email,contactnumber,type,tinnumber) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)");
			
			preparedStatement.setString(1, customersVO.getCustomerName());
			preparedStatement.setString(2, customersVO.getCompanyName());
			preparedStatement.setString(3, customersVO.getAddress());
			preparedStatement.setString(4, customersVO.getCity());
			preparedStatement.setString(5, customersVO.getState());
			preparedStatement.setString(6, customersVO.getEmailId());
			preparedStatement.setString(7, customersVO.getContactNumber());
			preparedStatement.setString(8, customersVO.getCustomerType());
			preparedStatement.setString(9, customersVO.getTinNumber());
			
			preparedStatement.execute();
		}
		catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
	}
	
	
	public void updateCustomer(CustomersVO customersVO)
	{
		try
		{
			conn = DBConnector.getConnection();
			preparedStatement = conn.prepareStatement("UPDATE customers SET customername=?,companyname=?,address=?,city=?,state=?,email=?,contactnumber=?,type=?,tinnumber=? where ID=?");
			
			preparedStatement.setString(1, customersVO.getCustomerName());
			preparedStatement.setString(2, customersVO.getCompanyName());
			preparedStatement.setString(3, customersVO.getAddress());
			preparedStatement.setString(4, customersVO.getCity());
			preparedStatement.setString(5, customersVO.getState());
			preparedStatement.setString(6, customersVO.getEmailId());
			preparedStatement.setString(7, customersVO.getContactNumber());
			preparedStatement.setString(8, customersVO.getCustomerType());
			preparedStatement.setString(9, customersVO.getTinNumber());
			preparedStatement.setInt(10, customersVO.getId());
			
			preparedStatement.execute();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public ObservableList<CustomersVO> getCustomers() throws SQLException
	{		
		ObservableList<CustomersVO> listOfCustomers = FXCollections.observableArrayList();
		try{
			conn = DBConnector.getConnection();
			statement = conn.createStatement();
			resultSet = statement.executeQuery("SELECT * FROM CUSTOMERS");
			
			while(resultSet.next())
			{
				CustomersVO customersVO = new CustomersVO();
				customersVO.setId(resultSet.getInt(1));
				customersVO.setCustomerName(resultSet.getString(2));
				customersVO.setCompanyName(resultSet.getString(3));
				customersVO.setAddress(resultSet.getString(4));
				customersVO.setCity(resultSet.getString(5));
				customersVO.setState(resultSet.getString(6));
				customersVO.setEmailId(resultSet.getString(7));
				customersVO.setContactNumber(resultSet.getString(8));
				if("D".equalsIgnoreCase(resultSet.getString(9)))
				{
					customersVO.setCustomerType("Dealer");
				}
				else
				{
					customersVO.setCustomerType("End User");
				}
				customersVO.setTinNumber(resultSet.getString(10));
				
				listOfCustomers.add(customersVO);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			if(conn !=null)
			{
				conn.close();
			}
		}
		return listOfCustomers;
	}
	public void deleteCustomers(List<CustomersVO> listCustomers) throws Exception {
		try {
			conn = DBConnector.getConnection();
			statement = conn.createStatement();
			for (CustomersVO customersVO : listCustomers) {
				statement.addBatch("DELETE FROM CUSTOMERS WHERE ID="
						+ customersVO.getId());
			}
			statement.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

}