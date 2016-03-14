package com.Utility;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.log4j.Logger;

import com.westgroup.novus.configmon.data.ldap.NamingIterator;
import com.westgroup.novus.configmon.data.ldap.datastore.Datastore;

public class LdapUtility {
	static Logger log = Logger.getLogger("LdapUtility");

	public static Datastore getDatabaseDetails(String ldapUrl, String baseDn, String resource, boolean isBase)
			throws Exception {
		// Set up the environment for creating the initial context
		DirContext ctx = null;
		Hashtable<String, String> map = new Hashtable<String, String>();
		map.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");

		Datastore datastore = new Datastore();
		try {
			map.put(Context.PROVIDER_URL, ldapUrl);
			// Create initial context
			ctx = new InitialDirContext(map);
			ctx = (DirContext) ctx.lookup(baseDn);

			Attributes matchAttrs = new BasicAttributes(true); // ignore case
			matchAttrs.put(new BasicAttribute("objectclass", "wgDataStore"));

			SearchControls controls = new SearchControls();
			controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			NamingEnumeration<SearchResult> namingEnum = ctx.search("", "(CN=" + resource + ")", controls);

			String actualSubDN = null;
			if (namingEnum.hasMore()) {
				SearchResult sr =  namingEnum.next();
				actualSubDN = sr.getName();
			}

			namingEnum = ctx.search(actualSubDN, matchAttrs);
			boolean ok=false;
			boolean found=false;
			while (namingEnum.hasMore()) {
				SearchResult sr = namingEnum.next();
				if(ok)break;
				if (sr.getName() != null &&( sr.getName().toLowerCase().indexOf("master.r") != -1 || sr.getName().toLowerCase().indexOf("master.u") != -1)) { //it cover the scenerio when no master.r (e.g. prism)
					found = true;
					String connectionUrl = null;

					NamingIterator<String> ids = new NamingIterator<String>(sr.getAttributes().getIDs());
					
					
					
					for (String name : ids) {
						Attribute attr = sr.getAttributes().get(name);
						String value = toString(attr.get());
						
						if (name.equalsIgnoreCase("cn"))
							datastore.setName(value);
						else if (name.equalsIgnoreCase("wgdatabasehost"))
							datastore.setDatabaseHost(value);
						else if (name.equalsIgnoreCase("wgdatastoretype")) {
							if (value != null && value.contains("ORA"))
								datastore.setJdbcDriverName("oracle.jdbc.driver.OracleDriver");
							datastore.setDatastoreType(value);
						} else if (name.equalsIgnoreCase("wgjdbcconnectionurl")) {
							if (connectionUrl == null)
								connectionUrl = value;
						} else if (name.equalsIgnoreCase("wgjdbcdrivername"))
							datastore.setJdbcDriverName(value);
						else if (name.equalsIgnoreCase("wgjdbcconnurl2"))// Added
																			// because
																			// of
																			// schema
																			// length
																			// restriction
							connectionUrl = value;
						else if (name.equalsIgnoreCase("wgpassword"))
							datastore.setPassword(value);
						else if (name.equalsIgnoreCase("wgpasswordencrypted"))
							datastore.setPasswordEncrypted(value);
						else if (name.equalsIgnoreCase("wgreadonly"))
							datastore.setReadOnly(value.equalsIgnoreCase("true"));
						else if (name.equalsIgnoreCase("wgresourcename")) {
							datastore.setResourceName(value);
							if (name.equalsIgnoreCase("wgresourcename") && value.equalsIgnoreCase(resource) ) {
								ok=true;	//exaclty matching resource what is passed
							}
						} else if (name.equalsIgnoreCase("wguserid"))
							datastore.setUserId(value);
						else if (name.equalsIgnoreCase("modifyTimestamp"))
							datastore.setModified(value);
						else if (name.equalsIgnoreCase("createTimestamp"))
							datastore.setCreated(value);
						else if (name.equalsIgnoreCase("wgDataSourceAlias2"))
							connectionUrl = value;

					}
					datastore.setConnectionUrl(connectionUrl);
				}

			}
			if(!found){throw new Exception("no match found for the resource");}
		} catch (Exception e) {
			log.error("Error when perform Ldap Operation : could not get DB information for master.r and wgresourcename=" + resource + " from "+ldapUrl+baseDn+"  Exception Message="
					+ e.getMessage());
			throw new Exception("Error when perform Ldap Operation :  could not get DB information for master.r and wgresourcename=" + resource + " from "+ldapUrl+baseDn+" Exception Message="
					+ e.getMessage());
		} finally {
			if (ctx != null)
				try {
					ctx.close();
				} catch (NamingException e) {
					log.error("Error in closing Ldap Connection : Resource=" + resource + " Exception Message="
							+ e.getMessage());
				}
		}
		return datastore;
	}

	/**
	 * Convert an object to its string representation
	 * 
	 * @param obj
	 *            Source object
	 * @return A normalized string representation of an object
	 */
	private static String toString(Object obj) {
		return obj == null ? "" : obj.toString().trim();
	}

	public static void main(String[] args) throws Exception {
		Datastore dt = LdapUtility.getDatabaseDetails("ldap://nibc1-infra.amers1.cis.trcloud.com:389/",
				"CN=resources,cn=NIB,ou=NOVUS,o=WESTGROUP.COM", "loadqueue", false);
		System.out.println(dt);

	}
}
