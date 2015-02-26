package com.theice.fileprocessor.web;

import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class JobListResult extends StatusResult
{
	private List<Job> jobList;
	
	public static JobListResult successfulResult(List<Job> jobList)
	{
		JobListResult result = new JobListResult();
		result.processed=true;
		result.jobList=jobList;
		return result;
	}
	
	public static JobListResult failureResult(String err)
	{
		JobListResult result = new JobListResult();
		result.processed=false;
		result.err=err;
		return result;
	}
	
	public List<Job> getJobList()
   {
   	return jobList;
   }

	public void setJobList(List<Job> jobList)
   {
   	this.jobList = jobList;
   }

	@Override
   public String toString()
   {
      return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
   }
}
