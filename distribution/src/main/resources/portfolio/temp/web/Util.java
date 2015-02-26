package com.theice.fileprocessor.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;

import com.theice.fileprocessor.domain.SNMPObject;
import com.theice.logging.core.AlarmLogger;
import com.theice.logging.core.domain.Alarm;
import com.theice.logging.core.domain.BasicAlarm;
import com.theice.logging.domain.Severity;

public class Util
{
	private Util(){}
	
	public static void sendResult(HttpServletResponse response, StatusResult result) throws IOException
	{
		ObjectMapper mapper = new ObjectMapper();
		response.setContentType("application/json");
      PrintWriter out = response.getWriter();
      mapper.writeValue(out, result);
	}
	
   public static void snmpLog(String title, String detail, boolean pause)
	{
		SNMPObject snmpObject = new SNMPObject();
		Alarm alarm = new BasicAlarm(snmpObject.getManagedObjectKey(), title, Severity.Critical,snmpObject);
		alarm.addAlarmDetails(detail);
		AlarmLogger.getInstance().log(alarm); 
		if(pause==true)
		{
			try
			{
				Thread.sleep(4000);//for SNMP call to finish in case followed by System.exit();
			}
			catch (Throwable th1){}
		}
   }
}
