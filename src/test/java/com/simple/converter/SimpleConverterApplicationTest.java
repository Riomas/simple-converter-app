package com.simple.converter;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

/**
 *
 * @author Administrator
 */
public class SimpleConverterApplicationTest {

    private static final Option OPTION_OUTPUT_FORMAT = new Option("f",
			"output-format", true, "default output format: txt");
    private static final Option OPTION_BADFILE_FOLDER = new Option("b",
			"badfiles-folder", true, "default badfiles folder : ${input-folder}-bad");
    private static final Option OPTION_NB_RETRY = new Option("r",
			"max-retries", true, "default number of retries: 2");
    private static final Options OPTIONS = initOptions();

    private static final int EXIT_CODE_TOO_FEW_ARGS = 255;

    private static Options initOptions() {
            Options options = new Options();
            options.addOption(OPTION_OUTPUT_FORMAT);
            options.addOption(OPTION_BADFILE_FOLDER);
            options.addOption(OPTION_NB_RETRY);
            return options;
    }

    public static void main(String[] arguments) throws Exception {
        CommandLineParser commandLineParser = new PosixParser();
        CommandLine commandLine = commandLineParser.parse(OPTIONS, arguments);

        String outputFormat = Constants.TXT;
        int iMaxRetry = 2;
        
        if (commandLine.hasOption(OPTION_OUTPUT_FORMAT.getOpt())) {
            outputFormat = commandLine.getOptionValue(OPTION_OUTPUT_FORMAT.getOpt());
        }

        if (commandLine.hasOption(OPTION_NB_RETRY.getOpt())) {
            iMaxRetry = Integer.valueOf(commandLine.getOptionValue(OPTION_NB_RETRY.getOpt()));
        }

        String[] fileNames = commandLine.getArgs();
        if (fileNames.length < 2) {
            String syntax = "SimpleConverterApplicationTest [options] input-file output-file; or\n"
                            + "[options] input-folder output-folder";
            HelpFormatter helpFormatter = new HelpFormatter();
            helpFormatter.printHelp(syntax, OPTIONS);
            System.exit(EXIT_CODE_TOO_FEW_ARGS);
        }
        else if(fileNames.length==2)
        {
            Document inputFile = new Document(fileNames[0]);
            Document outFile = new Document(fileNames[1]);
            StringBuilder badfilesPath = new StringBuilder();
            if (commandLine.hasOption(OPTION_BADFILE_FOLDER.getOpt())) {
                badfilesPath.append(commandLine.getOptionValue(OPTION_BADFILE_FOLDER.getOpt()));
            }
            else
            {
                if (inputFile.isFile())
                    badfilesPath = new StringBuilder(inputFile.getParentFile().getAbsolutePath());
                else if (inputFile.isDirectory())
                    badfilesPath = new StringBuilder(inputFile.getAbsolutePath());
                badfilesPath.append("-bad");
            }

            outFile.mkdirs();
            SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd HH:mm");
            long startTime = System.currentTimeMillis();
            String dateStamp = df.format(new Date());
            ConverterReport.statictics.info("Start time: "+dateStamp);
            ConverterReport.badFiles.info("Start time: "+dateStamp);
            ConverterReport.notSupportedExtention.info("Start time: "+dateStamp);
            ConverterReport.convertedFiles.info("Start time: "+dateStamp);

            SimpleConverterApplication converter = SimpleConverterApplication.getInstance();
            converter.setMaxRetry(iMaxRetry);
            converter.setOutputFormat(outputFormat);
            converter.convertObject(inputFile, outFile, badfilesPath.toString());

            ConverterReport.statictics.info("------------------------------ Statistics --------------------------");
            ConverterReport.statictics.info("Number of treated files                    : "+converter.getGlobalFilesCount());
            ConverterReport.statictics.info("Number of converted files                  : "+converter.getConvertedCount());
            ConverterReport.statictics.info("------------------------------ Unsuccess ---------------------------");
            ConverterReport.statictics.info("Number of not supported file's extentions  : "+converter.getNotSupportedCount());
            ConverterReport.statictics.info("  list of extentions ["+converter.getNotSupportedExtentionList()+"]");
            ConverterReport.statictics.info("Number of bad files                        : "+converter.getBadfilesCount());
            ConverterReport.statictics.info("------------------------------ End ---------------------------------");

            dateStamp = df.format(new Date());
            ConverterReport.statictics.info("Stop time: "+dateStamp);
            ConverterReport.badFiles.info("Stop time: "+dateStamp);
            ConverterReport.notSupportedExtention.info("Stop time: "+dateStamp);
            ConverterReport.convertedFiles.info("Stop time: "+dateStamp);
            ConverterReport.statictics.info("Total time: "+(System.currentTimeMillis()-startTime)+"ms");

            // Close OpenOffice instance if any
            ConverterFactoryOffice.close();
        }

        

        System.exit(0);
    }
}
