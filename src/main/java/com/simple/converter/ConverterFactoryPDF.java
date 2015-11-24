/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.simple.converter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import org.apache.log4j.Logger;
import org.pdfbox.exceptions.CryptographyException;
import org.pdfbox.exceptions.InvalidPasswordException;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.util.PDFText2HTML;
import org.pdfbox.util.PDFTextStripper;

/**
 *
 * @author Administrator
 */
public class ConverterFactoryPDF extends AbstractConverterFactory {

    private static final Logger _logger = Logger.getLogger(ConverterFactoryPDF.class);
    private String password = "password";

    public static AbstractConverterFactory getInstance() {
        _logger.debug("Build new : ConverterFactoryPDF");
        return new ConverterFactoryPDF();
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return this.password;
    }

    @Override
    public void startConvertion() {
         _logger.debug("Start converting "+source.getName());

        boolean sort = true;
        Charset encoding = Charset.forName("UTF-8");
        int startPage = 1;
        int endPage = Integer.MAX_VALUE;

        Writer output = null;
        Writer outputFile = null;
        PDDocument document = null;
        try {
            document = PDDocument.load(source);

            if (document.isEncrypted()) {
                _logger.warn("Try to extract text from encrypted document :" + source.getAbsolutePath());
                document.decrypt(password);
            }

            PDFTextStripper stripper = null;
            if (outputFormat.equalsIgnoreCase(Constants.HTML)
                    || outputFormat.equalsIgnoreCase(Constants.HTM)) {
                stripper = new PDFText2HTML();
            } else if (outputFormat.equalsIgnoreCase(Constants.TXT)) {
                stripper = new PDFTextStripper();
            } else {
                _logger.warn("Could not convert PDF file to " + outputFormat);
            }

            if (stripper!=null)
            {
                _logger.debug("open stripper : " + encoding.name());
                stripper.setSortByPosition(sort);
                stripper.setStartPage(startPage);
                stripper.setEndPage(endPage);

                ByteArrayOutputStream buf = new ByteArrayOutputStream();
                output = new OutputStreamWriter(buf, encoding);
                output.write(stripper.getText(document));
                output.flush();
                
                outputFile = new OutputStreamWriter(new FileOutputStream(target), encoding);
                outputFile.write(buf.toString(encoding.name()));
                buf=null;
                success = true;
                _logger.debug("(writed) file : " + target.getAbsolutePath());
            }
            else
                 _logger.debug("Destination format not yet implemented");

        } catch (CryptographyException ex) {
            _logger.error("(Error while decripting) file : " + source.getAbsolutePath(), ex);
        } catch (InvalidPasswordException ex) {
            _logger.error("(Bad password) file : " + source.getAbsolutePath(), ex);
        } catch (IOException ex) {
            _logger.error("(rejected cannot be opened) file : " + source.getAbsolutePath(), ex);
        } catch (Exception ex) {
            _logger.error("(rejected) file : " + source.getAbsolutePath(), ex);
        } finally {
            try {
                _logger.debug("(closing) file : " + target.getAbsolutePath());
                if (outputFile != null) {
                    outputFile.close();
                }
                if (output != null) {
                    output.close();
                }
                if (document != null) {
                    document.close();
                }
            } catch (IOException e) {
                _logger.error("Could not close document", e);
            }
        }
    }

    @Deprecated
    public void startConvertion2() {
        _logger.debug("startConvertion");
        byte[] pdf = readBytes(source.getAbsolutePath());
        if (pdf != null) {
            boolean sort = true;

            String encoding = "UTF-8";
            int startPage = 1;
            int endPage = Integer.MAX_VALUE;

            Writer output = null;
            Writer outputFile = null;
            PDDocument document = null;
            try {
                document = PDDocument.load(new ByteArrayInputStream(pdf));

                if (document.isEncrypted()) {
                    _logger.warn("Try to extract text from encrypted document :" + source.getAbsolutePath());
                    document.decrypt(password);
                }

                ByteArrayOutputStream buf = new ByteArrayOutputStream();
                output = new OutputStreamWriter(buf, encoding);

                PDFTextStripper stripper = null;
                if (outputFormat.equalsIgnoreCase(Constants.HTML)
                        || outputFormat.equalsIgnoreCase(Constants.HTM)) {
                    stripper = new PDFText2HTML();
                } else if (outputFormat.equalsIgnoreCase(Constants.TXT)) {
                    stripper = new PDFTextStripper();
                    _logger.debug("open stripper");
                } else {
                    _logger.warn("Could not convert PDF file to " + outputFormat);
                }

                stripper.setSortByPosition(sort);
                stripper.setStartPage(startPage);
                stripper.setEndPage(endPage);
                stripper.writeText(document, output);

                String s = new String(buf.toString(encoding));

                if (!isText(s.substring(0, Math.min(2000, s.length())))) {
                    _logger.warn("(rejected) file: " + source.getAbsolutePath() + " = " + s.substring(0, Math.min(2000, s.length())));
                } else {
                    _logger.debug("(accepted) file : " + source.getAbsolutePath());
                    outputFile = new FileWriter(target);
                    outputFile.write(s);
                    outputFile.flush();
                    //outputFile.close();
                    success = true;
                    _logger.debug("(writed) file : " + target.getAbsolutePath());
                }

            } catch (CryptographyException ex) {
                _logger.error("(Error while decripting) file : " + source.getAbsolutePath(), ex);
            } catch (InvalidPasswordException ex) {
                _logger.error("(Bad password) file : " + source.getAbsolutePath(), ex);
            } catch (IOException ex) {
                _logger.error("(rejected cannot be opened) file : " + source.getAbsolutePath(), ex);
            } finally {
                try {
                    _logger.debug("(closing) file : " + target.getAbsolutePath());
                    if (outputFile != null) {
                        outputFile.close();
                    }
                    if (output != null) {
                        output.close();
                    }
                    if (document != null) {
                        document.close();
                    }
                } catch (IOException e) {
                    _logger.error("Could not close document", e);
                }
            }
        } else {
            _logger.debug("Source file is not a PDF file");
        }
    }

    public static boolean isText(String s) {
        int count = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == ' ' || s.charAt(i) == '\n' || s.charAt(i) == '\t') {
                count++;
            }
        }
        if (count * 100 / (s.length() + 1) > 6) {  // +1 pour éviter les divisions par zéro

            return true;
        } // au moins x % de blancs 8 semble assez raisonable
        else {
            return false;
        }
    }
}
