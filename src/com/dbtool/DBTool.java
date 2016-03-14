package com.dbtool;

import java.io.File;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.diff.output.DiffOutputControl;
import liquibase.integration.commandline.CommandLineUtils;
import liquibase.resource.FileSystemResourceAccessor;

import org.apache.log4j.Logger;

import com.Utility.LiquiBaseUtils;
import com.constants.Constant;
import com.zookeeper.ZkUtils;
import com.zookeeper.ZkUtilsImpl;

public class DBTool {
	Logger log = Logger.getLogger(DBTool.class);

	ZkUtils zk = ZkUtilsImpl.getInstance();

	public String generateChangelog(String base, String targetNib, String resource, String schema, String platform)
			throws Exception {
		Database baseDB = null;
		Database targetDB = null;
		if (base.toLowerCase().contains("nib")) {
			String baseJdbcURL = zk.getOracleJdbcUrl(base);
			baseDB = LiquiBaseUtils.getNovusDatabase(baseJdbcURL, schema, true); // true to force read only on base DB
		} else {
			String baseDn = null;
			String ldapUrl = null;
			try {
				// Search for objects that have those matching attributes
				if (platform.equalsIgnoreCase("prism")) {
					baseDn = "CN=resources,CN=NIB,OU=PRISM,O=TLRG.COM";
					if (base.equalsIgnoreCase("client")) {
						baseDn = "CN=resources,CN=CLIENT,OU=PRISM,O=TLRG.COM";
						ldapUrl = "ldap://ldapclient.westlan.com:389/";
					}
					if (base.equalsIgnoreCase("prod")) {
						baseDn = "CN=resources,CN=CLIENT,OU=PRISM,O=TLRG.COM";
						ldapUrl = "ldap://ldap:389/";
					}
				} else {
					baseDn = "CN=resources,cn=NIB,ou=NOVUS,o=WESTGROUP.COM";
					if (base.equalsIgnoreCase("client")) {
						baseDn = "CN=resources,CN=CLIENT,OU=NOVUS,O=WESTGROUP.COM";
						ldapUrl = "ldap://ldapclient.westlan.com:389/";
					}
					if (base.equalsIgnoreCase("prod")) {
						baseDn = "CN=resources,CN=CLIENT,OU=NOVUS,O=WESTGROUP.COM";
						ldapUrl = "ldap://ldap:389/";
					}
				}
			} catch (Exception e) {
				throw new RuntimeException("Not able to connect LDAP" + " ldapUrl " + e.getMessage() + " From: "
						+ this.getClass());
			}
			baseDB = LiquiBaseUtils.getNovusDatabase(ldapUrl, baseDn, resource, schema, true); //// true to force read only on base DB
		}
		String targetNibJdbcURL = zk.getOracleJdbcUrl(targetNib); /*
																 * assuming
																 * target is
																 * always nib
																 * and have
																 * prism and
																 * novus schema
																 * only on
																 * single
																 * database
																 */
		targetDB = LiquiBaseUtils.getNovusDatabase(targetNibJdbcURL, schema, false);

		DiffOutputControl diffOutputControl = new DiffOutputControl();
		String snapShotType = " ";
		String changeLogFile = Constant.CHANGELOG_FILE_DIR + platform.toUpperCase() + "_" + base.toUpperCase() + "_to_"
				+ targetNib.toUpperCase() + "_" + schema.toUpperCase() + "_" + Constant.CHANGELOG_FILE_SUFFIX;
		try {
			CommandLineUtils.doDiffToChangeLog(changeLogFile, baseDB, targetDB, diffOutputControl, snapShotType);
		} catch (Exception e) {
			throw e;
		} finally {
			targetDB.close();
			baseDB.close();
		}
		return changeLogFile;
	}

	public void applyChangeLog(String changeLogFile, String targetNib, String schema, String platform) throws Exception {
		FileSystemResourceAccessor fileOpener = new FileSystemResourceAccessor();
		String targetNibJdbcURL = zk.getOracleJdbcUrl(targetNib);
		Database targetDB = LiquiBaseUtils.getNovusDatabase(targetNibJdbcURL, schema, false);
		log.debug("from applyChangeLog:=" + targetDB + " changeLogFile=" + changeLogFile);
		Liquibase lbase = new Liquibase(Constant.CHANGELOG_FILE_DIR + changeLogFile, fileOpener, targetDB);
		lbase.update(changeLogFile); //here changeLogFile is a context name
	}

	public void deleteFile(String changeLogFile) throws Exception {
		File file = new File(Constant.CHANGELOG_FILE_DIR + changeLogFile);
		if (file.exists()) {
			file.delete();
			log.debug("from deleteFile: " + file.getName() + " has been deleted");
		}
	}
}
