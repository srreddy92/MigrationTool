package com.lakshya;

import java.util.logging.Logger;

/**
 * Abstract target for migration.
 * <p>
 * There are three types of methods. Migration level and step level and row
 * level. Migration level methods are using once per migration process (one XML
 * migration file). Step level methods are using for each step ('step' tag in
 * the XML migration file).
 * <p>
 * Target can use <CODE>Logger stepLog</CODE> for logging migration process.
 * <code>MigrationJDBCTarget</code> uses <CODE>stepLog</CODE> in <CODE>FINEST</CODE>
 * level for storing all SQL statements in a log file and <CODE>FINER</CODE>
 * level for logging SQL exceptions.
 */
public interface MigrationTarget
{
	/**
	 * Migration level method
	 */
	void setTarget(String driver, String url) throws MigrationException;

	public void setTarget(String driver, String url, String username,
			String password) throws MigrationException;

	public void setQuotedNames(boolean quotedNames);

	/**
	 * Step level method
	 */
	void initTarget(String target, Step step, boolean clear, String clearCmd,
			Logger stepLog) throws MigrationException;

	/**
	 * Row level method
	 */
	void storeRow(Object[] row, Logger stepLog) throws MigrationException;

	/**
	 * Step level method
	 */
	void setAutoCommit(boolean autocommit) throws MigrationException;

	/**
	 * Step level method
	 */
	void commit(Logger stepLog) throws MigrationException;

	/**
	 * Step level method
	 */
	void close() throws MigrationException;

	/**
	 * Migration level method
	 */
	void shutdown() throws MigrationException;
}
