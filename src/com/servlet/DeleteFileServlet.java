package com.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.constants.Constant;
import com.dbtool.DBTool;

/**
 * Servlet implementation class DeleteFileServlet
 */
public class DeleteFileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Logger log=Logger.getLogger(DeleteFileServlet.class);
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DeleteFileServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String changeLogFile = request.getParameter("changeLogFile");
		if((null==changeLogFile || changeLogFile.isEmpty()) ){
			log.debug("required parameter changeLogFile is not set");
			request.setAttribute(Constant.MESSAGE, "required parameter changeLogFile is not set");
			request.getRequestDispatcher("index.jsp").forward(request, response);
			return;
		}
		DBTool dbtool = new DBTool();
		try {
			dbtool.deleteFile(changeLogFile);
			request.setAttribute(Constant.MESSAGE, changeLogFile+" file deleted successfully");
			request.getRequestDispatcher("history").forward(request, response);
		} catch (Exception e) {
			request.setAttribute(Constant.MESSAGE, e.getMessage());
		}	
	}
	

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
