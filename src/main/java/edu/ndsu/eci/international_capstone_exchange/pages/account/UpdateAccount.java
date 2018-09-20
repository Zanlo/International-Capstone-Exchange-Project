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
package edu.ndsu.eci.international_capstone_exchange.pages.account;

import org.apache.cayenne.ObjectContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.log4j.Logger;
import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.BeanEditForm;
import org.apache.tapestry5.ioc.annotations.Inject;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

import edu.ndsu.eci.international_capstone_exchange.persist.User;
import edu.ndsu.eci.international_capstone_exchange.services.UserInfo;

public class UpdateAccount {
  
  /** logger */
  private static final Logger LOGGER = Logger.getLogger(UpdateAccount.class);
  
  /** user info service */
  @Inject
  private UserInfo userInfo;

  /** logged in user */
  @Property
  private User user;
  
  /** cayenne context */
  @Inject
  private ObjectContext context;
  
  /** form */
  @Component
  private BeanEditForm form;
  
  /** alerts */
  @Inject
  private AlertManager alerts;
  
  @InjectPage
  private Dashboard index;
  
  /**
   * Setup render, get logged in user
   */
  public void setupRender() {
    user = userInfo.getUser();
  }
  
  /**
   * Validate form submission
   */
  public void onValidateFromForm() {
    String phone = user.getWorkPhone();
    if (!StringUtils.startsWith(phone, "+")) {
      phone = "+" + phone;
    }
    
    PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
    PhoneNumber number;
    try {
      number = phoneUtil.parse(phone, null);
      if (!phoneUtil.isValidNumber(number)) {
        form.recordError("Phone number format isn't valid, be sure to include country code. For most of North America, this is a 1");
      } else {
        user.setWorkPhone(phoneUtil.format(number, PhoneNumberFormat.INTERNATIONAL));
      }
    } catch (NumberParseException e) {
      form.recordError("Phone number format isn't valid, be sure to include country code. For most of North America, this is a 1");
      LOGGER.info("Failed to parse number", e);
    }
    
    
    if (!EmailValidator.getInstance().isValid(user.getEmail())) {
      form.recordError("Email address is not valid");
    }
    
  }
  
  /** 
   * Success from form
   * @return dashboard page
   */
  public Object onSuccessFromForm() {
    // version in memory isn't pulled from activation context, so it has no db context
    User dbUser = userInfo.getUser();
    dbUser.copyPublic(user);
    dbUser.getObjectContext().commitChanges();
    alerts.success("Updated account info");
    return index;
  }
  
}
