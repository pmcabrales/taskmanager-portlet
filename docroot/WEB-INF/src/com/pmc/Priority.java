package com.pmc;

public class Priority {
	public static final long VERY_HIGH = 3;
	public static final long HIGH = 2;
	public static final long MEDIUM = 1;  
	public static final long LOW =0;
    
	private Priority(){}
	
    public static final String[] priority = new String[]{"Very high","High","Medium","Low"};
	
	public static final long[] aPriority = new long[]{VERY_HIGH,HIGH,MEDIUM,LOW};
        
}