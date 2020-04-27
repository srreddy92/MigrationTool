package com.lakshya;

import static java.util.logging.Level.FINER;
import static java.util.logging.Level.FINEST;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.medfoster.sqljep.BaseJEP;
import org.medfoster.sqljep.ResultSetJEP;

import com.lakshya.Field;
import com.lakshya.MigrationException;
import com.lakshya.MigrationSource;
import com.lakshya.MigrationTarget;
import com.lakshya.Step;

/**
 * MigrationJDBC is a source and a target for JDBC.
 * 
 * <p>
 * In the {@link Step} as source and taget can be the same MigartionJDBC object.
 * In this case it will use the same connection.
 * <p>
 * MigrationJDCB can use external connection by
 * {@link #setConnection(Connection conn)}.
 */
final public class MigrationJDBC implements MigrationSource, MigrationTarget
{
	private static final Logger logger = Logger.getLogger("com.lakshya.dmt");

	protected String driver;
	protected String url;
	protected String username = null;
	protected String password = null;
	protected boolean quotedNames = false;

	private String insertString = null;
	private String source = null;
	private String target = null;
	private ArrayList<Field> fields;
	private Connection conn;
	private PreparedStatement ps;
	private ResultSet srcRs = null;

	/**
	 * 
	 * @param driver
	 *            JDBC connection driver class.
	 * @param url
	 *            JDBC connection url.
	 */
	public void setSource(String driver, String url) throws MigrationException
	{
		this.setSource(driver, url, "", "");
	}

	public void setSource(String driver, String url, String username,
			String password) throws MigrationException
	{
		this.driver = driver;
		this.url = (url != null) ? url : "";
		this.username = username;
		this.password = password;
	}

	public void setTarget(String driver, String url, String username,
			String password) throws MigrationException
	{
		this.driver = driver;
		this.url = (url != null) ? url : "";
		this.username = username;
		this.password = password;
	}

	/**
	 * 
	 * @param driver
	 *            JDBC connection driver class.
	 * @param url
	 *            JDBC connection url.
	 */
	public void setTarget(String driver, String url) throws MigrationException
	{
		this.setTarget(driver, url, "", "");
	}

	/**
	 * 
	 * @param conn
	 *            External JDBC connection.
	 */
	public void setConnection(Connection conn)
	{
		this.conn = conn;
	}

	public void initSource(String source, Step step) throws MigrationException
	{
		this.source = source;
		
		if (this.quotedNames)
		{
			source = "\"" + source + "\"";
		}
		if (conn == null)
		{
			logger.fine("Open Connection:" + url);
			try
			{
				Class.forName(driver);
				if ("".equals(username) && "".equals(password))
				{
					conn = DriverManager.getConnection(url);
				}
				else
				{
					conn = DriverManager.getConnection(url, username, password);
				}

			}
			catch (Exception e)
			{
				throw new MigrationException(e);
			}
		}
		try
		{
			Statement stmt = conn.createStatement();
			if ("select".regionMatches(true, 0, source, 0, 6))
			{
				logger.finest(source);
				srcRs = stmt.executeQuery(source);
			}
			else if ("call ".regionMatches(true, 0, source, 0, 5))
			{
				CallableStatement st = conn.prepareCall(source);
				st.execute();
				srcRs = st.getResultSet();
			}
			else
			{
				logger.finest("select * from " + source);
				srcRs = stmt.executeQuery("select * from " + source);
			}
			/* compile 'from' field */
			for (Field field : step.getFields())
			{
				try
				{
					int col = srcRs.findColumn(field.getFrom());
					field.setColumnIndex(col - 1);
				}
				catch (SQLException se)
				{
					ResultSetJEP jep = new ResultSetJEP(field.getFrom());
					try
					{
						jep.parseExpression(srcRs);
						field.setJEP(jep);
					}
					catch (org.medfoster.sqljep.ParseException e)
					{
						throw new MigrationException(e);
					}
				}
			}
		}
		catch (SQLException e)
		{
			throw new MigrationException(e);
		}
	}

	public BaseJEP compileWhere(String whereCondition)
			throws org.medfoster.sqljep.ParseException
	{
		if (whereCondition != null)
		{
			ResultSetJEP where = new ResultSetJEP(whereCondition);
			where.parseExpression(srcRs);
			return where;
		}
		else
		{
			return null;
		}
	}

	public void initTarget(String target, Step step, boolean clear,
			String clearCmd, Logger stepLog) throws MigrationException
	{
		this.target = target;
		if (this.quotedNames)
		{
			target = "\"" + target + "\"";
			this.target = target;
		}
		this.fields = step.getFields();
		if (conn == null)
		{
			try
			{
				Class.forName(driver);
				if ("".equals(username) && "".equals(password))
				{
					conn = DriverManager.getConnection(url);
				}
				else
				{
					conn = DriverManager.getConnection(url, username, password);
				}
			}
			catch (Exception e)
			{
				throw new MigrationException(e);
			}
		}
		try
		{
			getPreparedStatement(fields);
			if (clear)
			{
				/*
				 * MaxDB driver for PreparedStatements and for
				 * CallableStatements the class CallableStatementSapDB is used
				 */
				if ("call ".regionMatches(true, 0, target, 0, 5))
				{
					if ("call ".regionMatches(true, 0, clearCmd, 0, 5))
					{
						CallableStatement st = conn.prepareCall(clearCmd);
						st.execute();
					}
					else
					{
						Statement del = conn.createStatement();
						del.executeUpdate(clearCmd);
					}
				}
				else
				{
					clearCmd = "DELETE FROM " + target;
					Statement del = conn.createStatement();
					del.executeUpdate(clearCmd);
				}
				if (stepLog != null && stepLog.isLoggable(FINEST))
				{
					stepLog.finest(clearCmd);
				}
				logger.info("Clearing finished");
			}
		}
		catch (SQLException e)
		{
			throw new MigrationException(". Clear cmd:'" + clearCmd + "'", e);
		}
	}

	public void setAutoCommit(boolean autocommit) throws MigrationException
	{
		try
		{
			conn.setAutoCommit(autocommit);
		}
		catch (SQLException e)
		{
			throw new MigrationException(e);
		}
	}

	public boolean next() throws MigrationException
	{
		try
		{
			return srcRs.next();
		}
		catch (SQLException e)
		{
			throw new MigrationException(e);
		}
	}

	public Comparable getColumnObject(int column) throws MigrationException
	{
		try
		{
			return (Comparable) srcRs.getObject(column + 1);
		}
		catch (SQLException e)
		{
			throw new MigrationException(e);
		}
	}

	public void storeRow(Object[] row, Logger stepLog)
			throws MigrationException
	{
		try
		{
			if (stepLog != null && stepLog.isLoggable(FINEST))
			{ // output to FILE
				StringBuilder str = new StringBuilder(insertString);
				rowToString(str, row);
				stepLog.finest(str.toString());
			}
			/*
			 * MaxDB driver for PreparedStatements and for CallableStatements
			 * the class CallableStatementSapDB is used
			 */
			if ("call ".regionMatches(true, 0, target, 0, 5))
			{
				for (int i = 0; i < row.length; i++)
				{
					((CallableStatement) ps).setObject(fields.get(i).getTo(),
							row[i]);
				}
			}
			else
			{
				for (int i = 0; i < row.length; i++)
				{
					ps.setObject(i + 1, row[i]);
				}
			}
			ps.execute();
		}
		catch (SQLException e)
		{
			StringBuilder str = new StringBuilder(insertString);
			rowToString(str, row);
			if (stepLog != null && stepLog.isLoggable(FINER))
			{ // output to FILE
				stepLog
						.finer("Error:" + e.getMessage() + "\t"
								+ str.toString());
			}
			throw new MigrationException(str.toString(), e);
		}
	}

	public void commit(Logger stepLog) throws MigrationException
	{
		if (stepLog != null && stepLog.isLoggable(FINEST))
		{ // output to FILE
			stepLog.finest("COMMIT\n");
		}
		try
		{
			conn.commit();
		}
		catch (SQLException e)
		{
			throw new MigrationException("COMMIT", e);
		}
	}

	public void close() throws MigrationException
	{
	}

	public void shutdown() throws MigrationException
	{
		if (conn != null)
		{
			try
			{
				conn.close();
			}
			catch (SQLException e)
			{
				throw new MigrationException("CLOSE", e);
			}
			conn = null;
		}
	}

	/**
	 * Gets the preparedStatement attribute of the Step object
	 * 
	 * @return The preparedStatement value
	 * @exception Exception
	 *                Description of Exception
	 */
	private void getPreparedStatement(ArrayList<Field> fields)
			throws SQLException
	{
		if ("call ".regionMatches(true, 0, target, 0, 5))
		{
			ps = conn.prepareCall(target);
			int i = target.indexOf('(');
			if (i > 0)
			{
				insertString = target.substring(0, i);
			}
			else
			{
				insertString = target;
			}
		}
		else
		{
			StringBuilder sql = new StringBuilder("INSERT INTO ");
			sql = sql.append(target);
			sql = sql.append(" (");
			int count = 0;
			for (Field field : fields)
			{
				count++;
				if (this.quotedNames)
				{
					sql = sql.append("\"" + field.getTo() + "\"");
				}
				else
				{
					sql = sql.append(field.getTo());
				}

				if (count != fields.size())
				{
					sql = sql.append(",");
				}
			}
			sql = sql.append(") VALUES ");
			insertString = sql.toString();
			sql = sql.append("(");
			for (int j = 0; j < count; j++)
			{
				sql = sql.append("?");
				if (j < count - 1)
				{
					sql = sql.append(",");
				}
			}
			sql = sql.append(")");
			try
			{
				ps = conn.prepareStatement(sql.toString());
			}
			catch (SQLException e)
			{
				throw e;
			}
		}
	}

	public static void rowToString(StringBuilder buf, Object row[])
	{
		if (row.length == 0)
		{
			buf.append("()");
		}
		else
		{
			for (int i = 0; i < row.length; i++)
			{
				buf.append(i == 0 ? '(' : ",");
				if (row[i] instanceof String)
				{
					buf.append("'");
				}
				buf.append(String.valueOf(row[i]));
				if (row[i] instanceof String)
				{
					buf.append("'");
				}
			}
			buf.append(")");
		}
	}

	public void setQuotedNames(boolean quotedNames)
	{
		this.quotedNames = quotedNames;
	}

}
