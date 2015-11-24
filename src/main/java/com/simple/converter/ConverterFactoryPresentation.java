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
public class ConverterFactoryPresentation extends ConverterFactoryOffice {

    private static final Logger _logger = Logger.getLogger(ConverterFactoryPresentation.class);

    private Document targetPDF = null;
    
    public static ConverterFactoryPresentation getInstance()
    {
        return new ConverterFactoryPresentation();
    }

    @Override
    public void init(Document docSource, Document docTarget) {
        super.init(docSource, docTarget);
        if (outputFormat.equals(Constants.TXT))
        {
            targetPDF = new Document(docTarget.getParent()+Document.separatorChar+docTarget.getName().substring(0, docTarget.getName().lastIndexOf('.')+1)+Constants.PDF);
            _logger.debug("target: "+targetPDF);
        }
    }

    @Override
    public void startConvertion() {
         _logger.debug("Start converting "+source.getName());
         
        OfficeDocumentConverter converter = new OfficeDocumentConverter(ConverterFactoryOffice.getOfficeManager());
        
        long startTime = System.currentTimeMillis();
        try {
            converter.convert(source, targetPDF);
            //success = true;
        }
        catch (OfficeException e)
        {
            //close();
            _logger.error(e.getMessage());
        }
        close();
        if (targetPDF.exists())
        {
            AbstractConverterFactory converterFactory = ConverterFactoryPDF.getInstance();
            if (converterFactory!=null)
            {
                converterFactory.init(targetPDF, target);
                converterFactory.setOutputFormat(outputFormat);
                converterFactory.startConvertion();
                success = converterFactory.isConverted();
            }
            converterFactory=null;
        }
        targetPDF.delete();
        _logger.debug("convertion time: "+(System.currentTimeMillis()-startTime)+" ms");
    }

}
