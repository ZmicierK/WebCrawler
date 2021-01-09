
# Web Crawler

This package implements a web crawler. Web crawler is a program that visits web pages starting from a given one, and follows all the links presented on the pages (in accordance with a given depth and a limit on the number of pages visited), in addition, in the process of work, it performs some useful actions on the page, in this case collects statistics (the number of occurrences of the specified terms).

## Algorithm

The algorithm for this package is as follows:

1. Recognition of input data.  
2. Traversing the tree of web pages in width (that is, the algorithm will not start working at the next level without finishing work at the current level). At each level of the tree, an array of links to pages of the lower level is formed, as soon as all links at this level are processed, a transition to the lower level is made and the formation of a new level begins.

Each page can be processed only once, not only within the same level, but throughout the entire process, that is, there should be no duplicate nodes in the link tree. For this, a list of processed pages is implemented in which pages after processing are placed. When a page is placed in a queue for processing, a check is made whether this page has been processed earlier and whether it is already in the current queue.

The algorithm terminates in the following cases:

1) Maximum depth reached.  
2) The limit of visited pages has been reached.  
3) On the pages of the current level, there are no links to pages that have not been processed.  

## Input parameters

The mandatory input parameters are start-url and a string of terms for collecting statistics on them. More input parameters are described in documentation.

## Execution result

In the process, the algorithm writes to the statistics file, and upon completion generates a file containing the top pages by the total number of occurrences of the given terms. The result of the package operation is 2 files, the first of which contains all the information obtained in the crawling process without sorting or any selection, and the second contains information containing information about the top pages by the total number of entries.

## Installation

For the end user, or for use as a black box, regardless of the platform, just download the JAR file with the dependencies.
```bash
 wget "https://github.com/ZmicierK/WebCrawler/raw/main/release/WebCrawler.jar"
``` 
The project contains a file pom.xml. For self-assembly of the project, Maven can be used.
```bash
 git clone "https://github.com/ZmicierK/WebCrawler"
 mvn
``` 
Or just open the project in your ide, the source of which is specified by this repository.

## Dependencies

```
JUnit - for testing.
HtmlUnit - to open web pages and execute js.
``` 

## Usage example


```bash
java -jar WebCrawler.jar -a https://en.wikipedia.org/wiki/Java_(programming_language) -d 4 -v 30 -t "Java,Oracle,James Gosling,programming language" -static -noheader -to 10500 -nt 12 -tf top_file_example.csv -f raw_file_example.csv
``` 
This means that you need to crawl starting from the page "https://en.wikipedia.org/wiki/Java_(programming_language) with a depth of 4, a maximum number of pages visited 30, search terms are: "Java, Oracle, James Gosling, programming language", the java script will not be executed, the output tables will not have a header, maximum response time for each page: "10500. The number of records in the topFile: 12. The path to the CSV file in which all statistical data will be serialized (without predefined sorting) - raw_file_example.csv (located in current dir). The path to the CSV file in which top by total hits statistical data will be serialized (sorted by total hits) - top_file_example.csv(located in current dir).
          For more information about the parameters, see the help:
```bash
 java -jar WebCrawler.jar -?
``` 

