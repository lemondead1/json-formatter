package lemondead.jsonformatter;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import picocli.CommandLine;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

@CommandLine.Command(
    name = "formatjson",
    description = "Formats JSON files"
)
public class Main implements Runnable {
  @CommandLine.Parameters(paramLabel = "FILE")
  private Path[] files = {};

  @CommandLine.Option(names = "--line-length", defaultValue = "120", description = "The maximum length of line.")
  private int lineLength;

  @CommandLine.Option(names = "--indent", defaultValue = "2", description = "Indentation character count. Ignored when using tabs.")
  private int indentation;

  @CommandLine.Option(names = "--tabs", description = "Use tabs instead of spaces.")
  private boolean tabs;

  @CommandLine.Option(names = "--chop", description = "Chop top-level object.")
  private boolean chop;

  public static void main(String[] args) {
    new CommandLine(new Main()).execute(args);
  }

  @Override
  public void run() {
    JsonFormatter formatter = new JsonFormatter(tabs, tabs ? 1 : indentation, lineLength, chop);
    for (Path file : files) {
      if (!Files.exists(file)) {
        System.out.println("File " + file + " does not exist.");
        continue;
      }
      JsonElement element;
      try (Reader reader = new InputStreamReader(Files.newInputStream(file))) {
        element = JsonParser.parseReader(reader);
      } catch (IOException e) {
        e.printStackTrace();
        continue;
      } catch (JsonSyntaxException ignored) {
        System.out.println("File " + file + " is not a proper json, skipping...");
        continue;
      }
      String formatted = formatter.format(element);
      try (Writer writer = new OutputStreamWriter(Files.newOutputStream(file))) {
        writer.write(formatted);
        System.out.println("Formatted " + file);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
