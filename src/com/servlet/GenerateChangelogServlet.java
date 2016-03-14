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
 * Servlet implementation class GenerateChangelogServlet
 */
public class GenerateChangelogServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Logger log = Logger.getLogger(GenerateChangelogServlet.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GenerateChangelogServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String baseEnvironment = request.getParameter("baseNib");
		String targetNib = request.getParameter("targetNib");
		String platform = request.getParameter("platform");
		String recipients = request.getParameter("email");

		if (null == baseEnvironment || baseEnvironment.isEmpty() || baseEnvironment.equalsIgnoreCase("dummy")) {
			request.setAttribute(Constant.MESSAGE, "Please select the base environment");
			request.getRequestDispatcher("index.jsp").forward(request, response);
			return;
		}
		if (null == targetNib || targetNib.isEmpty() || targetNib.equalsIgnoreCase("dummy")) {
			request.setAttribute(Constant.MESSAGE, "Please select the both base and target environment");
			request.getRequestDispatcher("index.jsp").forward(request, response);
			return;
		}
		String schema = request.getParameter("schema");
		if (null == schema || schema.isEmpty() || schema.equalsIgnoreCase("dummy")) {
			request.setAttribute(Constant.MESSAGE, "Schema is not set to compare");
			request.getRequestDispatcher("index.jsp").forward(request, response);
			return;
		}

		String resource = request.getParameter("resource");

		log.debug("generate changelog request has been made with base environment=" + baseEnvironment + " targetNib="
				+ targetNib + " platform= " + platform + " schema= " + schema);

		DBTool dbtool = new DBTool();
		boolean doMail = true;
		String[] emails = null;
		if (null == recipients || recipients.isEmpty()) {
			log.debug("no email id input to send confirmation mail");
			doMail = false;
		}

		try {
			String changeLogFile = dbtool.generateChangelog(baseEnvironment, targetNib, resource, schema, platform);
			request.setAttribute(Constant.MESSAGE, changeLogFile + "   Changelog File has been generated");
			log.debug(changeLogFile + "   Changelog File has been generated");

			if (doMail) {
				try {
					emails = recipients.split(",");
					MailUtility.email("Changelog File Has Been Generated", " changeLogFile=" + changeLogFile
							+ " baseEnvironment=" + baseEnvironment + " targetNib=" + targetNib + " schema=" + schema
							+ " platform=" + platform, emails);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}

		} catch (Exception e) {
			log.debug(e.getMessage());
			request.setAttribute(Constant.MESSAGE,e.getMessage());
			if (doMail) {
				emails = recipients.split(",");
				try {
					MailUtility.email("Exception", e.getMessage(), emails);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}
		request.getRequestDispatcher("history").forward(request, response);
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
