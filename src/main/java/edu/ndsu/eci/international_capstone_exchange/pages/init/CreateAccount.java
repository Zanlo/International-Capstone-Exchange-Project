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
package edu.ndsu.eci.international_capstone_exchange.pages.init;

import java.util.Collections;
import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.log4j.Logger;
import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

import edu.ndsu.eci.international_capstone_exchange.pages.Index;
import edu.ndsu.eci.international_capstone_exchange.persist.CapstoneDomainMap;
import edu.ndsu.eci.international_capstone_exchange.persist.Country;
import edu.ndsu.eci.international_capstone_exchange.persist.Institution;
import edu.ndsu.eci.international_capstone_exchange.persist.User;
import edu.ndsu.eci.international_capstone_exchange.services.UserInfo;
import edu.ndsu.eci.international_capstone_exchange.services.VelocityEmailService;
import edu.ndsu.eci.international_capstone_exchange.util.Status;

/**
 * Create account from SSO provided information
 *
 * Get name, email address, prepopulated with SSO information.
 * Have them specify an institution from autocomplete and then 
 * either choose or name a department at that institution.
 * 
 * http://jumpstart.doublenegative.com.au/jumpstart/examples/ajax/autocompletemixin
 */
public class CreateAccount {

  /** logger */
  private static final Logger LOGGER = Logger.getLogger(CreateAccount.class);
  
  /** alerts */
  @Inject
  private AlertManager alerts;

  /** user info service */
  @Inject
  private UserInfo userInfo;

  /** cayenne context */
  @Inject
  private ObjectContext context;

  /** ajax handler */
  @Inject
  private AjaxResponseRenderer ajaxReponse;

  /** request handler */
  @Inject
  private Request request;

  /** form */
  @Component
  private Form form;

  /** institution zone */
  @InjectComponent
  private Zone institutionZone;

  /** Country */
  @Property
  private Country country;

  @Property
  private List<Institution> institutions;

  /** institution */
  @Property 
  private Institution institution;

  /** logged in user */
  @Property
  private User user;

  /** name that the user wants to use, rather than the one from SSO */
  @Property
  private String name;

  /** email that the user wants to use, rather than the one from SSO */
  @Property
  private String email; 
  
  /** work phone */
  @Property
  private String phone;
  
  /** url of personal website if so desired */
  @Property
  private String url;

  /** name for new dept */
  @Property
  private String departmentName;
  
  /** page to send user to */
  @InjectPage
  private Index index;
  
  @Property
  private boolean agree;
  
  @Inject
  private VelocityEmailService emailService;

  /** 
   * Page setup
   */
  public void setupRender() {
    user = userInfo.getUser();
    name = user.getName();
    email = user.getEmail();
    institutions = Collections.emptyList();
  } 

  /**
   * Handle when country is chosen to setup institutions
   * @param country chosen country
   */
  public void onValueChangedFromCountry(Country country) {
    if (country == null) {
      institutions = Collections.emptyList();
    } else {
      // FIXME sort
      institutions = country.getInstitutions();
    }

    if (request.isXHR()) {
      ajaxReponse.addRender(institutionZone);
    }

  }

  /**
   * Validate form submission
   */
  public void onValidateFromForm() {
    
    if (!StringUtils.startsWith(phone, "+")) {
      phone = "+" + phone;
    }
    
    PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
    PhoneNumber number;
    try {
      number = phoneUtil.parse(phone, null);
      if (!phoneUtil.isValidNumber(number)) {
        if (country.getName().equals("United States") && !phone.startsWith("+1")) {
          phone = StringUtils.replace(phone, "+", "+1");
          onValidateFromForm();
          return;
        } else {
          form.recordError("Phone number format isn't valid, be sure to include country code. For most of North America, this is a 1");
          LOGGER.warn("Failed to validate number: " + number + " " + country.getName());
        }  
      } else {
        phone = phoneUtil.format(number, PhoneNumberFormat.INTERNATIONAL);
      }
    } catch (NumberParseException e) {
      form.recordError("Phone number format isn't valid, be sure to include country code. For most of North America, this is a 1");
      LOGGER.info("Failed to parse number", e);
    }
    
    
    if (!EmailValidator.getInstance().isValid(email)) {
      form.recordError("Email address is not valid");
    }
    
    if (!agree) {
      form.recordError("Must agree to privacy policy.");
    }
  }

  /**
   * Handle a successful form validate 
   * @return page to send user to
   * @throws Exception 
   * @throws ParseErrorException 
   * @throws ResourceNotFoundException 
   */
  public Object onSuccessFromForm() throws ResourceNotFoundException, ParseErrorException, Exception{
    
    User usr = (User) context.localObject(userInfo.getUser().getObjectId(), null);
    if (StringUtils.isBlank(email)) {
      usr.setEmail(usr.getSsoEmail());
    } else {
      usr.setEmail(email);
    }
    
    if (StringUtils.isBlank(name)) {
      usr.setName(usr.getSsoName());
    } else {
      usr.setName(name);
    }
    usr.setUrl(url);
    usr.setDepartmentName(departmentName);
    usr.setStatus(Status.PENDING);
    usr.setWorkPhone(phone);
    usr.setInstitution(institution);

    context.commitChanges();
    alerts.success("Account Creation Request Submitted. Wait until your request is approved");
    
    notifyAdmins();
    
    return index;
  }
  
  private void notifyAdmins() throws ResourceNotFoundException, ParseErrorException, Exception {
    VelocityContext velContext = new VelocityContext();
    velContext.put("user", user);
    emailService.sendAdminEmail(velContext, "new-user.vm", "New user signup");
  }

  /**
   * List of countries to do initial selection from
   * @return list of countries
   */
  public List<Country> getCountries() {
    return CapstoneDomainMap.getInstance().performCountries(context, Status.APPROVED);
  }
}


































