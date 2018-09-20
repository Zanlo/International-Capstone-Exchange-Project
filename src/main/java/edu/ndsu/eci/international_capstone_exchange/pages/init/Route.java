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

import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.ioc.annotations.Inject;

import edu.ndsu.eci.international_capstone_exchange.pages.Index;
import edu.ndsu.eci.international_capstone_exchange.pages.account.Dashboard;
import edu.ndsu.eci.international_capstone_exchange.pages.admin.Admin;
import edu.ndsu.eci.international_capstone_exchange.persist.User;
import edu.ndsu.eci.international_capstone_exchange.services.UserInfo;
import edu.ndsu.eci.international_capstone_exchange.util.Status;

/**
 * Routes user upon login to the correct place.
 *
 */
public class Route {

  /** user info service */
  @Inject
  private UserInfo userInfo;
  
  /** admin page for admins */
  @InjectPage
  private Admin admin;
  
  /** account creation page for new users */
  @InjectPage
  private CreateAccount create;
  
  /** dashboard for confirmed users */
  @InjectPage
  private Dashboard dashboard;

  /** index for accounts that haven't been authorized */
  @InjectPage
  private Index index;
  
  /** alerts for unauthorized users */
  @Inject
  private AlertManager alerts;
  
  /** 
   * On activate for login
   * @return page for user to view by default
   */
  public Object onActivate() {
    
    User user = userInfo.getUser();
    
    if (user.getStatus() == Status.INIT) {
      return create;
    }
    
    if (userInfo.isAdmin()) {
      return admin;
    }
    
    if (user.getStatus() == Status.APPROVED) {
    	//return create;
    	//return admin;
    	return dashboard;
    }
    
    alerts.warn("Account isn't authorized at this point in time.");
    return index;
  }
}
