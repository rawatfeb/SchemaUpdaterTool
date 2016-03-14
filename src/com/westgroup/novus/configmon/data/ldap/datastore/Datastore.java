package com.westgroup.novus.configmon.data.ldap.datastore;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.westgroup.novus.configmon.data.util.MD5;

/**
 * Novus Datastore Object. represents a datastore LDAP entry
 * 
 * @author David S. Sundry
 * @since 04/05/2010
 */
public class Datastore implements Serializable, Comparable<Datastore> {
	private static final long serialVersionUID = 2913860777655046832L;
	
	private String path;				//Primary key
	private String jdbcDriverName;
	private String password;
	private String passwordEncrypted;
	private String resourceName;
	private String userId;
	private String databaseHost;
	private String connectionUrl;
	private String datastoreType;
	private String name;
	private String created;
	private String modified;
	private boolean readOnly;
	private boolean slave;

	private Pattern pattern = Pattern.compile("cn=(.*)");
	
	/**
	 * Returns the LDAP distingushed name of this entry
	 * 
	 * @return The LDAP path name
	 */
	public String getPath() {
		return toString(path).toLowerCase();
	}
	
	/**
	 * Sets the LDAP path name
	 * 
	 * @param path An LDAP path
	 */
	public void setPath(String path) {
		this.path = path;
	}
	
	/**
	 * Returns a driver classname
	 * 
	 * @return A driver name
	 */
	public String getJdbcDriverName() {
		return toString(jdbcDriverName);
	}
	
	/**
	 * Sets the jdbc driver name
	 * 
	 * @param jdbcDriverName A jdbc name
	 */
	public void setJdbcDriverName(String jdbcDriverName) {
		this.jdbcDriverName = jdbcDriverName;
	}
	
	/**
	 * Gets the database password
	 * @return A password
	 */
	public String getPassword() {
		return toString(password);
	}
	
	/**
	 * Sets the database password
	 * 
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	
	/**
	 * Returns the encrypted form of the password
	 * 
	 * @return encrypted password
	 */
	public String getPasswordEncrypted() {
		return toString(passwordEncrypted);
	}
	
	/**
	 * Sets the encrypted form of the password
	 * 
	 * @param passwordEncripted
	 */
	public void setPasswordEncrypted(String passwordEncripted) {
		this.passwordEncrypted = passwordEncripted;
	}
	
	/**
	 * Is database readonly
	 * 
	 * @return readonly flag
	 */
	public boolean isReadOnly() {
		return readOnly;
	}
	
	/**
	 * Sets the database readonly flag
	 * @param readOnly
	 */
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
	
	/**
	 * Returns the database user id
	 * 
	 * @return A user id
	 */
	public String getUserId() {
		return toString(userId);
	}
	
	/**
	 * Sets the database user id
	 * @param userId
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	/**
	 * Returns a database host name
	 * 
	 * @return hostname, never null
	 */
	public String getDatabaseHost() {
		return toString(databaseHost);
	}
	
	/**
	 * Sets a database host name
	 * 
	 * @param databaseHost A database host
	 */
	public void setDatabaseHost(String databaseHost) {
		this.databaseHost = databaseHost;
	}
	
	/**
	 * Return the database connection url string
	 * 
	 * @return A connection string
	 */
	public String getConnectionUrl() {
		return toString(connectionUrl);
	}
	
	/**
	 * Sets the connection url
	 * 
	 * @param connectionUrl
	 */
	public void setConnectionUrl(String connectionUrl) {
		this.connectionUrl = connectionUrl;
	}
	
	/**
	 * A string representing various datastore types, may or may not be set
	 * @return
	 */
	public String getDatastoreType() {
		return toString(datastoreType);
	}
	
	/**
	 * Set the datastore type.
	 * @param datastoreType
	 */
	public void setDatastoreType(String datastoreType) {
		this.datastoreType = datastoreType;
	}
	
	/**
	 * Returns whether this database is a slave
	 * 
	 * @return flag for being a slave
	 */
	public boolean isSlave() {
		return slave;
	}
	
	/**
	 * Sets the database slave flag.
	 * 
	 * @param b boolean value
	 */
	public void setSlave(boolean b) {
		this.slave = b;
	}
	
	/**
	 * Set the Novus resource name
	 * 
	 * @return
	 */
	public String getResourceName() {
		return toString(resourceName);
	}
	
	/**
	 * Sets the novus resource name
	 * 
	 * @param resourceName
	 */
	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}
	
	/**
	 * gets the entry created date
	 * 
	 * @return A date string
	 */
	public String getCreated() {
		return toString(created);
	}
	
	/**
	 * Sets the entry created date
	 * 
	 * @param created
	 */
	public void setCreated(String created) {
		this.created = created;
	}
	
	/**
	 * gets the date the entry was last modified
	 * 
	 * @return A date string
	 */
	public String getModified() {
		return toString(modified);
	}
	
	/**
	 * Sets the date the entry was last modified
	 * 
	 * @param modified
	 */
	public void setModified(String modified) {
		this.modified = modified;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringWriter s = new StringWriter();
		PrintWriter pw = new PrintWriter(s);
		
		pw.println("jdbcDriverName: " + getJdbcDriverName());
		pw.println("password: " + getPassword());
		pw.println("passwordEncrypted: " + getPasswordEncrypted());
		pw.println("readOnly: " + isReadOnly());
		pw.println("resourceName: " + getResourceName());
		pw.println("userId: " + getUserId());
		pw.println("databaseHost: " + getDatabaseHost());
		pw.println("connectionUrl: " + getConnectionUrl());
		pw.println("datastoreType: " + getDatastoreType());
		pw.println("path: " + getPath());
		pw.println("slave:" + isSlave());
		pw.println("created" + getCreated());
		pw.println("modified:" + getModified());
		
		return s.toString();
	}
	
	/**
	 * Normalize a string
	 * 
	 * @param s Source string.
	 * @return Non null string
	 */
	protected String toString(String s) {
		return s==null?"":s.trim();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Datastore arg0) {
		return this.getPath().compareTo(arg0.getPath());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj instanceof Datastore) {
			Datastore ds = (Datastore) obj;
			return compareTo(ds)==0;
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return getPath().hashCode();
	}
	
	/**
	 * Return the parent LDAP name
	 * 
	 * @return
	 */
	public String getParentName() {
		String[] components = getPath().split(",");	
		if (components.length>2) {
			Matcher matcher = pattern.matcher(components[1]);
			if (matcher.matches())
				return matcher.group(1);
		}
		return null;
	}
	
	/**
	 * Return the parent LDAP distinguished name
	 *  
	 * @return The name of the parent
	 */
	public String getParentPath() {
		StringBuffer result = new StringBuffer();
		String[] components = getPath().split(",");	
		for (int i=1; i<components.length; i++) {
			result.append(components[i]);
			if (i!=components.length-1)
				result.append(',');
		}
		return result.toString().toLowerCase();
	}
	
	/**
	 * Generate a MD5 unique primary key
	 * 
	 * @return
	 */
	public String getPrimaryKey() {
		return MD5.compute(getPath());
	}
	
	/**
	 * Return the name
	 * 
	 * @return
	 */
	public String getName() {
		return toString(name);
	}
	
	/**
	 * Set the name
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
}
