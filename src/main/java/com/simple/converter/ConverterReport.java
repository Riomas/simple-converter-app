/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.simple.converter;

import org.apache.log4j.Logger;

/**
 *
 * @author Administrator
 */
public class ConverterReport {

    public final static Logger statictics = Logger.getLogger("Statistics");

    public final static Logger badFiles = Logger.getLogger("BadFiles");

    public final static Logger notSupportedExtention = Logger.getLogger("NotSupportedFiles");

    public final static Logger convertedFiles = Logger.getLogger("ConvertedFiles");


}
