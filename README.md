# WordCloudGenerator

Y2S2 Data Structures & Algorithms

## Description
A Java command-line application that can parse a text file or a URL and generate a simple word cloud displaying the most prominent words in decreasing font size.

### Features
Users are presented with a menu offering some control over how to program will operate. Options include:

- Setting the name of the output file.
- Regulating the number of words which will be drawn onto the image.
- Adding a custom colour scheme.

## Build
### Requirements
- JavaSE-1.8 or higher.
- [jsoup](https://jsoup.org/) is used to extract text from URLs.

Download the project and run the following from inside the `bin` directory to create a JAR file.

```sh
$ jar –cf wordcloud.jar ie/gmit/sw/*.class
```

## Run
Navigate to the directory in which your wordcloud.jar file is located and run the following:

```sh
$ java –cp ./wordcloud.jar ie.gmit.sw.Runner
```

If you want to parse a URL you will need jsoup. The easiest way is to include the jsoup JAR in the same directory as wordcloud.jar and run:

```sh
$ java –cp ./wordcloud.jar;./jsoup-1.11.3.jar ie.gmit.sw.Runner
```

If you're using Linux, replace the semi-colon (`;`) with a colon (`:`) instead.

## Blacklisting Words
It is recommended that you place a file called *ignorewords.txt* in the same directory as the executable when running the program. This file should contain a list of words that won't appear in the outputted image.

If a text file is passed as input it should also be situated in this directory.

## Output Sample

1. De Bello Gallico

<img src="https://user-images.githubusercontent.com/37158241/54931507-09dc9480-4f11-11e9-9717-ab1105ca6107.png" alt="DeBelloGallico - DarkBG" height="420" width="560"/>

<br/>

2. Gallic Wars - Wikipedia

<img src="https://user-images.githubusercontent.com/37158241/54931508-09dc9480-4f11-11e9-9f0c-195ae9e26931.png" alt="GallicWarsWikipedia - LightBG" height="420" width="560"/>
