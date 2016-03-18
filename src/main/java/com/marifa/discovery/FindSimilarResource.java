package com.marifa.discovery;

import com.sun.org.apache.xpath.internal.SourceTree;
import com.sun.xml.internal.ws.encoding.fastinfoset.FastInfosetCodec;
import org.apache.jena.query.*;
import org.apache.jena.sparql.engine.http.QueryExceptionHTTP;

/**
 * Created by AHMED on 3/5/2016.
 */
public class FindSimilarResource {
    public FindSimilarResource() {
        /*String queryString = "SELECT ?o WHERE {"+
                                "?s ?p ?o ."+
                                "} LIMIT 10";*/
        String str="Obama";
        String queryString = "PREFIX pr:<http://xmlns.com/foaf/0.1/>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
                "SELECT DISTINCT ?s ?label WHERE {" +
                "?s rdfs:label ?label . "+
                "?s a pr:Person . "+
                "FILTER (lang(?label) = 'en') . "+
                "?label <bif:contains> \""+str+"\" ."+
                "}";

        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);
        try
        {
            ResultSet results = qexec.execSelect();
            while(results.hasNext()){
                QuerySolution soln = results.nextSolution();
                //Literal name = soln.getLiteral("x");
                System.out.println(soln);
            }
        }
        finally{
            qexec.close();
        }
    }
}
