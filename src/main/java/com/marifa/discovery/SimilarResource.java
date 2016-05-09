package com.marifa.discovery;

import com.marifa.mappings.Mappings;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.OWL;
import org.apache.log4j.Level;

/**
 * This class handles the discovery task (similar triples in other datasets).
 */
public class SimilarResource {

    private static Model newModel;
    private QueryExecution queryExecution, queryExecutionAr;
    private QuerySolution solution;
    private Literal literal, literalTitle;
    private Statement statement;
    private ResultSet results, resultsAr;
    private int count, countAr, countWikiData;
    private int dbpediaCount = 0, arBbpediaCount = 0, wikidataCount = 0;
    private Property property;

    public SimilarResource(Model model) {
        Mappings.logMessage(Level.INFO, "Initializing the discovery module. This may take a while, please be patient...");
        property = model.createProperty(OWL.getURI(), "sameAs");
        newModel = ModelFactory.createDefaultModel();
        StmtIterator stmtIterator = model.listStatements();
        while (stmtIterator.hasNext()) {
            statement = stmtIterator.next();
            if (statement.getPredicate().getURI().equalsIgnoreCase("http://purl.org/dc/terms/title")) {
                literalTitle = statement.getLiteral();
                if (Mappings.detectLanguage(literalTitle.getString()))
                    literal = ResourceFactory.createLangLiteral(literalTitle.getString(), "ar");
                else
                    literal = ResourceFactory.createPlainLiteral(literalTitle.getString());
                ParameterizedSparqlString queryString = new ParameterizedSparqlString("" +
                        "SELECT DISTINCT ?subject WHERE {\n" +
                        "?subject ?property ?label .\n" +
                        "} LIMIT 100");
                queryString.setParam("label", literal);
                queryExecution = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", queryString.asQuery());
                queryExecutionAr = QueryExecutionFactory.sparqlService("http://ar.dbpedia.org/sparql", queryString.asQuery());
                addTripleToGraph(queryExecution, queryExecutionAr, statement.getSubject());
            }
        }
        generateDiscoveryStats(newModel);
        model.add(newModel);
    }

    private void addTripleToGraph(QueryExecution query1, QueryExecution query2, Resource resource) {
        try {
            results = query1.execSelect();
            resultsAr = query2.execSelect();
            countAr = 0;
            count = 0;
            countWikiData = 0;
            Mappings.logMessage(Level.INFO, "Finding similar resource(s) for: <" + resource.getURI() + ">");
            if (results.hasNext()) {
                while (results.hasNext()) {
                    solution = results.nextSolution();
                    newModel.add(resource, property, solution.getResource("subject"));
                    if (solution.getResource("subject").getURI().contains("wikidata")) countWikiData++;
                    else count++;
                    Mappings.logMessage(Level.INFO, "Resource found: <" + solution.getResource("subject").getURI() + ">");
                }
            }
            if (resultsAr.hasNext()) {
                while (resultsAr.hasNext()) {
                    solution = resultsAr.nextSolution();
                    newModel.add(resource, property, solution.getResource("subject"));
                    countAr++;
                    Mappings.logMessage(Level.INFO, "Resource found: <" + solution.getResource("subject").getURI() + ">");
                }
            }
            if ((count > 0 | countWikiData > 0) & countAr > 0) {
                Mappings.logMessage(Level.INFO, "Total similar resource(s) for <" + resource.getURI() + "> found: " + (count + countWikiData + countAr));
            } else if ((count > 0 | countWikiData > 0) & countAr == 0) {
                Mappings.logMessage(Level.INFO, "Total similar resource(s) for <" + resource.getURI() + "> found: " + count + countWikiData);
            } else if (count == 0 & countAr > 0) {
                Mappings.logMessage(Level.INFO, "Total similar resource(s) for <" + resource.getURI() + "> found: " + countAr);
            } else {
                Mappings.logMessage(Level.INFO, "No similar resource is found");
            }
        } finally {
            queryExecution.close();
            queryExecutionAr.close();
        }
    }

    private void generateDiscoveryStats(Model model) {
        RDFNode node;
        NodeIterator nodeIterator = model.listObjects();
        while (nodeIterator.hasNext()) {
            node = nodeIterator.nextNode();
            if (node.toString().contains("wikidata")) {
                wikidataCount++;
            } else if (node.toString().contains("ar.dbpedia.org")) {
                arBbpediaCount++;
            } else {
                dbpediaCount++;
            }
        }
        Mappings.logMessage(Level.INFO, "Number of discovered triples in dbpedia.org: " + dbpediaCount);
        Mappings.logMessage(Level.INFO, "Number of discovered triples in wikidata.org: " + wikidataCount);
        Mappings.logMessage(Level.INFO, "Number of discovered triples in ar.dbpedia.org: " + arBbpediaCount);
        Mappings.logMessage(Level.INFO, "Discovery finished successfully. Total number of discovered triples: " + model.size());
    }
}
