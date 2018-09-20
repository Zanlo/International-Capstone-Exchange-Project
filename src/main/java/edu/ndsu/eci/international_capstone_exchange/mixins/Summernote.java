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
package edu.ndsu.eci.international_capstone_exchange.mixins;

import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectContainer;
import org.apache.tapestry5.corelib.components.TextArea;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.apache.tapestry5.services.javascript.StylesheetLink;

@Import(library = {"init-summernote.js"})
public class Summernote {

  /** javascript support service */
  @Inject
  private JavaScriptSupport javaScriptSupport;
  
  /** container to add mixin to */
  @InjectContainer
  private TextArea textArea;
  
  /**
   * Apply mixin after render
   * @param writer write to write to
   */
  public void afterRender(MarkupWriter writer) {
    javaScriptSupport.importJavaScriptLibrary("https://cdnjs.cloudflare.com/ajax/libs/summernote/0.8.9/summernote-lite.js");
    StylesheetLink stylesheet = new StylesheetLink("https://cdnjs.cloudflare.com/ajax/libs/summernote/0.8.9/summernote-lite.css");
    javaScriptSupport.importStylesheet(stylesheet);
    String id = textArea.getClientId();
    javaScriptSupport.addInitializerCall("initSummernote", id);
  }
}
