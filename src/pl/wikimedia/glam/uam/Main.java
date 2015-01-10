/*
 * The MIT License
 *
 * Copyright 2015 Paweł Marynowski.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package pl.wikimedia.glam.uam;

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
    System.out.println("\n*************************************************************************");
    System.out.println(" WMPL GLAM Upload Tool\n Cyfrowe Archiwum im. Jozefa Burszty ");
    System.out.println("*************************************************************************");

    System.out.println("\n Enter single photo ID (eg. 113), range (eg. 300-310) to process files.");
    System.out.println(" Enter 0 for exit.");
    
    for (;;) {
      System.out.print("\n > ");

      try {
        BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
        String text = bufferRead.readLine();

        if (text.equals("0")) {
          // exit 
          System.exit(0);

        } else if (text.matches("[0-9]* ?- ?[0-9]*")) {
          // range
          String[] range = text.split("-");
          int min = Integer.parseInt(range[0]);
          int max = Integer.parseInt(range[1]);

          for (; min < max+1; ++min) {
            System.out.println(getPhotoDesc(min));
          }

        } else {
          // one image
          int number = Integer.parseInt(text);
          System.out.println(getPhotoDesc(number));
        }

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
      System.out.println("[" + id + "] Getting data...");
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
      if (elem_.text().matches("Zdjęcie wykonan[eo] .* r\\.")) {
        photo.setDate(elem_.text());
      } else {
        photo.setDate(elem.text());
        photo.setComment(elem_.text());
      }

      elem = doc.select(".metadata .Lokacja").get(0);
      photo.setLocation(elem.text());

      elems = doc.select(".metadata .Tagi a");
      photo.setTags(elems);

      elem = doc.select(".metadata .title").get(0);
      photo.setTitle(elem.text());

      // result
      System.out.println("[" + id + "] Result:");
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
  String comment = "";
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

  public void setComment(String _comment) {
    if(!_comment.isEmpty())
      comment = "{{pl|" + _comment + "}}";
  }

  public void setDate(String _date) {
    String d;

    if (_date.startsWith("Rok:")) {
      d = _date.substring(4);
    } else if (_date.startsWith("Zdjęcie wykonan")) {
      d = _date.substring(17);
    } else {
      d = _date;
    }

    if (d.endsWith("r.")) {
      d = d.replace("r.", "");
    }

    date = d.trim();
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
            + " |date               = " + parseDate(date) + "\n"
            + " |medium             = {{technique|photo}}\n"
            + " |dimensions         = \n"
            + " |institution        = {{Institution:Institute of Ethnology and Cultural Anthropology, Adam Mickiewicz University}}\n"
            + " |references         = \n"
            + " |object history     = \n"
            + " |exhibition history = \n"
            + " |credit line        = \n"
            + " |inscriptions       = \n"
            + " |notes              = " + comment + "\n"
            + " |accession number   = " + accession_number + "\n"
            + " |source             = http://cyfrowearchiwum.amu.edu.pl/archive/" + id + "\n"
            + " |permission         = \n"
            + " |other_versions     = \n"
            + "}}\n";

    text = text.replaceAll(" +", " ");

    return text;
  }

  // other
  //
  String parseDate(String date) {
    // date month year
    if (date.matches("[0-9]{1,2} [IVX]{1,5} [0-9]{4}")) {
      String[] dates = date.split(" ");
      
      if (dates[0].length() == 1)
        dates[0] = "0" + dates[0];
      
      return dates[2] + "-" + parseMonth(dates[1]) + "-" + dates[0];
    }
    return date;
  }

  String parseMonth(String month) {
    String m = "??";
    switch (month) {
      case "I": m = "01"; break;
      case "II": m = "02"; break;
      case "III": m = "03"; break;
      case "IV": m = "04"; break;
      case "V": m = "05"; break;
      case "VI": m = "06"; break;
      case "VII":  m = "07"; break;
      case "VIII":  m = "08"; break;
      case "IX":  m = "09"; break;
      case "X":  m = "10"; break;
      case "XI": m = "11"; break;
      case "XII": m = "12"; break;
    }
    return m;
  }

}
