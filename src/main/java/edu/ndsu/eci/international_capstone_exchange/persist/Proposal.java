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

import com.googlecode.tapestry5cayenne.annotations.Label;

import edu.ndsu.eci.international_capstone_exchange.persist.auto._Proposal;
import edu.ndsu.eci.international_capstone_exchange.util.ProposalStatus;

public class Proposal extends _Proposal {

  @Label
  @Override
  public String getName() {
    return super.getName();
  }
  
  /**
   * If the proposal is editable or eligible for deletion
   * @return if the proposal can be changed by the author
   */
  public boolean isEditable() {
    return getProposalStatus() == ProposalStatus.PENDING;
  }
  
  /**
   * If it is in a state where it can be deleted
   * @return true if it can be deleted, false otherwise
   */
  public boolean isDeletable() {
    return getProposalStatus() == ProposalStatus.PENDING;
  }
  
  @Override
  public void setUser(User user) {
    super.setUser(user);
    setInstitution(user.getInstitution());
  }
  
  public Proposal getPaired() {
    if (getPairing() == null) {
      return null;
    }
    
    Pairing pairing = getPairing();
    for (Proposal prop : pairing.getProposals()) {
      if (!prop.equals(this)) {
        return prop;
      }
    }
    return null;
  }
  
}
