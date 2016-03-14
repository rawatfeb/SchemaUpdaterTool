package com.Utility;

import java.sql.Connection;
import java.sql.DriverManager;

import liquibase.database.Database;
import liquibase.database.DatabaseConnection;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;

import com.westgroup.novus.configmon.data.ldap.datastore.Datastore;

public class LiquiBaseUtils {

	public static Database getNovusDatabase(String ldapUrl, String baseDn,
			String resource, String schema, boolean isBase) throws Exception {
		Datastore dataStore = LdapUtility.getDatabaseDetails(ldapUrl, baseDn,
				resource, isBase);
		Connection con = DBUtility.getConnection(dataStore);
		//con.setReadOnly(isBase);
		DatabaseFactory dbf = DatabaseFactory.getInstance();
		Database db = dbf.findCorrectDatabaseImplementation(new JdbcConnection(
				con));
		db.setDefaultSchemaName(schema);
		return db;
	}
	
	public static Database getNovusDatabase(String jdbcUrl,String schema, boolean isBase) throws Exception {	
		Connection con =null;
		con = DBUtility.getConnection(jdbcUrl,"oracle.jdbc.driver.OracleDriver");   //assuming all target nib will be using oracle driver
		
		
		/* *temporary change to support target db as HSQLDB  comment above line and uncomment this block of code*
		 * if(!isBase){ 

			try {
				Class.forName("org.hsqldb.jdbcDriver");
			} catch (Exception e) {
				throw new Exception("clould not load the class"+e.getMessage());
			}
		con= DriverManager.getConnection(jdbcUrl);
		}		//testing hsql db locally
		else{ con = DBUtility.getConnection(jdbcUrl,"oracle.jdbc.driver.OracleDriver");	}	//assuming all target nib will be using oracle driver
*/		
		con.setReadOnly(isBase);
		DatabaseFactory dbf = DatabaseFactory.getInstance();
		DatabaseConnection dbcon = (DatabaseConnection)new JdbcConnection(con);
		Database db = dbf.findCorrectDatabaseImplementation(dbcon);
		db.setDefaultSchemaName(schema);
		return db;
	}
	
	
	
	
	
}
