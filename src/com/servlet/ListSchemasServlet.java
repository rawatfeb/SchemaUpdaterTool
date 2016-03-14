package com.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.constants.Constant;
import com.zookeeper.ZkUtils;
import com.zookeeper.ZkUtilsImpl;

/**
 * Servlet implementation class ListSchemasServlet
 */
public class ListSchemasServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Logger log = Logger.getLogger(ListSchemasServlet.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ListSchemasServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String env = request.getParameter("env");
		if (null == env || env.isEmpty() || "dummy".equalsIgnoreCase(env)) {
			log.debug("base environment is empty or not supported");
			return;
		} else {
			env=env.toUpperCase();
			if(env.contains("NIB")){
			ZkUtils zk = ZkUtilsImpl.getInstance();
			List<String> schemaList = null;
			try {
				schemaList = zk.listNibSchemas(env);
			} catch (Exception e) {
				log.debug("Exception while fetching Schema list from Zookeeper for env="+env +" "+e.getMessage());
				response.setStatus(1000);
				return;
			}
			response.setContentType("text/plain;charset=UTF-8");
			response.getWriter().write(schemaList.toString().trim());
			log.debug("Data:" + schemaList);
			return;
		}
			else{
				log.debug("how do you come here check javascript controlAjax function");
			}
			
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
