package com.lakshya;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.sql.Statement;
import java.util.*;
class MigratorUI extends JFrame implements ActionListener,ItemListener,FocusListener{

	Image img;
	URL iurl;
	JRadioButton rb1,rb2,rb3,r1,r2;
	ButtonGroup bg1,bg2;
	JLabel l,l1,s,s1,s2,s3,s4,d,d1,d2,d3,d4,d5,stn,dn,de;
	JPanel src,dest;
	JComboBox scb1,dcb1,ecb,sttf;
	JTextField stf1,stf2,dtf1,dtf2,tdn,                                                                                                                              dntf;
	JPanel p1,p2;
	TextField stf3,dtf3;
	JButton button;
	Connection scon,tcon;
	String sdriver,url,user,pwd;
	String tdriver,turl,tuser,tpwd;
	MigratorUI(){
		Container c=getContentPane();
		setLayout(null);
		c.setBackground(Color.cyan);
		Font f=new Font("Arial",Font.BOLD,30);
		l1=new JLabel("Data Base Migrator");
		l1.setFont(f);
		l1.setBounds(100, 5, 300, 50);
		c.add(l1);

		l=new JLabel("Migrate Into....");
		l.setBounds(20, 50, 80, 20);
		c.add(l);
		rb1=new JRadioButton("SameDB");
		rb2=new JRadioButton("DiffDB");
		rb3=new JRadioButton("Document");
		rb1.setBounds(100, 80, 100, 20);
		rb2.setBounds(200, 80, 100, 20);
		rb3.setBounds(300, 80, 100, 20);
		c.add(rb1);
		c.add(rb2);
		c.add(rb3);
		bg1=new ButtonGroup();
		bg1.add(rb1);
		bg1.add(rb2);
		bg1.add(rb3);
		r1=new JRadioButton("All Tables");
		r2=new JRadioButton("Specific");
		de=new JLabel("Save The Document with An Extension");
		de.setBounds(80, 120, 300, 20);
		c.add(de);
		de.setVisible(false);

		ecb=new JComboBox();
		ecb.setBounds(320, 120, 100, 20);
		ecb.addItem("--select--");
		ecb.addItem(".doc");
		ecb.addItem(".txt");
		ecb.addItem(".csv");
		c.add(ecb);
		ecb.setVisible(false);

		

		s=new JLabel("Source");
		s.setBounds(100, 150, 100, 20);
		c.add(s);
		s.setVisible(false);

		s1=new JLabel("DataBase");
		s1.setBounds(20, 190, 100, 20);
		c.add(s1);
		s1.setVisible(false);

		scb1=new JComboBox();
		scb1.setBounds(100, 190, 100, 20);
		scb1.addItem("--select");
		scb1.addItem("MsAccess");
		scb1.addItem("Mysql");
		scb1.addItem("Oracle");
		c.add(scb1);
		scb1.setVisible(false);
        scb1.addItemListener(this);
		s2=new JLabel("DSN");
		s2.setBounds(20, 230, 100, 20);
		c.add(s2);
		s2.setVisible(false);
		

		stf1=new JTextField(20);
		stf1.setBounds(100, 230, 100, 20);
		c.add(stf1);
		stf1.setVisible(false);
        stf1.addFocusListener(this);
        
		s3=new JLabel("User Name");
		s3.setBounds(20, 270, 100, 20);
		c.add(s3);
		s3.setVisible(false);

		stf2=new JTextField(20);
		stf2.setBounds(100, 270, 100, 20);
		c.add(stf2);
		stf2.setVisible(false);
		stf2.addFocusListener(this);

		s4=new JLabel("Password");
		s4.setBounds(20, 310, 100, 20);
		c.add(s4);
		s4.setVisible(false);

		stf3=new TextField(20);
	    stf3.setText("");
	    stf3.setEchoChar('*');
		stf3.setBounds(100, 310, 100, 20);
		c.add(stf3);
		stf3.setVisible(false);

		d=new JLabel("Target");
		d.setBounds(350, 150, 100, 20);
		c.add(d);
		d.setVisible(false);

		d5=new JLabel("DataBase");
		d5.setBounds(250, 190, 100, 20);
		c.add(d5);
		d5.setVisible(false);

		dcb1=new JComboBox();
		dcb1.addItem("select--");
		dcb1.addItem("MsAccess");
	    dcb1.addItem("Mysql");
	     dcb1.addItem("Oracle");
		dcb1.setBounds(350, 190, 100, 20);
		c.add(dcb1);
		dcb1.setEnabled(false);
		dcb1.setVisible(false);
		//dcb1.addItemListener(this);

		d2=new JLabel("DSN");
		d2.setBounds(250, 230, 100, 20);
		c.add(d2);
		d2.setVisible(false);

		dtf1=new JTextField(20);
		dtf1.setBounds(350, 230, 100, 20);
		c.add(dtf1);
		dtf1.setVisible(false);

		d3=new JLabel("User Name");
		d3.setBounds(250, 270, 100, 20);
		c.add(d3);
		d3.setVisible(false);

		dtf2=new JTextField(20);
		dtf2.setBounds(350, 270, 100, 20);
		
		c.add(dtf2);
		dtf2.setVisible(false);
		dtf2.setText("");
		d4=new JLabel("Password");
		d4.setBounds(250, 310, 100, 20);
		c.add(d4);
	
		d4.setVisible(false);

		//dtf3=new JPasswordField(20);
		dtf3=new TextField(20);
		dtf3.setBounds(350, 310, 100, 20);
		dtf3.setEchoChar('*');
		dtf3.setText("");
		c.add(dtf3);
		dtf3.setVisible(false);
		r1.setBounds(90,360, 100, 20);
		r2.setBounds(190, 360, 100, 20);
		bg2=new ButtonGroup();
		bg2.add(r1);
		bg2.add(r2);
		c.add(r1);
		c.add(r2);
        
		//Table Name
		stn=new JLabel("Table Name");
		stn.setBounds(90, 390, 100, 20);
		c.add(stn);
		stn.setVisible(false);
		sttf=new JComboBox();
		sttf.setBounds(180, 390, 100, 20);
		c.add(sttf);
		sttf.setVisible(false);
		
		//button
		button =new JButton();
		button.setBounds(180,420,100,30);
		button.setVisible(false);
		
		c.add(button);
		
		r1.setVisible(false);
		r2.setVisible(false);
		rb1.addActionListener(this);
		rb2.addActionListener(this);
		rb3.addActionListener(this);
		r1.addActionListener(this);
		r2.addActionListener(this);
		button.addActionListener(this);
		

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


	}
	

	//Listeners implementation code
	
	public void focusGained(FocusEvent f){}	
	
	public void focusLost(FocusEvent f){
	if(f.getSource()==stf1)
	{     bg2.clearSelection();
	      sttf.removeAllItems();
	      stf2.setText("");
	      sttf.setVisible(false);
		  stn.setVisible(false);
	     
		 if( ( (String)scb1.getSelectedItem()).equalsIgnoreCase("msaccess"))
         { 
			 if(!(stf1.getText().equals("")))
     		  {r1.setEnabled(true);
   			    r2.setEnabled(true);
   			 
   			    }
         }
	}//endif
	if(f.getSource()==stf2)
     {
		    sttf.removeAllItems();
		    bg2.clearSelection();
		    sttf.setVisible(false);
		    stn.setVisible(false);
		    
     	   if(!(stf2.getText().equals(""))&&(!(stf3.getText().equals(""))||stf3.getText().equals(""))){
     	        
     	                             r1.setEnabled(true);
		                            r2.setEnabled(true);
		                            stn.setVisible(false);
		                    		sttf.setVisible(false);
		                            //JOptionPane.showMessageDialog(this,"other tan msaccess");
		                            
		                            }
     	 
     	 
		}//end of if
	}//end of focusLost()

          
         
	
	public void actionPerformed(ActionEvent ie){
		if(ie.getSource()==button)
		{
			if(rb3.isSelected())
			          saveFile();		
			
			else
				{validateDetails();
			    if(tcon!=null)
                 	migrate();		
				}
			
			
			
			
		}//end of the  listener implementation for button
		if(ie.getSource()==rb1){
			initial();
			d5.setVisible(false);
			dcb1.setVisible(false);
			dcb1.setEditable(false);
			stn.setVisible(false);
			sttf.setVisible(false);
			de.setVisible(false);
			ecb.setVisible(false);
			
		}
		if(ie.getSource()==rb2)
		{
			initial();
			 r1.setSelected(false);
			 r2.setSelected(false);
			dcb1.setVisible(true);
			if(scb1.getSelectedIndex()==0)
			dcb1.setEditable(false);
			d5.setVisible(true);
			
			stn.setVisible(false);
			sttf.setVisible(false);
			de.setVisible(false);
			ecb.setVisible(false);
			
		
		}

		if(ie.getSource()==rb3)
		{
			scb1.setSelectedIndex(0);
             stf1.setText("");
			stf2.setText("");
			dtf1.setText("");
			dtf2.setText("");
			                                                                                                            
			 stf3.setText("");
			 dtf3.setText("");
			 button.setText("save");
			 button.setToolTipText("save the source data base records into document");
			 button.setVisible(true);
			r1.setVisible(false);
			r2.setVisible(false);
			s.setVisible(true);
			s1.setVisible(true);
			s2.setVisible(true);
			s3.setVisible(true);
			s4.setVisible(true);
			stf1.setVisible(true);
			stf2.setVisible(true);
			stf3.setVisible(true);
			scb1.setVisible(true);
			stn.setVisible(false);
			sttf.setVisible(false);
							
			d.setVisible(false);
			d2.setVisible(false);
			d3.setVisible(false);
			d4.setVisible(false);
			d5.setVisible(false);
			dtf1.setVisible(false);
			dtf2.setVisible(false);
			dtf3.setVisible(false);
			dcb1.setVisible(false);
			de.setVisible(true);
			ecb.setVisible(true);
			


		}
		if(ie.getSource()==r1)
		{
			
			scon=sourceConnection();
		}
		
		
		if(ie.getSource()==r2)
		{
			sttf.removeAllItems();			
			String sitem=(String)scb1.getSelectedItem();
			scon=sourceConnection();
			if (scon==null) bg2.clearSelection();
			else{
			ResultSet rs=null;
			try{
				if(sitem.equals("Oracle"))
				{
					Statement stmt=scon.createStatement();
					rs=stmt.executeQuery("select * from tab");
				}
				else
				{ DatabaseMetaData alpha = scon.getMetaData();
				  rs = alpha.getTables(null, null, null,
							new String[] { "TABLE" });
				  }
	              sttf.addItem("--select--");
				 //for table names
				 while (rs.next())
					{   if(sitem.equals("Oracle"))sttf.addItem(rs.getString("TNAME"));
					else sttf.addItem(rs.getString("TABLE_NAME"));
						
					}
				
				 stn.setVisible(true);
				 sttf.setVisible(true);
			}
		   
			catch(Exception e){e.printStackTrace(); 
		    
			}//end of catch
			}//end of else 
		}//end of if	
				
			  
	}//end of Listener


	
	
	public void itemStateChanged(ItemEvent ie)
	{
		
		
	    
		
		if(ie.getSource()==dcb1)
		{   String ditem=(String)dcb1.getSelectedItem();
		    destinationSetting(ditem);
		    
		  
		}//end of dcb1 option
		
	
		if(ie.getSource()==scb1)
		{   stf1.setText("");
		    stf2.setText("");
		    stf3.setText("");
			bg2.clearSelection();
			sttf.removeAllItems();
			String sitem=(String)scb1.getSelectedItem();
			//samedb is seleced 
			if(rb1.isSelected()) {
				dcb1.setSelectedItem(sitem);
				destinationSetting(sitem);
				}
			//if diffdb is selected 
			else{
			if(sitem.equals("--select"))dcb1.setEnabled(false);
			else{
			dcb1.setEnabled(true);
			//removing previous items in the dcb1
			int l=dcb1.getItemCount();
    		for( int i=l-1;i>0;i--)
    		   dcb1.removeItemAt(i);
    		    			
		    //add new items in the dcb1
			dcb1.addItem("MsAccess");
		    dcb1.addItem("Mysql");
		    dcb1.addItem("Oracle");
			r1.setEnabled(false);
			r2.setEnabled(false);
			stf1.setText("");
			stf2.setText("");
			stn.setVisible(false);
			sttf.setVisible(false);
			//remove the item which is selected in the source when diffdb is selected
			 dcb1.removeItem(sitem);
			 dcb1.addItemListener(this);			 
			}	
		
			}
		
		   if(sitem.equalsIgnoreCase("MSACCESS"))
		   {   s3.setVisible(false);
		       stf2.setVisible(false);
		       s4.setVisible(false);
				stf3.setVisible(false);
		   }
		   
		   else 
		   {s3.setVisible(true);
	       stf2.setVisible(true);
	       s4.setVisible(true);
			stf3.setVisible(true);
			}
			
		}	//end of the scb1 option
}		  
		
	
	//user defined Functions
	
	private void initial()
	 {
		    bg2.clearSelection();
		    r1.setVisible(true);
			r2.setVisible(true);
			r1.setEnabled(false);
			r2.setEnabled(false);
			button.setText("migration");
			button.setToolTipText("Transferdata from specified source to Target database");
			button.setVisible(true);
			
			//source details
			s.setVisible(true);
			s1.setVisible(true);
			s2.setVisible(true);
			s3.setVisible(true);
			s4.setVisible(true);
			scb1.setVisible(true);
			stf1.setVisible(true);
			stf2.setVisible(true);
			stf3.setVisible(true);
			
			//destination
			d.setVisible(true);
			d2.setVisible(true);
			d3.setVisible(true);
			d4.setVisible(true);
			dtf1.setVisible(true);
			dtf2.setVisible(true);
			dtf3.setVisible(true);
			
			//default values
			stf1.setText("");
			stf2.setText("");
			dtf1.setText("");
			dtf2.setText("");
			//tdn.setText("");                                                                                                                              
			stf3.setText("");
			dtf3.setText("");
		    scb1.setSelectedIndex(0);
		    dcb1.setSelectedIndex(0);
		    ecb.setSelectedIndex(0);
		 
	 }

	private Connection sourceConnection()
	{   Connection con=null;
	String sitem=(String)scb1.getSelectedItem();
	
	stn.setVisible(false);
	sttf.setVisible(false);
	
	if(sitem.equalsIgnoreCase("MYSQL"))
	{  
		sdriver="com.mysql.jdbc.Driver";
		  url="jdbc:mysql://localhost/"+stf1.getText();
	}
	else 
	{ sdriver="sun.jdbc.odbc.JdbcOdbcDriver";
	  url="jdbc:odbc:";
	  url=url.concat(stf1.getText());
	  
	}
	user=stf2.getText();
	pwd=stf3.getText();
	 
	try{
		
		ResultSet rs=null;
	    Class.forName(sdriver);
	     con=DriverManager.getConnection(url,user,pwd);
	    }
  catch(Exception e){
		
		e.printStackTrace(); 
	    JOptionPane.showMessageDialog(this,"invalid source details/There is no Supported Drivers ");
	    bg2.clearSelection();
	   r1.setEnabled(false);
		r2.setEnabled(false);
	    stn.setVisible(false);
		sttf.setVisible(false);
	  }
  
      return con;
	}
	
	private void saveFile()
   	{   
		 if(scb1.getSelectedIndex()==0)
			   JOptionPane.showMessageDialog(this,"Source database must be selected");
		   else if(stf1.getText().equals(""))
			   JOptionPane.showMessageDialog(this,"Source DSN must be specified");
		   else if(stf2.getText().equals("")&&scb1.getSelectedIndex()>1)
			   JOptionPane.showMessageDialog(this,"Source username must be specified");
		   else if(stf3.getText().equals("")&&scb1.getSelectedItem().equals("Oracle"))
			   JOptionPane.showMessageDialog(this,"Source password  must be specified");
		   else if(ecb.getSelectedIndex()==0)
			   JOptionPane.showMessageDialog(this,"document extension  must be specified");
		   else {
			   scon=sourceConnection();
		        if(scon==null) JOptionPane.showMessageDialog(this,"Invalid SourceDetails/There is no Supported Drivers ");
		        else{
		        	String s="",f="",f1="";
		         	s=(String) scb1.getSelectedItem();
                FileDialog fd=new FileDialog(this,"Save",FileDialog.SAVE);
	            fd.setVisible(true);
	           f1=fd.getDirectory()+fd.getFile();
	          System.out.println("filename issssssssssssssssssssssssssssssss"+f);
	              if(!f1.equalsIgnoreCase("nullnull"))
	              {  
	            	  f=f1+ecb.getSelectedItem();
	            	  Copy c=new Copy();
	                  c.dump(s,f,scon);
	                  JOptionPane.showMessageDialog(this,"Migration done successfull");
	           
		        }
		        }
   	    /* JFileChooser fc=new JFileChooser("save");
   		int i=fc.showSaveDialog(tdn);
   		if(i==JFileChooser.APPROVE_OPTION){
   			System.out.println(fc.getCurrentDirectory()+"\\"+ fc.getSelectedFile().getName());
   		}
   		else
   		{System.out.println("else loop");}*/
   		//return s;
		        }
   	}
	void destinationSetting(String ditem)
	{     dtf1.setText("");
	     dtf2.setText("");
         dtf3.setText("");
		if(ditem.equalsIgnoreCase("MSACCESS"))
		   {
			   d3.setVisible(false);
		       dtf2.setVisible(false);
		       d4.setVisible(false);
				dtf3.setVisible(false);
		   }
		   
		   else 
		   {
			   
		      d3.setVisible(true);
	          dtf2.setVisible(true);
	          d4.setVisible(true);
			  dtf3.setVisible(true);
		   }
	}
	private void validateDetails()
	{
	   if(scb1.getSelectedIndex()==0)
		   JOptionPane.showMessageDialog(this,"Source database must be selected");
	   else if(stf1.getText().equals(""))
		   JOptionPane.showMessageDialog(this,"Source DSN must be specified");
	   else if(stf2.getText().equals("")&&scb1.getSelectedIndex()>1)
		   JOptionPane.showMessageDialog(this,"Source username must be specified");
	   else if(stf3.getText().equals("")&&scb1.getSelectedItem().equals("Oracle"))
		   JOptionPane.showMessageDialog(this,"Source password  must be specified");
	   else if(rb2.isSelected()&& dcb1.getSelectedIndex()==0)
		   JOptionPane.showMessageDialog(this,"Target database must be selected");
	   else if(dtf1.getText().equals(""))
		   JOptionPane.showMessageDialog(this,"Target DSN must be specified");
	   else if(dtf2.getText().equals("")&&dcb1.getSelectedIndex()>1)
		   JOptionPane.showMessageDialog(this,"Target username must be specified");
	   else if(dtf3.getText().equals("")&&dcb1.getSelectedItem().equals("Oracle"))
		   JOptionPane.showMessageDialog(this,"Target password  must be specified");
	   else if(!(r1.isSelected()||r2.isSelected()))
		   		   JOptionPane.showMessageDialog(this,"select the number of tables must be selected");
	  
	   else
	   {
		   String ditem=(String)dcb1.getSelectedItem();
		   
		
		if(ditem.equalsIgnoreCase("MYSQL"))
		{  
			tdriver="com.mysql.jdbc.Driver";
			  turl="jdbc:mysql://localhost/"+dtf1.getText();
		}
		else 
		{ tdriver="sun.jdbc.odbc.JdbcOdbcDriver";
		  turl="jdbc:odbc:";
		  turl=turl.concat(dtf1.getText());
		  
		}
		tuser=dtf2.getText();
		tpwd=dtf3.getText();
		
		
		try{
			
		   
		   Class.forName(tdriver);
		   tcon=DriverManager.getConnection(turl,tuser,tpwd);
			
		   }
		catch (Exception e)
		{e.printStackTrace();
		JOptionPane.showMessageDialog(this,"Invalid target Details/There is no Supported Drivers ");  
	        
		
	    }
	   }
	
	}
	private void migrate()
	{   String sourcedb=(String) scb1.getSelectedItem();
	    String targetdb=(String) dcb1.getSelectedItem();
	    String tabname="";
	    int i=0;
	  
	   if(sttf.getSelectedIndex()>0)   tabname=(String)sttf.getSelectedItem();
	    CreateTabs2 ct=new CreateTabs2();
		 i=ct.generate(this,scon, tcon, sourcedb, targetdb,tabname);
		
		if(i==0)JOptionPane.showMessageDialog(this,"unable to create tables.Tables may have the column name which is the key word of the targetDatabase")	;
		else
		{	
		
		CreateMigration load;
	
		try {
			load = new CreateMigration(sdriver,url,user,pwd,tdriver,turl,tuser,tpwd,sourcedb);
		
					
		   if(tabname=="")
			 load.generate(null);
			
		     else 
			    load.generate(tabname);
			    Migration m=new Migration();
			    m.init("output\\migration.xml");
				m.migrate();
				JOptionPane.showMessageDialog(this,"Migration done successfull");
			    //initial();
			
		}
		catch (Exception e) {e.printStackTrace();JOptionPane.showMessageDialog(this,"unable to migrate.Check the details and Try again");}
		
	}
	}
	
	


	public static void main(String[] args) {
		MigratorUI t=new MigratorUI();
		t.setTitle("Migration");
		t.setSize(600, 600);
		t.setVisible(true);

	}

}
