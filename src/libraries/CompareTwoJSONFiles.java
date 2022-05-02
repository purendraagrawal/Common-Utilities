package libraries;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class CompareTwoJSONFiles {

  private static final Logger logger = LogManager.getLogger(CompareTwoJSONFiles.class);

  public static void main(String[] args) {
    CompareTwoJSONFiles compareObject = new CompareTwoJSONFiles();
    File fileA = new File("C:\\Users\\puren\\Documents\\temp\\FileA.json");
    File fileB = new File("C:\\Users\\puren\\Documents\\temp\\FileB.json");
    List<JSONReport> result =
        compareObject.compareBaseJSONWithLatest(fileA, fileB);
    if (result != null && !result.isEmpty()) {
      for (JSONReport report : result) {
        System.out.println(report.getChangeType());
        System.out.println(report.getNodeName() == null ? "No Node" : report.getNodeName());
        System.out.println(report.getPreviousValue());
        System.out.println(report.getNewValue());
        System.out.println("++++++++++++++++");
      }
    }
  }

  public List<JSONReport> compareBaseJSONWithLatest(File baseJSONInString,
      File latestJSONInString) {
    ObjectMapper objectMapper = new ObjectMapper();
    List<JSONReport> result = null;
    try {
      JsonNode baseJSON = objectMapper.readTree(baseJSONInString);
      JsonNode newJSON = objectMapper.readTree(latestJSONInString);

      if (baseJSON.has("responseBody")) {
        stringValueToJSON(baseJSON);
        baseJSON = baseJSON.get("responseBody");
      }
      if (newJSON.has("responseBody")) {
        stringValueToJSON(newJSON);
        newJSON = newJSON.get("responseBody");
      }
      if (!baseJSON.equals(newJSON)) {
        logger.info("----MISMATCH FOUND-----");
        logger.info("base json = " + baseJSON);
        logger.info("new json = " + newJSON);
        result = compare(null, baseJSON, newJSON, false);
        result.addAll(compare(null, newJSON, baseJSON, true));
      }
    } catch (Throwable e) {
      e.printStackTrace();
    }
    return result;
  }

  public List<JSONReport> compareBaseJSONWithLatest(String baseJSONInString,
      String latestJSONInString) {
    ObjectMapper objectMapper = new ObjectMapper();
    List<JSONReport> result = null;
    try {
      JsonNode baseJSON = objectMapper.readTree(baseJSONInString);
      JsonNode newJSON = objectMapper.readTree(latestJSONInString);

      if (baseJSON.has("responseBody")) {
        stringValueToJSON(baseJSON);
        baseJSON = baseJSON.get("responseBody");
      }
      if (newJSON.has("responseBody")) {
        stringValueToJSON(newJSON);
        newJSON = newJSON.get("responseBody");
      }
      if (!baseJSON.equals(newJSON)) {
        logger.info("----MISMATCH FOUND-----");
        logger.info("base json = " + baseJSON);
        logger.info("new json = " + newJSON);
        result = compare(null, baseJSON, newJSON, false);
        result.addAll(compare(null, newJSON, baseJSON, true));
      }
    } catch (Throwable e) {
      e.printStackTrace();
    }
    return result;
  }

  private void stringValueToJSON(JsonNode node) {
    Iterator<Entry<String, JsonNode>> itr = node.fields();
    ObjectMapper mapper = new ObjectMapper();
    while (itr.hasNext()) {
      Entry<String, JsonNode> entry = itr.next();
      JsonNode temp = entry.getValue();
      if (temp.isObject()) {
        stringValueToJSON(temp);
      } else if (temp.isTextual()) {
        try {
          ((ObjectNode) node).put(entry.getKey(), mapper.readTree(temp.asText()));
        } catch (IOException e) {
          // TODO: handle exception
        }
      }
    }
  }

  private JsonNode getNearestJson(String key, JsonNode prev, JsonNode prevList, JsonNode latest) {
    JsonNode result = null;
    int v = 0;
    List<JsonNode> f = new ArrayList<>();
    for (JsonNode node : latest) {
      List<JSONReport> a = compare(key, prev, node, false);
      if ((v == 0 && a.size()>0) || a.size() < v) {
        f = new ArrayList<>();
        v = a.size();
        f.add(node);
      }else if(a.size()==v) {
        f.add(node);
      }
    }
    List<JsonNode> f1 = new ArrayList<>();
    for (JsonNode n : f) {
      boolean m = false;
      for (JsonNode n1 : prevList) {
        if (!prev.equals(n1)) {
          List<JSONReport> a = compare(key, n1, n, false);
          if (a.size()==0 || (a.size() <= v && getRespectiveJson(n1, latest) == null))
            m = true;
        }
      }
      if (!m)
        f1.add(n);
    }
    if (f1.size() == 1)
      result = f1.get(0);
    return result;
  }

  private JsonNode getRespectiveJson(JsonNode prev, JsonNode latest) {
    JsonNode result = null;
    for (JsonNode node : latest) {
      if (prev.equals(node)) {
        result = node;
        break;
      }
    }
    return result;
  }

  private JsonNode getRespectiveJson(String key, JsonNode latest) {
    Iterator<Entry<String, JsonNode>> itr = latest.fields();
    JsonNode result = null;
    while (itr.hasNext()) {
      Entry<String, JsonNode> temp = itr.next();
      if (temp.getKey().equals(key)) {
        result = temp.getValue();
        break;
      }
    }
    return result;
  }

  private List<JSONReport> compare(String key, JsonNode base, JsonNode latest, boolean isReverse) {
    List<JSONReport> result = new ArrayList<>();
    if (base.isArray()) {
      for (JsonNode node : base) {
        JsonNode temp = getRespectiveJson(node, latest);
        if (temp != null)
          continue;
        temp = getNearestJson(key, node, base, latest);
        if (temp == null) {
          JSONReport report = new JSONReport();
          report.setNodeName(key);
          if (!isReverse) {
            report.setChangeType("ARRAY_DECREASED");
            report.setPreviousValue(node);
          } else {
            report.setChangeType("ARRAY_INCREASED");
            report.setNewValue(node);
          }
          result.add(report);
        } else {
          List<JSONReport> temp1 = compare(key, node, temp, isReverse);
          if (temp1 != null && temp1.size() > 0) {
            result.addAll(temp1);
          }
        }
      }
    } else {
      Iterator<Entry<String, JsonNode>> itr = base.fields();
      while (itr.hasNext()) {
        Entry<String, JsonNode> temp = itr.next();
        JsonNode baseNode = temp.getValue();
        JsonNode latestNode = getRespectiveJson(temp.getKey(), latest);
        if (latestNode == null) {
          JSONReport report = new JSONReport();
          report.setNodeName(temp.getKey());
          if (!isReverse) {
            report.setChangeType("ELEMENT_REMOVED");
            report.setPreviousValue(baseNode);
          } else {
            report.setChangeType("NEW_ELEMENT");
            report.setNewValue(baseNode);
          }
          result.add(report);
        } else if (!baseNode.equals(latestNode)) {
          if (!hasChildren(baseNode)) {
            if (!isReverse) {
              JSONReport report = new JSONReport();
              report.setNodeName(temp.getKey());
              report.setChangeType("VALUE_CHANGED");
              report.setPreviousValue(baseNode);
              report.setNewValue(latestNode);
              result.add(report);
            }
          } else {
            List<JSONReport> temp1 = compare(temp.getKey(), baseNode, latestNode, isReverse);
            if (temp1 != null && temp1.size()>0) {
              for (JSONReport report : temp1) {
                if (!temp.getKey().equals(report.getNodeName())) {
                  report.setNodeName(temp.getKey() + "." + report.getNodeName());
                }
                result.add(report);
              }
            }
          }
        }
      }
    }
    return result;
  }


  private boolean hasChildren(JsonNode node) {
    boolean result = false;
    if (node.isArray()) {
      for (JsonNode node1 : node) {
        result = hasChildren(node1);
        if (result)
          break;
      }
    } else {
      Iterator<Entry<String, JsonNode>> itr = node.fields();
      while (itr.hasNext()) {
        result = true;
        break;
      }
    }
    return result;
  }
}
