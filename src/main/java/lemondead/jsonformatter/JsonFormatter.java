package lemondead.jsonformatter;

import com.google.gson.*;

import java.util.Iterator;
import java.util.Map;

public class JsonFormatter {
  private final int indent;
  private final int lineLength;
  private final boolean chop;
  private final boolean tabs;

  public JsonFormatter(boolean tabs, int indent, int lineLength, boolean chop) {
    this.tabs = tabs;
    this.indent = indent;
    this.lineLength = lineLength;
    this.chop = chop;
  }

  public String format(String json) {
    return format(JsonParser.parseString(json));
  }

  public String format(JsonElement element) {
    StringBuilder builder = new StringBuilder();
    append(element, 0, chop || getLength(element) > lineLength, builder);
    builder.append('\n');
    return builder.toString();
  }

  private static void writeString(String string, StringBuilder builder) {
    builder.append('"');
    for (int i = 0; i < string.length(); i++) {
      char ch = string.charAt(i);
      switch (ch) {
        case '\b':
          builder.append("\\b");
          break;
        case '\f':
          builder.append("\\f");
          break;
        case '\n':
          builder.append("\\n");
          break;
        case '\r':
          builder.append("\\r");
          break;
        case '\t':
          builder.append("\\t");
          break;
        case '"':
          builder.append("\\\"");
          break;
        case '\\':
          builder.append("\\\\");
          break;
        default:
          builder.append(ch);
          break;
      }
    }
    builder.append('"');
  }

  private void append(JsonElement element, int indent, boolean chop, StringBuilder builder) {
    if (element.isJsonPrimitive()) {
      if (element.getAsJsonPrimitive().isString()) {
        writeString(element.getAsJsonPrimitive().getAsString(), builder);
      } else {
        builder.append(element.getAsString());
      }
    } else if (element.isJsonNull()) {
      builder.append("null");
    } else if (element.isJsonArray()) {
      builder.append('[').append(chop ? '\n' : ' ');
      Iterator<JsonElement> itr = element.getAsJsonArray().iterator();
      while (itr.hasNext()) {
        JsonElement arrayElement = itr.next();
        addIndentation(builder, chop ? indent + this.indent : 0);
        append(arrayElement, indent + this.indent,
            chop && getLength(arrayElement) + indent + this.indent + (itr.hasNext() ? 2 : 1) > lineLength, builder);
        if (itr.hasNext()) {
          builder.append(',');
        }
        builder.append(chop ? '\n' : ' ');
      }
      addIndentation(builder, chop ? indent : 0);
      builder.append(']');
    } else if (element.isJsonObject()) {
      builder.append('{').append(chop ? '\n' : ' ');
      Iterator<Map.Entry<String, JsonElement>> itr = element.getAsJsonObject().entrySet().iterator();
      while (itr.hasNext()) {
        Map.Entry<String, JsonElement> entry = itr.next();
        int i = indent + this.indent;
        addIndentation(builder, chop ? i : 0);
        builder.append('"').append(entry.getKey()).append('"').append(':').append(' ');
        append(entry.getValue(), i,
            chop && getLength(entry.getValue()) + i + entry.getKey().length() + (itr.hasNext() ? 5 : 4) > lineLength, builder);
        if (itr.hasNext()) {
          builder.append(',');
        }
        builder.append(chop ? '\n' : ' ');
      }
      addIndentation(builder, chop ? indent : 0);
      builder.append('}');
    } else {
      throw new RuntimeException("WTF??");
    }
  }

  private void addIndentation(StringBuilder builder, int indentation) {
    if (tabs) {
      builder.append('\t');
    } else {
      for (int i = 0; i < indentation; i++) {
        builder.append(' ');
      }
    }
  }

  private static int getLength(JsonElement element) {
    int length = 0;
    if (element.isJsonArray()) {
      length += 2;
      JsonArray array = element.getAsJsonArray();
      for (JsonElement json : array) {
        length += getLength(json) + 2;
      }
    } else if (element.isJsonPrimitive()) {
      if (element.getAsJsonPrimitive().isString()) {
        length += 2;
      }
      length += element.getAsString().length();
    } else if (element.isJsonObject()) {
      length += 3;
      JsonObject object = element.getAsJsonObject();
      for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
        length += entry.getKey().length() + 6 + getLength(entry.getValue());
      }
    }
    return length;
  }
}
