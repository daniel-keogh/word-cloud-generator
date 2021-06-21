# Word Cloud Generator

Y2S2 Data Structures & Algorithms Assignment

## Description

A Java command-line application that can parse a text file or a URL and generate a simple word-cloud providing a visual representation of the document's most prominent words.

### Features

- Users are presented with a menu offering some control over how to programme will function. Options include:

    - Setting the name of the output (PNG) file.
    - Specifying the number of words which will be drawn onto the image.
    - Adding a custom colour scheme, enabling you to personalise what colours are used for displaying the words.

- The words are scaled onto the image in accordance with their frequency (i.e. The most common words in the text are featured more prominently).
- The words and their associated frequencies can be saved to a file for analysis.

## Build

### Requirements

- JavaSE-1.8 or higher.
- [jsoup](https://jsoup.org/) is required in order to extract text from a URL.

Download the project and run the following from inside the `bin` directory to create a JAR file.

```sh
$ jar –cf wordcloud.jar ie/gmit/sw/*.class
```

## Run

### Parse a file

Navigate to the directory in which the wordcloud.jar file is located and then run the following:

```sh
$ java –cp ./wordcloud.jar ie.gmit.sw.Runner
```

### Parse a URL

The easiest way to parse a URL is to include the jsoup JAR in the same directory as wordcloud.jar and run:

```sh
$ java –cp ./wordcloud.jar;./jsoup-1.11.3.jar ie.gmit.sw.Runner
```

(Note: Make sure you replace the above version number with whatever version of jsoup you are using. Also if you're using a Linux machine, you must also replace the semi-colon (`;`) with a colon (`:`) instead.)

## Blacklisting Words

It is recommended that you place a file called *ignorewords.txt* in the same directory as the executable when running the program. This file should contain a list of words that won't appear in the outputted image.

If a text file is passed as input it should also be situated in this directory, unless the path to the file is otherwise provided.

## Limitations

The algorithm for placing the words onto the image is (very) primitive, and merely randomly places the words onto the canvas. Therefore there will most likely be some overlapping of the words in the output.

## Output Sample

1. De Bello Gallico (Text file)

<img src="https://user-images.githubusercontent.com/37158241/54931507-09dc9480-4f11-11e9-9717-ab1105ca6107.png" alt="DeBelloGallico - DarkBG" height="420" width="560"/>

<br/>

2. Gallic Wars (Wikipedia page)

<img src="https://user-images.githubusercontent.com/37158241/54931508-09dc9480-4f11-11e9-9f0c-195ae9e26931.png" alt="GallicWarsWikipedia - LightBG" height="420" width="560"/>
