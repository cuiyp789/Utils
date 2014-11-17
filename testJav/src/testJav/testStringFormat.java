package testJav;

import java.text.DecimalFormat;

public class testStringFormat {

	public static void main(String[] args) {
		System.out.println(String.format("%.2f", 123.456));
		System.out.println(String.format("%.2f", 123.0));
		
		String   a   =  new DecimalFormat("###,###,###.##").format(100.12345  );
		float   scale  =   34.2f;   
		Double d = Double.valueOf(scale);
		System.out.println(d); 
		DecimalFormat   fnum  =   new  DecimalFormat("##0.00");    
		String   dd=fnum.format(scale);      
		System.out.println(dd); 
		
		DecimalFormat formater = new DecimalFormat("#0.##");
		System.out.println(formater.format(34.234)); 
		
	}

}
