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

import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.commons.mail.SimpleEmail;

public interface EmailService {
  /**
   * Get a configured (host/port) Email object
   * @return configured email object
   */
  public SimpleEmail getSimpleEmail();
  
  /**
   * Get a configured (host/port) email object that supports attachments
   * @return configured email object that supports attachments
   */
  public MultiPartEmail getMultiPartEmail();
  
  /**
   * Get a configured (host/port) email object that supports HTML
   * @return configured email object that supports HTML
   */
  public HtmlEmail getHtmlEmail();
}
