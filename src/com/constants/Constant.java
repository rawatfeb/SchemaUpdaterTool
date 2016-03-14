package com.constants;

import java.io.File;

public interface Constant {

	public static final String LIST_SCHEMAS_ACTION = "listSchemas";
	public static final String GENERATE_CHANGELOG_ACTION = "GenerateChangeLog";
	public static final String APPLLY_CHANGELOG_ACTION = "ApplyChanges";
	public static final String DOWNLOAD_CHANGELOG_ACTION = "DownloadChangelog";
	public static final String CHANGELOG_FILE_DIR = File.separator+"tmp"+File.separator;
	public static final String HISTORY_ACTION="History";
	public static final String CHANGELOG_FILE_SUFFIX="ChangeLogFile.xml";
	public static final String MESSAGE="msg";
	public static final String DELETE_FILE = "Delete";
	public static final String EXP = "EXP";
	
}
