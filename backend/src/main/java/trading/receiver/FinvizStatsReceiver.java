package trading.receiver; 

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import trading.domain.Stock;
import trading.domain.StockType;
import trading.util.PropertyManager;

public class FinvizStatsReceiver
{ 
    private static Logger logger = Logger.getLogger(FinvizStatsReceiver.class);
  
    private static final long ONE_BILLION=1000000000;
    private static final long ONE_MILLION=1000000;
    private static final float ONE_PERCENT=0.01f;
    
    private static void fetchSectorAndIndustry(Document doc, Stock stock)
    {
    	 Element table= doc.select("table.fullview-title").first();
         Elements links = table.select("a[href]");
         stock.setSector(links.get(2).text());
         stock.setIndustry(links.get(3).text());
    }
   
    private static void fetchSnapshot(Document doc, Stock stock)
    {
    	 Element table= doc.select("table.snapshot-table2").first();
         Elements links = table.select("td");
         Map<String, String> props=new HashMap<String,String>();
         for(int i=0;i<links.size();i+=2)
         {
        	 props.put(links.get(i).text(),links.get(i+1).text());
         }
         
         for (Map.Entry<String, String> entry : props.entrySet()) {
        	    String key = entry.getKey();
        	    String value = entry.getValue();
        	    if("Shs Outstand".equals(key))
        	    {
        	    	long s=0;
        	    	if(value.endsWith("B"))
        	    	{
         	    		s=(long)(Double.parseDouble(value.substring(0, value.length()-1))*ONE_BILLION);
        	    	}
        	    	if(value.endsWith("M"))
        	    	{
        	    		s=(long)(Double.parseDouble(value.substring(0, value.length()-1))*ONE_MILLION);        	    		
        	    	}
        	    	
        	    	stock.getFundamentalData().setShareOutstanding(s);
        	    }
        	    else if("Book/sh".equals(key))
        	    {        	    	
        	    	stock.getFundamentalData().setBookPerShare(Float.parseFloat(value));
        	    }
        	    else if("Cash/sh".equals(key))
        	    {        	    	
        	    	stock.getFundamentalData().setCashPerShare(Float.parseFloat(value));
        	    } 
        	    else if("Dividend".equals(key))
        	    {        	    	
        	    	if(!"-".equals(value))
        	    	{
        	    		stock.getFundamentalData().setDividend(Float.parseFloat(value));
        	    	}
        	    } 
        	    else if("Optionable".equals(key))
        	    {        	
        	    	stock.setOptionable("Yes".equals(value));
        	    } 
        	    else if("Recom".equals(key))
        	    {        	
    	    		stock.getFundamentalData().setRecommendationRate(Float.parseFloat(value));
        	    }        	    
        	    else if("PEG".equals(key))
        	    {        	
    	    		stock.getFundamentalData().setPeg(Float.parseFloat(value));
        	    }              	    
        	    
        	    
        	    
        	    
        	}
         
         
         
    }
    
    public static void fetch(Stock stock) throws Exception
    {

    	String url = PropertyManager.getInstance().getProperty(PropertyManager.FINVIZ_STATS)+stock.getTicker();
        logger.debug("url="+url);
        Document doc = Jsoup.connect(url).get();
        fetchSectorAndIndustry(doc, stock);
        fetchSnapshot(doc, stock);
    }

    public static void main(String[] args) throws Exception
    {
    	Stock stock = new Stock();
    	stock.setTicker("T");
    	stock.setStockType(StockType.STOCK);
    	fetch(stock);
         System.out.println(stock);
    }
} 
