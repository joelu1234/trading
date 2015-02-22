package trading.indicator; 

public final class K 
{  
    private K(){} 

    public static int[] getK(int highs[], int lows[], int closes[],int numDays) 
    { 
        int length=closes.length; 
        
        int[] k=new int[length];
        
        int start=numDays-1;
        for(int i=start;i<length; i++)
        {
            int high=0;
            int low=Integer.MAX_VALUE;
            for(int j=i-start;j<=i;j++)
            {
                if(highs[j]>high) high=highs[j];
                if(lows[j]<low) low=lows[j];
            }
            k[i]=(high>low)? (closes[i]-low)*100/(high-low) :0;
        } 
        return k; 
    } 
} 
