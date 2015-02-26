package com.theice.fileprocessor.web;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.theice.fileprocessor.ProcessorException;

public class ExecServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;

   private static final Logger logger = Logger.getLogger(ExecServlet.class);

   private static final int MAX_THREAD_POOL_SIZE = 10;
   private static final int MAX_CORE_POOL_SIZE = 1;
   private static final long THREAD_EXEC_MILLIS = 30000;
   private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(MAX_CORE_POOL_SIZE, MAX_THREAD_POOL_SIZE, 10L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
      
	private static final String PARAM_FPNAME="fpname";
	private static final String PARAM_CMD="cmd";
	
   private Map<FpName,String> fpBinMap;
   
	public ExecServlet(Map<FpName,String> fpBinMap)
	{
		this.fpBinMap=fpBinMap;
	}
   
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,IOException
	{
		String strFpname = request.getParameter(PARAM_FPNAME);
		if(strFpname==null)
		{
			Util.sendResult(response, StatusResult.failureResult("Missing parameter: "+PARAM_FPNAME));
			return;
		}
		String strCmd = request.getParameter(PARAM_CMD);
		if(strCmd==null)
		{
			Util.sendResult(response, StatusResult.failureResult("Missing parameter: "+PARAM_CMD));
			return;
		}
		
		FpName fpName=FpName.lookupByValue(strFpname.toLowerCase());
		if(fpName==null)
		{
			Util.sendResult(response, StatusResult.failureResult("Invalid fp name parameter: "+strFpname));
			return;
		}
		
		if(validateScript(fpName, strCmd)==false)
		{
			Util.sendResult(response, StatusResult.failureResult("Invalid cmd: "+strCmd));
			return;
		}
		
		Future<Boolean> future=null;
		
		long millis=System.currentTimeMillis();
		try
	   {
			future = executor.submit(new RunScriptThread(fpName, strCmd));
	   }
	   catch (RejectedExecutionException rej) 
	   {
	   	String msg="Server is busy, please submit request later.";
	   	logger.error(msg,rej);
	   	Util.sendResult(response, StatusResult.failureResult(msg));
	   	return;
      }  
      catch(Throwable th)
		{
      	String err="Unexpected error";
      	logger.error(err, th);
         Util.snmpLog(err, th.getMessage(), false);
	   	Util.sendResult(response, StatusResult.failureResult(err));
	   	return;
		}
		
		try
		{
      	future.get(THREAD_EXEC_MILLIS, TimeUnit.MILLISECONDS);
      	millis=System.currentTimeMillis()-millis;
      	logger.debug(String.format("FP started in %d milliseconds", millis));
   	   Util.sendResult(response, StatusResult.successfulResult());
		}
		catch(TimeoutException ex)
		{
			future.cancel(true);
			String err=String.format("FP unable to start in %d (millis)", THREAD_EXEC_MILLIS);
      	logger.error(err, ex);
         Util.snmpLog(err, ex.getMessage(), false);
	   	Util.sendResult(response, StatusResult.failureResult(err));
		}
		catch(ExecutionException ex1)
		{
			if(ex1.getCause() instanceof ProcessorException)
			{
		   	Util.sendResult(response, StatusResult.failureResult(ex1.getCause().getMessage()));
			}
			else
			{
	         Util.snmpLog("Unexpected error", ex1.getMessage(), false);
		   	Util.sendResult(response, StatusResult.failureResult(ex1.getMessage()));
			}
		}
		catch(Throwable th)
		{
         Util.snmpLog("Unexpected error", th.getMessage(), false);
	   	Util.sendResult(response, StatusResult.failureResult(th.getMessage()));
		}
	}
	
	private boolean validateScript(FpName fpName, String strCmd)
	{
		String fileName=fpBinMap.get(fpName)+strCmd;
		int firstSpace = fileName.indexOf(" ");
		if(firstSpace>0)
		{
			fileName=fileName.substring(0,firstSpace);
		}
		File file = new File(fileName);
		logger.debug("script to validate: "+fileName);
		return (file.exists() && file.isFile());
	}

}

class RunScriptThread implements Callable<Boolean>
{  
	private static Logger logger = Logger.getLogger(RunScriptThread.class);	

	private FpName fpName;
	private String strCmd;

	public RunScriptThread(FpName fpName, String strCmd)
	{
		this.fpName=fpName;
		this.strCmd=strCmd;
	}
	
	@Override
   public Boolean call() throws ProcessorException
   {  
   	logger.debug("Process thread started for uploadId="+strCmd);

		String script;
		try
		{
			String cmd=null;
			
			if(fpName==FpName.SETTLE)
			{		
				cmd="bash";
				script = "bash -c \"cd; . .bash_profile; "+strCmd+"\"";
			}
			else
			{
				
				//cmd="bash -c \"sudo su - "+fpName.getValue()+"\"";
				cmd="/usr/local/bin/sudo su - "+fpName.getValue();
				script = "bash -c \" . .bash_profile; "+strCmd+"\"";
			}
			logger.debug(cmd+"\n"+script);
			Process p = Runtime.getRuntime().exec(cmd);
			OutputStream stdin = p.getOutputStream ();

			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stdin));

			writer.write(script);
			writer.write("\n");
			writer.flush();

			writer.write("exit");
			writer.write("\n");
			writer.flush();
			logger.debug("end of cmd");
			return true;
		}
		catch(Throwable th)
		{
			String err=String.format("Run script error for %s, %s",fpName.getValue(), strCmd);
			logger.error(err, th);
         Util.snmpLog(err, th.getMessage(), false);
   		throw new ProcessorException(err,th);
		}
	}
}
