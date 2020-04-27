package com.lakshya;

public class MigrationException extends Exception
{
	private String stepName = null;

	public MigrationException()
	{
		super();
	}

	public MigrationException(Exception e)
	{
		super(e.getMessage());
		initCause(e);
		setStackTrace(e.getStackTrace());
	}

	public MigrationException(String str, Exception e)
	{
		super(e.getMessage() + "  " + str);
		initCause(e);
		setStackTrace(e.getStackTrace());
	}

	public MigrationException(String str)
	{
		super(str);
	}

	public void setStepName(String stepName)
	{
		this.stepName = stepName;
	}

	public String getStepName()
	{
		return stepName;
	}
}
