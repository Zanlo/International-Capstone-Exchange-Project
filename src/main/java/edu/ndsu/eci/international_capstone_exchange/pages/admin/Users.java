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
package edu.ndsu.eci.international_capstone_exchange.pages.admin;

import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.commons.mail.SimpleEmail;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

import com.googlecode.tapestry5cayenne.annotations.CommitAfter;

import edu.ndsu.eci.international_capstone_exchange.auth.FederatedAccountsRealm;
import edu.ndsu.eci.international_capstone_exchange.persist.CapstoneDomainMap;
import edu.ndsu.eci.international_capstone_exchange.persist.Role;
import edu.ndsu.eci.international_capstone_exchange.persist.User;
import edu.ndsu.eci.international_capstone_exchange.services.UserInfo;
import edu.ndsu.eci.international_capstone_exchange.services.VelocityEmailService;
import edu.ndsu.eci.international_capstone_exchange.util.Status;
import edu.ndsu.eci.international_capstone_exchange.util.UserRole;

// FIXME A lot remains to be done here.
public class Users {

  @Inject
  private ObjectContext context;
  
  @Property
  private User row;
  
  @Inject
  private UserInfo userInfo;
  
  @Inject
  private VelocityEmailService emailService;
  
  private CapstoneDomainMap map = CapstoneDomainMap.getInstance();
  
  public List<User> getPending() {
    return map.performUsersByStatus(context, Status.PENDING);
  }
  
  public List<User> getActive() {
    return map.performUsersByStatus(context, Status.APPROVED);
  }
  
  public List<User> getDeactivated() {
    return map.performUsersByStatus(context, Status.DECOMMISSIONED);
  }
  
  public List<User> getAdmins() {
    return map.performUsersByRoleQuery(context, UserRole.ADMIN);
  }

  @Inject
  private JavaScriptSupport javaScriptSupport;
  
  @CommitAfter
  public void onApprove(User user) throws ResourceNotFoundException, ParseErrorException, Exception {
    user.setStatus(Status.APPROVED);
    VelocityContext velContext = new VelocityContext();
    emailService.sendUserEmail(velContext, "account-approved.vm", user, "International Capstone Exchange account approved.");
  }
  
  @CommitAfter
  public void onDeny(User user) {
    user.setStatus(Status.DECLINED);
  }
  
  @CommitAfter
  public void onDeactivateUser(User user) {
    user.setStatus(Status.DECOMMISSIONED);
  }
  
  @CommitAfter
  public void onReactivateUser(User user) {
    user.setStatus(Status.APPROVED);
  }
  
  @CommitAfter
  public void onMakeAdmin(User user) {
    // cop out to not have to do a check for the grid
    if (user.getRoles().contains(UserRole.ADMIN)) {
      return;
    }
    Role role = context.newObject(Role.class);
    role.setRole(UserRole.ADMIN);
    role.setUser(user);
   }
  
  @CommitAfter
  public void onRemoveAdmin(User user) {
    // can't remove self
    if (userInfo.getUser().getFederatedId().equals(user.getFederatedId())) {
      return;
    }
    
    for (Role role : user.getRoles()) {
      if (role.getRole() == UserRole.ADMIN) {
        context.deleteObject(role);
        break;
      }
    }
  }
  void afterRender() {
    javaScriptSupport.require("bootstrap/tab");
  }
  
}
