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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.login.FailedLoginException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.wikipedia.Wiki;

/**
 * Main class
 *
 */
public class Main {

  static boolean UPLOAD = false;
  
  /**
   * Main program
   *
   * @param args args
   */
  public static void main(String[] args) {
    
    if(args.length > 0 && args[0].equals("upload"))
      UPLOAD = true;
    
    System.out.println("\n*************************************************************************");
    System.out.println(" WMPL GLAM Upload Tool\n Cyfrowe Archiwum im. Jozefa Burszty ");
    System.out.println("*************************************************************************");

    BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));

    if (UPLOAD) {
      for (;;) {
        try {
          System.out.print("\n > User: ");
          String user;
          user = bufferRead.readLine();
          System.out.print(" > Password: ");
          String password = bufferRead.readLine();

          System.out.print("\n[.] Logging in...");
          Wiki wiki = new Wiki("commons.wikimedia.org");
          wiki.login(user, password);
          System.out.print(" OK!\n");
          break;
        } catch (IOException ex) {
          System.out.println("[!] Error!");
        } catch (FailedLoginException ex) {
          System.out.println("\n[!] Could not login. Wrong password?");
        }
      }
    }

    System.out.println("\n Enter single photo ID (eg. 113), range (eg. 300-310) to process files.");
    System.out.println(" Enter 0 for exit.");
    
    for (;;) {
      System.out.print("\n > ");

      try {

        String text = bufferRead.readLine();

        if (text.equals("0")) {
          // exit 
          System.exit(0);

        } else if (text.matches("[0-9]* ?- ?[0-9]*")) {
          // range
          String[] range = text.split("-");
          int min = Integer.parseInt(range[0]);
          int max = Integer.parseInt(range[1]);

          for (; min < max + 1; ++min) {
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