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
    private int count, countAr;
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
        Mappings.logMessage(Level.INFO, "Discovery finished successfully. Total number of discovered triples: " + newModel.size());
        model.add(newModel);
    }

    private void addTripleToGraph(QueryExecution query1, QueryExecution query2, Resource resource) {
        try {
            results = query1.execSelect();
            resultsAr = query2.execSelect();
            countAr = 0;
            count = 0;
            Mappings.logMessage(Level.INFO, "Finding similar resource(s) for: <" + resource.getURI() + ">");
            if (results.hasNext()) {
                while (results.hasNext()) {
                    solution = results.nextSolution();
                    newModel.add(resource, property, solution.getResource("subject"));
                    count++;
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
            if (count > 0 & countAr > 0) {
                Mappings.logMessage(Level.INFO, "Total similar resource(s) for <" + resource.getURI() + "> found: " + (count + countAr));
            } else if (count > 0 & countAr == 0) {
                Mappings.logMessage(Level.INFO, "Total similar resource(s) for <" + resource.getURI() + "> found: " + count);
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
}
