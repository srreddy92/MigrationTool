package com.lakshya;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class CreateMigration
{
	private String url;
	private String driver;
	private String username;
	private String password;
	private String turl;
	private String tdriver;
	private String tusername;
	private String tpassword;
    private String sourcedb;

	private DocumentBuilder builder;

	/**
	 * Creates a new instance of LoadDbMetaData
	 */
	public CreateMigration(String driver, String url, String username,
			String password,String tdriver, String turl, String tusername,
			String tpassword,String sourcedb) throws Exception
	{
		this.url = url;
		this.driver = driver;
		this.username = username;
		this.password = password;
		this.turl = turl;
		this.tdriver = tdriver;
		this.tusername = tusername;
		this.tpassword = tpassword;
        this.sourcedb=sourcedb;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(true);
		factory.setNamespaceAware(false);
		this.builder = factory.newDocumentBuilder();
	}

	/**
	 * @param table
	 * @param tableKey
	 * @return
	 * @throws Exception
	 */
	private Element getRootNode(String table, String tableKey) throws Exception
	{
		Document doc = builder.newDocument();
		Element root = (Element) doc.createElement("step");
		root.setAttribute("name", "migration-" + table);
		root.setAttribute("source_table", table);
		root.setAttribute("target_table", table);
		root.setAttribute("clear_target", "true");
		root.setAttribute("output", "file");
		root.setAttribute("sliced", "false");
		root.setAttribute("slice_size", "100");
		root.setAttribute("slice_key", tableKey);
		doc.appendChild(root);
		return root;
	}

	/**
	 * @param conn
	 * @param table
	 * @return
	 * @throws Exception
	 */
	private String getTableKey(Connection conn, String table) throws Exception
	{
		String pkColumn = "";

		/*
		 * DatabaseMetaData meta = conn.getMetaData(); ResultSet rs =
		 * meta.getPrimaryKeys(null, null, table); if (rs.next()) pkColumn =
		 * rs.getString("COLUMN_NAME");
		 */

		return pkColumn;
	}

	public void extractTable(Element step, String table) throws Exception
	{
		Connection conn = null;
		step.setAttribute("name", table);
		step.setAttribute("source_table", table);
		step.setAttribute("target_table", table);
		step.setAttribute("clear_target", "true");
		step.setAttribute("output", "file");
		step.setAttribute("sliced", "false");
		step.setAttribute("slice_size", "100");
		try
		{
			conn = getConnection();
			step.setAttribute("slice_key", getTableKey(conn, table));
			addFields(conn, step, table);
		}
		catch (Exception e)
		{
			throw new Exception(e.getMessage(), e);
		}
		finally
		{
			if (conn != null)
				conn.close();
		}
	}

	/**
	 * @return
	 * @throws Exception
	 */
	public Node extractTables() throws Exception
	{
		Document doc = builder.newDocument();
		Element tables = (Element) doc.createElement("tables");
		doc.appendChild(tables);

		List tablesList = getTableNames();
		Element table;
		for (Iterator i = tablesList.iterator(); i.hasNext();)
		{
			table = (Element) tables.getOwnerDocument().createElement("table");
			table.setAttribute("name", (String) i.next());
			tables.appendChild(table);
		}
		return tables;
	}

	
	private List getTableNames() throws Exception
	{
		Connection conn = null;
		List result = new ArrayList();
		try
		{
			conn = getConnection();

			
            if(sourcedb.equalsIgnoreCase("oracle"))
            {
                Statement sct=conn.createStatement();
                
            	ResultSet rs=sct.executeQuery("select * from tab");
            	while(rs.next())
            	{
            		result.add(rs.getString("TNAME"));
            	}
            }
            else{
            	DatabaseMetaData alpha = conn.getMetaData();
			ResultSet rs = alpha.getTables(null, null, null,
					new String[] { "TABLE" });

			while (rs.next())
			{  
				result.add(rs.getString("TABLE_NAME"));
			}
            }
			return result;
		}
		catch (Exception e)
		{
			throw new Exception(e.getMessage(), e);
		}
		finally
		{
			if (conn != null)
				conn.close();
		}
	}

	/**
	 * @param conn
	 * @param step
	 * @param table
	 * @throws Exception
	 */
	private void addFields(Connection conn, Element step, String table)
			throws Exception
	{
		DatabaseMetaData meta = conn.getMetaData();
		ResultSet rs = meta.getColumns(null, null, table, "%");
		Element field = null;
		while (rs.next())
		{
			field = (Element) step.getOwnerDocument().createElement("field");
			String columnName = rs.getString("COLUMN_NAME");
			String typeName = rs.getString("TYPE_NAME");
			int dataType = rs.getInt("DATA_TYPE");

			field.setAttribute("from", columnName);
			field.setAttribute("to", columnName);
			// field.setAttribute("type", typeName);
			field.setAttribute("type", getFieldType(dataType));
			field.setAttribute("value", "");
			step.appendChild(field);
		}
	}

	/**
	 * @param jdbcType
	 * @return
	 */
	private String getFieldType(int jdbcType)
	{
		String type;
		switch (jdbcType)
		{
			case java.sql.Types.REAL:
			case java.sql.Types.FLOAT:
			case java.sql.Types.DECIMAL:
			case java.sql.Types.DOUBLE:
			{
				type = "bigdecimal";
				break;
			}
			case java.sql.Types.BIT:
			case java.sql.Types.TINYINT:
			case java.sql.Types.INTEGER:
			case java.sql.Types.SMALLINT:
			{
				type = "integer";
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
				type = "string";
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
			{ type = "string";
			break;}

			default:
			{
				 type = "unknow"+jdbcType;

				break;
			}
		}
		return type;
	}

	/**
	 * @return
	 * @throws Exception
	 */
	private Connection getConnection() throws Exception
	{
		try
		{
			Class.forName(driver);
			return java.sql.DriverManager
					.getConnection(url, username, password);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	void generate(String table) throws Exception
	{

		Document doc = builder.newDocument();
		Element migration = (Element) doc.createElement("migration");
		doc.appendChild(migration);
		Element databank = (Element) doc.createElement("databank");
		migration.appendChild(databank);

		Element source = (Element) doc.createElement("source");
		source.setAttribute("url", url);
		source.setAttribute("driver", driver);
		source.setAttribute("driverType", "com.lakshya.MigrationJDBC");
		source.setAttribute("username", username);
		source.setAttribute("password", password);
		source.setAttribute("quoted-names", "false");
		databank.appendChild(source);

		Element target = (Element) doc.createElement("target");
		target.setAttribute("url", turl);
		target.setAttribute("driverType", "com.lakshya.MigrationJDBC");
		target.setAttribute("driver", tdriver);
		target.setAttribute("username", tusername);
		target.setAttribute("password", tpassword);
		target.setAttribute("quoted-names", "false");
		databank.appendChild(target);

		Element steps = (Element) doc.createElement("steps");
		migration.appendChild(steps);

		String filename = null;
		if (table != null)
		{
			filename = "/migration.xml";
			Element step = (Element) doc.createElement("step");
			extractTable(step, table);
			steps.appendChild(step);

			// steps.appendChild(step);
		}
		else
		{
			filename = "/migration.xml";

			List tablesList = getTableNames();
			for (Iterator i = tablesList.iterator(); i.hasNext();)
			{
				Element step = (Element) doc.createElement("step");
				extractTable(step, (String) i.next());
				steps.appendChild(step);
			}
		}

		DocumentDumper dd = new DocumentDumper();
		dd.dump(doc, "output",filename);
		
		/*
		 * <PRE>
		 * File f = new File("output"); 
		 * f.mkdirs(); 
		 * System.out.println("Writing migration to: " + filename); 
		 * TransformerFactory transFactory = TransformerFactory.newInstance(); 
		 * Transformer transformer = transFactory.newTransformer(); 
		 * DOMSource domSource = new DOMSource(doc); 
		 * File newXML = new File(filename); 
		 * FileOutputStream os = new FileOutputStream(newXML); 
		 * StreamResult result = new StreamResult(os); 
		 * transformer.transform(domSource, result);
		 * System.out.println(doc);
		 * </PRE>
		 */
	}

}