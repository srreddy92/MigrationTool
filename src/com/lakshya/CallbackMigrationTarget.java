package com.lakshya;

/**
 * Types of MigrationTarget which are callback based. Example -
 * MigrationFreemarkerTarget.
 */

public interface CallbackMigrationTarget
{
	void startMigration() throws MigrationException;
}
