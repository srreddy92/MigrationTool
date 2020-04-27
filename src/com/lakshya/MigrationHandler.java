package com.lakshya;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * SAX handler to read the config file.
 */
class MigrationHandler extends DefaultHandler
{
	private Locator locator;

	private String value;
	private Migration migration;
	private Step currentStep;
	private boolean skipCurrent = false;

	MigrationHandler(Migration migration)
	{
		this.migration = migration;
	}

	public void setDocumentLocator(Locator locator)
	{
		this.locator = locator;
	}

	public void startElement(String namespaceURI, String localName,
			String qName, Attributes attr) throws SAXException
	{
		if (qName.equals("source"))
		{
			try
			{
				migration.setSource(attr.getValue("driverType"), attr
						.getValue("driver"), attr.getValue("url"), attr
						.getValue("username"), attr.getValue("password"), attr
						.getValue("quoted-names"));
			}
			catch (Exception e)
			{
				throw new SAXException(e);
			}
		}
		else if (qName.equals("target"))
		{
			try
			{
				migration.setTarget(attr.getValue("driverType"), attr
						.getValue("driver"), attr.getValue("url"), attr
						.getValue("username"), attr.getValue("password"), attr
						.getValue("quoted-names"));
			}
			catch (Exception e)
			{
				throw new SAXException(e);
			}
		}
		else if (qName.equals("step"))
		{
			String skip = attr.getValue("skip");
			if (skip != null && "true".equals(skip))
			{
				skipCurrent = true;
				return;
			}
			skipCurrent = false;
			currentStep = new Step(attr.getValue("name"), attr.getValue("log_level"));
			currentStep.setSourceTable(attr.getValue("source_table"));
			currentStep.setTargetTable(attr.getValue("target_table"));
			currentStep.setWhereCondition(attr.getValue("where"));
			currentStep.setClearTarget(attr.getValue("clear_target"));
			try
			{
				int slice = Integer.valueOf(attr.getValue("slice_size"));
				currentStep.setSlice(slice);
			}
			catch (NumberFormatException e)
			{
			}
			String stopOnError = attr.getValue("stop_on_error");
			if (stopOnError != null)
			{
				currentStep
						.setStopOnError("true".equalsIgnoreCase(stopOnError));
			}
			migration.addStep(currentStep);
		}
		else if (!skipCurrent && qName.equals("field"))
		{
			Field newField = new Field(attr.getValue("from"), attr
					.getValue("to"), attr.getValue("type"));
			currentStep.getFields().add(newField);
		}
		else if (qName.equals("log"))
		{
			Logger logger = Logger.getLogger("com.lakshya");
			String logLevel = attr.getValue("level");
			if (logLevel != null)
			{
				logger.setLevel(Level.parse(logLevel));
			}
		}
	}

	public void endElement(String namespaceURI, String localName, String rawName)
			throws SAXException
	{
		value = null;
	}

	public void characters(char[] ch, int start, int end) throws SAXException
	{
		value = new String(ch, start, end);
		value = value.trim();
	}

	public String toString()
	{
		String loc = "";
		if (locator != null)
		{
			loc += "Line " + locator.getLineNumber();
			int col = locator.getColumnNumber();
			if (col >= 0)
			{
				loc += ", Column " + col;
			}
		}
		else
		{
			loc = "";
		}
		return (currentStep != null) ? loc + " (STEP:" + currentStep.getName()
				+ ")" : loc;
	}
}
