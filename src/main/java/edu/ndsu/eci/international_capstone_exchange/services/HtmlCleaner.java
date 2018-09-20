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
package edu.ndsu.eci.international_capstone_exchange.services;

/**
 * Service to clean user provided HTML.
 *
 */
public interface HtmlCleaner {

  /**
   * Corresponds to jsoups simpleText: strong, em 
   * @param input html to clean
   * @return cleaned html
   */
  public String cleanSimple(String input);
  
  /**
   * Corresponds to jsoups simpleText with anchors added: strong, em, a with href and title
   * @param input html to clean
   * @return cleaned html
   */
  public String cleanSimpleAnchor(String input);
  
  /**
   * Custom configuration corresponding to what is most likely needed locally:
   * strong, em, a, lists, tables, p/br/hr, blockquotes, h1-h6. Does not include images
   * @param input html to clean
   * @return cleaned html
   */
  public String cleanCapstone(String input);
}