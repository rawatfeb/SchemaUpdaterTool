package com.servlet;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.constants.Constant;

/**
 * Servlet implementation class HistoryServlet
 */
public class HistoryServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Logger log = Logger.getLogger(HistoryServlet.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public HistoryServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		StringBuffer changeLogFilesTable=new StringBuffer();
		String anyPrevMsg=(String)request.getAttribute(Constant.MESSAGE);
		log.debug("message from previous page:"+anyPrevMsg);
		if(null != anyPrevMsg && !anyPrevMsg.isEmpty()){
			changeLogFilesTable.append("<h4>"+anyPrevMsg+"</h4>");
			}
		changeLogFilesTable.append("<table id=\"t\" class=\"tablesorter\" border=\"1\" cellpadding=\"1\"cellspacing=\"1\" ><thead><tr><th>Base</th><th>Target</th><th>Schema</th><th>Platform</th><th>ChangeLogFile(Click To Download)</th><th>Last Modified</th><th>Target NIB</th><th>E Mail</th><th>Apply Change</th><th>Delete</th></tr></thead><tbody>");
		File dir = new File(Constant.CHANGELOG_FILE_DIR);
		 File[] files = dir.listFiles();
		// Obtain the array of (file, timestamp) pairs.
		 Pair[] pairs = new Pair[files.length];
		 for (int i = 0; i < files.length; i++)
		     pairs[i] = new Pair(files[i]);

		 // Sort them by timestamp.
		 Arrays.sort(pairs);

		 // Take the sorted pairs and extract only the file part, discarding the timestamp.
		 for (int i = 0; i < files.length; i++)
		     files[i] = pairs[i].f;
		 
		for (File file : files) {
			if(file.getName().endsWith(Constant.CHANGELOG_FILE_SUFFIX)){
				String fileName=file.getName();
				FileNameParserUtil parseFileNamedData = new FileNameParserUtil(fileName);
				changeLogFilesTable.append("<tr>");
				changeLogFilesTable.append("<form name=\"" + fileName+"\" action=\""+request.getContextPath()+"/home\" method=\"get\" >");
				changeLogFilesTable.append("<td><input type=\"hidden\" name=\"base\" id=\"base\" value=\""+ parseFileNamedData.getBase()+ "\">"+ parseFileNamedData.getBase()+ "</td>");
				changeLogFilesTable.append("<td><input type=\"hidden\" name=\"target\" id=\"target\" value=\""+ parseFileNamedData.getTarget()+ "\">"+ parseFileNamedData.getTarget()+ "</td>");
				changeLogFilesTable.append("<td><input type=\"hidden\" name=\"schema\" id=\"schema\" value=\""+ parseFileNamedData.getSchema()+ "\">"+parseFileNamedData.getSchema()+"</td>");
				changeLogFilesTable.append("<td><input type=\"hidden\" name=\"platform\" id=\"platform\" value=\""+ parseFileNamedData.getPlatform()+ "\">"+ parseFileNamedData.getPlatform()+ "</td>");
				changeLogFilesTable.append("<td><a href=\"download?&changeLogFile="+fileName+"\" >" + fileName+ "</a> <input type=\"hidden\" value=\""+ fileName+ "\" name=\"changeLogFile\" /></td>");
				changeLogFilesTable.append("<td>" +new Date(file.lastModified())+ "</td>");
				changeLogFilesTable.append("<td><input type=\"text\" name=\"targetEnv\" id=\"targetEnv\" value=\""+parseFileNamedData.getTarget()+"\"></td>");
				changeLogFilesTable.append("<td><input type=\"text\" name=\"email\" ID=\"email\"></input></td>");	
				changeLogFilesTable.append("<td><input type=\"submit\" name=\"action\" value=\"ApplyChanges\" onclick=\"form.action='applychangelog'\"></td>");
				changeLogFilesTable.append("<td><div><a href=\"deletefile?changeLogFile="+fileName+"\" >Delete</a></div></td>");
				changeLogFilesTable.append("</form>");
				changeLogFilesTable.append("</tr>");
			}
		}
		changeLogFilesTable.append("</tbody></table>");
		request.setAttribute(Constant.MESSAGE, changeLogFilesTable.toString());
		request.getRequestDispatcher("index.jsp").forward(request,response);
		}

	
	static class Pair implements Comparable {
	    public long t;
	    public File f;

	    public Pair(File file) {
	        f = file;
	        t = file.lastModified();
	    }

	    public int compareTo(Object o) {
	        long u = ((Pair) o).t;
	        return u < t ? -1 : t == u ? 0 : 1;
	    }
	}
	
	static class FileNameParserUtil {
		Logger log = Logger.getLogger(getClass());
		private String fileName = "";
		private int length = 0;
		static String[] parsedContent = null;

		public FileNameParserUtil(String fileName) {
			this.fileName = fileName;
			parsedContent = fileName.split("_");
			length = parsedContent.length;
			log.info(fileName + "    " + parsedContent.length);
		}

		// platform+"_"+base+"_to_"+targetNib+"_"+schema+"_"+Constant.CHANGELOG_FILE_SUFFIX;
		public String getPlatform() {
			if (length > 0)
				return parsedContent[0];
			return null;
		}

		public String getBase() {
			if (length > 1)
				return parsedContent[1];
			return null;
		}

		public String getTarget() {
			if (length > 2)
				return parsedContent[3];
			return null;
		}

		public String getSchema() {
			if (length > 3)
				return parsedContent[4];
			return null;
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		doGet(request, response);
	}

}
