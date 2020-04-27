package com.lakshya;
import java.sql.*;
import java.util.*;
class MigrationsClass
{
	String object,sourcedb,targetdb,sourceinf,targetinf;
	String sourcedriver,sourceurl,sourceuid,sourcepwd;
	String targetdriver,targeturl,targetuid,targetpwd;

	MigrationsClass(String s1,String s2,String s3,String s4,String s5)
	{
		this.object=s1;
		this.sourcedb=s2;
		this.targetdb=s3;
		this.sourceinf=s4;
		this.targetinf=s5;
		System.out.println("object "+object);
		System.out.println("sourcedb "+sourcedb);
		System.out.println("targetdb "+targetdb);
		System.out.println("sourceinf "+sourceinf);
		System.out.println("targetinf "+targetinf);
		getNames();
		tfsmigrate();
	}
	public void getNames()
	{
		int index=sourceinf.indexOf("$");
        System.out.println(index);
		sourcedriver=sourceinf.substring(0,index);
		System.out.println(sourcedriver);
		
		sourceinf=sourceinf.substring(index+1);
		index=sourceinf.indexOf("$");

		sourceurl=sourceinf.substring(0,index);
		System.out.println(sourceurl);
		
		sourceinf=sourceinf.substring(index+1);
		index=sourceinf.indexOf("$");
		
		sourceuid=sourceinf.substring(0,index);
		System.out.println(sourceuid);
		
		sourcepwd=sourceinf.substring(index+1);
		System.out.println(sourcepwd);

		index=targetinf.indexOf("$");

		targetdriver=targetinf.substring(0,index);
		System.out.println(targetdriver);
		
		targetinf=targetinf.substring(index+1);
		index=targetinf.indexOf("$");

		targeturl=targetinf.substring(0,index);
		System.out.println(targeturl);
		
		targetinf=targetinf.substring(index+1);
		index=targetinf.indexOf("$");
		
		targetuid=targetinf.substring(0,index);
		System.out.println(targetuid);
		
		targetpwd=targetinf.substring(index+1);
		System.out.println(targetpwd);

	}
	public void tfsmigrate()
	{
		Connection con=null;
		if(sourcedb.equals("Oracle"))
		{
			if(object.equals("Procedures"))
			{
				if(targetdb.equals("Oracle"))
				{
					String procdef="";
					Vector proceduresvector=new Vector();
					System.out.println("Procedure oracle to oracle migrated");
				try
					{
						Class.forName(sourcedriver);
						System.out.println("Driver Loaded");	
						con=DriverManager.getConnection(sourceurl,sourceuid,sourcepwd);	
						System.out.println("Connection established");
						DatabaseMetaData dbmd=con.getMetaData();
						ResultSet rs=dbmd.getProcedures(null,sourceuid,null);
						System.out.println("Got Procedures Names");
						ResultSet rs1=null;
						Statement st=con.createStatement();
						
						while(rs.next())
						{
							System.out.println("Entered into while");
							procdef=procdef.concat("create or replace ");
							String procedurename=rs.getString(3);
							
							ResultSet rs2=st.executeQuery("select count(*) from user_source where name like '"+procedurename+"'");								System.out.println("Procedure name "+procedurename);
							if(rs2.next())
							{
								int number=rs2.getInt(1);
								int linenumber=1;
								while(linenumber<=number)
								{
								
								rs1=st.executeQuery("select text from user_source where name like '"+procedurename+"' and line="+linenumber+"");
								if(rs1.next())
								{
									procdef=procdef.concat(rs1.getString(1));
									procdef=procdef.concat(" ");
						
								}
								linenumber++;
								}
								if(procdef.equals("create or replace "))
								{
									procdef="";

								}
								else
								{
								System.out.println("JJJJJJJJJJJ ddddd:"+procdef);
								proceduresvector.add(procdef);
								procdef="";
								}
							}
							
						}
						Enumeration e=proceduresvector.elements();
						
						while(e.hasMoreElements())
						{
							String procdefi=(String)e.nextElement();
							System.out.println("HHHHHHHHHHH  "+procdefi);
						


						}
						System.out.println(procdef);
						st.close();
						rs.close();
						con.close();

					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
					
					try
					{
						Class.forName(targetdriver);
						System.out.println("Driver Loaded");	
						con=DriverManager.getConnection(targeturl,targetuid,targetpwd);	
						System.out.println("Connection established");
						Enumeration e=proceduresvector.elements();
						Statement st=null;
						st=con.createStatement();
						while(e.hasMoreElements())
						{
							String procdefi=(String)e.nextElement();
							System.out.println("jjjjjjjj  "+procdefi);
							st.executeUpdate(procdefi);


						}
						System.out.println("procedures added");

					}
					catch(Exception e)
					{
						e.printStackTrace();
					}



				}
				if(targetdb.equals("DB2"))
				{
					System.out.println("Procedure oracle to db2 migrated");
				}
				if(targetdb.equals("MySql"))
				{
					System.out.println("Procedure oracle to mysql migrated");
				}
			}
			if(object.equals("Triggers"))
			{
				Vector triggersvector=new Vector();
				if(targetdb.equals("Oracle"))
				{
					try{

					Class.forName(sourcedriver);
					System.out.println("Driver Loaded");
					con=DriverManager.getConnection(sourceurl,sourceuid,sourcepwd);	
					System.out.println("Connection established");
					DatabaseMetaData dbmd=con.getMetaData();
					Statement st=con.createStatement();
					ResultSet rs=st.executeQuery("select description,trigger_body from user_triggers");
					while(rs.next())
					{
						String app="create or replace trigger ";
						app=app.concat(rs.getString(1));
						app=app.concat(" ");
						app=app.concat(rs.getString(2));
						System.out.println("ddddd "+app);
						triggersvector.add(app);

					}
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
					try
					{
						Class.forName(targetdriver);
						System.out.println("Driver Loaded");	
						con=DriverManager.getConnection(targeturl,targetuid,targetpwd);
						
						System.out.println("Connection established");
						Enumeration e=triggersvector.elements();
						Statement st=null;
						st=con.createStatement();
						while(e.hasMoreElements())
						{
							st.executeUpdate((String)e.nextElement());

						}
						System.out.println("triggers added");

					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
					
				}
				if(targetdb.equals("DB2"))
				{
					System.out.println("Triggers oracle to db2 migrated");
				}
				if(targetdb.equals("MySql"))
				{
					System.out.println("Triggers oracle to mysql migrated");
				}
			}
			if(object.equals("Functions"))
			{
				if(targetdb.equals("Oracle"))
				{
					 Vector functionssvector=new Vector(); 
					try{
					  
						String fundef="";
						Class.forName(sourcedriver);
						System.out.println("Driver Loaded");	
						con=DriverManager.getConnection(sourceurl,sourceuid,sourcepwd);	
						System.out.println("Connection established");
						DatabaseMetaData dbmd=con.getMetaData();
						//ResultSet rs=dbmd.getProcedures(null,sourceuid,null);
						System.out.println("Got Procedures Names");
						ResultSet rs1=null;
						Statement st=con.createStatement();
						ResultSet rs=st.executeQuery("select name from user_dependencies where type like 'FUNCTION'");
						while(rs.next())
						{
							System.out.println("Entered into while");
							fundef=fundef.concat("create or replace ");
							//String type=rs.getString(1);
							String functionname=rs.getString(1);
							
							ResultSet rs2=st.executeQuery("select count(*) from user_source where name like '"+functionname+"'");
								System.out.println("fUNCTION name "+functionname);
							if(rs2.next())
							{
								int number=rs2.getInt(1);
								int linenumber=1;
								while(linenumber<=number)
								{
								
								rs1=st.executeQuery("select text from user_source where name like '"+functionname+"' and line="+linenumber+"");
								if(rs1.next())
								{
									fundef=fundef.concat(rs1.getString(1));
									fundef=fundef.concat(" ");
						
								}
								linenumber++;
								}
								if(fundef.equals("create or replace "))
								{
									fundef="";

								}
								else
								{
								System.out.println("JJJJJJJJJJJ ddddd:"+fundef);
								functionssvector.add(fundef);
								fundef="";
								}
							}
						}
						}
						catch(Exception e)
								{
								
								}
								try
					{
						Class.forName(targetdriver);
						System.out.println("Driver Loaded");	
						con=DriverManager.getConnection(targeturl,targetuid,targetpwd);
						
						System.out.println("Connection established");
						Enumeration e=functionssvector.elements();
						Statement st=null;
						st=con.createStatement();
						while(e.hasMoreElements())
						{
							st.executeUpdate((String)e.nextElement());

						}
						System.out.println("Functions added");

					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
				if(targetdb.equals("DB2"))
				{
					System.out.println("Functions oracle to db2 migrated");
				}
				if(targetdb.equals("MySql"))
				{
					System.out.println("Fuction oracle to mysql migrated");
				}
			}
		}
		if(sourcedb.equals("DB2"))
		{
			if(object.equals("Procedures"))
			{
			}
			if(object.equals("Triggers"))
			{
			}
			if(object.equals("Functions"))
			{
			}
		}
		if(sourcedb.equals("MySql"))
		{
			if(object.equals("Procedures"))
			{
				if(targetdb.equals("Oracle"))
				{
				}
				if(targetdb.equals("DB2"))
				{
				}
				if(targetdb.equals("MySql"))
				{
				}
			}
			if(object.equals("Triggers"))
			{
				if(targetdb.equals("Oracle"))
				{
				}
				if(targetdb.equals("DB2"))
				{
				}
				if(targetdb.equals("MySql"))
				{
				}
			}
			if(object.equals("Functions"))
			{
				if(targetdb.equals("Oracle"))
				{
				}
				if(targetdb.equals("DB2"))
				{
				}
				if(targetdb.equals("MySql"))
				{
				}
			}
		}




	}
	

}
