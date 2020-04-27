package com.lakshya;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.JOptionPane;

public class CreateTabs2 {
	public int generate(MigratorUI ui,Connection sourcecon,Connection targetcon,String sourcedb,String targetdb,String sourcetabname)
	{
		try{
			
			 DatabaseMetaData alpha;
			 ResultSet rs =null;
            String s1="";
            String s2="";
            int i=0;
            
            //target tables connection
            if(targetdb.equalsIgnoreCase("oracle"))
		    {Statement stmt=targetcon.createStatement();
		    	rs=stmt.executeQuery("select * from tab");
		    	}
		    else { alpha= targetcon.getMetaData();
			      rs= alpha.getTables(null, null, null,
					new String[] { "TABLE" });
		         }
           

				while (rs.next())
				{   if(targetdb.equalsIgnoreCase("oracle"))s2+=rs.getString("TNAME")+",";
				    else s2+=rs.getString("TABLE_NAME")+",";
					
				}
				
				String s3[]=s2.split(",");
          if(!sourcetabname.equals(""))
          {
        	  for(int j=0;j<s3.length;j++)
				{  
					if(sourcetabname.equalsIgnoreCase(s3[j]))
					{PreparedStatement pst;
					
					if(targetdb.equalsIgnoreCase("oracle"))
						pst=targetcon.prepareStatement("DROP TABLE "+s3[j]);
										
					else pst=targetcon.prepareStatement("DROP TABLE "+s3[j]+";");
					pst.executeUpdate();
				     System.out.println("table delted");
				     }
				}
        	  String t1=addFields(sourcecon,sourcetabname,targetdb,sourcedb);	
			   String t2[]=t1.split(",");
			   String t="";
			   for(int p=0;p<t2.length-1;p++)t+=t2[p]+",";
			  t+=t2[t2.length-1];
			  if(targetdb.equals("Mysql"))sourcetabname="`"+sourcetabname+"`";
				 
			  System.out.println("create table "+sourcetabname+"("+t+");");
			  PreparedStatement pst=null;
			  if(targetdb.equals("Oracle"))
			   pst=targetcon.prepareStatement("CREATE TABLE "+sourcetabname+"("+t+")");
			  else pst=targetcon.prepareStatement("CREATE TABLE "+sourcetabname+"("+t+");");
			  int ex=0;
			    ex=pst.executeUpdate();
               if(ex!=0)System.out.println("tables created");
        	  
          }
          else{
            System.out.println("GGGGGGGGGGGGGGGGGGGGGGGGGGGGGG");
            if(sourcedb.equalsIgnoreCase("oracle"))
		    {Statement stmt=sourcecon.createStatement();
		    	rs=stmt.executeQuery("select * from tab");
		    	}
		    else { alpha= sourcecon.getMetaData();
			      rs= alpha.getTables(null, null, null,
					new String[] { "TABLE" });
		         }
           

				while (rs.next())
				{   if(sourcedb.equalsIgnoreCase("oracle"))s1+=rs.getString("TNAME")+",";
				    else s1+=rs.getString("TABLE_NAME")+",";
					
				}
		       
				
				
				rs.close();
				int l=0;
				System.out.println("s1:"+s1);
				System.out.println("s2:"+s2);
				if(s1.equals(""))
				{
					JOptionPane.showMessageDialog(ui,"soucedb has no tables to migrate");
					return 0;
				}
				else{
			    String s[]=s1.split(",");
			//	String s3[]=s2.split(",");
			for( i=0;i<s.length;i++) {int k=0;
			                         l=1;
				for(int j=0;j<s3.length;j++)
				{  
					if(s[i].equalsIgnoreCase(s3[j]))
					{PreparedStatement pst;
					
					if(targetdb.equalsIgnoreCase("oracle"))
						pst=targetcon.prepareStatement("DROP TABLE "+s3[j]);
										
					else pst=targetcon.prepareStatement("DROP TABLE "+s3[j]+";");
					pst.executeUpdate();
				     System.out.println("table delted");
				     }
				}
			   String tabname=s[i];
			   String t1=addFields(sourcecon,tabname,targetdb,sourcedb);	
			   String t2[]=t1.split(",");
			   String t="";
			   for(int p=0;p<t2.length-1;p++)t+=t2[p]+",";
			  t+=t2[t2.length-1];
			  if(targetdb.equals("Mysql"))
					  {
				         String z=s[i];
				         s[i]="`"+s[i]+"`";
					  }
			  int ex=0;
			  System.out.println("create table "+s[i]+"("+t+");");
			  PreparedStatement pst=null;
			  if(targetdb.equals("Oracle"))
			   pst=targetcon.prepareStatement("CREATE TABLE "+s[i]+"("+t+")");
			  else pst=targetcon.prepareStatement("CREATE TABLE "+s[i]+"("+t+");");
			    ex=pst.executeUpdate();
               if(ex!=0)System.out.println("tables created");
		     }
				}//end of inner else
          }//end of else
          return 1;
		}//end of try
			catch(Exception e)
			{
				e.printStackTrace();
				return 0;
			   
			}
			
	}
	
	
	
	
	
	
	
	private String addFields(Connection conn, String table,String td,String sd)
	throws Exception
{
		
DatabaseMetaData meta = conn.getMetaData();
ResultSet rs = meta.getColumns(null, null, table, "%");

String f;
int n= rs.getMetaData().getColumnCount();
String t="";
String t1="";
while (rs.next())
{  	t="";
     
    String columnName = rs.getString("COLUMN_NAME");
	String typeName = rs.getString("TYPE_NAME");
    int dataType = rs.getInt("DATA_TYPE");
     String s = rs.getString(7);
     
    String d=getFieldType(dataType,td,s);
    	
	 if(td.equalsIgnoreCase("MsAccess")){ 
		 System.out.println("if block");
		
		 t=t.concat(columnName).concat(" ");
		 t=t.concat(d).concat(",");
		 t1=t1.concat(t);
		 }
		 
	 else 
		 
	 {  
	    if(td.equalsIgnoreCase("Mysql")){
         t=t.concat("`")+t.concat(columnName).concat("`")+t.concat(" ");
		 t=t.concat(d).concat("(");
	     t=t.concat(s).concat("),");
		  t1=t1.concat(t);
	 }
	 else {System.out.println("else block");
		 t=t.concat(columnName)+t.concat(" ");
		    if(d.equalsIgnoreCase("date")){t=t.concat(d).concat(",");t1=t1.concat(t);}
		    else{	
			t=t.concat(d).concat("(");
		    t=t.concat(s).concat("),");
			 t1=t1.concat(t);
	         }
	       }
	 }
  }

return t1;
}


	
	
	
/*	private String getFieldType(int jdbcType)
{
String type;
switch (jdbcType)
{
case java.sql.Types.REAL:
case java.sql.Types.FLOAT:
case java.sql.Types.DECIMAL:
case java.sql.Types.DOUBLE:	
{
	type = "float";
	break;
}
case java.sql.Types.BIT:
case java.sql.Types.TINYINT:
case java.sql.Types.INTEGER:
case java.sql.Types.SMALLINT:
{
	type = "INT";
	break;
}
case java.sql.Types.DATE:
{
	type = "date";
	break;
}
case java.sql.Types.TIMESTAMP:
{
	type = "timestamp";
	break;
}
case java.sql.Types.VARCHAR:
case java.sql.Types.CHAR:
{
	type = "varchar";
	break;
}
case java.sql.Types.BLOB:
{
	type = "blob";
	break;
}
case java.sql.Types.CLOB:
{
	type = "clob";
	break;
}
default:
{
	type = "!!! UNKNOWN !!!. java.sql.Types: " + jdbcType;
	break;
}
}
return type;
}*/
	private String getFieldType(int jdbcType,String td,String s)
	{
	String type;
	switch (jdbcType)
	{
	case java.sql.Types.REAL:
	case java.sql.Types.FLOAT:
	case java.sql.Types.DECIMAL:
	case java.sql.Types.DOUBLE:	
	{if(td.equalsIgnoreCase("mysql")){type = "float";}
    else
    	type="number";
	break;
	}
	case java.sql.Types.BIT:
	case java.sql.Types.TINYINT:
	case java.sql.Types.INTEGER:
	case java.sql.Types.SMALLINT:
	{  if(td.equalsIgnoreCase("oracle")){type="number";}
	   else type = "INT";
		break;
	}
	case java.sql.Types.DATE:
	{    if(td.equalsIgnoreCase("MsAccess")){type="DATETIME";}
	     else
		type = "date";
		break;
	}
	case java.sql.Types.TIMESTAMP:
	{    if(td.equalsIgnoreCase("MsAccess")){type="DATETIME";}
	    else if(td.equalsIgnoreCase("oracle")){type="date";}
		type = "timestamp";
		break;
	}
	case java.sql.Types.VARCHAR:
	case java.sql.Types.CHAR:
	{if(td.equalsIgnoreCase("oracle"))type = "varchar2";
	else type = "varchar";
		break;
	}
	case java.sql.Types.BLOB:
	{
		type = "blob";
		break;
	}
	case java.sql.Types.CLOB:
	{
		type = "clob";
		break;
	}
	case -1:
	{if(td.equalsIgnoreCase("oracle"))type = "varchar2";
	else if(td.equalsIgnoreCase("MsAccess"))type = "varchar";
	else type="text";
	break;}
	default:
	{
		type = "unknow"+jdbcType;
		break;
	}
	}
	return type;
	}

	}


