package com.movienizer.data.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.movienizer.data.exception.ConfigException;

public class BaseSQLConfig {

    private static Logger log = Logger.getLogger(BaseSQLConfig.class.getName());

    private String configFileName;

    private final String ELEMENTNAME_ROOT = "SQLConfig";
    private final String ELEMENTNAME_SQLSET = "sqlSet";
    private final String ELEMENTNAME_RETRIEVALSQL = "retrievalSQL";
    private final String ATTRIBUTENAME_NAME = "name";
    private final String ATTRIBUTENAME_KEY = "key";

    private Map<String, Map<String, String>> retrievalSQLMapBySQLSet = null;

    public BaseSQLConfig(String configFileName) throws ConfigException {
        this.configFileName = configFileName;
        initSQLConfig();
    }

    /**
     * The method initializes the internal configuration map from an external
     * xml configuration file specified by {@link BaseSQLConfig#CONFIGFILENAME}.
     *
     * @throws ConfigException
     */
    private final synchronized void initSQLConfig() throws ConfigException {
        InputStream configStream;
        retrievalSQLMapBySQLSet = new HashMap<String, Map<String, String>>();
        try {
            configStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(configFileName);
            DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            if (configStream==null) {
                ConfigException c =  new ConfigException("BaseSQLConfig.initSQLConfig: config file: "+configFileName+" is missing.");
                log.throwing("BaseSQLConfig", "initSQLConfig", c);
                throw c;
            }
            Element root = dBuilder.parse(configStream).getDocumentElement();
            if (!root.getNodeName().equals(ELEMENTNAME_ROOT)) {
                ConfigException c = new ConfigException(
                        "SQLConfig.initSQLConfig: expected root element "
                                +ELEMENTNAME_ROOT+" while got "+root.getNodeName()+".");
                log.throwing("SQLConfig", "initSQLConfig", c);
                throw c;
            }

            NodeList attributeMapElements = root.getElementsByTagName(ELEMENTNAME_SQLSET);
            for (int i=0; i<attributeMapElements.getLength(); i++) {
                Node attributeMapElement = attributeMapElements.item(i);
                String objectSetName = attributeMapElement.getAttributes().getNamedItem(ATTRIBUTENAME_NAME).getNodeValue();
                Map<String, String> objectSetMap = new HashMap<String, String>();
                retrievalSQLMapBySQLSet.put(objectSetName, objectSetMap);

                if (attributeMapElement.getNodeType() == Node.ELEMENT_NODE) {
                    NodeList attributeMapSubElements = attributeMapElement.getChildNodes();
                    for (int j=0; j<attributeMapSubElements.getLength(); j++) {
                        Node retrievalSQLElement = attributeMapSubElements.item(j);
                        if (retrievalSQLElement.getNodeType() == Node.ELEMENT_NODE) {
                            if (retrievalSQLElement.getNodeName().equals(ELEMENTNAME_RETRIEVALSQL)) {
                                String key = retrievalSQLElement.getAttributes().getNamedItem(ATTRIBUTENAME_KEY).getNodeValue();
                                String retrievalSQLText = retrievalSQLElement.getTextContent();
                                objectSetMap.put(key, retrievalSQLText);
                            } else {
                                ConfigException c =  new ConfigException(
                                        "SQLConfig.initSQLConfig: unexpected element under /"
                                                +ELEMENTNAME_ROOT+"/"+ELEMENTNAME_SQLSET+": "+retrievalSQLElement.getNodeName()+".");
                                log.throwing("SQLConfig", "initSQLConfig", c);
                                throw c;
                            }
                        }
                    }
                }
            }
        } catch (ParserConfigurationException e) {
            log.throwing("SQLConfig", "initSQLConfig", e);
            ConfigException c = new ConfigException("SQLConfig.initSQLConfig: cannot parse config file: " + configFileName + ". " + e.getMessage(), e);
            throw c;
        } catch (SAXException e) {
            log.throwing("SQLConfig", "initSQLConfig", e);
            ConfigException c = new ConfigException("SQLConfig.initSQLConfig: cannot parse config file: " + configFileName + ". " + e.getMessage(), e);
            throw c;
        } catch (IOException e) {
            log.throwing("SQLConfig", "initSQLConfig", e);
            ConfigException c = new ConfigException("SQLConfig.initSQLConfig: IOException while initializing from config file: " + configFileName + ". "
                    + e.getMessage(), e);
            throw c;
        }
    }

    /**
     * The method returns retrieval SQL for a given sql set and key.
     *
     * @param sqlSetName
     * @param sqlKey
     * @return null if no retrieval SQL found in configuration
     * @throws ConfigException
     */
    public final String getRetrievalSQL(String sqlSetName, String sqlKey) throws ConfigException {
        Map<String, String> sqlSetConfigMap = retrievalSQLMapBySQLSet.get(sqlSetName);
        if (sqlSetConfigMap== null) {
            return null;
        } else {
            return sqlSetConfigMap.get(sqlKey);
        }
    }
}