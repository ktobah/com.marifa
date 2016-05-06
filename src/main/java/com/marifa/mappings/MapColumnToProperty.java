package com.marifa.mappings;

import com.cybozu.labs.langdetect.LangDetectException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapColumnToProperty {

    private Mappings mappings;
    private String mappedColumns[];

    public MapColumnToProperty(ArrayList list, String namespacePrefix) throws LangDetectException, IOException, URISyntaxException {
        mappings = new Mappings(namespacePrefix);
        mappedColumns = new String[list.size()];
        for (int index = 0; index < list.size(); index++) {
            mappedColumns[index] = mappings.getKey(list.get(index).toString());
        }
        mappings.printMappingStats();
    }

    public String[] getMappedColumns() {
        List mappedColumnsList = Arrays.asList(mappedColumns);
        //Used to correct the wrong mappings!
        if (mappedColumnsList.contains("foaf:homepage"))
            mappedColumns[mappedColumnsList.indexOf("foaf:homepage")] = "bibo:numPages";
        if (mappedColumnsList.contains("dcterms:rights"))
            mappedColumns[mappedColumnsList.indexOf("dcterms:rights")] = "bibo:editorList";
        return mappedColumns;
    }
}
