package com.theice.fileprocessor.web;

public enum FpName
{
   SETTLE("settle"),
   NGX("ngx"),
   TCC("tcc");

   private FpName(String value)
   {
      this.value = value;
   }

   public String getValue()
   {
      return value;
   }

   public static FpName lookupByValue(String value)
   {
      for (FpName fpName : values())
      {
         if (fpName.getValue().equalsIgnoreCase(value))
         {
            return fpName;
         }
      }
      return null;
   }
   private String value;
}