package com.example.hellojni;

import java.util.ArrayList;
import java.util.List;

public class StringInt  {
	int wifiNumber_max=100;

		  private int wifiNumber;
		  private String wifiname;

		  public StringInt( int wifiNumber, String name ) {
		    this.wifiNumber = wifiNumber;
		    this.wifiname = name;}

		  public int getwifiNumber() { return wifiNumber; }
		  public String getName() { return wifiname ;}
	
}
