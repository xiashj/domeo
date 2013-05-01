package org.mindinformatics.domeo.persistence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


/**
 * Basic HTTP wrapper for ElasticSearch
 * 
 * This is not production quality code.  For one thing, the simplistic GET and
 * POST code should be replaced with something like Jakarta Commons HttpClient.
 * 
 * If retrieving a document by id then the results header followed by the complete 
 * document in the _source field is returned 
 * Failed:
 * {"took":1,"timed_out":false,"_shards":{"total":5,"successful":5,"failed":0},"hits":{"total":0,"max_score":null,"hits":[]}}
 * 
 * Successful:
 * {"took":1,"timed_out":false,"_shards":{"total":5,"successful":5,"failed":0},"hits":{"total":1,"max_score":1.0,"hits":[{"_index":"twitter","_type":"test","_id":"aviMdI48QkSGOhQL6ncMZw","_score":1.0, "_source" : { "f1" : "field value > & one", "f2" : "field value two" }}]}}
 * 
 * The following is returned:
 * {
 *   "ok" : true,
 *   "_index" : "twitter",
 *   "_type" : "tweet",
 *   "_id" : "1",
 *   "found" : true
 * }
 * 
 * WARNING: If the document to be deleted is not in the index then this generates 
 * a java.io.FileNotFoundException
 * 
 * Sample search returns:
 * Failed search:
 * {"took":1,"timed_out":false,"_shards":{"total":5,"successful":5,"failed":0},
 *  "hits":{"total":0,"max_score":null,"hits":[]}}
 * 
 * Successful search (2 results):
 * {"took":2,"timed_out":false,"_shards":{"total":5,"successful":5,"failed":0},
 *  "hits":{"total":5,"max_score":1.0,
 *  "hits":[{"_index":"twitter","_type":"test","_id":"Y-SKPAcBQeefMJNKBxCdmg","_score":1.0},
 *          {"_index":"twitter","_type":"test","_id":"5","_score":1.0}]}}
 * 
 * @author Keith Gutfreund, Elsevier Labs 2013
 */
public class ElasticSearchWrapper {

    /** Last response from GET, POST - Good for debug but if static this is not thread safe! */
    String lastResponse;
    
    /** Index name set at construction */
    final String index;
    
    /** Type name set at construction */
    final String type;
    
    /** ElasticSearch insert and delete url */
    final String esInsertDeleteUrl;
    
    /** ElasticSearch insert url */
    final String esSearchUrl;
    
    /** Set read timeout to 20s - just a guess if this is sufficient */
    final int HTTP_READ_TIMEOUT = 20000;
    
    /** Get doc by _id field */
    final String ES_QUERY_BY_DOCID = "{ \"query\" : { \"term\" : { \"_id\" : \"%s\" } } }";

    /** Boolean AND or OR query on 1 or more parsed terms in a single field */
    final String ES_BOOL_QUERY_SINGLE_PARSED_FIELD = 
        "{ \"fields\" : [\"_id\"], \"query\": { \"match\": { \"%s\": { \"query\" : \"%s\", \"operator\" : \"%s\" } } } }";

    // Boolean operators
    final String AND_OPERATOR = "and";
    final String OR_OPERATOR = "or";
    
    // HTTP Operations
    final String HTTP_POST   = "POST";
    final String HTTP_PUT    = "PUT";
    final String HTTP_GET    = "GET";
    final String HTTP_DELETE = "DELETE";
    
    /** Unique replacement character string for colons found in JSON object key */
    final static String COLON_REPLACEMENT = "_!DOMEO_NS!_";

    /** Match "field_name":  this is quoted field name followed by : */
    final static Pattern PAT_NSMATCH = Pattern.compile("(\".+?\"):");

    final static String NEWLINE = System.getProperty("line.separator");
    
    
    /**
     * Construct wrapper with for specified index and type
     * @param index
     * @param type
     */
    public ElasticSearchWrapper(String index, String type, String serverIP, String port) {
        this.index = index;
        this.type = type;
        //esUrl = "http://75.101.244.195:8081/" + index + "/" + type + "/";
        esInsertDeleteUrl = "http://" + serverIP + ":" + port + "/" + index + "/" + type + "/";
        esSearchUrl = "http://" + serverIP + ":" + port + "/" + index + "/" + type + "/_search";
    }
    
    
    /**
     * Insert a document into the index using the specified document Id.  This
     * allows for replacing existing documents.  Remove colons from field names
     * @param doc to be inserted
     * @param docId document id for inserted document
     * @return documnent id for inserted document
     */
    String insertDocument(String doc, String docId) {
        String encodedDoc = encodeNS(doc);
        String url = esInsertDeleteUrl + docId;
        String response = doHttpOperation(url, HTTP_PUT, encodedDoc);
        return response;
    }

    /**
     * Insert a document into the index.  Remove colons from field names
     * @param doc to be inserted
     * @return result header with document id for inserted document
     */
    String insertDocument(String doc) {
        String encodedDoc = encodeNS(doc);
        String response = doHttpOperation(esInsertDeleteUrl, HTTP_POST, encodedDoc);
        return response;
    }

    /**
     * Remove a document from the index
     * 
     * WARNING: If the document to be deleted is not in the index then this generates 
     * a java.io.FileNotFoundException
     * 
     * @param docId of doc to be removed
     * @return
     */
    String deleteDocument(String docId) {
        String deleteUrl = esInsertDeleteUrl + docId;
        String response = doHttpOperation(deleteUrl, HTTP_DELETE, null);
        return response;
    }
    
    /**
     * Retrieve a single document using its _id field.  Doc should be decoded
     * so that any namespace characters that were removed at insertion are replaced.
     * @param docID
     * @return retrieved document preceded by results header
     */
    String getDocument(String docID) {
        String data = String.format(ES_QUERY_BY_DOCID, docID);
        String response = doHttpOperation(esSearchUrl, HTTP_POST, data);
        return decodeNS(response);
    }
    
    /**
     * Build a simple phrase query string
     * @param field field we are searching against
     * @param val parsed phrase to match
     * @param from starting result number
     * @param size maximum number of results to show
     * @return formatted query string
     */ 
    String buildPhraseQuery(String field, String val, int from, int size) {
        StringBuffer sb = new StringBuffer("{ ");
        
        // Check for starting position (from) and max results (size)
        if ((from > -1) && (size > -1)) {
            sb.append("\"from\" : " + from + ", \"size\" : " + size + ", ");
        }       
        else if (from > -1) { // from only, no size
            sb.append("\"from\" : " + from + ", ");
        }       
        else if (size > -1) { // size only, no from
            sb.append("\"size\" : " + size + ", ");
        }

        sb.append("\"fields\" : [\"_id\"], \"query\" : { \"match_phrase\" : { \"" + field + "\": \"" + val + "\" } } } ");
        return sb.toString();
    }
    
    /**
     * Build a simple term query string
     * @param field field we are searching against
     * @param val unparsed keyword to match
     * @param from starting result number
     * @param size maximum number of results to show
     * @return formatted query string
     */ 
    String buildTermQuery(String field, String val, int from, int size) {
        StringBuffer sb = new StringBuffer("{ ");
        
        // Check for starting position (from) and max results (size)
        if ((from > -1) && (size > -1)) {
            sb.append("\"from\" : " + from + ", \"size\" : " + size + ", ");
        }       
        else if (from > -1) { // from only, no size
            sb.append("\"from\" : " + from + ", ");
        }       
        else if (size > -1) { // size only, no from
            sb.append("\"size\" : " + size + ", ");
        }

        sb.append("\"fields\" : [\"_id\"], \"query\" : { \"term\" : { \"" + field + "\": \"" + val + "\" } } } ");
        return sb.toString();
    }
    
    /**
     * Build a simple boolean query string
     * @param field field we are searching against
     * @param val parsed string of 1 or more words
     * @param operator and or or
     * @param from starting result number
     * @param size maximum number of results to show
     * @param from starting result number
     * @param size maximum number of results to show
     * @return formatted query string
     */
    String buildSimpleParsedBooleanQuery(String field, String val, String operator, int from, int size) {
        StringBuffer sb = new StringBuffer("{ ");
        
        // Check for starting position (from) and max results (size)
        if ((from > -1) && (size > -1)) {
            sb.append("\"from\" : " + from + ", \"size\" : " + size + ", ");
        }       
        else if (from > -1) { // from only, no size
            sb.append("\"from\" : " + from + ", ");
        }       
        else if (size > -1) { // size only, no from
            sb.append("\"size\" : " + size + ", ");
        }
        
        // Use "fields" to limit fields returned in results
        sb.append("\"fields\" : [\"_id\"], \"query\" : { \"match\" : { \"" + field + "\": { \"query\" : \"" + val + "\", \"operator\" : \"" + operator + "\" } } } }");
        return sb.toString();
    }
    
    /**
     * Build a generic boolean query string
     * @param fields array of fields
     * @param vals array of vals
     * @param parsed array of "match" or "term" for parsed and unparsed
     * @param operator "or" or "and"
     * @param from starting result number
     * @param size maximum number of results to show
     * @return formatted query string
     */ 
    String buildGenericBooleanQuery(String[] fields, String[] vals, String[] parsed, String operator, int from, int size) {
        StringBuffer sb = new StringBuffer("{ ");
        
        // Check for starting position (from) and max results (size)
        if ((from > -1) && (size > -1)) {
            sb.append("\"from\" : " + from + ", \"size\" : " + size + ", ");
        }       
        else if (from > -1) { // from only, no size
            sb.append("\"from\" : " + from + ", ");
        }       
        else if (size > -1) { // size only, no from
            sb.append("\"size\" : " + size + ", ");
        }
        
        // Use "fields" to limit fields returned in results
        sb.append("\"fields\" : [\"_id\"], \"query\" : { \"bool\" : { ");
        
        // Operator AND -> must and OR -> should
        if (operator.equals(AND_OPERATOR)) {
            sb.append("\"must\" : [  ");
        }
        else {
            sb.append("\"should\" : [  ");
        }
        
        // Append clause for each field
        for (int i = 0; i < fields.length; i++) {
            if (i != 0) {
                sb.append(", ");
            }
            sb.append("{ " + "\"" + parsed[i] + "\"" + " : { " + "\"" + fields[i] + "\" : " + "\"" + vals[i] + "\"" + "} }");
        }
        sb.append("] } } }");
        
        return sb.toString();
    }

    /**
     * Perform a single-field term (keyword) query.  Doc should be decoded
     * so that any namespace characters that were removed at insertion are replaced.
     * @param field to be searched
     * @param val one word to be matched (unparsed)
     * @param from starting result number
     * @param size maximum number of results to show
     * @return list of matching document ids
     */
    String termQuery(String field, String val, int from, int size) {
        String query = buildTermQuery(field, val, from, size);
        String response = doHttpOperation(esSearchUrl, HTTP_POST, query);
        return decodeNS(response);
    }

    /**
     * Perform a single-field phrase query.  Doc should be decoded
     * so that any namespace characters that were removed at insertion are replaced.
     * @param field to be searched
     * @param val parsed phrase to be matched
     * @param from starting result number
     * @param size maximum number of results to show
     * @return list of matching document ids
     */
    String phraseQuery(String field, String val, int from, int size) {
        String query = buildPhraseQuery(field, val, from, size);
        String response = doHttpOperation(esSearchUrl, HTTP_POST, query);
        return decodeNS(response);
    }

    /**
     * Perform a boolean query against all the parsed words withinin 1 field
     * Doc should be decoded so that any namespace characters that were removed 
     * at insertion are replaced.
     * Sample Results: {"took":3,"timed_out":false,"_shards":{"total":5,"successful":5,"failed":0},"hits":{"total":1,"max_score":0.008439008,"hits":[{"_index":"domeo","_type":"test","_id":"1","_score":0.008439008}]}}
     * @param field to be searched
     * @param val one or more words to be matched
     * @param operator "or" or "and"
     * @param from starting result number
     * @param size maximum number of results to show
     * @return list of matching document ids
     */
    String booleanQuerySingleParsedField(String field, String val, String operator, int from, int size) {
        String query = buildSimpleParsedBooleanQuery(field, val, operator, from, size);
        String response = doHttpOperation(esSearchUrl, HTTP_POST, query);
        return decodeNS(response);
    }
    
    /**
     * Perform a boolean query against all the specified fields
     * Doc should be decoded so that any namespace characters that were removed 
     * at insertion are replaced.
     * @param fields array of fields
     * @param vals array of vals
     * @param parsed array of "match" or "term" for parsed and unparsed
     * @param operator "or" or "and"
     * @param from starting result number
     * @param size maximum number of results to show
     * @return search results
     */
    String booleanQueryMultipleFields(String[] fields, String[] vals, String[] parsed, String operator, int from, int size) {
        String query = buildGenericBooleanQuery(fields, vals, parsed, operator, from, size);
        String response = doHttpOperation(esSearchUrl, HTTP_POST, query);
        return decodeNS(response);
    }

    
    /**
     * Do the specified HTTP operation: GET, POST, PUT, DELETE
     * @param urlString
     * @param operation is GET, POST, PUT, etc
     * @param data may be null if GET
     * @return
     */
    String doHttpOperation(String urlString, String operation, String data) {
        OutputStreamWriter wr = null;
        BufferedReader rd = null;
        
        StringBuffer sb = new StringBuffer();
        lastResponse = "";

        try {
            // Send data
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();;
            conn.setDoOutput(true);
            conn.setRequestMethod(operation);
            conn.setReadTimeout(HTTP_READ_TIMEOUT);

            // POST and PUT write data
            if (data != null) {
                wr = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
                wr.write(data);
                wr.flush();
            }

            // Get the response
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line + NEWLINE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try { if (wr != null) { wr.close(); } } catch (IOException e) { }
            try { if (rd != null) { rd.close(); } } catch (IOException e) { }
        }
        
        lastResponse = sb.toString();
        return lastResponse;
    }

    /**
     * Replace any ':' characters in field names with a replacement string since
     * ElasticSearch does not permit these.
     * Uses JSON-Simple parser
     * @param doc that may contain ':' character in field names
     * @return document with field names encoded
     */ 
    String encodeNS(String doc)  {
        JSONParser parser = new JSONParser();
        Transformer transformer = new Transformer();
        try {
            parser.parse(doc, transformer);
            Object value = transformer.getResult();
            return value.toString();
        } catch (ParseException e) { 
            e.printStackTrace(); 
            return "";
        }
    }
    
    
    /**
     * Replace all encoded colons in field names with the single colon character
     * @param doc to be decoded
     * @return decoded doc
     */
    String decodeNS(String doc) {
        return doc.replaceAll(COLON_REPLACEMENT, ":");
    }

    
    // Convenience method to read in sample json doc for debug
    String readSampleJsonDoc(String file) {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        
        try {
            br = new BufferedReader(new FileReader(new File(file)));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + NEWLINE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (br != null) { br.close(); } } catch (IOException e) { }
        }
        return sb.toString();
    }
    
    /**
     * Run some basic tests
     */
    void doTests() {
        String doc = "", r = "";
        
        // Test: Term (keyword) query
        r = termQuery("domeo_!DOMEO_NS!_agents.@type", "foafx:Person", 0, 10);
        
        // Test: Phrase query
        r = phraseQuery("dct_!DOMEO_NS!_description", "created automatically", 0, 10);
        
        
        // Test: Delete a document
        //r = deleteDocument("7TdnuBsjTjWaTcbW7RVP3Q");
        
        // Test: Generic boolean query: 4 fields (3 keyword fields, 1 parsed field)
        String[] fields = {"ao_!DOMEO_NS!_item.@type", "ao_!DOMEO_NS!_item.@type", "ao_!DOMEO_NS!_item.ao_!DOMEO_NS!_body.@type", "ao_!DOMEO_NS!_item.ao_!DOMEO_NS!_body.cnt_!DOMEO_NS!_chars"}; 
        String[] vals = {"ao:PostIt", "ao:Tag", "cnt:ContentAsText", "paolo"};
        String[] parsed = {"term", "term", "term", "match"};
        r = booleanQueryMultipleFields(fields, vals, parsed, "and", 0, 10);
        
        // Test: Single field boolean query
        r = booleanQuerySingleParsedField(
                "ao_!DOMEO_NS!_item.ao_!DOMEO_NS!_context.ao_!DOMEO_NS!_hasSelector.ao_!DOMEO_NS!_suffix",
                "ontology blizzard",
                "or", 0, 10);
        
        // Test: Retrieve a single doc by id
        r = getDocument("aviMdI48QkSGOhQL6ncMZw");

        // Test: insert a document, return it's auto-assigned id
        doc = "{ \"f1\" : \"field value one\", \"f2\" : \"field value two\" }";
        r = insertDocument(doc);

        // Test: insert a doc with specified id (replace if already present)
        doc = "{ \"f1\" : \"field value one\", \"f2\" : \"field value two\" }";
        r = insertDocument(doc, "5");
        System.out.println(r);  
        
        // Test: insert json document and try to remove it
        doc = readSampleJsonDoc("/temp/sample_domeo_doc.json");
        System.out.println(doc);    
        r = insertDocument(doc);
    }

    
    /**
     * Sample calling examples
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        //ElasticSearchWrapper s = new ElasticSearchWrapper("domeo", "test", "localhost", "9200");
        //s.doTests();
    }

}