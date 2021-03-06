package com.bs.ifx.merge.entities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.io.PrintWriter;

/**
 * .
 * ErrorHandler to generate DocumentBuiler (File to NodeList)
 */
public class MergeErrorHandler implements ErrorHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MergeErrorHandler.class);

    private PrintWriter out;

    public MergeErrorHandler(final PrintWriter outV) {
        this.out = outV;
    }

    private String getParseExceptionInfo(final SAXParseException spe) {
        String systemId = spe.getSystemId();
        if (systemId == null) {
            systemId = "null";
        }

        String info = "URI=" + systemId + " Line=" + spe.getLineNumber() + ": " + spe.getMessage();
        return info;
    }

    public void warning(final SAXParseException spe) throws SAXException {
        LOGGER.warn(this.getParseExceptionInfo(spe));
    }

    public void error(final SAXParseException spe) throws SAXException {
        String message = "Error: " + this.getParseExceptionInfo(spe);
        throw new SAXException(message);
    }

    public void fatalError(final SAXParseException spe) throws SAXException {
        String message = "Fatal Error: " + this.getParseExceptionInfo(spe);
        throw new SAXException(message);
    }

}
