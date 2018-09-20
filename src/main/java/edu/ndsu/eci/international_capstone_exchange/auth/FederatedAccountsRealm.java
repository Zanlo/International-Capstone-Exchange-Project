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
package edu.ndsu.eci.international_capstone_exchange.auth;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.authz.permission.WildcardPermissionResolver;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.tynamo.security.federatedaccounts.oauth.tokens.OauthAccessToken;

import edu.ndsu.eci.international_capstone_exchange.persist.Role;
import edu.ndsu.eci.international_capstone_exchange.persist.User;
import edu.ndsu.eci.international_capstone_exchange.services.UserInfo;
import edu.ndsu.eci.international_capstone_exchange.util.Status;

public class FederatedAccountsRealm extends AuthorizingRealm {

  public static final String ADMIN_ROLE = "admin";
  
  public static final String APPROVED_USER_ROLE = "user";
  
  private final UserInfo userInfo;
  
  public FederatedAccountsRealm(UserInfo userInfo) {
    super(new MemoryConstrainedCacheManager());
    this.userInfo = userInfo;
    setName("oauthauthorizer");
    setAuthenticationTokenClass(OauthAccessToken.class);
    setPermissionResolver(new WildcardPermissionResolver());
  }
  
  @Override
  protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
    SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
    
    User user = userInfo.getUser();
    
    if (user == null) {
      return authorizationInfo;
    }
    
    for (Role role : user.getRoles()) {
      switch(role.getRole()) {
      case ADMIN: authorizationInfo.addRole(ADMIN_ROLE); break;
      }
    }
    
    if (user.getStatus() == Status.APPROVED) {
      authorizationInfo.addRole(APPROVED_USER_ROLE);
    }
    
    return authorizationInfo;
  }

  @Override
  protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
 // Never participate in authentication process
    return null;
  }

}
