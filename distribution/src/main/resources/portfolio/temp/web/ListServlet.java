package com.theice.fileprocessor.web;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ListServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	private static final String PARAM_CLIENT="client";
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,IOException
	{
		String client = request.getParameter(PARAM_CLIENT);
		List<Job> list = AdminServer.getJobList(client);
		if(list != null)
		{
			Util.sendResult(response, JobListResult.successfulResult(list));
		}
		else
		{
			Util.sendResult(response, JobListResult.failureResult("Invalid Client, name="+client));
		}
	}

}
