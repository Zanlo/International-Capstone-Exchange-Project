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

import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.access.DataContext;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.pac4j.oauth.profile.google2.Google2Profile;
import org.tynamo.security.federatedaccounts.pac4j.Pac4jAuthenticationToken;
import org.tynamo.security.services.SecurityService;

import edu.ndsu.eci.international_capstone_exchange.auth.FederatedAccountsRealm;
import edu.ndsu.eci.international_capstone_exchange.persist.CapstoneDomainMap;
import edu.ndsu.eci.international_capstone_exchange.persist.User;
import edu.ndsu.eci.international_capstone_exchange.services.UserInfo;
import edu.ndsu.eci.international_capstone_exchange.util.SingleAuthToken;

/**
 * Implementation of the userInfo service
 *
 */
public class UserInfoImpl implements UserInfo {

  /** security service */
  @Inject
  private SecurityService security;
  
  /** map */
  private CapstoneDomainMap map = CapstoneDomainMap.getInstance();
  
  @Override
  public User getUser() {
    ObjectContext context = DataContext.createDataContext();
    
    Subject subject = security.getSubject();
    PrincipalCollection principals = subject.getPrincipals();
    String userId = (String) principals.getPrimaryPrincipal();
    List l = principals.asList();
    
    for (Object obj : l) {
      if (obj instanceof Pac4jAuthenticationToken) {
        return getPac4jUser((Pac4jAuthenticationToken) obj, userId, context);
      } else if (obj instanceof SingleAuthToken) {
        return getSingleUser((SingleAuthToken) obj, userId, context);
      }
    }
        
    return null;
  }

  private User getPac4jUser(Pac4jAuthenticationToken token, String userId, ObjectContext context) {
    Object principal = token.getPrincipal();
    // find type, then go.
    if (principal instanceof Google2Profile) {
      // TODO remove the context?
      return map.getUser(context, "pac4j_google2", userId);
    }
    return null;
  }
  
  private User getSingleUser(SingleAuthToken token, String userId, ObjectContext context) {
    Object principal = token.getPrincipal();
    return map.getUser(context, "pac4j_google2", userId);
  }
  
  @Override
  public boolean isAdmin() {
	  
    return security.hasRole(FederatedAccountsRealm.ADMIN_ROLE);
  }

  @Override
  public boolean isLoggedIn() {
    return security.isAuthenticated();
  }

}
