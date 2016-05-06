package com.marifa.extractor;

import com.marifa.mappings.Mappings;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.log4j.Level;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * The conversion of the structured data to RDF is done within this class.
 */
public class RDFExtractor {

    private static Model model;
    private static String resourceIRI;
    private static String nameSpacePrefix, iri, propertyName, prefixNS;
    private Resource resource;
    private String[] propertyValue;
    private Property property;
    private Map<String, Integer> propertiesStat;

    public RDFExtractor(String[][] data, String iri, String nameSpacePrefix) throws UnsupportedEncodingException, URISyntaxException, MalformedURLException {
        //Create the model
        model = ModelFactory.createDefaultModel();
        this.iri = iri;
        this.nameSpacePrefix = nameSpacePrefix;
        if (!iri.endsWith("/")) iri = iri + "/";
        //set the namespaces
        loadNameSpaces(model);
        propertiesStat = new HashMap<>();

        String intermediaryValue[];
        for (int i = 0; i < data.length; i++) {
            intermediaryValue = data[i][data[i].length - 1].split(Pattern.quote("/*"));
            resourceIRI = iri + "resource/" + intermediaryValue[1].replace("/", "_").replaceAll(" ", "_");
            resource = model.createResource(resourceIRI);
            Mappings.logMessage(Level.INFO, "Extracting the properties corresponding to: " + resource.getURI());
            for (int j = 0; j < data[i].length; j++) {
                if (!data[i][j].isEmpty()) {
                    propertyValue = data[i][j].toString().split(Pattern.quote("/*"));
                    property = model.createProperty(getFullURI(propertyValue[0]));
                    Mappings.logMessage(Level.INFO, "Property Extracted: " + property.getURI());
                    resource.addProperty(property, propertyValue[1]);
                    if (!propertiesStat.containsKey(property.getURI())) {
                        propertiesStat.put(property.getURI(), 1);
                    } else {
                        propertiesStat.replace(property.getURI(), propertiesStat.get(property.getURI()) + 1);
                    }
                }
            }
        }
        printStats();
    }

    private static String getFullURI(String prefix) throws UnsupportedEncodingException, MalformedURLException, URISyntaxException {
        prefixNS = prefix.substring(0, prefix.indexOf(':'));
        propertyName = prefix.substring(prefix.indexOf(':') + 1, prefix.length());
        switch (prefixNS) {
            case "bibo":
                return "http://purl.org/ontology/bibo/" + propertyName;
            case "dcterms":
                return "http://purl.org/dc/terms/" + propertyName;
            case "degrees":
                return "http://purl.org/ontology/bibo/degrees/" + propertyName;
            case "event":
                return "http://purl.org/NET/c4dm/event.owl#" + propertyName;
            case "foaf":
                return "http://xmlns.com/foaf/0.1/" + propertyName;
            case "ns":
                return "http://www.w3.org/2003/06/sw-vocab-status/ns#" + propertyName;
            case "prism":
                return "http://prismstandard.org/namespaces/1.2/basic/" + propertyName;
            case "schema":
                return "http://schemas.talis.com/2005/address/schema#" + propertyName;
            case "skos":
                return "http://www.w3.org/2004/02/skos/core#" + propertyName;
            case "status":
                return "http://purl.org/ontology/bibo/status/" + propertyName;
            case "vann":
                return "http://purl.org/vocab/vann/" + propertyName;
        }
        if (nameSpacePrefix.contains(prefixNS)) {
            return iri + propertyName.replace("(", "").replace(")", "");
        }
        return null;
    }

    public static void writeGraphToFile(String filePath, String serialization) {
        String fileName;
        if (!filePath.endsWith("/")) fileName = filePath + "/" + nameSpacePrefix;
        else fileName = filePath + nameSpacePrefix;

        //Determine the correct file extension based on the serialization used.
        switch (serialization) {
            case "RDF/XML-ABBREV":
                fileName = fileName + ".rdf";
                break;
            case "RDF/XML":
                fileName = fileName + ".rdf";
                break;
            case "TURTLE":
                fileName = fileName + ".ttl";
                break;
            case "NTRIPLES":
                fileName = fileName + ".nt";
                break;
            case "N3":
                fileName = fileName + ".n3";
                break;
            case "JSONLD":
                fileName = fileName + ".jsonld";
                break;
            case "RDFJSON":
                fileName = fileName + "rj";
                break;
            default:
                fileName = fileName + ".rdf";
                break;
        }

        //Write the triples to a file.
        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(fileName);
            switch (serialization) {
                case "RDF/XML-ABBREV":
                    RDFDataMgr.write(outputStream, model, RDFFormat.RDFXML_ABBREV);
                    break;
                case "RDF/XML":
                    RDFDataMgr.write(outputStream, model, RDFFormat.RDFXML);
                    break;
                case "TURTLE":
                    RDFDataMgr.write(outputStream, model, RDFFormat.TURTLE);
                    break;
                case "NTRIPLES":
                    RDFDataMgr.write(outputStream, model, RDFFormat.NTRIPLES_UTF8);
                    break;
                case "N3":
                    RDFDataMgr.write(outputStream, model, RDFFormat.TURTLE);
                    break;
                case "JSONLD":
                    RDFDataMgr.write(outputStream, model, RDFFormat.JSONLD);
                    break;
                case "RDFJSON":
                    RDFDataMgr.write(outputStream, model, RDFFormat.RDFJSON);
                    break;
                default:
                    RDFDataMgr.write(outputStream, model, RDFFormat.RDFXML_ABBREV);
                    break;
            }
        } catch (FileNotFoundException e) {
            Mappings.logMessage(Level.FATAL, e.getMessage());
        }
    }

    public static Model getModel() {
        return model;
    }

    private void printStats() {
        Mappings.logMessage(Level.INFO, "**************************************");
        for (String key : propertiesStat.keySet()) {
            Mappings.logMessage(Level.INFO, "Triples using (" + key + "): " + propertiesStat.get(key));
        }
        Mappings.logMessage(Level.INFO, "Total Extracted Triples: " + model.size());
        Mappings.logMessage(Level.INFO, "Total Extracted Resources: " + model.listSubjects().toList().size());
        Mappings.logMessage(Level.INFO, "**************************************");
    }

    private void loadNameSpaces(Model model) {
        model.setNsPrefix("bibo", "http://purl.org/ontology/bibo/");
        model.setNsPrefix("dcterms", "http://purl.org/dc/terms/");
        model.setNsPrefix("degrees", "http://purl.org/ontology/bibo/degrees/");
        model.setNsPrefix("event", "http://purl.org/NET/c4dm/event.owl#");
        model.setNsPrefix("foaf", "http://xmlns.com/foaf/0.1/");
        model.setNsPrefix("ns", "http://www.w3.org/2003/06/sw-vocab-status/ns#");
        model.setNsPrefix("prism", "http://prismstandard.org/namespaces/1.2/basic/");
        model.setNsPrefix("schema", "http://schemas.talis.com/2005/address/schema#");
        model.setNsPrefix("skos", "http://www.w3.org/2004/02/skos/core#");
        model.setNsPrefix("status", "http://purl.org/ontology/bibo/status/");
        model.setNsPrefix("vann", "http://purl.org/vocab/vann/");
        model.setNsPrefix(nameSpacePrefix, iri);
        model.setNsPrefix(nameSpacePrefix + "Res", iri + "resource/");
    }
}
