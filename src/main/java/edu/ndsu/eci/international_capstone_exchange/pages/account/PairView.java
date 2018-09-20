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

import org.apache.commons.codec.binary.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import edu.ndsu.eci.international_capstone_exchange.auth.ILACRealm;
import edu.ndsu.eci.international_capstone_exchange.persist.Pairing;
import edu.ndsu.eci.international_capstone_exchange.persist.Proposal;
import edu.ndsu.eci.international_capstone_exchange.services.UserInfo;

public class PairView {

  @Property
  private Pairing pairing;
  
  @Property
  private Proposal row;
  
  @Inject
  private UserInfo userInfo;
  
  @RequiresPermissions(ILACRealm.PAIRING_VIEW_INSTANCE)
  public void onActivate(Pairing pairing) {
    this.pairing = pairing;
  }
  
  public Pairing onPassivate() {
    return pairing;
  }
  
  public Proposal getProposal() {
    for (Proposal prop : pairing.getProposals()) {
      if (StringUtils.equals(prop.getUser().getFederatedId(), userInfo.getUser().getFederatedId())) {
        return prop;
      }
    }
    return null;
  }
  
}
