package pl.wikimedia;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Main class
 *
 */
public class Main {

  /**
   * Main program
   *
   * @param args args
   */
  public static void main(String[] args) {
    System.out.println("*************************************************************************");
    System.out.println(" WMPL GLAM Upload Tool\n Cyfrowe Archiwum im. Jozefa Burszty ");
    System.out.println("*************************************************************************");

    for (;;) {
      System.out.print("\n > Enter photo ID (0 for close program): ");

      try {
        BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
        String text = bufferRead.readLine();

        if (text.equals("0")) {
          System.exit(0);
        }

        int number = Integer.parseInt(text);
        System.out.println(getPhotoDesc(number));

      } catch (NumberFormatException ex) {
        System.out.println("[!] Could not parse as a number.");
      } catch (IOException ex) {
        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }

  /**
   * Returns photo description ready for upload
   *
   * @param id photo ID
   * @return photo desc in wikicode
   */
  static String getPhotoDesc(int id) {
    Document doc;
    try {
      System.out.println("[.] Getting data...");
      doc = Jsoup.connect("http://cyfrowearchiwum.amu.edu.pl/archive_ajax/" + id).get();

      Photo photo = new Photo(id);
      Element elem;
      Element elem_;
      Elements elems;

      elem = doc.select(".file a").get(0);
      photo.setPath(elem.attr("href"));

      // data
      elem = doc.select(".metadata .nr_invent").get(0);
      photo.setAccNumber(elem.text());

      elem = doc.select(".metadata .author").get(0);
      photo.setAuthor(elem.text());

      elem = doc.select(".metadata .Rok").get(0);
      elem_ = doc.select(".metadata .description").get(0);
      photo.setDate(elem_.text().startsWith("Zdjęcie wykonane ") ? elem_.text() : elem.text());

      elem = doc.select(".metadata .Lokacja").get(0);
      photo.setLocation(elem.text());

      elems = doc.select(".metadata .Tagi a");
      photo.setTags(elems);

      elem = doc.select(".metadata .title").get(0);
      photo.setTitle(elem.text());

      // result
      System.out.println("[.] Result:");
      System.out.println("\nFile:" + photo.getName() + "\n");
      return photo.getWikiText();

    } catch (IOException ex) {
      System.out.println("[!] Could not get data. Probably this ID is not used.");
    }
    System.out.println("[!] Error!");
    return "";
  }
}

/**
 * Class containing information about photo
 * 
 */
class Photo {

  int id = 0;
  String path = "";

  String accession_number = "";
  String author = "";
  String date = "";
  String location = "";
  String title = "";

  ArrayList<String> tags = new ArrayList<>();

  Photo(int _id) {
    id = _id;
  }

  // sets
  //
  public void setAccNumber(String _accession_number) {
    accession_number = _accession_number.startsWith("Nr inwent.: ") ? _accession_number.substring(12) : _accession_number;
  }

  public void setAuthor(String _author) {
    author = _author.startsWith("Autor: ") ? _author.substring(7) : _author;
  }

  public void setDate(String _date) {
    String d;

    if (_date.startsWith("Rok:")) {
      d = _date.substring(4);
    } else if (_date.startsWith("Zdjęcie wykonane ")) {
      d = _date.substring(17);
    } else {
      d = _date;
    }

    date = d;
  }

  public void setLocation(String _location) {
    location = _location.startsWith("Lokacja:") ? _location.substring(8) : _location;
  }

  public void setPath(String _path) {
    path = "http://cyfrowearchiwum.amu.edu.pl" + _path;
  }

  public void setTags(Elements _elems) {
    for (Element e : _elems) {
      tags.add(e.text());
    }
  }

  public void setTitle(String _title) {
    title = _title;
  }

  // gets  
  //
  public String getName() {
    String[] loc = location.split(",");
    return loc[0] + " - " + title + " (" + accession_number + ").jpg";
  }

  public String getWikiText() {
    String text = "=={{int:filedesc}}==\n"
            + "{{Photograph\n"
            + " |photographer       = " + author + "\n"
            + " |title              = {{pl|" + title + ".}}\n"
            + " |description        = \n"
            + " |depicted people    = \n"
            + " |depicted place     = " + location + "\n"
            + " |date               = " + date + "\n"
            + " |medium             = {{technique|photo}}\n"
            + " |dimensions         = \n"
            + " |institution        = {{Institution:Institute of Ethnology and Cultural Anthropology, Adam Mickiewicz University}}\n"
            + " |references         = \n"
            + " |object history     = \n"
            + " |exhibition history = \n"
            + " |credit line        = \n"
            + " |inscriptions       = \n"
            + " |notes              = \n"
            + " |accession number   = " + accession_number + "\n"
            + " |source             = http://cyfrowearchiwum.amu.edu.pl/archive/" + id + "\n"
            + " |permission         = \n"
            + " |other_versions     = \n"
            + "}}";

    text = text.replaceAll(" +", " ");

    return text;
  }

  // other
  //
  String parseDate() {
    return "";
  }

}
