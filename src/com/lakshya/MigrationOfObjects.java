package com.lakshya;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
class MigrationOfObjects extends JFrame implements ActionListener
{
	JLabel objtype,source,target,sourceDriverlabel,sourceurllabel,sourceuseridlabel,sourcepwdlabel,targetDriverlabel,targeturllabel,targetuseridlabel,targetpwdlabel,sourceinfo,targetinfo;
	
	JComboBox objname,sourcedb,targetdb;
			
	JTextField sourceDriver,sourceurl,sourceuserid,sourcepwd,targetDriver,targeturl,targetuserid,targetpwd;
	
	JButton migrate,clear;
	
	Dimension dimension;
	
	Box objtypebox,sourcedbbox,targetdbbox,selectionbox,srcdriverbox,srcurlbox,srcuidbox,srcpwdbox,tgtdriverbox,tgturlbox,tgtuidbox,tgtpwdbox,sourcebox,targetbox,buttonbox,fullbox,sourceinfobox,targetinfobox;


	MigrationOfObjects()
	{
		setLayout(new FlowLayout(FlowLayout.CENTER));  
		dimension  = new Dimension(150,22);

		objtype = new JLabel("Migration Object");
		objtype.setPreferredSize(dimension);
		Vector objtypevector=new Vector();
		objtypevector.add("---Select---");
		objtypevector.add("Procedures");
		objtypevector.add("Triggers");
		objtypevector.add("Functions");
		objname = new JComboBox(objtypevector);
		objname.setPreferredSize(dimension);

		objtypebox = new Box(BoxLayout.X_AXIS);
		objtypebox.add(objtype);
		objtypebox.add(Box.createHorizontalStrut(5));
		objtypebox.add(objname);	

		source = new JLabel("Source Database");
		source.setPreferredSize(dimension);
		Vector sourcedbvector=new Vector();
		sourcedbvector.add("---Select---");
		sourcedbvector.add("Oracle");
		sourcedbvector.add("DB2");
		sourcedbvector.add("MySql");
		sourcedb = new JComboBox(sourcedbvector);
		sourcedb.setPreferredSize(dimension);
		
		sourcedbbox = new Box(BoxLayout.X_AXIS);
		sourcedbbox.add(source);
		sourcedbbox.add(Box.createHorizontalStrut(5));
		sourcedbbox.add(sourcedb);	


		target = new JLabel("Target Database");
		target.setPreferredSize(dimension);
		Vector targetdbvector=new Vector();
		targetdbvector.add("---Select");
		targetdbvector.add("Oracle");
		targetdbvector.add("DB2");
		targetdbvector.add("MySql");
		targetdb = new JComboBox(targetdbvector);
		targetdb.setPreferredSize(dimension);

		targetdbbox = new Box(BoxLayout.X_AXIS);
		targetdbbox.add(target);
		targetdbbox.add(Box.createHorizontalStrut(5));
		targetdbbox.add(targetdb);	
		
		selectionbox = new Box(BoxLayout.Y_AXIS);//this is the first selection box
		selectionbox.add(objtypebox);
		selectionbox.add(Box.createVerticalStrut(5));
		selectionbox.add(sourcedbbox);
		selectionbox.add(Box.createVerticalStrut(5));
		selectionbox.add(targetdbbox);
		
		
		sourceinfo = new JLabel("Enter Source Information");
		sourceinfo.setPreferredSize(dimension);
		sourceDriverlabel=new JLabel("Driver Name");
		sourceDriverlabel.setPreferredSize(dimension);
		sourceurllabel=new JLabel("URL");
		sourceurllabel.setPreferredSize(dimension);
		sourceuseridlabel=new JLabel("user id");
		sourceuseridlabel.setPreferredSize(dimension);
		sourcepwdlabel=new JLabel("Passowrd");
		sourcepwdlabel.setPreferredSize(dimension);
		
		sourceDriver=new JTextField();
		sourceDriver.setPreferredSize(new Dimension(50,20));
		sourceurl=new JTextField();
		sourceurl.setPreferredSize(new Dimension(50,20));
		sourceuserid=new JTextField();
		sourceuserid.setPreferredSize(new Dimension(50,20));
		sourcepwd=new JTextField();
		sourcepwd.setPreferredSize(new Dimension(50,20));
		
		sourceinfobox = new Box(BoxLayout.X_AXIS);
		sourceinfobox.add(sourceinfo);
		sourceinfobox.add(Box.createHorizontalStrut(5));

		srcdriverbox = new Box(BoxLayout.X_AXIS);
		srcdriverbox.add(sourceDriverlabel);
		srcdriverbox.add(Box.createHorizontalStrut(5));
		srcdriverbox.add(sourceDriver);	

		srcurlbox = new Box(BoxLayout.X_AXIS);
		srcurlbox.add(sourceurllabel);
		srcurlbox.add(Box.createHorizontalStrut(5));
		srcurlbox.add(sourceurl);	

		srcuidbox=new Box(BoxLayout.X_AXIS);
		srcuidbox.add(sourceuseridlabel);
		srcuidbox.add(Box.createHorizontalStrut(5));
		srcuidbox.add(sourceuserid);	

		srcpwdbox=new Box(BoxLayout.X_AXIS);
		srcpwdbox.add(sourcepwdlabel);
		srcpwdbox.add(Box.createHorizontalStrut(5));
		srcpwdbox.add(sourcepwd);	
		
		sourcebox=new Box(BoxLayout.Y_AXIS);//all source information
		sourcebox.add(sourceinfobox);
		sourcebox.add(Box.createVerticalStrut(5));
		sourcebox.add(srcdriverbox);
		sourcebox.add(Box.createVerticalStrut(5));
		sourcebox.add(srcurlbox);	
		sourcebox.add(Box.createVerticalStrut(5));
		sourcebox.add(srcuidbox);	
		sourcebox.add(Box.createVerticalStrut(5));
		sourcebox.add(srcpwdbox);	



		targetinfo = new JLabel("Enter Target Information");
		targetinfo.setPreferredSize(dimension);

		targetDriverlabel=new JLabel("Driver Name");
		targetDriverlabel.setPreferredSize(dimension);
		targeturllabel=new JLabel("URL");
		targeturllabel.setPreferredSize(dimension);
		targetuseridlabel=new JLabel("user id");
		targetuseridlabel.setPreferredSize(dimension);
		targetpwdlabel=new JLabel("Passowrd");
		targetpwdlabel.setPreferredSize(dimension);

		targetDriver=new JTextField();
		targetDriver.setPreferredSize(new Dimension(50,20));
		targeturl=new JTextField();
		targeturl.setPreferredSize(new Dimension(50,20));
		targetuserid=new JTextField();
		targetuserid.setPreferredSize(new Dimension(50,20));
		targetpwd=new JTextField();
		targetpwd.setPreferredSize(new Dimension(50,20));

		targetinfobox = new Box(BoxLayout.X_AXIS);
		targetinfobox.add(targetinfo);
		targetinfobox.add(Box.createHorizontalStrut(5));

		tgtdriverbox = new Box(BoxLayout.X_AXIS);
		tgtdriverbox.add(targetDriverlabel);
		tgtdriverbox.add(Box.createHorizontalStrut(5));
		tgtdriverbox.add(targetDriver);	

		tgturlbox = new Box(BoxLayout.X_AXIS);
		tgturlbox.add(targeturllabel);
		tgturlbox.add(Box.createHorizontalStrut(5));
		tgturlbox.add(targeturl);	

		tgtuidbox=new Box(BoxLayout.X_AXIS);
		tgtuidbox.add(targetuseridlabel);
		tgtuidbox.add(Box.createHorizontalStrut(5));
		tgtuidbox.add(targetuserid);	

		tgtpwdbox=new Box(BoxLayout.X_AXIS);
		tgtpwdbox.add(targetpwdlabel);
		tgtpwdbox.add(Box.createHorizontalStrut(5));
		tgtpwdbox.add(targetpwd);	
		
		targetbox=new Box(BoxLayout.Y_AXIS);//all target information
		targetbox.add(targetinfobox);
		targetbox.add(Box.createVerticalStrut(5));
		targetbox.add(tgtdriverbox);
		targetbox.add(Box.createVerticalStrut(5));
		targetbox.add(tgturlbox);	
		targetbox.add(Box.createVerticalStrut(5));
		targetbox.add(tgtuidbox);	
		targetbox.add(Box.createVerticalStrut(5));
		targetbox.add(tgtpwdbox);	
		
		migrate=new JButton("Migrate");
		migrate.setPreferredSize(dimension);
		migrate.addActionListener(this);
		clear=new JButton("Clear");
		clear.setPreferredSize(dimension);
		clear.addActionListener(this);

		buttonbox=new Box(BoxLayout.X_AXIS);
		buttonbox.add(migrate);
		buttonbox.add(Box.createHorizontalStrut(5));
		buttonbox.add(clear);	
		
		fullbox=new Box(BoxLayout.Y_AXIS);
		/*fullbox.add(objtypebox);
		fullbox.add(Box.createVerticalStrut(5));
		fullbox.add(sourcedbbox);
		fullbox.add(Box.createVerticalStrut(5));
		fullbox.add(targetdbbox);
		fullbox.add(Box.createVerticalStrut(5));*/
		fullbox.add(selectionbox);
		/*fullbox.add(Box.createVerticalStrut(5));
		fullbox.add(srcdriverbox);
		fullbox.add(Box.createVerticalStrut(5));
		fullbox.add(srcurlbox);
		fullbox.add(Box.createVerticalStrut(5));
		fullbox.add(srcuidbox);
		fullbox.add(Box.createVerticalStrut(5));
		fullbox.add(srcpwdbox);
		fullbox.add(Box.createVerticalStrut(5));*/
		fullbox.add(Box.createVerticalStrut(10));
		fullbox.add(sourcebox);
		fullbox.add(Box.createHorizontalStrut(5));
		fullbox.add(targetbox);
		fullbox.add(Box.createVerticalStrut(10));

		/*fullbox.add(tgtdriverbox);
		fullbox.add(Box.createVerticalStrut(5));
		fullbox.add(tgturlbox);
		fullbox.add(Box.createVerticalStrut(5));
		fullbox.add(tgtuidbox);
		fullbox.add(Box.createVerticalStrut(5));
		fullbox.add(tgtpwdbox);
		fullbox.add(Box.createVerticalStrut(5));
		fullbox.add(targetbox);
		fullbox.add(Box.createVerticalStrut(5));*/
		fullbox.add(buttonbox);
		fullbox.add(Box.createVerticalStrut(5));

		add(fullbox);
		sourceDriver.setText("");
		targetDriver.setText("");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(1000,1000);
		setVisible(true);
		
	}
	public void actionPerformed(ActionEvent ae)
	{
		if(ae.getSource()==migrate)
		{
			if (objname.getSelectedIndex()==0)
			{
				JOptionPane.showMessageDialog(this,"Object Type must be selected");
				
			}
			else if (sourcedb.getSelectedIndex()==0)
			{
				JOptionPane.showMessageDialog(this,"Source Database must be selected");
			}
			else if (targetdb.getSelectedIndex()==0)
			{
				JOptionPane.showMessageDialog(this,"Source Database must be selected");
			}
			else if(sourceDriver.getText().equals(""))
			{
				JOptionPane.showMessageDialog(this,"sourceDriver must be entered");
			}
			else if(sourceurl.getText().equals("")) 
			{
				JOptionPane.showMessageDialog(this,"Source Url must be entered");
			}
			else if(sourceuserid.getText().equals(""))
			{
				JOptionPane.showMessageDialog(this,"Source User Name must be entered");
			}
			else if(targetDriver.getText().equals(""))
			{
				JOptionPane.showMessageDialog(this,"Target Driver must be entered");
			}
			else if(targeturl.getText().equals(""))
			{
				JOptionPane.showMessageDialog(this,"Target Url must be entered");
			}
			else if(targetuserid.getText().equals(""))
			{
				JOptionPane.showMessageDialog(this,"Target UserName must be entered");
			}
			else
			{
				JOptionPane.showMessageDialog(this,"Will be sent to migration funtion");
				String objecttype=objname.getSelectedItem().toString();
				String srcdb=sourcedb.getSelectedItem().toString();
				String trgdb=targetdb.getSelectedItem().toString();
				String sourceinformation="";
					sourceinformation=sourceDriver.getText();
				System.out.println("LLLLL dddddddd "+sourceinformation);
				
				 System.out.println("LLLLL "+sourceinformation);
				
				sourceinformation=sourceinformation.concat("$");	

				sourceinformation=sourceinformation.concat(sourceurl.getText());	
			   sourceinformation=sourceinformation.concat("$");	
			   sourceinformation=sourceinformation.concat(sourceuserid.getText());	
			   sourceinformation=sourceinformation.concat("$");			
			   sourceinformation=sourceinformation.concat(sourcepwd.getText());	 
	           //sourceinformation=sourceinformation.concat("$");		
			   System.out.println("LLLLL "+sourceinformation);
				String targetinformation=sourceDriver.getText();
				
				targetinformation=targetinformation.concat("$");
				
				targetinformation=targetinformation.concat(targeturl.getText());	
				targetinformation=targetinformation.concat("$");
				targetinformation=targetinformation.concat(targetuserid.getText());	
				targetinformation=targetinformation.concat("$");
				targetinformation=targetinformation.concat(targetpwd.getText());
				//targetinformation=targetinformation.concat("$");	
				 System.out.println("LLLLL "+targetinformation);
				MigrationsClass mc=new MigrationsClass(objecttype,srcdb,trgdb,sourceinformation,targetinformation);

			}
		}
		else
		{
			objname.setSelectedIndex(0); 
			sourcedb.setSelectedIndex(0); 
			targetdb.setSelectedIndex(0);
			sourceDriver.setText("");
			sourceurl.setText("");
			sourceuserid.setText("");
			sourcepwd.setText("");
			targetDriver.setText("");
			targeturl.setText("");
			targetuserid.setText("");
			targetpwd.setText("");	
		}
	}
	public static void main(String args[])
	{
		MigrationOfObjects moo=new MigrationOfObjects();
	}

}