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
package edu.ndsu.eci.international_capstone_exchange.components;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.tynamo.security.services.SecurityService;

import edu.ndsu.eci.international_capstone_exchange.auth.FederatedAccountsRealm;
import edu.ndsu.eci.international_capstone_exchange.services.UserInfo;

/**
 * Layout component for pages of application test-project.
 */
@Import(module="bootstrap/collapse")
public class Layout {
  @Inject
  private ComponentResources resources;

  /**
   * The page title, for the <title> element and the <h1> element.
   */
  @Property
  @Parameter(required = true, defaultPrefix = BindingConstants.LITERAL)
  private String title;

  /** current page name for loop */
  @Property
  private String pageName;

  /** user info */
  @Inject
  private UserInfo userInfo;
  
  @Inject
  private SecurityService securityService;

  /**
   * Get the styling CSS class for specified page
   * @return css styling class
   */
  public String getClassForPageName() {
    return resources.getPageName().equalsIgnoreCase(pageName)
        ? "active"
        : null;
  }

  /**
   * List of page names to be shown in the menu
   * FIXME handle pages and page names
   * @return list of page names
   */
  public String[] getPageNames() {
    if (!userInfo.isLoggedIn()) {
      return new String[]{"Index","Contact","Privacy","Login"};
    }
    
    List<String> pageNames = new ArrayList<>();
    pageNames.add("Index");
    pageNames.add("Contact");
    pageNames.add("Privacy");
    
    if (userInfo.isAdmin()) {
      pageNames.add("Admin/Admin");
    }
    
    if (securityService.hasRole(FederatedAccountsRealm.APPROVED_USER_ROLE)) {
      pageNames.add("Account/Dashboard");
    }
    
    pageNames.add("Logout");
    
    return pageNames.toArray(new String[pageNames.size()]);
  }

}
