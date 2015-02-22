package trading.indicator; 

import trading.domain.Quote;

public final class RSI  
{  
	private static final int DAYS = 5;
	
    private RSI(){} 

    public static void calcRSI(Quote[] quotes)
    {
    	calcRSI(quotes, DAYS);
    }
    
    public static void calcRSI(Quote[] quotes, int days)  
    { 
		int end = quotes.length - 1;
		int start = days;
		int pos = start;
		for (int i = end; i >= start; i--) {
			if (quotes[i].getRsi5() != null) {
				pos = i + 1;
				break;
			}
		}
		if (pos > end) {
			return;
		}
		
        float[] diff=new float[quotes.length];
        
        for(int i=pos-days+1;i<=end;i++){
        	diff[i]=quotes[i].getClose()-quotes[i-1].getClose();
        }
        
        float ps=0;
        float ns=0;
        
        for(int i=pos-days+1;i<=pos;i++)
        {
            if(diff[i]<0)
            {
                ns+=diff[i];
            }
            else if(diff[i]>0)
            {
                ps+=diff[i];                    
            }
        }
        ps=ps/days;
        ns=0-ns/days;
        quotes[pos].setRsi5(100*ps/(ns+ps));
 
        
        for(int i=pos+1;i<=end; i++)
        {
            if(diff[i]<0)
            {
                ns=(ns*(days-1)-diff[i])/days;
                ps=(ps*(days-1))/days; 
            }
            else if(diff[i]>0)
            {
                ns=(ns*(days-1))/days;
                ps=(ps*(days-1)+diff[i])/days;                    
            }                
            quotes[i].setRsi5((100*ps/(ns+ps)));
        } 
    } 
} 
