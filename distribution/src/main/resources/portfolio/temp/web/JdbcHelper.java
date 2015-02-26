package com.theice.fileprocessor.web;

import com.theice.fileprocessor.web.Job;
import com.theice.fileprocessor.util.OdsJdbcHelper;

import java.sql.SQLException;
import java.util.List;

public class JdbcHelper extends OdsJdbcHelper
{
   @SuppressWarnings("unchecked")
   public static List<Job> getJobs() throws SQLException
   {
   	 return sqlMap.queryForList("webadmin.getJobs");
   }
}
    