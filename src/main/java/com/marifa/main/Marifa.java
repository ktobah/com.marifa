package com.marifa.main;

import com.cybozu.labs.langdetect.LangDetectException;
import com.marifa.discovery.SimilarResource;
import com.marifa.extractor.RDFExtractor;
import com.marifa.mappings.Mappings;
import com.marifa.parser.ExcelParser;
import org.apache.jena.iri.IRI;
import org.apache.jena.iri.IRIFactory;
import org.apache.log4j.Level;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

public class Marifa {

    private static String namespacePrefix;
    private static ExcelParser excelParser;
    private static RDFExtractor rdfExtractor;

    public static void main(String[] args) throws IOException, LangDetectException, URISyntaxException {

        if (args.length > 2 && args.length < 5) {
            long startTime = System.nanoTime();
            if (args[3] == null) args[3] = "";
            if (args[0] != null & args[1] != null & args[2] != null) {
                namespacePrefix = formatNameSpacePrefix(args[1]);
                excelParser = new ExcelParser(args[0], namespacePrefix);
                excelParser.parse();
                rdfExtractor = new RDFExtractor(excelParser.getParsedData(), args[1], namespacePrefix);
                new SimilarResource(rdfExtractor.getModel());

                switch (args[3]) {
                    case "RDF/XML-ABBREV":
                        rdfExtractor.writeGraphToFile(args[2], "RDF/XML");
                        break;
                    case "RDF/XML":
                        rdfExtractor.writeGraphToFile(args[2], "RDFXML_PLAIN");
                        break;
                    case "TURTLE":
                        rdfExtractor.writeGraphToFile(args[2], "TURTLE");
                        break;
                    case "Turtle":
                        rdfExtractor.writeGraphToFile(args[2], "TURTLE");
                        break;
                    case "TTL":
                        rdfExtractor.writeGraphToFile(args[2], "TURTLE");
                        break;
                    case "NT":
                        rdfExtractor.writeGraphToFile(args[2], "NTRIPLES");
                        break;
                    case "N-TRIPLES":
                        rdfExtractor.writeGraphToFile(args[2], "NTRIPLES");
                        break;
                    case "N-TRIPLE":
                        rdfExtractor.writeGraphToFile(args[2], "NTRIPLES");
                        break;
                    case "N3":
                        rdfExtractor.writeGraphToFile(args[2], "N3");
                        break;
                    case "JSON-LD":
                        rdfExtractor.writeGraphToFile(args[2], "JSONLD");
                        break;
                    case "RDF/JSON":
                        rdfExtractor.writeGraphToFile(args[2], "RDFJSON");
                        break;
                    default:
                        rdfExtractor.writeGraphToFile(args[2], "RDF/XML");
                        break;
                }
                Mappings.logMessage(Level.INFO, "Total time elapsed: " + TimeUnit.NANOSECONDS.toMinutes((System.nanoTime() - startTime)) + " minutes");
            } else {
                if (args[1] == null | args[1].isEmpty())
                    Mappings.logMessage(Level.WARN, "The namespace you entered is not a valid IRI. Please try again!");
                else if (args[0] == null | args[0].isEmpty())
                    Mappings.logMessage(Level.WARN, "No input file is specified. Please specify one!");
                else if (args[2] == null | args[2].isEmpty())
                    Mappings.logMessage(Level.WARN, "No output file is specified. Please specify the file path.");
            }
        } else {
            Mappings.logMessage(Level.WARN, "Parameters missing! Please specify at least the input, namespace, and output parameters.");
        }

    }

    private static String formatNameSpacePrefix(String namespace) throws MalformedURLException {
        IRIFactory iriFactory = IRIFactory.iriImplementation();
        IRI iri = iriFactory.construct(namespace);
        namespace = iri.getASCIIHost();
        if (namespace != null) return namespace.substring(namespace.indexOf('.') + 1, namespace.lastIndexOf('.'));
        else return null;
    }
}
