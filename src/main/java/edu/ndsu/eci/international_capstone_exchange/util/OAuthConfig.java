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
package edu.ndsu.eci.international_capstone_exchange.util;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class OAuthConfig {

  private String facebookId;
  private String facebookSecret;
  private String twitterId;
  private String twitterSecret;
  private String hostname;
  private String hmac;
  private String googleId;
  private String googleSecret;
  
  public static OAuthConfig getFromJNDI(String jndiName) throws NamingException {
    Context initCtx = new InitialContext();
    Context envCtx = (Context) initCtx.lookup("java:comp/env");
    return (OAuthConfig) envCtx.lookup(jndiName);
  }
  
  
  
  public String getGoogleId() {
    return googleId;
  }



  public void setGoogleId(String googleId) {
    this.googleId = googleId;
  }



  public String getGoogleSecret() {
    return googleSecret;
  }



  public void setGoogleSecret(String googleSecret) {
    this.googleSecret = googleSecret;
  }



  public String getHmac() {
    return hmac;
  }

  public void setHmac(String hmac) {
    this.hmac = hmac;
  }

  public String getFacebookId() {
    return facebookId;
  }
  public void setFacebookId(String facebookId) {
    this.facebookId = facebookId;
  }
  public String getFacebookSecret() {
    return facebookSecret;
  }
  public void setFacebookSecret(String facebookSecret) {
    this.facebookSecret = facebookSecret;
  }
  public String getTwitterId() {
    return twitterId;
  }
  public void setTwitterId(String twitterId) {
    this.twitterId = twitterId;
  }
  public String getTwitterSecret() {
    return twitterSecret;
  }
  public void setTwitterSecret(String twitterSecret) {
    this.twitterSecret = twitterSecret;
  }
  public String getHostname() {
    return hostname;
  }
  public void setHostname(String hostname) {
    this.hostname = hostname;
  }
  
  
  
}
