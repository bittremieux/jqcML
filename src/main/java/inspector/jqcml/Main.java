package inspector.jqcml;

/*
 * #%L
 * jqcML
 * %%
 * Copyright (C) 2013 - 2015 InSPECtor
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import inspector.jqcml.io.QcMLReader;
import inspector.jqcml.io.db.QcDBManagerFactory;
import inspector.jqcml.io.db.QcDBReader;
import inspector.jqcml.io.xml.QcMLFileReader;
import inspector.jqcml.model.AttachmentParameter;
import inspector.jqcml.model.QualityParameter;
import inspector.jqcml.model.Threshold;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.EntityManagerFactory;
import java.util.Arrays;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    private static final Logger LOGGER = LogManager.getLogger(Main.class);

    private Main() {

    }

    public static void main(String[] args) {
        // create commandline options
        Options options = createOptions();

        // parse arguments
        CommandLineParser parser = new GnuParser();
        try {
            CommandLine cmd = parser.parse(options, args);

            // help
            if(cmd.hasOption("h")) {
                new HelpFormatter().printHelp("jqcML", options);
            } else {
                // input source
                String qcmlStr = cmd.getOptionValue("qcml");
                QcMLReader reader;
                EntityManagerFactory emf = null;
                if(cmd.hasOption("f")) {
                    reader = new QcMLFileReader();
                } else if(cmd.hasOption("db")) {
                    emf = createEntityManagerFactory(cmd);
                    reader = new QcDBReader(emf);
                } else {
                    LOGGER.error("No/invalid input source selected");
                    throw new IllegalArgumentException("No/invalid input source selected");
                }

                // output parameters
                if(cmd.hasOption("qa")) {
                    String qaStr = cmd.getOptionValue("qa");
                    if(cmd.hasOption("qp")) {
                        printQualityParameter(cmd, reader, qcmlStr, qaStr);
                    } else if(cmd.hasOption("ap")) {
                        printAttachmentParameter(cmd, reader, qcmlStr, qaStr);
                    } else {
                        LOGGER.error("No parameter selected");
                        throw new IllegalArgumentException("No parameter selected");
                    }
                } else {
                    LOGGER.error("No qualityAssessment selected");
                    throw new IllegalArgumentException("No qualityAssessment selected");
                }

                // close the database connection
                if(emf != null) {
                    emf.close();
                }
            }

        } catch(AlreadySelectedException e) {
            LOGGER.error("Incompatible command-line arguments", e);
            System.err.println("Incompatible command-line arguments: " + e.getMessage());
            new HelpFormatter().printHelp("jqcML", options);
        } catch(ParseException e) {
            LOGGER.error("Error while parsing the command-line arguments", e);
            System.err.println("Error while parsing the command-line arguments: " + e.getMessage());
            new HelpFormatter().printHelp("jqcML", options);
        } catch(IllegalArgumentException e) {
            LOGGER.error("Invalid command-line arguments", e);
            System.err.println("Invalid command-line arguments: " + e.getMessage());
            new HelpFormatter().printHelp("jqcML", options);
        }
    }

    private static Options createOptions() {
        Options options = new Options();
        // help
        options.addOption("h", "help", false, "show help");
        // input
        OptionGroup source = new OptionGroup();
        source.addOption(new Option("f", "file", false, "read from a qcML file"));
        source.addOption(new Option("db", "database", false, "read from a qcDB RDBMS"));
        options.addOptionGroup(source);
        OptionGroup db = new OptionGroup();
        db.addOption(new Option("mysql", true, "connect to the MySQL database with the given url \'username[:password]@host:port/db\'"));
        db.addOption(new Option("sqlite", true, "connect to the SQLite database with the given path"));
        options.addOptionGroup(db);
        // output
        options.addOption("qcml", "qcml", true, "read from the given qcML source (either file name or name in the database)");
        options.addOption("qa", "qualityAssessment", true, "get parameters from the qualityAssessment (runQuality or setQuality) with the given ID");
        OptionGroup paramGr = new OptionGroup();
        paramGr.addOption(new Option("qp", "qualityParameter", true, "print the qualityParameter with the given accession number"));
        paramGr.addOption(new Option("ap", "attachmentParameter", true, "print the attachmentParameter with the given accession number"));
        options.addOptionGroup(paramGr);
        return options;
    }

    private static EntityManagerFactory createEntityManagerFactory(CommandLine cmd) {
        EntityManagerFactory emf;
        if(cmd.hasOption("mysql")) {
            // username[:password]@host:port/database
            Pattern regex = Pattern.compile("([a-zA-z]+):?([a-zA-z]+)?@([a-zA-z]+):(\\d{1,5})/([a-zA-z]+)");
            Matcher matcher = regex.matcher(cmd.getOptionValue("mysql"));
            if(matcher.matches()) {
                emf = QcDBManagerFactory.createMySQLFactory(matcher.group(3), matcher.group(4), matcher.group(5), matcher.group(1), matcher.group(2));
            } else {
                LOGGER.error("Invalid MySQL information: {}", cmd.getOptionValue("mysql"));
                throw new IllegalArgumentException("Invalid MySQL information");
            }
        } else if(cmd.hasOption("sqlite")) {
            emf = QcDBManagerFactory.createSQLiteFactory(cmd.getOptionValue("sqlite"));
        } else {
            LOGGER.error("Invalid qcDB information: No RDBMS details provided");
            throw new IllegalArgumentException("Invalid qcDB information");
        }
        return emf;
    }

    private static void printQualityParameter(CommandLine cmd, QcMLReader reader, String qcmlStr, String qaStr) {
        StringBuilder sb = new StringBuilder();
        if(reader.getQualityAssessment(qcmlStr, qaStr) == null) {
            LOGGER.error("Invalid qualityAssessment specified: <{}>", qaStr);
            throw new IllegalArgumentException("Invalid qualityAssessment specified");
        } else if(reader.getQualityAssessment(qcmlStr, qaStr).getQualityParameter(cmd.getOptionValue("qp")) == null) {
            LOGGER.error("Invalid qualityParameter specified: <{}>", cmd.getOptionValue("qp"));
            throw new IllegalArgumentException("Invalid qualityParameter specified");
        } else {
            QualityParameter param = reader.getQualityAssessment(qcmlStr, qaStr).getQualityParameter(cmd.getOptionValue("qp"));
            sb.append("qualityParameter <ID=").append(param.getId()).append(">\n");
            sb.append("\tname = ").append(param.getName()).append("\n");
            sb.append("\taccession = ").append(param.getAccession()).append("\n");
            sb.append("\tcv = ").append(param.getCvRef()).append("\n");
            if(param.getValue() != null) {
                sb.append("\tvalue = ").append(param.getValue()).append("\n");
            }
            if(param.getUnitName() != null) {
                sb.append("\tunit name = ").append(param.getUnitName()).append("\n");
            }
            if(param.getUnitAccession() != null) {
                sb.append("\tunit accession = ").append(param.getUnitAccession()).append("\n");
            }
            if(param.getUnitCvRef() != null) {
                sb.append("\tunit cv = ").append(param.getUnitCvRef()).append("\n");
            }
            if(param.hasFlag()) {
                sb.append("\tthreshold file(s) = ");
                for(Iterator<Threshold> it = param.getThresholdIterator(); it.hasNext(); ) {
                    sb.append(it.next().getFileName());
                    if(it.hasNext()) {
                        sb.append(", ");
                    }
                }
                sb.append("\n");
            }
        }

        // print result
        System.out.print(sb.toString());
    }

    private static void printAttachmentParameter(CommandLine cmd, QcMLReader reader, String qcmlStr, String qaStr) {
        StringBuilder sb = new StringBuilder();
        if(reader.getQualityAssessment(qcmlStr, qaStr) == null) {
            LOGGER.error("Invalid qualityAssessment specified: <{}>", qaStr);
            throw new IllegalArgumentException("Invalid qualityAssessment specified");
        } else if(reader.getQualityAssessment(qcmlStr, qaStr).getAttachmentParameter(cmd.getOptionValue("ap")) == null) {
            LOGGER.error("Invalid attachmentParameter specified: <{}>", cmd.getOptionValue("ap"));
            throw new IllegalArgumentException("Invalid attachmentParameter specified");
        } else {
            AttachmentParameter param = reader.getQualityAssessment(qcmlStr, qaStr).getAttachmentParameter(cmd.getOptionValue("ap"));
            sb.append("attachmentParameter <ID=").append(param.getId()).append(">\n");
            sb.append("\tname = ").append(param.getName()).append("\n");
            sb.append("\taccession = ").append(param.getAccession()).append("\n");
            sb.append("\tcv = ").append(param.getCvRef()).append("\n");
            if(param.getValue() != null) {
                sb.append("\tvalue = ").append(param.getValue()).append("\n");
            }
            if(param.getUnitName() != null) {
                sb.append("\tunit name = ").append(param.getUnitName()).append("\n");
            }
            if(param.getUnitAccession() != null) {
                sb.append("\tunit accession = ").append(param.getUnitAccession()).append("\n");
            }
            if(param.getUnitCvRef() != null) {
                sb.append("\tunit cv = ").append(param.getUnitCvRef()).append("\n");
            }
            if(param.getQualityParameterRef() != null) {
                sb.append("\tqualityParameter = ").append(param.getQualityParameterRef()).append("\n");
            }
            if(param.getBinary() != null) {
                sb.append("\tbinary = ").append(param.getBinary()).append("\n");
            } else if(param.getTable() != null) {
                sb.append("\ttable = ").append(Arrays.deepToString(param.getTable().toArray())).append("\n");
            }
        }

        // print result
        System.out.print(sb.toString());
    }
}
