package com.theice.fileprocessor.web;

public class FileResult extends StatusResult
{
	private String name;
	private boolean isCompressed=true;
	private boolean base64=true;
	private String body;

	public static FileResult successfulResult()
	{
		FileResult result = new FileResult();
		result.processed=true;
		return result;
	}
	
	public static FileResult failureResult(String err)
	{
		FileResult result = new FileResult();
		result.processed=false;
		result.err=err;
		return result;
	}
	
	public String getName()
   {
   	return name;
   }
	
	public void setName(String name)
   {
   	this.name = name;
   }

	public boolean isCompressed()
   {
   	return isCompressed;
   }

	public void setCompressed(boolean isCompressed)
   {
   	this.isCompressed = isCompressed;
   }

	public boolean isBase64()
   {
   	return base64;
   }

	public void setBase64(boolean base64)
   {
   	this.base64 = base64;
   }

	public String getBody()
   {
   	return body;
   }

	public void setBody(String body)
   {
   	this.body = body;
   }
}
