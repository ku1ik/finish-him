Finish Him! Plugin
================

About
-----

Finish Him! is a smart and quick word completion (expansion) plugin similar to Textmate's or Netbeans word completion. It will complete the current word based on matches in the current and other visible buffers.

How it works?
-------------

It's main differences with standard JEdit's "Complete Word" action are:

* it doesn't show popup with completion options. If there are multiple matches, you can cycle through these by pressing Finish Him! keyboard shortcut continuously - hitting keyboard shortcut few times is much faster than locating the word in popup list
* suggested words are sorted by distance from the caret according to these rules:
  * first, words from current buffer found before caret, nearest words first;
  * then words from current buffer found after caret, nearest words first;
  * then all words from other visible buffers.

Requirements
------------
* JDK 1.6
* JEdit 4.3pre16+

Author / Contact
----------------

Finish Him! was created by Marcin Kulik.

Check out my website for more cool software: [sickill.net](http://sickill.net/)