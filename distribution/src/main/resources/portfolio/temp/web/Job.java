package com.theice.fileprocessor.web;

import java.util.List;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class Job implements Comparable<Job>
{
	private String id;
	private FpName fpName;
   private String catetory;
	private String display;
	private String cmd;
	private List<String> roles;
	
	private String strApps;
	private String strRoles;	

	public Job()
	{
	}
	
	public Job(String id)
	{
		this.id=id;
	}
	
	public String getId()
   {
   	return id;
   }

	public void setId(String id)
   {
   	this.id = id;
   }

	public FpName getFpName()
   {
   	return fpName;
   }
	
	public void setFpName(String fpName)
   {
   	this.fpName = FpName.lookupByValue(fpName);
   }

	public String getDisplay()
	{
		return display;
	}

	public void setDisplay(String display)
	{
		this.display = display;
	}

	public String getCmd()
   {
   	return cmd;
   }

	public void setCmd(String cmd)
   {
   	this.cmd = cmd;
   }

	public String getCatetory()
   {
   	return catetory;
   }

	public void setCatetory(String catetory)
   {
   	this.catetory = catetory;
   }

	public List<String> getRoles()
   {
   	return roles;
   }

	public void setRoles(List<String> roles)
   {
   	this.roles = roles;
   }

	public String getStrApps()
	{
		return strApps;
	}

	public void setStrApps(String strApps)
	{
		this.strApps = strApps;
	}

	public String getStrRoles()
	{
		return strRoles;
	}

	public void setStrRoles(String strRoles)
	{
		this.strRoles = strRoles;
	}

	@Override
   public String toString()
   {
      return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
   }

	@Override
   public int compareTo(Job o)
   {
		return new CompareToBuilder()
      .append(this.fpName, o.fpName)
      .append(this.catetory, o.catetory)
      .append(this.display, o.display)
      .toComparison();
   }
}
