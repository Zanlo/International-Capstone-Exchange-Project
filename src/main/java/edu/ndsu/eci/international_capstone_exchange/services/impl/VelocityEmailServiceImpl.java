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

import java.io.StringWriter;
import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.access.DataContext;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.apache.log4j.Logger;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

import edu.ndsu.eci.international_capstone_exchange.persist.CapstoneDomainMap;
import edu.ndsu.eci.international_capstone_exchange.persist.User;
import edu.ndsu.eci.international_capstone_exchange.services.AppModule;
import edu.ndsu.eci.international_capstone_exchange.services.EmailService;
import edu.ndsu.eci.international_capstone_exchange.services.UserInfo;
import edu.ndsu.eci.international_capstone_exchange.services.VelocityEmailService;
import edu.ndsu.eci.international_capstone_exchange.services.VelocityService;
import edu.ndsu.eci.international_capstone_exchange.util.UserRole;


public class VelocityEmailServiceImpl implements VelocityEmailService {
  /** logger */
  private static final Logger LOGGER = Logger.getLogger(VelocityEmailServiceImpl.class);
  
  /** velocity service */
  private final VelocityService velocityService;
  /** email service */
  private final EmailService emailService;
  
  /** user info */
  private final UserInfo userInfo;
  
  /** if in production or not */
  @Inject
  @Symbol(SymbolConstants.PRODUCTION_MODE)
  private boolean production;
  
  /** from address */
  @Inject
  @Symbol(AppModule.FROM_ADDRESS)
  private String fromAddress;
  
  /**
   * Constructor
   * @param velocity velocity service
   * @param email email service
   */
  public VelocityEmailServiceImpl(VelocityService velocity, EmailService email, UserInfo userInfo) {
    velocityService = velocity;
    emailService = email;
    this.userInfo = userInfo;
  }

  @Override
  public SimpleEmail setupSimpleEmail(VelocityContext context, String templateName, String subject) throws ResourceNotFoundException, ParseErrorException, Exception {
    VelocityEngine engine = velocityService.getEngine();
    
    StringWriter writer = new StringWriter();
    Template template = engine.getTemplate("edu/ndsu/eci/international_capstone_exchange/velocity/" + templateName);
    template.merge(context, writer);
    
    SimpleEmail email = emailService.getSimpleEmail();
    
    try {
      email.setMsg(writer.toString());
    } catch (EmailException e) {
      LOGGER.fatal("This should have never happened", e);
    }
    
    if (!production) {
      email.setSubject("[TEST] " + subject);
    } else {
      email.setSubject(subject);
    }
    
    return email;
  }

  @Override
  public boolean sendAdminEmail(VelocityContext context, String templateName, String subject) throws ResourceNotFoundException, ParseErrorException, Exception {
    SimpleEmail email = setupSimpleEmail(context, templateName, subject);
    email.setFrom(fromAddress);

    ObjectContext objContext = DataContext.createDataContext();
    List<User> admins = CapstoneDomainMap.getInstance().performUsersByRoleQuery(objContext, UserRole.ADMIN);
    
    if (!production) {
      User logged = userInfo.getUser();
      for (User user : admins) {
        if (logged.getFederatedId().equals(user.getFederatedId())) {
          email.addTo(user.getEmail());
          break;
        }
      }
      if (email.getToAddresses().isEmpty()) {
        email.addTo("richard.frovarp@ndsu.edu");
      }
    } else {
      for (User user : admins) {
        email.addTo(user.getEmail());
      }
    }
    
    email.send();
    return true;
  }

  @Override
  public boolean sendUserEmail(VelocityContext context, String templateNull, User user, String subject) throws ResourceNotFoundException, ParseErrorException, Exception {
    SimpleEmail email = setupSimpleEmail(context, templateNull, subject);
    email.setFrom(fromAddress);
    
    if (production) {
      email.addTo(user.getEmail());
    } else {
      email.addTo(userInfo.getUser().getEmail());
    }    
    
    email.send();
    
    return true;
  }

}
