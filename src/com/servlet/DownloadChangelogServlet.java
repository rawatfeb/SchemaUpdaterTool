package com.servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.constants.Constant;

/**
 * Servlet implementation class DownloadChangelogServlet
 */
public class DownloadChangelogServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Logger log=Logger.getLogger(DownloadChangelogServlet.class);
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DownloadChangelogServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String changeLogFile = request.getParameter("changeLogFile");
		if(null==changeLogFile || changeLogFile.isEmpty()){
			request.setAttribute(Constant.MESSAGE, "changelog file name was empty");
			request.getRequestDispatcher("index.jsp").forward(request, response);
			return;
		}
		response.setContentType("application/force-download");
		response.setHeader("Content-Disposition", "attachment; filename=\""+changeLogFile);
		PrintWriter writer = response.getWriter();		
		File file = new File(Constant.CHANGELOG_FILE_DIR+changeLogFile);
		if (!file.exists()) {
			request.setAttribute(Constant.MESSAGE, "bad changelog file name . file does not exist on system");
			request.getRequestDispatcher("index.jsp").forward(request, response);
			return;
		}
		
		BufferedReader br =  new BufferedReader(new InputStreamReader( new FileInputStream(file)));
		String userInput;
		while ((userInput = br.readLine()) != null) {
			writer.println(userInput);
		}
		br.close();
		log.debug(changeLogFile+" changeLogFile file downloaded successfully");
		request.setAttribute(Constant.MESSAGE, "downloaded successfully");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
