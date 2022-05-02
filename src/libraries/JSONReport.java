package libraries;

import com.fasterxml.jackson.databind.JsonNode;

public class JSONReport {
  private String nodeName;
  private JsonNode previousValue;
  private JsonNode newValue;

  public String getNodeName() {
    return nodeName;
  }

  public void setNodeName(String nodeName) {
    this.nodeName = nodeName;
  }

  public JsonNode getPreviousValue() {
    return previousValue;
  }

  public void setPreviousValue(JsonNode previousValue) {
    this.previousValue = previousValue;
  }

  public JsonNode getNewValue() {
    return newValue;
  }

  @Override
  public String toString() {
    return "{'nodeName':" + nodeName + ", 'previousValue':" + previousValue + ", 'newValue':"
        + newValue + ", 'changeType':" + changeType + "}";
  }

  public void setNewValue(JsonNode newValue) {
    this.newValue = newValue;
  }

  public String getChangeType() {
    return changeType;
  }

  public void setChangeType(String changeType) {
    this.changeType = changeType;
  }

  private String changeType;
}
