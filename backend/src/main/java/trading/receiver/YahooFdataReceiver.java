package trading.receiver; 

import org.apache.log4j.Logger;

import trading.domain.Stock;
import trading.util.TradingIOUtils;

public class YahooFdataReceiver
{ 
    final private static String SO_URL="/q/in?s=";
    private static Logger logger = Logger.getLogger(YahooFdataReceiver.class);
    
    public static void download(Stock stock) throws Exception
    {
        String url=SO_URL+stock.getTicker();
        logger.debug("url="+url);
        String page=TradingIOUtils.getStringFromURL(url);
    	  
        int pos=0;        
        if(page==null || (pos=page.indexOf("Sector:"))<0)
        {
            throw(new Exception("No data"));
        }             
        page=page.substring(pos);//Sector:</td><td class="yfnc_tabledata1" nowrap="nowrap"><a href="http://us.rd.yahoo.com/finance/industry/leaf/1/1/*http://biz.yahoo.com/p/5conameu.html">Healthcare</a></td></tr><tr><td class="yfnc_tablehead1" valign="top">Industry:</td><td class="yfnc_tabledata1" nowrap="nowrap"><a href="http://us.rd.yahoo.com/finance/industry/leaf/1/2/*http://biz.yahoo.com/ic/515.html">Biotechnology</a>
        pos=page.indexOf(":");        
        page=page.substring(pos+1);//</td><td class="yfnc_tabledata1" nowrap="nowrap"><a href="http://us.rd.yahoo.com/finance/industry/leaf/1/1/*http://biz.yahoo.com/p/5conameu.html">Healthcare</a></td></tr><tr><td class="yfnc_tablehead1" valign="top">Industry:</td><td class="yfnc_tabledata1" nowrap="nowrap"><a href="http://us.rd.yahoo.com/finance/industry/leaf/1/2/*http://biz.yahoo.com/ic/515.html">Biotechnology</a>
        pos=page.indexOf(">");
        page=page.substring(pos+1);//<td class="yfnc_tabledata1" nowrap="nowrap"><a href="http://us.rd.yahoo.com/finance/industry/leaf/1/1/*http://biz.yahoo.com/p/5conameu.html">Healthcare</a></td></tr><tr><td class="yfnc_tablehead1" valign="top">Industry:</td><td class="yfnc_tabledata1" nowrap="nowrap"><a href="http://us.rd.yahoo.com/finance/industry/leaf/1/2/*http://biz.yahoo.com/ic/515.html">Biotechnology</a>
        pos=page.indexOf(">");
        page=page.substring(pos+1);//<a href="http://us.rd.yahoo.com/finance/industry/leaf/1/1/*http://biz.yahoo.com/p/5conameu.html">Healthcare</a></td></tr><tr><td class="yfnc_tablehead1" valign="top">Industry:</td><td class="yfnc_tabledata1" nowrap="nowrap"><a href="http://us.rd.yahoo.com/finance/industry/leaf/1/2/*http://biz.yahoo.com/ic/515.html">Biotechnology</a>
        pos=page.indexOf(">");
        page=page.substring(pos+1);//Healthcare</a></td></tr><tr><td class="yfnc_tablehead1" valign="top">Industry:</td><td class="yfnc_tabledata1" nowrap="nowrap"><a href="http://us.rd.yahoo.com/finance/industry/leaf/1/2/*http://biz.yahoo.com/ic/515.html">Biotechnology</a>
        pos=page.indexOf("<");
        stock.setSector(page.substring(0,pos));
            
        pos=page.indexOf("Industry:");       
        page=page.substring(pos);//Industry:</td><td class="yfnc_tabledata1" nowrap="nowrap"><a href="http://us.rd.yahoo.com/finance/industry/leaf/1/2/*http://biz.yahoo.com/ic/515.html">Biotechnology</a>
        pos=page.indexOf(">");
        page=page.substring(pos+1);//<td class="yfnc_tabledata1" nowrap="nowrap"><a href="http://us.rd.yahoo.com/finance/industry/leaf/1/2/*http://biz.yahoo.com/ic/515.html">Biotechnology</a>
        pos=page.indexOf(">");
        page=page.substring(pos+1);//<a href="http://us.rd.yahoo.com/finance/industry/leaf/1/2/*http://biz.yahoo.com/ic/515.html">Biotechnology</a>
        pos=page.indexOf(">");
        page=page.substring(pos+1);//Biotechnology</a>
        pos=page.indexOf("<");
        stock.setIndustry(page.substring(0,pos));
    }

    public static void main(String[] args) throws Exception
    {
    	Stock stock = new Stock();
    	stock.setTicker("T");
         download(stock);
         System.out.println(stock);
    }
} 
