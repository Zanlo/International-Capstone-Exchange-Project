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
package edu.ndsu.eci.international_capstone_exchange.persist;

import java.util.Date;

import org.apache.commons.codec.binary.StringUtils;
import org.pac4j.oauth.profile.facebook.FacebookProfile;
import org.pac4j.oauth.profile.google2.Google2Profile;
import org.tynamo.security.federatedaccounts.FederatedAccount;

import com.googlecode.tapestry5cayenne.annotations.Label;

import edu.ndsu.eci.international_capstone_exchange.persist.auto._User;
import edu.ndsu.eci.international_capstone_exchange.util.Status;

public class User extends _User implements FederatedAccount {
	
  @Override
  public boolean isCredentialsExpired() {
    // TODO Auto-generated method stub
    return false;
  }
  

  @Label
  @Override
  public String getName() {
    return super.getName();
  }

  @Override
  public void setCredentialsExpired(boolean value) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public boolean federate(String realmName, Object remotePrincipal, Object remoteAccount) {
    // if we have a context, it is a looked up user.
    // as of right now, if it is looked up, we don't want to modify it (pulling from edugain would be different)
    // return false means that it wasn't modified.
    if (getObjectContext() != null) {
      return false;
    }
    
    setAccountLocked(false);
    setStatus(Status.INIT);
    setSource(realmName);
    setId((String) remotePrincipal);
    if (StringUtils.equals(realmName, "pac4j_facebook")) {
      FacebookProfile profile = (FacebookProfile) remoteAccount; 
      setEmail(profile.getEmail());
      setName(profile.getFirstName() + " " + profile.getFamilyName());
    } else if (StringUtils.equals(realmName, "pac4j_google2")) {
      Google2Profile profile = (Google2Profile) remoteAccount;  
      setEmail(profile.getEmail());
      setName(profile.getDisplayName());
    }
    setSsoName(getName());
    setSsoEmail(getEmail());
    setCreated(new Date());
    setDepartmentName("Unknown");
   
    return false;
  }

  @Override
  public Object getLocalAccountPrimaryPrincipal() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean isAccountLocked() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void setAccountLocked(boolean value) {
    // TODO Auto-generated method stub
    
  }


  /**
   * Copy the public user info from input to this object.
   * @param user user to copy from
   */
  public void copyPublic(User user) {
    setEmail(user.getEmail());
    setName(user.getName());
    setUrl(user.getUrl());
    setWorkPhone(user.getWorkPhone());
    setDepartmentName(user.getDepartmentName());
  }

  /**
   * Combines the id from the federation, and the federation name.
   * Should keep things unique across all federations
   * @return combination of unique from the federation, and the federation name.
   */
  public String getFederatedId() {
    return getId() + getSource();
  }
  
}
