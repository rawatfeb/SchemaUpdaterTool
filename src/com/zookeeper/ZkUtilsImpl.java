package com.zookeeper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooKeeper.States;
import org.apache.zookeeper.data.Stat;

import edu.emory.mathcs.backport.java.util.Arrays;

public class ZkUtilsImpl implements ZkUtils {

	private static Logger log = Logger.getLogger("webtool");

	private static ZkUtils instance = null;
	private ZooKeeper zooKeeper;
	java.util.concurrent.CountDownLatch connectedSignal = new java.util.concurrent.CountDownLatch(1);

	private final String PATH_SEPARATOR = "/";
	private final String PLACE_HOLDER_NIBNAME = "<nibName>";
	private final String PLACE_HOLDER_NODENAME = "<nodeName>";

	private final String NIB_ROOT = "/NIB";
	private final String NODE_ENV = "/ENV";
	private final String NODE_NIB_PATH = "/NIB/<nibName>";
	private final String NODE_NODES = "/NIB/<nibName>/NODES";
	private final String NODE_NODE_PATH = "/NIB/<nibName>/NODES/<nodeName>";
	private final String NODE_EXCLUDE = "/NIB/<nibName>/exclude";
	private final String NODE_INCLUDE = "/NIB/<nibName>/include";

	private static final String ZK_HOSTS = "swat-tools-a.westlan.com:2181,swat-tools-b.westlan.com:2181,";

	private static final int SESSION_TIME_OUT = 50000;


	/**
	 * Private constructor as it is singleton object
	 * 
	 * @
	 */
	private ZkUtilsImpl()  {
		init();
	}

	/**
	 * Will returns default zookeeper instance pointing to "swat-tools-a.westlan.com:2181,swat-tools-b.westlan.com:2181"
	 * 
	 * @return zookeeper instance
	 * 
	 * @
	 */
	public synchronized static final ZkUtils getInstance()  {
		if (null == instance) {
			instance = new ZkUtilsImpl();
		}
		return instance;
	}

	/**
	 * Will returns default zookeeper instance pointing to "swat-tools-a.westlan.com:2181,swat-tools-b.westlan.com:2181" with default session timeout 5000 msec
	 * 
	 * @param strNibName
	 *            Nib name under the Znode "/NIB"
	 * @return zookeeper instance pointing to given nib node
	 * @
	 */

	private synchronized boolean init() {
		try {
			zooKeeper = new ZooKeeper(ZK_HOSTS, SESSION_TIME_OUT, new Watcher() {
				@Override
				public void process(WatchedEvent event) {
					if (event.getState() == KeeperState.SyncConnected) {
						connectedSignal.countDown();
					}
				}

			});
			zooKeeper.addAuthInfo("digest", "nibr:nibr".getBytes());
			log.debug("***** ZooKeeper insace has been created!*****");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return zooKeeper.getState().isAlive();
	}

	/**
	 * Reads the data from z node /ENV which is delimited with ";"
	 */
	@Override
	public List<String> listAllEnvs() {
		String strData = getData(NODE_ENV);
		return convertDelimitedDataToList(strData, DEFAULT_PROP_DELIMITER);
	}

	/**
	 * Reads the data from z node /NIB which is delimited with ";"
	 */
	@Override
	public List<String> listAllNIBS() {
		return getChildren(NIB_ROOT);
	}

	/**
	 * Reads the data from z node /NIB which is delimited with ";" and select only iscloud=true nodes
	 */
	@Override
	public List<String> listCloudNibs()  {
		List<String> allNibs = listAllNIBS();
		ArrayList<String> cloudNibs = new ArrayList<String>();
		for (String node : allNibs) {
			HashMap<String, String> props = getMetaData(NIB_ROOT + PATH_SEPARATOR + node);
			if (null != props.get(PROP_KEY_IS_CLOUD) && Boolean.parseBoolean(props.get(PROP_KEY_IS_CLOUD))) {
				cloudNibs.add(node);
			}
		}
		return cloudNibs;
	}

	/**
	 * Reads the data from z node /NIB/<nibName>/NODES which is delimited with ";"
	 */
	@Override
	public List<String> listNibNodes(String strNibName)  {
		String nodePath = NODE_NODES.replace(PLACE_HOLDER_NIBNAME, strNibName);
		List<String> list = getChildren(nodePath);
		return list;
	}

	/**
	 * Reads the data from z node "/NIB/<nibName>/exclude" which is delimited with ","
	 */
	@Override
	public List<String> listExcludeNodes(String strNibName)  {
		String nodePath = NODE_EXCLUDE.replace(PLACE_HOLDER_NIBNAME, strNibName);
		String data = getData(nodePath);
		return convertCsvToList(data);
	}

	/**
	 * Reads the data from z node "/NIB/<nibName>/include" which is delimited with ","
	 */
	@Override
	public List<String> listIncludeNodes(String strNibName)  {
		String nodePath = NODE_INCLUDE.replace(PLACE_HOLDER_NIBNAME, strNibName);
		String data = getData(nodePath);
		return convertCsvToList(data);
	}

	/**
	 * Reads the data from z node "/NIB/<nibName>" which is delimited with ";"
	 */
	@Override
	public HashMap<String, String> getNibMetaData(String strNibName)  {
		String nodePath = NIB_ROOT + PATH_SEPARATOR + strNibName;
		return getMetaData(nodePath);
	}

	/**
	 * Reads the data from z node "/NIB/<nibName>/NODES/<nodeName>" which is delimited with ";"
	 */
	@Override
	public HashMap<String, String> getNodeMetaData(String strNibName, String NodeName)  {
		String nodePath = NODE_NODE_PATH.replace(PLACE_HOLDER_NIBNAME, strNibName).replace(PLACE_HOLDER_NODENAME, NodeName);
		return getMetaData(nodePath);
	}

	public List<String> convertCsvToList(String csv) {
		return convertDelimitedDataToList(csv, DEFAULT_DATA_SEPERATOR);
	}

	@SuppressWarnings("unchecked")
	public List<String> convertDelimitedDataToList(String strData, String dataSeparator) {
		List<String> result = new ArrayList<String>();
		if (null != strData) {
			String[] csvArray = strData.split(dataSeparator);
			if (null != csvArray && csvArray.length > 0) {
				result.addAll(Arrays.asList(csvArray));
			}
		}
		return result;
	}

	/**
	 * Reads the data from z node "/NIB/<nibName>/NODES/<nodeName>" which is delimited with ";"
	 */
	public HashMap<String, String> getMetaData(String nodePath)  {
		String data = getData(nodePath);
		HashMap<String, String> props = convertDelimitedDataToMap(data, DEFAULT_PROP_DELIMITER);
		return props;
	}

	public HashMap<String, String> convertDelimitedDataToMap(String strData, String dataSeparator) {
		HashMap<String, String> resultMap = new HashMap<String, String>();
		@SuppressWarnings("unchecked")
		List<String> list = Arrays.asList(strData.split(dataSeparator));
		for (String prop : list) {
			String[] strings = prop.split(DEFAULT_PROP_SEPERATOR);
			if (null != strings && strings.length > 1) {
				resultMap.put(strings[0].trim().toLowerCase(), strings[1].trim().toLowerCase());
			}
		}
		return resultMap;
	}

	// @SuppressWarnings("unchecked")
	// private List<String> convertDataToList(String data) {
	// List<String> result = new ArrayList<String>();
	// if (null != data) {
	// String[] dataArray = data.split(DEFAULT_DATA_SEPERATOR);
	// if (null != dataArray && dataArray.length > 0) {
	// result.addAll(Arrays.asList(dataArray));
	// }
	// }
	// return result;
	// }

	@Override
	public HashMap<String, HashMap<String, String>> dbServersInfo(String strNibName)  {
		return getNIbHostsPropertiesForServiceType(strNibName, SERVICE_TYPE_ORACLE);
	}

	@Override
	public boolean isCloudEnv(String nibName) {
		boolean flag = false;
		HashMap<String, String> props = getMetaData(NODE_NIB_PATH.replace(PLACE_HOLDER_NIBNAME, nibName));
		flag = Boolean.valueOf(props.get(PROP_KEY_IS_CLOUD));
		return flag;
	}

/*	@Override
	public HashMap<String, Map<String, String>> allNibsInfo()  {
		log.debug("*** allNibsInfo() - Start");
		Date start = new Date();
		String nodePath = NIB_ROOT;
		HashMap<String, Map<String, String>> result = new HashMap<String, Map<String, String>>();
		List<String> children = getChildren(nodePath);
		if (null != children & !children.isEmpty()) {
			for (String child : children) {
				HashMap<String, String> props = getMetaData(nodePath + PATH_SEPARATOR + child);
				result.put(child, props);
			}
		}
		log.debug("*** allNibsInfo() - End \n \t\t\t Time elapsed (msec) : "+(new Date().getTime()-start.getTime()));
		return result;
	}*/

	@Override
	public String getNIbHostPropertyForServiceType(String nibName, String serviceType, String propertyKey)  {
		String result = null;
		List<String> nodesList = getNIbHostsForServiceType(nibName, serviceType);
		log.debug(serviceType + " type nodes in NIB " + nibName + " : " + nodesList);
		for (String nodeName : nodesList) {
			HashMap<String, String> nodeMetaData = getNodeMetaData(nibName, nodeName);
			log.debug(nodeName + " Metadata : " + nodeMetaData);
			result = nodeMetaData.get(propertyKey.toLowerCase());
			if (null != result) {
				log.debug("Property " + propertyKey + " find in node metadata and returning the value.");
				break;
			} else {
				log.debug("Property " + propertyKey + " not find in node metadata ");
			}
		}
		return result;
	}

	@Override
	public List<String> getNIbHostsForServiceType(String nibName, String serviceType) {
		List<String> resultList = null;
		String nodePath = NODE_NODES.replace(PLACE_HOLDER_NIBNAME, nibName.toUpperCase());
		HashMap<String, String> dataMap = getMetaData(nodePath);
		String serviceInfo = dataMap.get(serviceType.toLowerCase());
		resultList = convertCsvToList(serviceInfo);
		return resultList;
	}

	@Override
	public HashMap<String, HashMap<String, String>> getNIbHostsPropertiesForServiceType(String nibName, String serviceType)  {
		List<String> nodesList = getNIbHostsForServiceType(nibName, serviceType);
		log.debug(serviceType + " type nodes in NIB " + nibName + " : " + nodesList);
		HashMap<String, HashMap<String, String>> result = new HashMap<String, HashMap<String, String>>();
		for (String nodeName : nodesList) {
			HashMap<String, String> nodeMetaData = getNodeMetaData(nibName, nodeName);
			log.debug(nodeName + " Metadata : " + nodeMetaData);
			result.put(nodeName, nodeMetaData);
		}
		return result;
	}

	@Override
	public String getNIbHostProperty(String nibName, String nodeName, String propertyKey)  {
		HashMap<String, String> nodeMetaData = getNodeMetaData(nibName, nodeName);
		return nodeMetaData.get(propertyKey);
	}

	public static void main(String[] args)  {
		ZkUtilsImpl obj = new ZkUtilsImpl();
		String serviceType = "oracle";
		String nibName = "NIBYNR";
		
		List<String> out = obj.listNibSchemas(nibName);
		
		System.out.println(out);
		
		
		
		// String csv = "oradbslp,oracle2,";
		// String strData = "iscloud=true;name=nibynr;projectId=696f16d1-5ab8-4762-9d55-ff804f88d471;dnsName=.amers1.ciscloud;agent=agent.nib.1;";
		// System.out.println(obj.convertCsvToList(csv));
		// System.out.println(obj.convertDelimitedDataToMap(strData, ZkUtilsImpl.DEFAULT_PROP_DELIMITER));
		// System.out.println("\tNIBYNR MetaData -> " + obj.getNibMetaData(nibName));
		// List<String> list = obj.getNIbHostsForServiceType(nibName, "oracle");
		// System.out.println("\tNIBYNR + oracle = " + list);
		// List<String> list1 = obj.getNIbHostsForServiceType(nibName.toLowerCase(), serviceType);
		// System.out.println("\t" + nibName.toLowerCase() + " " + serviceType + " = " + list1);
		// for (String node : list1) {
		// System.out.println("\tnibynr /" + serviceType + " /" + node + " -> " + obj.getNodeMetaData(nibName, node));
		// }
		// System.out.println(obj.listAllEnvs());
		// System.out.println(obj.listAllNIBS());
		// System.out.println(obj.listCloudNibs());
		/*System.out.println(obj.listExcludeNodes(nibName));
		System.out.println(obj.listIncludeNodes(nibName));
		System.out.println(obj.listIncludeNodes(nibName));
		System.out.println(obj.getData("/NIB/NIBYNR/NODES/nibynr-oracle"));
		System.out.println(obj.getChildren("/NIB/NIBYNR/NODES"));
		System.out.println(obj.isCloudEnv(nibName));
		System.out.println(obj.getNIbHostPropertyForServiceType(nibName, serviceType, "jdbcurl"));
		System.out.println(obj.getNIbHostsPropertiesForServiceType(nibName, serviceType));*/
		//System.out.println(obj.allNibsInfo());

	}

	@Override
	public String getNovusLdapHostName(String nibName)  {
		String result = null;
		HashMap<String, HashMap<String, String>> map = getNIbHostsPropertiesForServiceType(nibName, PROP_KEY_LDAP_NOVUS);
		if (!map.isEmpty()) {
			result = map.entrySet().iterator().next().getValue().get(PROP_KEY_NAME);
			log.debug("nibName = " + nibName + "\tpropKey = ldap:novus \t zk result = " + result);
		} else {
			log.warn("No data found in Zookeeper for " + "nibName = " + nibName + "\tpropKey = ldap:novus \t zk result = " + result);
		}
		return result;
	}
	
	@Override
	public HashMap<String, String> getNovusLdapHostProperties(String mNib)  {
		HashMap<String, HashMap<String, String>> map = getNIbHostsPropertiesForServiceType(mNib, PROP_KEY_LDAP_NOVUS);
		HashMap<String, String> result = null;
		if (!map.isEmpty()) {
			result = map.entrySet().iterator().next().getValue();
		}
		return result;
	}

	@Override
	public String getPrismLdapHostName(String nibName)  {
		HashMap<String, HashMap<String, String>> map = getNIbHostsPropertiesForServiceType(nibName, PROP_KEY_LDAP_PRISM);
		String result = null;
		if (!map.isEmpty()) {
			result = map.entrySet().iterator().next().getValue().get("name");
			log.info("nibName = " + nibName + "\tpropKey = ldap:novus \t zk result = " + result);
		} else {
			log.warn("No data found in Zookeeper for " + "nibName = " + nibName + "\tpropKey = ldap:prism \t zk result = " + result);
		}
		return result;
	}

	@Override
	public HashMap<String, String> getPrismLdapHostProperties(String mNib)  {
		HashMap<String, HashMap<String, String>> map = getNIbHostsPropertiesForServiceType(mNib, PROP_KEY_LDAP_PRISM);
		HashMap<String, String> result = null;
		if (!map.isEmpty()) {
			result = map.entrySet().iterator().next().getValue();
		}
		return result;
	}

	@Override
	public String getLoadHostName(String mNib) {
		String result = null;
		result = getLoadHostProperties(mNib).get(PROP_KEY_NAME);
		return result;
	}

	@Override
	public HashMap<String, String> getLoadHostProperties(String mNib)  {
		HashMap<String, HashMap<String, String>> map = getNIbHostsPropertiesForServiceType(mNib, PROP_KEY_LOAD);
		HashMap<String, String> result = null;
		if (!map.isEmpty()) {
			result = map.entrySet().iterator().next().getValue();
		}
		log.debug(mNib + " - LoadHostProperties -> "+result);
		return result;
	}
	
	@Override
	public String getOracleHostName(String mNib) {
		String result = null;
		result = getOracleHostProperties(mNib).get(PROP_KEY_NAME);
		return result;
	}

	@Override
	public HashMap<String, String> getOracleHostProperties(String mNib)  {
		HashMap<String, HashMap<String, String>> map = getNIbHostsPropertiesForServiceType(mNib, PROP_KEY_ORACLE);
		HashMap<String, String> result = null;
		if (!map.isEmpty()) {
			result = map.entrySet().iterator().next().getValue();
		}
		return result;
	}

	@Override
	public String getOracleJdbcUrl(String nibName) {
		return getNIbHostPropertyForServiceType(nibName, SERVICE_TYPE_ORACLE, PROP_KEY_JDBCURL) ;
	}
	
	@Override
	public String getNovusLdapUrl(String nibName) {
		return getNIbHostPropertyForServiceType(nibName, SERVICE_TYPE_LDAP_NOVUS, PROP_KEY_LDAP_NOVUS_URL) ;
	}
	
	@Override
	public String getMqUrl(String nibName) {
		return getNIbHostPropertyForServiceType(nibName, SERVICE_TYPE_MQ, PROP_KEY_MQURL) ;
	}


	// @SuppressWarnings("unchecked")
		// private List<String> convertDataToList(String data) {
		// List<String> result = new ArrayList<String>();
		// if (null != data) {
		// String[] dataArray = data.split(DEFAULT_DATA_SEPERATOR);
		// if (null != dataArray && dataArray.length > 0) {
		// result.addAll(Arrays.asList(dataArray));
		// }
		// }
		// return result;
		// }
	
		// @SuppressWarnings("unchecked")
	// private List<String> convertDataToList(String data) {
	// List<String> result = new ArrayList<String>();
	// if (null != data) {
	// String[] dataArray = data.split(DEFAULT_DATA_SEPERATOR);
	// if (null != dataArray && dataArray.length > 0) {
	// result.addAll(Arrays.asList(dataArray));
	// }
	// }
	// return result;
	// }
	
	private synchronized boolean checkSessionAlive() {
		States states = zooKeeper.getState();
		log.debug("ZK Status : " + states.toString());
		if (!states.isAlive()) {
			log.warn("ZK connection NOT alive : " + states.toString());
			log.warn("Trying to RECONNECT....");
			zooKeeper = null;
			init();
		}
		return zooKeeper.getState().isAlive();
	}

	/**
	 * Reads the data from z node "/NIB/<nibName>/NODES/<nodeName>"
	 */
	@Override
	public String getData(String nodePath) {
		String result = null;
		try {
			checkSessionAlive();
			result = "";
			byte[] data = zooKeeper.getData(nodePath, false, new Stat());
			if (null != data) {
				result = new String(data);
			}
		} catch (Exception e) {
			log.warn("Exception: While reading data from ZK! " + e);
			// e.printStackTrace();
			throw new RuntimeException(e);
		}
		return result;
	}

	@Override
	public List<String> getChildren(String nodePath) {
		List<String> children = null;
		try {
			checkSessionAlive();
			children = zooKeeper.getChildren(nodePath, false);
		} catch (Exception e) {
			log.error("Exception - while listing childrens of ZK node : " + nodePath);
			log.error(e);
			throw new RuntimeException(e);
		}
		return children;
	}

	@Override
	public List<String> listNibSchemas(String strNibName) {
		 HashMap<String, HashMap<String, String>> map = getNIbHostsPropertiesForServiceType(strNibName, PROP_KEY_ORACLE);
		 StringBuffer schemas= new StringBuffer();
		 if (!map.isEmpty()) {
//				schemas = map.entrySet().iterator().next().getValue().get(PROP_KEY_SCHEMAS);
			 for (String dbVm : map.keySet()) {
				 String tmpSchamaCsv = map.get(dbVm).get(PROP_KEY_SCHEMAS);
				 if (null!=tmpSchamaCsv) {
					 schemas.append(tmpSchamaCsv).append(",");
				}
			}
			log.debug("nibName = " + strNibName + "\tpropKey = ldap:novus \t zk result = " + schemas);
			} else {
				log.warn("No data found in Zookeeper for " + "nibName = " + strNibName + "\tpropKey = ldap:novus \t zk result = " + schemas);
			}
		 return Arrays.asList(schemas.toString().split(","));
	}

}
