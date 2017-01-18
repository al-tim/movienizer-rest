package com.movienizer.data.config;

import com.movienizer.data.exception.ConfigException;

public class SQLConfig {
    private static BaseSQLConfig baseSQLConfigInstance = null;
    public static final String CONFIGFILENAME = "META-INF/SQLConfig.xml";

    private static BaseSQLConfig getSQLConfigInstance() throws ConfigException {
        if (baseSQLConfigInstance == null) {
            synchronized (SQLConfig.class) {
                if(baseSQLConfigInstance == null){
                    baseSQLConfigInstance = new BaseSQLConfig(CONFIGFILENAME);
                }
            }
        }
        return baseSQLConfigInstance;
    }

    /**
     * The method returns retrieval SQL for a given sql set and key.
     *
     * @param sqlSetName
     * @param sqlKey
     * @return null if no retrieval SQL found in configuration
     * @throws ConfigException
     */
    public static String getRetrievalSQL(String sqlSetName, String sqlKey) throws ConfigException {
        return getSQLConfigInstance().getRetrievalSQL(sqlSetName, sqlKey);
    }
}