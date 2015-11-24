/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.simple.converter;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.log4j.Logger;

/**
 *
 * @author Administrator
 */
public class ConverterFactoryTXT extends AbstractConverterFactory {

    private static final Logger _logger = Logger.getLogger(ConverterFactoryTXT.class);

    public static AbstractConverterFactory getInstance() {
        _logger.debug("Build new : ConverterFactoryTXT");
        return new ConverterFactoryTXT();
    }
    private String outputEncoding = System.getProperty("file.encoding");

    public void setEncoding(String outputEncoding) {
        this.outputEncoding = outputEncoding;
    }

    @Override
    public void startConvertion() {
        _logger.debug("Start converting " + source.getName());

        BufferedInputStream streamData = null;
        BufferedReader in = null;
        BufferedWriter writer = null;
        try {

            CharsetDetector detector = new CharsetDetector();
            streamData = new BufferedInputStream(new FileInputStream(source));
            detector.setText(streamData);
            CharsetMatch match = detector.detect();
            in = new BufferedReader(new InputStreamReader(new FileInputStream(source), match.getName()));
            writer = new BufferedWriter(new FileWriterWithEncoding(target, outputEncoding));

            char[] cbuf = new char[1024 * 1024];
            int len = in.read(cbuf);
            while (len > 0) {
                writer.write(cbuf, 0, len);
                len = in.read(cbuf);
            }
            success = true;
        } catch (IOException e) {
            _logger.error(e.getMessage());
        }
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                _logger.error(e.getMessage());
            }
        }
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                _logger.error(e.getMessage());
            }
        }
        if (streamData != null) {
            try {
                streamData.close();
            } catch (IOException e) {
                _logger.error(e.getMessage());
            }
        }
    }
}
