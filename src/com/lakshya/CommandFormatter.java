package com.lakshya;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

import com.lakshya.MigrationJDBC;

/**
 * Logging formatter for logs. Is used in stepLog for {@link MigrationJDBC}.
 */
public class CommandFormatter extends Formatter
{
	public synchronized String format(LogRecord record)
	{
		return record.getMessage() + "\n";
	}
}
