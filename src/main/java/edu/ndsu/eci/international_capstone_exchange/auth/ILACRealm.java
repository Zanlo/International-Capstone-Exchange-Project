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

import org.apache.cayenne.PersistenceState;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.tapestry5.plastic.MethodInvocation;
import org.apache.tapestry5.services.Environment;

import edu.ndsu.eci.international_capstone_exchange.persist.Pairing;
import edu.ndsu.eci.international_capstone_exchange.persist.Proposal;
import edu.ndsu.eci.international_capstone_exchange.services.UserInfo;

public class ILACRealm extends BaseILACRealm {

  /** ability to edit a proposal */
  public static final String PROPOSAL_EDIT_INSTANCE = "proposal_edit:instance";

  /** ability to view pairing details */
  public static final String PAIRING_VIEW_INSTANCE = "pairing_view:instance";

  private final UserInfo userInfo;

  public ILACRealm(Environment environment, UserInfo userInfo) {
    super(environment);
    this.userInfo = userInfo;
  }

  @InstanceAccessMethod(PROPOSAL_EDIT_INSTANCE) 
  public boolean isProposalEditMemeber() {
    MethodInvocation invocation = getInvocation();

    if (invocation == null) {
      return false;
    }

    Proposal proposal = (Proposal) invocation.getParameter(0);

    if (proposal.getPersistenceState() == PersistenceState.TRANSIENT) {
      return true;
    }

    return StringUtils.equals(proposal.getUser().getFederatedId(), userInfo.getUser().getFederatedId());

  }

  @InstanceAccessMethod(PAIRING_VIEW_INSTANCE)
  public boolean isPairingMember() {
    MethodInvocation invocation = getInvocation();

    if (invocation == null) {
      return false;
    }

    Pairing pairing = (Pairing) invocation.getParameter(0);
    
    for (Proposal prop : pairing.getProposals()) {
      if (StringUtils.equals(prop.getUser().getFederatedId(), userInfo.getUser().getFederatedId())) {
        return true;
      }
    }
    
    return userInfo.isAdmin();
  }

}
