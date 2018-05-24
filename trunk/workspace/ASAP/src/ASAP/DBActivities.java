package ASAP;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

//import 
public class DBActivities {
	
	//*************************************************************
	/** List of Methods in this class **/
	//** Connection fConnectToXLS(String XLSPath) 
	//** ResultSet fExecuteQuery(String sSQL, Connection conn )
	/** List of Methods in this class **/
	//*************************************************************
	
	
    //*****************************************************************************************
    //*	Name		    : fConnectToXLS
    //*	Description	    : Function to make a connection to given XLS
	//*	Author		    : Anil Agarwal
    //*	Input Params	: string XLSPath - Path of the xls to query
    //*	Return Values	: Connection object having the connection
    //*****************************************************************************************
	public Connection fConnectToXLS(String XLSPath) {
		
		try {
			Connection conn = null;
			//Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
			//conn = DriverManager.getConnection("jdbc:odbc:Driver={Microsoft Excel Driver (*.xls)};DriverId=22;Dbq= " + XLSPath + ";ReadOnly=0;");
			//conn = DriverManager.getConnection( "jdbc:odbc:Driver={Microsoft Excel Driver (*.xls)};Data Source=/orpuser/orp/home/anilag/Automation/Projects/WhitePages/whitepages/trunk/Storage/Environments/Environments.ods;HDR=True;Format=ODS;");
			java.sql.Driver d = (java.sql.Driver) Class.forName("com.nilostep.xlsql.jdbc.xlDriver").newInstance();

			conn = DriverManager.getConnection("jdbc:nilostep:excel:/orpuser/orp/home/anilag/Automation/Projects/WhitePages/whitepages/trunk/Storage/Environments/Environments.xls");
			//conn = DriverManager.getConnection( "jdbc:odbc:Driver={Microsoft Excel Driver (*.xls)};Data Source=/orpuser/orp/home/anilag/Automation/Projects/WhitePages/whitepages/trunk/Storage/Environments/Environments.ods;HDR=True;Format=ODS;");
			//conn = DriverManager.getConnection("Provider=Microsoft.ACE.OLEDB.12.0;Data Source=" + XLSPath + ";Extended Properties=Excel 12.0 Xml;HDR=YES");
			

			return conn;
		} catch (Exception e) {
			System.err.println(e);
			return null;
			
		} 
	}
	
    //*****************************************************************************************
    //*	Name		    : fExecuteQuery
    //*	Description	    : Function to execute an query
    //*	Author		    : Anil Agarwal
    //*	Input Params	: string sSQL - SQL Query string
    //*                   Connection conn - Connection object
    //*	Return Values	: Resultset object having the results
    //*****************************************************************************************	
	public ResultSet fExecuteQuery(String sSQL, Connection conn ) {
		try {
			ResultSet rs = null;
			Statement stmnt = null;
			stmnt = conn.createStatement();            
			rs = stmnt.executeQuery(sSQL);
			return rs;

		} catch (SQLException eSQL) {
			System.err.println(eSQL);
			return null;
		} catch (Exception e) {
			System.err.println(e);
			return null;
		} 
	}

}
