package com.Utility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.westgroup.novus.config.Base64;
import com.westgroup.novus.config.StringCipher;
import com.westgroup.novus.configmon.data.ldap.datastore.Datastore;

public class DBUtility {
	static Logger log = Logger.getLogger("DBUtility");
	
	public static Connection getConnection(Datastore dataStore) throws Exception{
		try{
			Class.forName(dataStore.getJdbcDriverName());
			
			String password = dataStore.getPassword();
			if (dataStore.getPasswordEncrypted()!= ""){
                // Decode from base64 and decrypt using StringCipher
                String decoded = Base64.DecodeFromBase64(dataStore.getPasswordEncrypted());
                password = StringCipher.Decode(decoded);
            }
			
			/*
			 * This is a cheat sheet for NIB environemt. New Targets created do not have sufficient permissions to create table.
			 */
			if(dataStore.getJdbcDriverName().equalsIgnoreCase("oracle.jdbc.driver.OracleDriver") && dataStore.getUserId().equalsIgnoreCase("nibu") && password.equalsIgnoreCase("nibu")){
				dataStore.setUserId("system");
				password="west";
			}
			System.out.println(dataStore.getJdbcDriverName()+" \n"+dataStore.getConnectionUrl());
			return DriverManager.getConnection(dataStore.getConnectionUrl(), dataStore.getUserId(), password);
		}
		catch(SQLException e){
			log.error("Error in obtaining DB connection Exception="+e.getMessage()+" Datastore Used="+dataStore.toString());
			throw new Exception("Error in obtaining DB connection Exception: jdbcUrl="+dataStore.getConnectionUrl()+" driver="+dataStore.getJdbcDriverName()+"  "+e.getMessage());
		} catch (ClassNotFoundException e) {
			log.error("Error in obtaining DB connection Exception="+e.getMessage()+" Datastore Used="+dataStore.toString());
			throw new Exception("Error in obtaining DB connection Exception: jdbcUrl="+dataStore.getConnectionUrl()+" driver="+dataStore.getJdbcDriverName()+"  "+e.getMessage());
		} catch (Exception e) {
			log.error("Error in obtaining DB connection Exception="+e.getMessage()+" Datastore Used="+dataStore.toString());
			throw new Exception("Error in obtaining DB connection Exception: jdbcUrl="+dataStore.getConnectionUrl()+" driver="+dataStore.getJdbcDriverName()+"  "+e.getMessage());
		}
	}
	public static Connection getConnection(String jdbcUrl,String driver) throws Exception{
		try{
			Class.forName(driver);
			return DriverManager.getConnection(jdbcUrl, "system", "west");
		}
		catch(SQLException e){
			log.error("Error in obtaining DB connection Exception="+e.getMessage());
			throw new Exception("Error in obtaining DB connection Exception: jdbcUrl="+jdbcUrl+" driver="+driver+"  "+e.getMessage());
		} catch (ClassNotFoundException e) {
			log.error("Error in obtaining DB connection Exception="+e.getMessage());
			throw new Exception("Error in obtaining DB connection Exception: jdbcUrl="+jdbcUrl+" driver="+driver+"  "+e.getMessage());
		} catch (Exception e) {
			log.error("Error in obtaining DB connection Exception="+e.getMessage());
			throw new Exception("Error in obtaining DB connection Exception: jdbcUrl="+jdbcUrl+" driver="+driver+"  "+e.getMessage());
		}
	}
	
}
