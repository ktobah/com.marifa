# Description

**Marifa** is a Java framword developped to help publishers extract useful RDF data from catalogs and link it with other well-known datasets, such as [dbpedia.org](http://dbpedia.org/), [ar.dbpedia.org](http://ar.dbpedia.org/), and [wikidata.org](http://wikidata.org/).

# Using Marifa

To use Marifa, please follow the following steps:
  1.  **Clone** the project to your local machine.
  2.  **Complie** the project (Maven).
  3.  **Run** the framework (see the next section).
  
# Running the Framework

Marifa is used through the usage of arguments at launch time. Please add the arguments as described bellow to your IDE:

```
input namespace output_path [format]
```
Where:
* **input**: represents the catalog itself, for example: [pub.xls](data/pub.xls).
* **namespace**: represents the namespace used by the publisher, for example: http://www.marefah.com/ 
* **output_path**: represents the output path without a file name, for instance: ../Desktop/ 
* **format** [Otional]: represents the serialization format. Possible formats are (without quotes): "RDF/XML-ABBREV" "RDF/XML" "TURTLE" "N-TRIPLES" "N3" "JSON-LD" "RDF/JSON". If no formar is specified, then "RDF/XML" is used.

# Issues & Suggestions

Please feel free to contact @ktobah for any suggestions you might have, or issues your might find.
