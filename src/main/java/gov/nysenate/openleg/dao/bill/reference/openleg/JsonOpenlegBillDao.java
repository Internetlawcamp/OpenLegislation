package gov.nysenate.openleg.dao.bill.reference.openleg;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import gov.nysenate.openleg.client.view.bill.BillView;
import gov.nysenate.openleg.config.Environment;
import gov.nysenate.openleg.service.spotcheck.openleg.JsonOpenlegDaoUtils;
import gov.nysenate.openleg.util.OutputUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.net.HttpURLConnection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *  This Repository is used to provide json data from Openleg and use Jackson to convert json string to BillView.
 * Created by Chenguang He on 2017/3/21.
 */
@Repository
public class JsonOpenlegBillDao implements OpenlegBillDao {
    private static final Logger logger = LoggerFactory.getLogger(JsonOpenlegBillDao.class);

    @Autowired
    Environment env;

    HttpURLConnection connection = null;

    @Override
    public List<BillView> getOpenlegBillView(String sessionYear, String apiKey, int offset) {
        List<BillView> billViews = new LinkedList<>();
        StringBuffer response = new StringBuffer();
        connection = JsonOpenlegDaoUtils.setConnection(env.getOpenlegRefUrl()+"/api/3/bills/" + sessionYear + "?full=true&key=" + apiKey + "&limit=1000&offset=" + offset,"GET",false,true );
        JsonOpenlegDaoUtils.readInputStream(connection, response);
        logger.info("Fetching bill from openleg ref with offset " + offset);
        mapJSONToBillView(response, billViews);
        connection.disconnect();
        return billViews;
    }

    @Override
    public int getTotalRefBillsForSessionYear(int sessionYear, String apiKey) {
        StringBuffer response = new StringBuffer();
        HttpURLConnection connection = JsonOpenlegDaoUtils.setConnection(env.getOpenlegRefUrl()+"api/3/bills/" +
                sessionYear  + "?key=" + apiKey, "GET", false, true);
        JsonOpenlegDaoUtils.readInputStream(connection, response);
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new GuavaModule());

            JsonNode node = null;
            node = mapper.readTree(response.toString());
            return  node.get("total").asInt();
        }
        catch(IOException e) {
            logger.error("The JSON Object could not be mapped to a Json Node");
            e.printStackTrace();
        }

        return -1;
    }

    private List<BillView> toBillView(JsonNode node) throws IOException {
        ObjectMapper mapper = OutputUtils.getJsonMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        List<BillView> billViewList = new LinkedList<>();
        if (node.get("result").get("items") == null) { // if there is only 1 available bill
            billViewList.add(mapper.readValue(node.get("result").toString(), BillView.class));
        } else { // if there are many available bills.
            Iterator<JsonNode> nodeIterator = node.get("result").get("items").iterator();
            while (nodeIterator.hasNext()) {
                JsonNode node1 = nodeIterator.next();
                billViewList.add(mapper.readValue(node1.toString(), BillView.class));
            }
        }
        return billViewList;
    }

    private void mapJSONToBillView(StringBuffer response, List<BillView> billViews) {
        try {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new GuavaModule());

        JsonNode node = null;
        node = mapper.readTree(response.toString());
        billViews.addAll(toBillView(node));
        } catch (IOException e) {
            logger.error("The JSON Object could not be mapped to a bill view");
            e.printStackTrace();
        }
    }

}