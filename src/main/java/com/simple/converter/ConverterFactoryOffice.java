/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.simple.converter;


import java.io.IOException;
import java.util.logging.Level;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeException;
import org.artofsolving.jodconverter.office.OfficeManager;
import org.artofsolving.jodconverter.process.MacProcessManager;
import org.artofsolving.jodconverter.process.ProcessManager;
import org.artofsolving.jodconverter.process.ProcessQuery;
import org.artofsolving.jodconverter.process.PureJavaProcessManager;
import org.artofsolving.jodconverter.process.UnixProcessManager;
import org.artofsolving.jodconverter.process.WindowsProcessManager;
import org.artofsolving.jodconverter.util.PlatformUtils;


/**
 *
 * @author Administrator
 */
public abstract class ConverterFactoryOffice extends AbstractConverterFactory {

    private static final Logger _logger = Logger.getLogger(ConverterFactoryOffice.class);

    private static OfficeManager officeManager = null;

    private static int retries = 0;

    public static OfficeManager getOfficeManager()
    {
        if (officeManager == null)
        {
            try {
                officeManager = new DefaultOfficeManagerConfiguration()
                    .setTaskExecutionTimeout(30000L)
                    .buildOfficeManager();
                if (officeManager!=null)
                    officeManager.start();
            }
            catch (NullPointerException e) {
                _logger.error(e.getMessage());
                close();
            }
            catch (IllegalStateException e) {
                _logger.error(e.getMessage());
                close();
            }
            catch (OfficeException e) {
                _logger.error(e.getMessage(), e);
                close();
            }
            if (officeManager==null && retries < 3)
            {
                retries++;
                _logger.warn("Retry to start OpenOffice = "+retries);
                getOfficeManager();
            }
            else
                retries=0;
        }
        return officeManager;

    }


    public static void close()
    {
        try{
            if (officeManager!=null)
                officeManager.stop();
        }
        catch (OfficeException e)
        {
            _logger.error(e.getMessage());
            ProcessManager process = new PureJavaProcessManager();
            if (PlatformUtils.isWindows())
                process = new WindowsProcessManager();
            else if (PlatformUtils.isLinux())
                process = new UnixProcessManager();
            else if (PlatformUtils.isMac())
                process = new MacProcessManager();
            
            String processRegex = "soffice.*" + Pattern.quote("socket,host=127.0.0.1,port=2002");
            try {
                ProcessQuery processQuery=new ProcessQuery(processRegex, "");
				long pid = process.findPid(processQuery);
                process.kill(null, pid);
                
            } catch (IOException e1) {
                 _logger.error(e1.getMessage());
            }
        }
        finally {
            officeManager=null;
        }
    }

    @Override
    public abstract void startConvertion();

}
