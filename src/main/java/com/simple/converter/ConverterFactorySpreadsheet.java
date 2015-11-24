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
public class ConverterFactorySpreadsheet extends ConverterFactoryOffice {

    private static final Logger _logger = Logger.getLogger(ConverterFactorySpreadsheet.class);

    public static ConverterFactorySpreadsheet getInstance()
    {
        return new ConverterFactorySpreadsheet();
    }

    @Override
    public void init(Document docSource, Document docTarget) {
        super.init(docSource, docTarget);
//        if (outputFormat.equals(Constants.TXT))
//        {
//            target = new Document(target.getParent()+Document.separatorChar+getName().substring(0, getName().lastIndexOf('.')+1)+Constants.CSV);
//            _logger.debug("target: "+target);
//        }
    }

    @Override
    public void startConvertion() {

        OfficeDocumentConverter converter = new OfficeDocumentConverter(ConverterFactoryOffice.getOfficeManager());
         _logger.debug("Start converting "+source.getName());
        long startTime = System.currentTimeMillis();
        try {
            converter.convert(source, target);
            setModificationDate();
            target.copyTo(target.replaceExtention(Constants.TXT));
            target.deleteOnExit();
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
