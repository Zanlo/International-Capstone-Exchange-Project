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

import javax.naming.NamingException;

import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.commons.mail.SimpleEmail;
import org.apache.log4j.Logger;

import edu.ndsu.eci.international_capstone_exchange.services.EmailService;

public class EmailServiceImpl implements EmailService {
  
  /** mail session name */
  private static final String MAIL_SESSION = "mail/Session";
  /** Logger */
  private static final Logger LOGGER = Logger.getLogger(EmailServiceImpl.class);
  
  @Override
  public SimpleEmail getSimpleEmail() {
    SimpleEmail email = new SimpleEmail();
    try {
      email.setMailSessionFromJNDI(MAIL_SESSION);
    } catch (NamingException namingEx) {
      LOGGER.fatal("Failed to find naming information for email", namingEx);
      return null;
    }
    return email;
  }

  @Override
  public MultiPartEmail getMultiPartEmail() {
    MultiPartEmail email = new MultiPartEmail();
    try {
      email.setMailSessionFromJNDI(MAIL_SESSION);
    } catch (NamingException namingEx) {
      LOGGER.fatal("Failed to find naming information for email", namingEx);
      return null;
    }
    return email;
  }

  @Override
  public HtmlEmail getHtmlEmail() {
    HtmlEmail email = new HtmlEmail();
    try {
      email.setMailSessionFromJNDI(MAIL_SESSION);
    } catch (NamingException namingEx) {
      LOGGER.fatal("Failed to find naming information for email", namingEx);
      return null;
    }
    return email;
  }

}
