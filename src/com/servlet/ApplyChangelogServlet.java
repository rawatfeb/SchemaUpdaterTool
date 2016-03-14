package com.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.Utility.MailUtility;
import com.constants.Constant;
import com.dbtool.DBTool;

/**
 * Servlet implementation class ApplyChangelogServlet
 */
public class ApplyChangelogServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Logger log = Logger.getLogger(ApplyChangelogServlet.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ApplyChangelogServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setAttribute(Constant.EXP, "false");
		String changeLogFile = request.getParameter("changeLogFile");
		String targetEnv = request.getParameter("targetEnv");
		String schema = request.getParameter("schema");
		String platform = request.getParameter("platform");
		String recipients = request.getParameter("email");
		if ((null == changeLogFile || changeLogFile.isEmpty()) || (null == targetEnv || targetEnv.isEmpty())
				|| (null == schema || schema.isEmpty()) || (null == platform || platform.isEmpty())) {
			request.setAttribute(Constant.MESSAGE, "required parameter is not set");
			request.getRequestDispatcher("index.jsp").forward(request, response);
			return;
		}
		DBTool dbtool = new DBTool();
		try {
			dbtool.applyChangeLog(changeLogFile, targetEnv, schema, platform);
		} catch (Exception e) {
			request.setAttribute(Constant.MESSAGE, e.getMessage());
			request.setAttribute(Constant.EXP, "true");
		}

		String[] emails = null;
		boolean isExceptionOccured = "true".equalsIgnoreCase((String) request.getAttribute(Constant.EXP));
		String message = (String) request.getAttribute(Constant.MESSAGE);
		doMail(recipients, targetEnv, schema, platform, emails, changeLogFile, isExceptionOccured, message);
		deleteChangeLogFile(request, response, dbtool, changeLogFile, isExceptionOccured,message);
		request.getRequestDispatcher("index.jsp").forward(request, response);
	}

	private void deleteChangeLogFile(HttpServletRequest request, HttpServletResponse response, DBTool dbtool,
			String changeLogFile, boolean isExceptionOccured, String message) throws ServletException, IOException {
		
			//delete changeLogFile
			if (isExceptionOccured) {
				request.setAttribute(Constant.MESSAGE,message);
			}else{
				try {
				dbtool.deleteFile(changeLogFile);
				request.setAttribute(Constant.MESSAGE, "Change log has been successfully applied");
				} catch (Exception ed) {
					request.setAttribute(Constant.MESSAGE,
							"change log has been successfully applied Exception while deleting the changelog " + changeLogFile
									+ " file");
				}
			}
		
	}

	private void doMail(String recipients, String targetEnv, String schema, String platform, String[] emails,
			String changeLogFile, boolean isExceptionOccured, String message) {
		try {
			if (null != recipients && !recipients.isEmpty()) {
				if (isExceptionOccured) {
					MailUtility.email("Exception during applying changelog", message, emails);
				} else {
					emails = recipients.split(",");
					MailUtility.email("change log has been successfully applied", " changeLogFile=" + changeLogFile
							+ " targetEnv=" + targetEnv + " schema=" + schema + " platform=" + platform, emails);
				}
			} else {
				log.debug("no email id input to send confirmation mail");
			}
		} catch (Exception e) {
			e.printStackTrace();
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
