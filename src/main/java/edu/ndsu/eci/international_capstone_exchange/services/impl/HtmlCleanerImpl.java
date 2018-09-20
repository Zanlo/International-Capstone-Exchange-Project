// Copyright 2018 North Dakota State University
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package edu.ndsu.eci.international_capstone_exchange.services.impl;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import edu.ndsu.eci.international_capstone_exchange.services.HtmlCleaner;

/**
 * Implementation of the html cleaner service
 *
 */
public class HtmlCleanerImpl implements HtmlCleaner {
  
  /** whitelist of simple plus anchors */
  private Whitelist simpleAnchor;
  
  /** whitelist for most everything but images */
  private Whitelist capstone;
  
  /**
   * Constructor
   */
  public HtmlCleanerImpl() {
    simpleAnchor = Whitelist.simpleText();
    simpleAnchor.addTags("a").addAttributes("a", "href", "title");
    capstone = new Whitelist()
    .addTags(
            "a", "b", "blockquote", "br", "caption", "cite", "code", "col",
            "colgroup", "dd", "div", "dl", "dt", "em", "h1", "h2", "h3", "h4", "h5", "h6",
            "i", "li", "ol", "p", "pre", "q", "strong",
            "table", "tbody", "td", "tfoot", "th", "thead", "tr", "u",
            "ul")
    .addAttributes("a", "href", "title")
    .addAttributes("blockquote", "cite")
    .addAttributes("col", "span", "width")
    .addAttributes("colgroup", "span", "width")
    .addAttributes("img", "align", "alt", "height", "src", "title", "width")
    .addAttributes("ol", "start", "type")
    .addAttributes("q", "cite")
    .addAttributes("table", "summary", "width")
    .addAttributes("td", "abbr", "axis", "colspan", "rowspan", "width")
    .addAttributes(
            "th", "abbr", "axis", "colspan", "rowspan", "scope",
            "width")
    .addAttributes("ul", "type")
    .addProtocols("a", "href", "ftp", "http", "https", "mailto")
    .addProtocols("blockquote", "cite", "http", "https")
    .addProtocols("cite", "cite", "http", "https")
    .addProtocols("q", "cite", "http", "https");
  }
  
  @Override
  public String cleanSimple(String input) {
    return Jsoup.clean(input, Whitelist.simpleText());
  }

  @Override
  public String cleanSimpleAnchor(String input) {
    return Jsoup.clean(input, simpleAnchor);
  }

  @Override
  public String cleanCapstone(String input) {
    return Jsoup.clean(input, capstone);
  }

}