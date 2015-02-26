package com.theice.fileprocessor.web;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class StatusResult
{
	protected boolean processed;
	protected String err;

	public static StatusResult successfulResult()
	{
		StatusResult result = new StatusResult();
		result.processed=true;
		return result;
	}
	
	public static StatusResult failureResult(String err)
	{
		StatusResult result = new StatusResult();
		result.processed=false;
		result.err=err;
		return result;
	}
	
	public boolean isProcessed()
   {
   	return processed;
   }

	public void setProcessed(boolean processed)
   {
   	this.processed = processed;
   }

	public String getErr()
   {
   	return err;
   }

	public void setErr(String err)
   {
   	this.err = err;
   }

	@Override
   public String toString()
   {
      return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
   }
}
