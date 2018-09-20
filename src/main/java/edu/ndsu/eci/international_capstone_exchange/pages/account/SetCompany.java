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

import org.apache.cayenne.ObjectContext;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.BeanEditForm;
import org.apache.tapestry5.ioc.annotations.Inject;

import com.googlecode.tapestry5cayenne.annotations.CommitAfter;

import edu.ndsu.eci.international_capstone_exchange.auth.ILACRealm;
import edu.ndsu.eci.international_capstone_exchange.persist.PairingCompany;
import edu.ndsu.eci.international_capstone_exchange.persist.Proposal;
import edu.ndsu.eci.international_capstone_exchange.services.UserInfo;

public class SetCompany {
  
  @Property
  private PairingCompany company;
  
  @Inject
  private ObjectContext context;
  
  /** user info service */
  @Inject
  private UserInfo userInfo;
  
  /** form */
  @Component
  private BeanEditForm form;
  
  @Property
  private Proposal proposal;
  
  @InjectPage
  private PairView pairView;
  
  /**
   * Standard on passivate
   * @return proposal
   */
  public Object onPassivate() {
    return proposal;
  }

  @RequiresPermissions(ILACRealm.PROPOSAL_EDIT_INSTANCE)
  public void onActivate(Proposal proposal) {
    this.proposal = proposal;
    if (proposal.getCompany() != null) {
      company = proposal.getCompany();
    }
  }
    
  @CommitAfter
  public Object onSuccessFromForm() {
    if (company.getProposal() == null) {
     company.setProposal(proposal);
    }
    pairView.onActivate(proposal.getPairing());
    return pairView;
  }
}
