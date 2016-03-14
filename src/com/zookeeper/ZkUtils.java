package com.zookeeper;

import java.util.HashMap;
import java.util.List;

public interface ZkUtils {

	public List<String> listAllEnvs();

	public List<String> listAllNIBS();

	public List<String> listCloudNibs();

	public List<String> listNibNodes(String strNibName);
	
	public List<String> listNibSchemas(String strNibName);

	public List<String> listExcludeNodes(String strNibName);

	public List<String> listIncludeNodes(String strNibName);

	public HashMap<String, String> getNibMetaData(String strNibName);

	public HashMap<String, String> getNodeMetaData(String nibName, String nodeName);

	public String getData(String nodePath);

	public List<String> getChildren(String nodePath);

	public HashMap<String, HashMap<String, String>> dbServersInfo(String strNibName);

	public boolean isCloudEnv(String strNibName);

	public List<String> getNIbHostsForServiceType(String nibName, String serviceType);

	public String getNIbHostProperty(String nibName, String nodeName, String propertyKey);

	public String getNIbHostPropertyForServiceType(String nibName, String serviceType, String propertyKey);

	public HashMap<String, HashMap<String, String>> getNIbHostsPropertiesForServiceType(String nibName, String serviceType);

	public String getNovusLdapHostName(String nibName);

	public HashMap<String, String> getNovusLdapHostProperties(String mNib);

	public String getPrismLdapHostName(String nibName);

	public HashMap<String, String> getPrismLdapHostProperties(String mNib);

	public HashMap<String, String> getLoadHostProperties(String mNib);

	public String getLoadHostName(String mNib);

	public HashMap<String, String> getOracleHostProperties(String mNib);

	public String getOracleHostName(String mNib);

	public String getOracleJdbcUrl(String mNib);

	public String getNovusLdapUrl(String nibName);

	public String getMqUrl(String nibName);

	public List<String> convertCsvToList(String csv);

	final String PROP_KEY_NAME = "name";
	final String PROP_KEY_LOAD = "load";
	final String PROP_KEY_ORACLE = "oracle";
	final String PROP_KEY_LDAP_NOVUS = "ldap:novus";
	final String PROP_KEY_LDAP_NOVUS_URL = "ldap:novusurl"; // ldap:novusurl
	final String PROP_KEY_LDAP_PRISM = "ldap:prism";
	final String PROP_KEY_JDBCURL = "jdbcurl";
	final String PROP_KEY_IS_CLOUD = "iscloud";
	final String PROP_KEY_SCHEMAS = "schemas";

	final String DEFAULT_PROP_DELIMITER = ";";
	final String DEFAULT_PROP_SEPERATOR = "=";
	final String DEFAULT_DATA_SEPERATOR = ",";

	final String SERVICE_TYPE_LDAP_NOVUS = "ldap:novus";
	final String SERVICE_TYPE_ORACLE = "oracle";
	final String SERVICE_TYPE_MQ = "mq";
	final String PROP_KEY_MQURL = "mqurl"; 

}
