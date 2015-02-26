package com.theice.fileprocessor.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class StatusServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	
	private static final String PARAM_CLIENT="refresh";
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,IOException
	{
		
      if (request.getParameterMap().containsKey(PARAM_CLIENT)) 
      {
      	if(AdminServer.loadAllJobs())
      	{
      		Util.sendResult(response, StatusResult.successfulResult());
      	}
      	else
      	{
      		Util.sendResult(response, StatusResult.failureResult("Reload Job defs failed"));
      	}
      	
      }
      else
      {
      	response.setContentType("text/html");
      	PrintWriter out = response.getWriter();
      	out.println("resonate_f5_is_good");
      }
	}	

}
