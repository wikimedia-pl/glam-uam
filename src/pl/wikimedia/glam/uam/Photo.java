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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
            + "}}\n\n";

    text += "=={{int:license-header}}==\n"
            + "{{cc-by-sa-3.0-pl}}\n\n";

    text += parseTags(tags);
    text = text.replaceAll(" +", " ");

    return text;
  }

  // other
  //
  String parseDate(String date) {
    // date month year
    if (date.matches("[0-9]{1,2} [IVX]{1,5} [0-9]{4}")) {
      String[] dates = date.split(" ");

      if (dates[0].length() == 1) {
        dates[0] = "0" + dates[0];
      }

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

  String parseTags(ArrayList<String> tags) {
    Categories categories = new Categories();
    String text = "";

    for (int i = 0; i < tags.size(); ++i) {
      tags.set(i, categories.get(tags.get(i)));
    }
    
    HashSet hs = new HashSet();
    hs.addAll(tags);
    tags.clear();
    tags.addAll(hs);
    Collections.sort(tags);

    for (String tag : tags) {
      if(!tag.trim().isEmpty())
        text += "[[Category:" + tag + "]]\n";
    }

    return text;
  }
}

class Categories {
  Map<String, String> map = new HashMap<>();
  public Categories() {
    map.put("dzieci", "Children of Poland");
    map.put("folklor", "Folklore of Poland");
    map.put("narzędzia rolnicze", "Agricultural tools in Poland");
    map.put("rękodzieło", "Folk art in Poland");
    map.put("rolnictwo", "Agriculture in Poland");
    map.put("sztuka ludowa", "Folk art in Poland");
  }
  
  public String get(String key) {
    String value = map.get(key);
    return value == null ? "" : value;
  }
}
