package com.lakshya;

import org.medfoster.sqljep.BaseJEP;

import com.lakshya.MigrationException;
import com.lakshya.Step;

/**
 * Abstract source for migration.
 * <p>
 * <code>MigrationSource</code> should have names of columns.
 * <p>
 * There are three types of methods. Migration level and step level and row
 * level. Migration level methods are using once per migration process (one XML
 * migration file). Step level methods are using for each step ('step' tag in
 * the XML migration file).
 */
public interface MigrationSource
{

	/**
	 * Migration level method
	 */
	void setSource(String driver, String url) throws MigrationException;

	public void setSource(String driver, String url, String username,
			String password) throws MigrationException;

	public void setQuotedNames(boolean quotedNames);

	/**
	 * Step level method
	 */
	void initSource(String source, Step step) throws MigrationException;

	/**
	 * Step level method
	 */
	BaseJEP compileWhere(String whereCondition)
			throws org.medfoster.sqljep.ParseException;

	/**
	 * Row level method
	 * <p>
	 * Gets the value of the designated column in the current row of this
	 * <code>MigrationSource</code> object as an <code>Comparable</code> in
	 * the Java programming language.
	 * 
	 * @return <code>true</code> if the new current row is valid;
	 *         <code>false</code> if there are no more rows
	 */
	boolean next() throws MigrationException;

	/**
	 * Row level method
	 * 
	 * @param columnIndex
	 *            the first column is 0, the second is 1, ...
	 * @return a <code>java.lang.Comparable</code> holding the column value
	 * @exception MigrationException
	 *                if a database access error occurs
	 */
	Comparable getColumnObject(int columnIndex) throws MigrationException;

	/**
	 * Step level method
	 */
	void close() throws MigrationException;

	/**
	 * Migration level method
	 */
	void shutdown() throws MigrationException;
}
