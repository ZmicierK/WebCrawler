/**
 *
 * <p style="text-indent: 20px;">This package implements a web crawler. Web crawler is a program that visits web pages
 * starting from a given one, and follows all the links presented on the pages (in accordance with a given depth and
 * a limit on the number of pages visited), in addition, in the process of work, it performs some useful actions
 * on the page, in this case collects statistics (the number of occurrences of the specified terms)</p>
 *
 * <p style="text-indent: 20px;">The algorithm for this package is as follows:</p>
 * 1. Recognition of input data.<br>
 * 2. Traversing the tree of web pages in width (that is, the algorithm will not start working at the next level
 * without finishing work at the current level). At each level of the tree, an array of links to pages of the lower
 * level is formed, as soon as all links at this level are processed, a transition to the lower level is made and the
 * formation of a new level begins.
 * <p style="text-indent: 20px;">Each page can be processed only once, not only within the same level, but throughout
 * the entire process, that is, there should be no duplicate nodes in the link tree. For this, a list of processed
 * pages is implemented in which pages after processing are placed. When a page is placed in a queue for processing,
 * a check is made whether this page has been processed earlier and whether it is already in the current queue.</p>
 * <p style="text-indent: 20px;">The algorithm terminates in the following cases:</p>
 * 1) Maximum depth reached.<br>
 * 2) The limit of visited pages has been reached.<br>
 * 3) On the pages of the current level, there are no links to pages that have not been processed.<br>
 * <p style="text-indent: 20px;">In the process, the algorithm writes to the statistics file, and upon completion
 * generates a file containing the top pages by the total number of occurrences of the given terms.</p>
 * <p style="text-indent: 20px;">The mandatory input parameters are start-url and a string of terms for collecting
 * statistics on them. More input parameters are described in {@link com.zmicierk.webcrawler.WebCrawlerThread}.</p>
 * <p style="text-indent: 20px;">The result of the package operation is 2 files, the first of which contains all the
 * information obtained in the crawling process without sorting or any selection, and the second contains information
 * containing information about the top pages by the total number of entries.</p>
 */
package com.zmicierk.webcrawler;