import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lemondead.jsonformatter.JsonFormatter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class JsonFormatterTest {
  @Test
  public void jsonFormatterTest() {
    String unformatted = "{\"glossary\":{\"title\":\"example glossary\",\"GlossDiv\":{\"title\":\"S\",\"GlossList\":{\"GlossEntry\":{\"ID\":\"SGML\",\"SortAs\":\"SGML\",\"GlossTerm\":\"Standard Generalized Markup Language\",\"Acronym\":\"SGML\",\"Abbrev\":\"ISO 8879:1986\",\"GlossDef\":{\"para\":\"A meta-markup language, \\n used to create markup languages such as DocBook.\",\"GlossSeeAlso\":[\"GML\",\"XML\"]},\"GlossSee\":\"markup\"}}}}}";
    JsonElement elementIn = JsonParser.parseString(unformatted);

    String formatted = "{\n" +
                       "   \"glossary\": {\n" +
                       "      \"title\": \"example glossary\",\n" +
                       "      \"GlossDiv\": {\n" +
                       "         \"title\": \"S\",\n" +
                       "         \"GlossList\": {\n" +
                       "            \"GlossEntry\": {\n" +
                       "               \"ID\": \"SGML\",\n" +
                       "               \"SortAs\": \"SGML\",\n" +
                       "               \"GlossTerm\": \"Standard Generalized Markup Language\",\n" +
                       "               \"Acronym\": \"SGML\",\n" +
                       "               \"Abbrev\": \"ISO 8879:1986\",\n" +
                       "               \"GlossDef\": {\n" +
                       "                  \"para\": \"A meta-markup language, \\n used to create markup languages such as DocBook.\",\n" +
                       "                  \"GlossSeeAlso\": [ \"GML\", \"XML\" ]\n" +
                       "               },\n" +
                       "               \"GlossSee\": \"markup\"\n" +
                       "            }\n" +
                       "         }\n" +
                       "      }\n" +
                       "   }\n" +
                       "}\n";

    String result = new JsonFormatter(false, 3, 80, false).format(unformatted);
    JsonElement elementOut = JsonParser.parseString(result);

    Assertions.assertEquals(formatted, result);
    Assertions.assertEquals(elementIn, elementOut);
  }
}
