## glam-uam
![Wiki GLAM](http://i.imgur.com/QSVxZfL.png)

This tool is created for uploading images from [Józef Burszta Digital Archives](http://cyfrowearchiwum.amu.edu.pl/archive). It is part of GLAM cooperation with Institute of Ethnology and Cultural Anthropology at Adam Mickiewicz University. More information you can find on [Wikimedia Commons project page](https://commons.wikimedia.org/wiki/Commons:Institute_of_Ethnology_and_Cultural_Anthropology,_Adam_Mickiewicz_University).

#### how to run

For read-only purposes (eg. checking if everything is parsed well):

    java -jar store/glam-uam.jar
    
For upload:

    java -jar store/glam-uam.jar upload

For test upload (will use `test.wikipedia.org` API):

    java -jar store/glam-uam.jar upload test

#### license
```
The MIT License (MIT)

Copyright (c) 2015 Paweł Marynowski

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
```
