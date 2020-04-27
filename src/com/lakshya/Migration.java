package com.lakshya;

import static java.util.logging.Level.SEVERE;

import java.util.ArrayList;
import java.util.logging.Logger;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Main class for the migration process. Contains steps.
 */
public class Migration
{
	private static final Logger logger = Logger.getLogger("com.lakshya.Migrator");

	private MigrationSource source = null;
	private MigrationTarget target = null;
	private ArrayList<Step> steps = new ArrayList<Step>();

	void setSource(String driverType, String driver, String url)
			throws Exception
	{
		// source = (MigrationSource)createObjectByClassName(driverType);
		// source.setSource(driver, (url != null) ? url : "");
		this.setSource(driverType, driver, url, "", "", "false");
	}

	void setSource(String driverType, String driver, String url, String user,
			String password, String quotedNames) throws Exception
	{
		source = (MigrationSource) createObjectByClassName(driverType);
		source.setSource(driver, (url != null) ? url : "", user, password);
		if ("yes".equals(quotedNames) || "y".equals(quotedNames) || "true".equals(quotedNames))
		{
			source.setQuotedNames(true);
		}
	}

	void setTarget(String driverType, String driver, String url, String user,
			String password, String quotedNames) throws Exception
	{
		target = (MigrationTarget) createObjectByClassName(driverType);
		target.setTarget(driver, (url != null) ? url : "", user, password);
		if ("yes".equals(quotedNames) || "y".equals(quotedNames) || "true".equals(quotedNames))
		{
			target.setQuotedNames(true);
		}
	}

	void setTarget(String driverType, String driver, String url)
			throws Exception
	{
		// target = (MigrationTarget)createObjectByClassName(driverType);
		// target.setTarget(driver, (url != null) ? url : "");
		this.setTarget(driverType, driver, url, "", "", "false");
	}

	public void addStep(Step step)
	{
		steps.add(step);
	}

	public Step getStep(String name)
	{
		for (Step step : steps)
		{
			if (step.getName().equals(name))
			{
				return step;
			}
		}
		return null;
	}

	public MigrationSource getSource()
	{
		return source;
	}

	public MigrationTarget getTarget()
	{
		return target;
	}

	/**
	 * Parses XML file with migration's configuration. It uses JAXP SAX parser.
	 * In parsing time {@link MigrationSource}, {@link MigrationTarget} and all
	 * {@link Step} objects will be created.
	 * 
	 * @see com.lakshya.dmt.MigrationHandler
	 */
	public void init(String file) throws Exception
	{
		steps.clear();
		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser saxParser = spf.newSAXParser();
		XMLReader xmlReader = saxParser.getXMLReader();

		MigrationHandler handler = new MigrationHandler(this);
		xmlReader.setContentHandler(handler);
		logger.info("Parsing URI: " + file);
		xmlReader.parse(file);
		logger.info("file is taken");
		for (Step step : steps)
		{
			step.setSource(source);
			step.setTarget(target);
			
		}
	}

	public void migrate() throws MigrationException
	{
		logger.finest("Openning databank...");
		for (Step step : steps)
		{
			step.migrate();
		}
	}

	public void shutdown() throws MigrationException
	{
		logger.finest("Shutdown...");
		if (source != null)
		{
			source.shutdown();
		}
		if (target != null)
		{
			target.shutdown();
		}
	}

	static Object createObjectByClassName(String name) throws Exception
	{
		Class cl = Class.forName(name);
		Object comp = null;
		java.lang.reflect.Constructor[] constructors = cl.getConstructors();
		for (java.lang.reflect.Constructor constr : constructors)
		{
			Class[] params = constr.getParameterTypes();
			if (params.length == 0)
			{
				comp = constr.newInstance();
				return comp;
			}
		}
		throw new IllegalArgumentException("Can't find default constructor for class '" + name + "'");
	}

	/**
	 * Usage: java -cp XXXXX com.lakshya.dmt.Migration migration1.xml migration2.xml ...
	 */
	/*public static void main(String[] args)
	{
		if (args.length == 0)
		{
			System.out.println("Syntax: java -cp XXXXX com.lakshya.dmt.Migration migration1.xml migration2.xml ...");
			System.exit(-1);
		}
		logger.finest("Starting migration...");
		for (int i = 0; i < args.length; i++)
		{
			Migration m = new Migration();
			try
			{
				m.init(args[i]);
				m.migrate();
			}
			catch (SAXException e)
			{
				if (e.getException() != null)
				{
					logger.log(SEVERE, "", e.getException());
				}
				else
				{
					logger.log(SEVERE, "", e);
				}
			}
			catch (MigrationException e)
			{
				logger.log(SEVERE, "STEP:" + e.getStepName(), e);
			}
			catch (Exception e)
			{
				logger.log(SEVERE, "", e);
			}
			finally
			{
				try
				{
					m.shutdown();
				}
				catch (MigrationException e)
				{
					logger.log(SEVERE, "STEP SHUTDOWN:" + e.getStepName(), e);
				}
				catch (Exception e)
				{
					logger.log(SEVERE, "SHUTDOWN", e);
				}
			}
		}
	}*/
}
