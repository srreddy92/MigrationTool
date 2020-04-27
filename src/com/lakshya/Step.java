package com.lakshya;

import static java.util.logging.Level.FINEST;
import static java.util.logging.Level.SEVERE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.medfoster.sqljep.BaseJEP;

/**
 * Step describes how to convert the source table to the target table.
 * <p>
 * Step supports two types of sources - <code>MigrationSource</code> and
 * <code>CallbackMigrationSource</code> based. The first fetches records by
 * {@link MigrationSource#next()} and
 * {@link MigrationSource#getColumnObject(int column)} methods. In the second
 * type all records fetch in the method
 * {@link CallbackMigrationSource#startParse()} and this method calls
 * {@link #step()} for putting the source records into target. There two types
 * of targets - <code>MigrationTarget</code> and
 * <code>CallbackMigrationTarget</code> based. The first stores records by in
 * the <code>Step()</code> method. The second type is used for target driven
 * migration process. Migration is running in
 * <code>CallbackMigrationTarget.startMigration()</code>. Callback based
 * source and callback based target can be used together.
 */
public class Step
{
	private static final Logger logger = Logger.getLogger("com.lakshya.Migrator");

	/**
	 * Name of the step
	 */
	private String name;
	/**
	 * Source table. In general this field is source depended. For MigrationJDBC
	 * source it can be prepared or callable statement. For MigrationDBFSource
	 * it is the path to a DBF file.
	 */
	private String src;
	/**
	 * SQLJEP's expression. Variables of expression are columns' names of source
	 * table. Expression is parsed by MigrationSource.compileWhere method.
	 */
	private String whereCondition;
	/**
	 * Compiled whereCondition field.
	 * 
	 * @see org.javahispano.MigrationSource#compileWhere(String)
	 */
	private BaseJEP where;
	/**
	 * Target table. In general this field is target depended. For MigrationJDBC
	 * target it can be prepared or callable statement. For MigrationDBFTarget
	 * it is the path to a DBF file.
	 */
	private String tgt;
	/**
	 * true if target table should cleared. Clear behaviour is depended on
	 * tagret. In clearCmd field custom clear command can be stored. For
	 * MigrationJDBC target <CODE>clearCmd</CODE> can contain callable
	 * statement. It will be called when <CODE>clear=true</CODE> and <CODE>clearCmd</CODE>
	 * starts with "call ".
	 */
	private boolean clear = false;
	/**
	 * @see #clear
	 */
	private String clearCmd = null;
	/**
	 * true if migration will be stopped by throwing an MigrationException when
	 * an exception in
	 * {@link MigrationTarget.storeRow(Object[] row, Logger stepLog)} is
	 * occured. Otherwise the exception will be logged into 'com.lakshya.dmt'
	 * logger and process will be continued.
	 */
	private boolean stopOnError = true;
	/**
	 * Number of records after which
	 * {@link MigrationTarget#commit(Logger stepLog)} will be called. <p/> Value
	 * 1 means autocommit is activated
	 * {@link MigrationTarget#setAutoCommit(boolean autocommit)}. <br/> Value 0
	 * means commit after all records. <p/> By defaut autocommit enable.
	 */
	private int slice = 1;

	/**
	 * All fields which will be migrated
	 */
	private ArrayList<Field> fields;

	/**
	 * Each step can have own logger. This logger is used in MigrationTarget.
	 * Typically target uses logger to store inforamtion about migration
	 * process. MigrationJDBC target in {@link java.util.logging.Level#FINEST}
	 * log level stores all sql statements. In
	 * {@link java.util.logging.Level#FINER} log level is stores statements
	 * errors.
	 */
	private final Logger stepLog;

	private MigrationSource source = null;
	private MigrationTarget target = null;

	/**
	 * This field is used only in {@link Step#step}. It's in class only for
	 * optimal memory management reasons.
	 */
	private Object[] row;
	/**
	 * Number of current row in migrate process. After migration it contains
	 * number of rows in source table.
	 */
	private int rows;
	/**
	 * Number of rows skipped by where condition.
	 */
	private int skipped = 0;
	/**
	 * Number of rows skipped by errors. This value is valid in case when <CODE>stopOnError==false</CODE>.
	 */
	private int skippedByErrors = 0;
	/**
	 * Number of rows which were inserted into target by
	 * {@link MigrationTarget#storeRow(Object[] row, Logger stepLog)}
	 */
	private int inserted = 0;

	/**
	 * Constructs the Step object.
	 * 
	 * @param name
	 *            Name of the step
	 * @param stepLogLevel
	 *            Name of the step's log level (<code>java.util.logging.Level</code>).
	 *            If parameter is not <code>null</code> then logger with
	 *            specified log level and with file handler will be created.
	 *            Handler creates log file with {@link Step#name} in the working
	 *            directory.
	 */
	public Step(String name, String stepLogLevel)
	{
		if (name == null || "".equals(name.trim()))
		{
			throw new IllegalArgumentException("Name of the step can't be empty");
		}
		this.name = name;
		fields = new ArrayList<Field>();
		Level stepLevel;
		if (stepLogLevel != null && stepLogLevel.length() > 0)
		{
			stepLevel = Level.parse(stepLogLevel);
		}
		else
		{
			stepLevel = Level.OFF;
		}
		if (stepLevel != Level.OFF)
		{
			stepLog = Logger.getLogger("com.lakshya.step." + name);
			if (stepLog != null)
			{
				try
				{
					FileHandler loggingFileHandler = new FileHandler(name + ".sql");
					stepLog.addHandler(loggingFileHandler);
					CommandFormatter format = new CommandFormatter();
					loggingFileHandler.setFormatter(format);
				}
				catch (IOException e)
				{
					logger.log(SEVERE, "", e);
				}
				stepLog.setLevel(stepLevel);
			}
		}
		else
		{
			stepLog = null;
		}
	}

	public ArrayList<Field> getFields()
	{
		return fields;
	}

	void init() throws MigrationException
	{
		try
		{
			if (target instanceof CallbackMigrationTarget && source instanceof CallbackMigrationSource)
			{
				throw new MigrationException("source and target can be callback based in the same time");
			}
			source.initSource(src, this);
			where = source.compileWhere(whereCondition);
			target.initTarget(tgt, this, clear, clearCmd, stepLog);
		}
		catch (MigrationException e)
		{
			if (e.getMessage() != null)
			{
				logger.severe(e.getMessage());
			}
			e.setStepName(name);
			throw e;
		}
		catch (org.medfoster.sqljep.ParseException e)
		{
			MigrationException me = new MigrationException(e);
			me.setStepName(name);
			throw me;
		}
	}

	void run() throws MigrationException
	{
		try
		{
			target.setAutoCommit(slice == 1);
			logger.info(name + ": autoCommit:" + (slice == 1));
			rows = 1;
			row = new Object[this.fields.size()];
			skipped = 0;
			skippedByErrors = 0;
			inserted = 0;
			if (source instanceof CallbackMigrationSource)
			{
				((CallbackMigrationSource) source).startParse();
			}
			else if (target instanceof CallbackMigrationTarget)
			{
				((CallbackMigrationTarget) target).startMigration();
			}
			else
			{
				while (step())
				{
				}
			}
			if (slice != 1)
			{
				try
				{
					target.commit(stepLog);
				}
				catch (MigrationException e)
				{
					logger.severe("COMMIT ROWS(" + inserted + ")");
					throw e;
				}
			}
			logger.info(name + ": Inserted rows: " + (inserted)
					+ (where != null ? ". Skipped by where condition: "	+ skipped : "")
					+ (skippedByErrors > 0 ? ". Skipped by errors:"	+ skippedByErrors : ""));
		}
		catch (MigrationException e)
		{
			e.setStepName(name);
			throw e;
		}
	}

	public boolean step() throws MigrationException
	{
		if (!source.next())
		{
			return false;
		}
		try
		{
			if (where != null && !applyWhere())
			{
				skipped++;
				rows++;
				logger.finest("Where condition row:" + rows);
				return true;
			}
		}
		catch (org.medfoster.sqljep.ParseException e)
		{
			throw new MigrationException(e);
		}
		for (int column = 0; column < fields.size(); column++)
		{
			Field field = fields.get(column);
			Object value = null;
			if (field.getJEP() != null)
			{
				try
				{
					value = field.getJEP().getValue();
				}
				catch (org.medfoster.sqljep.ParseException e)
				{
					throw new MigrationException("field:'" + field.toString()+ "'", e);
				}
			}
			else
			{
				value = source.getColumnObject(field.getColumnIndex());
				System.out.println("Getting " + field.getFrom() + ": " + value);
			}
			row[column] = value;
		}
		try
		{
			target.storeRow(row, stepLog);
			inserted++;
		}
		catch (MigrationException e)
		{
			if (stopOnError)
			{
				logger.severe(e.getMessage());
				throw e;
			}
			else
			{
				if (logger.isLoggable(FINEST))
				{
					logger.finest("Error:" + e.getCause().getMessage() + "\t" + e.getMessage());
				}
				skippedByErrors++;
			}
		}
		if (slice > 1 && inserted % slice == 0)
		{
			try
			{
				target.commit(stepLog);
			}
			catch (MigrationException e)
			{
				logger.severe("COMMIT ROWS(" + inserted + ")");
				throw e;
			}
			logger.fine(name + ": Inserted:" + inserted);
		}
		rows++;
		return true;
	}

	void close() throws MigrationException
	{
		try
		{
			source.close();
			target.close();
		}
		catch (MigrationException e)
		{
			e.setStepName(name);
		}
	}

	/**
	 * Starts migration process
	 */
	public void migrate() throws MigrationException
	{
		init();
		run();
		close();
	}

	boolean applyWhere() throws org.medfoster.sqljep.ParseException
	{
		Comparable result = where.getValue();
		if (result instanceof Boolean)
		{
			return (Boolean) result;
		}
		else
		{
			throw new org.medfoster.sqljep.ParseException("Wrong expression. 'where' condition must be boolean");
		}
	}

	public void setSource(MigrationSource source)
	{
		this.source = source;
	}

	public void setTarget(MigrationTarget target)
	{
		this.target = target;
	}

	public void setWhere(BaseJEP jep)
	{
		where = jep;
	}

	public String getSourceTable()
	{
		return src;
	}

	public void setSourceTable(String src)
	{
		if (src == null || "".equals(src.trim()))
		{
			throw new IllegalArgumentException("'source_table' can't be empty");
		}
		this.src = src;
	}

	public String getTargetTable()
	{
		return tgt;
	}

	public void setTargetTable(String target)
	{
		if (target == null || "".equals(target.trim()))
		{
			throw new IllegalArgumentException("'target_table' can't be empty");
		}
		this.tgt = target;
	}

	public String getWhereCondition()
	{
		return whereCondition;
	}

	public void setWhereCondition(String whereCondition)
	{
		this.whereCondition = (whereCondition != null && whereCondition.length() > 0) ? whereCondition : null;
		this.where = null;
	}

	public String getClearTarget()
	{
		return clearCmd;
	}

	public void setClearTarget(String clear)
	{
		if (clear != null)
		{
			this.clear = !"false".equalsIgnoreCase(clear);
			if (this.clear)
			{
				this.clear = !"no".equalsIgnoreCase(clear);
			}
			if (this.clear)
			{
				clearCmd = clear;
			}
		}
	}

	public int getSlice()
	{
		return slice;
	}

	public void setSlice(int slice)
	{
		this.slice = slice;
	}

	public boolean getStopOnError()
	{
		return stopOnError;
	}

	public void setStopOnError(boolean stopOnError)
	{
		this.stopOnError = stopOnError;
	}

	public String getName()
	{
		return name;
	}

	public int getSkippedByWhere()
	{
		return skipped;
	}

	public int getSkippedByErrors()
	{
		return skippedByErrors;
	}

	public int getInserted()
	{
		return inserted;
	}

	public String toString()
	{
		return name;
	}
}
