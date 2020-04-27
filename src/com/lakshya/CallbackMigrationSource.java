package com.lakshya;

/**
 * Types of MigrationSources which are callback based. 
 * Example - XML SAX parser.
 */
public interface CallbackMigrationSource
{
	void startParse() throws MigrationException;
}
