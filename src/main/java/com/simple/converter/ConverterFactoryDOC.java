/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.simple.converter;


import org.apache.log4j.Logger;
import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.artofsolving.jodconverter.office.OfficeException;


/**
 *
 * @author Administrator
 */
public class ConverterFactoryDOC extends ConverterFactoryOffice {

    private static final Logger _logger = Logger.getLogger(ConverterFactoryDOC.class);

    public static ConverterFactoryDOC getInstance()
    {
        return new ConverterFactoryDOC();
    }

    @Override
    public void init(Document docSource, Document docTarget) {
        super.init(docSource, docTarget);
    }

    @Override
    public void startConvertion() {
        _logger.debug("Start converting "+source.getName());
        long startTime = System.currentTimeMillis();
        if (ConverterFactoryOffice.getOfficeManager()==null)
        {
            _logger.debug("convertion time: "+(System.currentTimeMillis()-startTime)+" ms");
            return;
        }

        OfficeDocumentConverter converter = new OfficeDocumentConverter(ConverterFactoryOffice.getOfficeManager());
         
        try {
            converter.convert(source, target);
            success = true;
        }
        catch (OfficeException e)
        {
            close();
            _logger.error(e.getMessage());
        }
        _logger.debug("convertion time: "+(System.currentTimeMillis()-startTime)+" ms");
    }

}
