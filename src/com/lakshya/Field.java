package com.lakshya;

import org.medfoster.sqljep.BaseJEP;

/**
 * Encapsulates a Field to be migrated (from, to, type, etc).
 * <p>
 * Mapping 'type' values to Java classes <table border=1 cellSpacing=0
 * summary="Field types and mapping to Java types">
 * <tr>
 * <th>'type' field</th>
 * <th>Java class</th>
 * </tr>
 * <tr>
 * <td>string</td>
 * <td>java.lang.String</td>
 * </tr>
 * <tr>
 * <td>short</td>
 * <td>java.lang.Short</td>
 * </tr>
 * <tr>
 * <td>integer</td>
 * <td>java.lang.Integer</td>
 * </tr>
 * <tr>
 * <td>long</td>
 * <td>java.lang.Long</td>
 * </tr>
 * <tr>
 * <td>logical</td>
 * <td>java.lang.Boolean</td>
 * </tr>
 * <tr>
 * <td>fixed(m,n)</td>
 * <td>java.math.BigDecimal</td>
 * </tr>
 * <tr>
 * <td>time</td>
 * <td>java.sql.Time</td>
 * </tr>
 * <tr>
 * <td>date</td>
 * <td>java.sql.Date</td>
 * </tr>
 * <tr>
 * <td>timestamp</td>
 * <td>java.sql.Timestamp</td>
 * </tr>
 * </table>
 */
public class Field
{

	/**
	 * field name or JEP
	 */
	final private String from;
	/**
	 * 'from' expression
	 */
	private BaseJEP jep;
	/**
	 * 'from' index in ResultSet
	 */
	private int columnIndex;
	/**
	 * field name
	 */
	final private String to;

	private int len = 0;
	private int dec = 0;

	private Class sqlType = null;

	/**
	 * Constructor for the Field object.
	 * 
	 * @param from
	 *            Name of the field in the source. This parameter can contain
	 *            SQLJEP expression. In the expression variables are names of
	 *            fields in the source.
	 * @param to
	 *            Name of the field in the target. This parameter can be
	 *            <code>null</code>. In this case this name of the field in
	 *            the target will be equals to <CODE>from</CODE> parameter.
	 * @param type
	 *            type of the {@link Field}. This parameter can be used in two
	 *            cases <br/> 1. Some <CODE>MigrationTarget</CODE> may not
	 *            have information about types of fields. For example <CODE>MigrationDBFTarget</CODE>.
	 *            When <CODE>MigrationDBFTarget</CODE> creates new DBF file it
	 *            doesn't have information about fields types. <br/> 2. Some
	 *            <CODE>MigrationSource</CODE> can convert types. For example
	 *            <CODE>MigrationDBFSource</CODE> represents dates only as
	 *            <CODE>java.util.Date</CODE> type but it can convert dates to
	 *            <CODE>java.sql.Time</CODE> or <CODE>java.sql.Timestamp</CODE>
	 *            or <CODE>java.sql.Date</CODE>.
	 */
	public Field(String from, String to, String type)
	{
		if (from == null || from.equals(""))
		{
			throw new IllegalArgumentException("'from' filed can't be empty");
		}
		if (to == null)
		{
			to = from;
		}
		if (to.equals(""))
		{
			throw new IllegalArgumentException("'to' filed can't be empty");
		}
		this.from = from;
		this.to = to;
		if (type == null || type.equals(""))
		{
			sqlType = null;
		}
		else if (type.regionMatches(true, 0, "string", 0, 6))
		{
			String d = type.substring(6).trim();
			if (parseWidth2(d))
			{
				sqlType = java.lang.String.class;
			}
		}
		else if (type.equalsIgnoreCase("short"))
		{
			sqlType = java.lang.Short.class;
			len = 5;
			dec = 0;
		}
		else if (type.equalsIgnoreCase("integer"))
		{
			sqlType = java.lang.Integer.class;
			len = 10;
			dec = 0;
		}
		else if (type.equalsIgnoreCase("bigdecimal"))
		{
			sqlType = java.math.BigDecimal.class;
			len = 20;
			dec = 20;
		}
		else if (type.equalsIgnoreCase("long"))
		{
			sqlType = java.lang.Long.class;
			len = 20;
			dec = 0;
		}
		else if (type.regionMatches(true, 0, "fixed", 0, 5))
		{
			String d = type.substring(5).trim();
			if (parseWidth(d))
			{
				sqlType = java.math.BigDecimal.class;
			}
		}
		else if (type.equals("date"))
		{
			sqlType = java.sql.Date.class;
		}
		else if (type.equalsIgnoreCase("timestamp"))
		{
			sqlType = java.sql.Timestamp.class;
		}
		else if (type.equals("time"))
		{
			sqlType = java.sql.Time.class;
		}
		else if (type.equals("blob"))
		{
		}
		else if (type.equals("logical") || type.equals("boolean"))
		{
			sqlType = java.lang.Boolean.class;
			len = 1;
			dec = 0;
		}
		else
		{
			throw new IllegalArgumentException("Undefined type:'" + type + "'");
		}
	}

	public Class getType()
	{
		return sqlType;
	}

	public String getTo()
	{
		return to;
	}

	public String getFrom()
	{
		return from;
	}

	public int getLength()
	{
		return len;
	}

	/**
	 * Valid only for type 'fixed'
	 */
	public int getDecimalCount()
	{
		return dec;
	}

	public BaseJEP getJEP()
	{
		return jep;
	}

	public void setJEP(BaseJEP jep)
	{
		this.jep = jep;
	}

	public int getColumnIndex()
	{
		return columnIndex;
	}

	public void setColumnIndex(int columnIndex)
	{
		this.columnIndex = columnIndex;
	}

	private boolean parseWidth(String d)
	{
		if (d.startsWith("(") && d.endsWith(")"))
		{
			int comma = d.indexOf(',');
			int w = 0;
			if (comma > 0)
			{
				try
				{
					w = Integer.valueOf(d.substring(1, comma));
					dec = Integer.valueOf(d
							.substring(comma + 1, d.length() - 1));
					len = w;
				}
				catch (NumberFormatException e)
				{
					w = -1;
				}
			}
			else
			{
				try
				{
					len = Integer.valueOf(d.substring(1, d.length() - 1));
				}
				catch (NumberFormatException e)
				{
					w = -1;
				}
			}
			if (w >= 0)
			{
				return true;
			}
		}
		else
		{
			return true;
		}
		return false;
	}

	private boolean parseWidth2(String d)
	{
		if (d.startsWith("(") && d.endsWith(")"))
		{
			int w = 0;
			try
			{
				len = Integer.valueOf(d.substring(1, d.length() - 1));
			}
			catch (NumberFormatException e)
			{
				w = -1;
			}
			if (w >= 0)
			{
				return true;
			}
		}
		else
		{
			return true;
		}
		return false;
	}

	/*
	 * public final static class BlobSetter implements FieldSetter { public void
	 * setField(PreparedStatement ps, ResultSet rs, String srcField, int tgtPos)
	 * throws SQLException { // Blobs need a little different data handling as
	 * other // data types provided by the ResultSet // Basically - Blob is an
	 * interface to the data which must be // got frist. boolean blobIsNull =
	 * true; // Flag for NULL blob content // Get the blob Blob srcBlob =
	 * rs.getBlob(srcField); // This must be checked immediately after the
	 * getBlob method.... blobIsNull = rs.wasNull() ? true : false ; // Get the
	 * metadata of the ResultSet ResultSetMetaData rsmd = rs.getMetaData(); //
	 * Check if the blob was NULL () if ( blobIsNull ) { // The destination Blob
	 * must be NULL to... ps.setNull(tgtPos, rsmd.getColumnType(
	 * rs.findColumn(srcField) ) ); } else { // The Blob data don't seat in the
	 * BLOB itself. // It must be accessed separately... InputStream is =
	 * srcBlob.getBinaryStream(); ps.setBinaryStream(tgtPos, is,
	 * (int)srcBlob.length()); } } }
	 */

	public String toString()
	{
		return "from=\"" + from + "\" to=\"" + to + "\" type=\""
				+ String.valueOf(sqlType) + "\"";
	}
}
