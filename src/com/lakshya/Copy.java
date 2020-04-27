
package com.lakshya;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Copy {
	

	public void dump(String source,String filename,Connection con)
	{
		
		
		
		
		try{
			 		  	
			File file = new File(filename);
		    Writer output = null;
		   		      
		    output = new BufferedWriter(new FileWriter(file));
		   	 String p[]=filename.split("."); 	
		   	 int l=p.length;    
		   	ResultSet rs=null;
			
			
		    DatabaseMetaData alpha = con.getMetaData();
		    if(source.equalsIgnoreCase("oracle"))
		    {Statement stmt=con.createStatement();
		    	rs=stmt.executeQuery("select * from tab");
		    	}
		    else rs= alpha.getTables(null, null, null,
					new String[] { "TABLE" });
	      
			List tablesList= new ArrayList();
			while (rs.next())
			{
				if(source.equalsIgnoreCase("oracle")) tablesList.add(rs.getString("TNAME"));
				else tablesList.add(rs.getString("TABLE_NAME"));
			}
			for (Iterator j = tablesList.iterator(); j.hasNext();)
			{
			if(!filename.endsWith(".csv"))
		    {		
			String t=(String) j.next();
			output.write("Table name:"+t);
			output.write("\n");
			output.write("------------------------------------------------------------");
			output.write("\n");
		    
			ResultSet rs1 = alpha.getColumns(null, null, t, "%");

			String s1="";
			int a=0;
			 while(rs1.next())
			 {	
			   String z= rs1.getString("COLUMN_NAME");
			   int e1=z.length()+4;
		         output.write("    "+z);
		         for(int f1=e1;f1<=25;f1++)output.append(" ");
	           s1=s1+rs1.getInt("DATA_TYPE")+",";
			  
			
			}
			 String s[]=s1.split(",");
			 //int eachlen=60/s.length;
			 
			 output.write("\n");
			 output.write("------------------------------------------------------------");
			 output.write("\n");
			 PreparedStatement pst;
	    	 if(source.equalsIgnoreCase("oracle"))pst=con.prepareStatement("select * from "+t);
			 else pst=con.prepareStatement("select * from "+t+" ;");
	    	 ResultSet rs2=pst.executeQuery();
			 int b=s.length;
			//System.out.println("welcome");			
			 while(rs2.next())
			 { for(int d=1;d<b;d++) 
			    {String t2=rs2.getString(d);
		         int e=t2.length();
		         output.write("    "+t2);
			    for(int space=e;space<10;space++)output.append(" ");
		         }
			    
				output.write("\n");
			
			    }
				output.write("------------------------------------------------------------");
				output.write("\n");
		
			
		    }
		    else
		    {
		    	String t=(String) j.next();
		    	output.write("Table name:"+t);
		    	 output.append("\n");
		    	ResultSet rs1 = alpha.getColumns(null, null, t, "%");
		    	String s1="";
				int a=0;
		    	 while(rs1.next())
				 {	
				   String z= rs1.getString("COLUMN_NAME");
				   output.append(z+",");
				   s1=s1+rs1.getInt("DATA_TYPE")+",";
				   }
		    	 output.append("\n");
		    	 String s[]=s1.split(",");
		    	 PreparedStatement pst;
		    	 if(source.equalsIgnoreCase("oracle"))pst=con.prepareStatement("select * from "+t);
				 else pst=con.prepareStatement("select * from "+t+" ;");
		    	
				 ResultSet rs2=pst.executeQuery();
				 int b=s.length;
				 while(rs2.next())
				 { 
					 for(int d=1;d<=b;d++) 
					    {String t2=rs2.getString(d);
					      output.append(t2+",");
					    }
					 output.append("\n");
		           }
				 output.append("\n");
				 output.append("\n");
			  }
			}
			
			  output.close();
			
		 
		}
		 catch(Exception e)
		 {   
			 e.printStackTrace();
			 
		    }

} 	
	
}
		
		
		
		
    