package com.theice.fileprocessor.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.zip.Deflater;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

import org.apache.log4j.Logger;

import com.theice.fileprocessor.util.IOUtil;

public class DownloadServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	
   private static final Logger logger = Logger.getLogger(DownloadServlet.class);
	
	private static final String PARAM_FPNAME="fpname";
	private static final String PARAM_FILENAME="filename";
	
   private Map<FpName,String> fpHomeMap;
	
	public DownloadServlet(Map<FpName,String> fpHomeMap)
	{
		this.fpHomeMap=fpHomeMap;
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,IOException
	{
		String strFpname = request.getParameter(PARAM_FPNAME);
		if(strFpname==null)
		{
			Util.sendResult(response, FileResult.failureResult("Missing parameter: "+PARAM_FPNAME));
			return;
		}
		String fileName = request.getParameter(PARAM_FILENAME);
		if(fileName==null)
		{
			Util.sendResult(response, FileResult.failureResult("Missing parameter: "+PARAM_FILENAME));
			return;
		}		
		
		FpName fpName=FpName.lookupByValue(strFpname.toLowerCase());
		if(fpName==null)
		{
			Util.sendResult(response, FileResult.failureResult("Invalid fp name parameter: "+strFpname));	
			return;
		}
		
		fileName=fpHomeMap.get(fpName)+fileName;
		FileResult fileResult=FileResult.successfulResult();
		fileResult.setName(fileName);
		try
		{
			byte[] bytes=IOUtil.getBytesFromFile(fileName);
			fileResult.setCompressed(true);
			fileResult.setBase64(true);
			fileResult.setBody(DatatypeConverter.printBase64Binary(compressByteArray(bytes)));
		}
		catch(Throwable th)
		{
			String msg="Unable to get file "+fileName;
			logger.error(msg, th);
			Util.sendResult(response, FileResult.failureResult(msg));
			return;
		}
		Util.sendResult(response, fileResult);
	}
		
   private byte[] compressByteArray(byte[] bytes)
   {
      Deflater dfl = new Deflater();
      dfl.setInput(bytes);
      dfl.finish();
      
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      byte[] tmp = new byte[4096];
      try
      {
      	while(!dfl.finished())
         {
      		int size = dfl.deflate(tmp);
            os.write(tmp, 0, size);
         }
      } 
      finally 
      {
      	try
      	{	
      		if(os != null) os.close();
         } catch(Exception ex){}
      }
      return os.toByteArray();
  }
}
