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


import java.util.ArrayList;
import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import edu.ndsu.eci.international_capstone_exchange.auth.ILACRealm;
import edu.ndsu.eci.international_capstone_exchange.persist.CapstoneDomainMap;
import edu.ndsu.eci.international_capstone_exchange.persist.Pairing;
import edu.ndsu.eci.international_capstone_exchange.persist.Proposal;
import edu.ndsu.eci.international_capstone_exchange.persist.ProposalType;
import edu.ndsu.eci.international_capstone_exchange.persist.Subject;
import edu.ndsu.eci.international_capstone_exchange.persist.User;
import edu.ndsu.eci.international_capstone_exchange.services.UserInfo;
import edu.ndsu.eci.international_capstone_exchange.util.ProposalStatus;
import edu.ndsu.eci.international_capstone_exchange.util.Status;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;

/**
 * User's dashboard to direct them to after login.
 *
 */
public class Dashboard {

  /** user info service */
  @Inject
  private UserInfo userInfo;

  /** logged in user */
  @Property
  private User user;
  
  /** cayenne context */
  @Inject
  private ObjectContext context;
  
  /** tml row for subjects */
  @Property
  private Subject subjectRow;
  
  /** tml row for proposals */
  @Property
  private Proposal proposalRow;
  
  @Property
  private ProposalType propTypeRow;
  
  @Property
  private List<Proposal> proposals;
  
  @Property
  private List<Pairing> pairings;
  
  @Property
  private Pairing pairRow;

  @Inject
  private JavaScriptSupport javaScriptSupport;
  
  /**
   * Setup render, get logged in user
   */
  public void setupRender() {
    user = userInfo.getUser();
    proposals = user.getProposals();
    pairings = new ArrayList<>();
    for (Proposal proposal : proposals) {
      if (proposal.getProposalStatus() == ProposalStatus.PAIRED) {
        pairings.add(proposal.getPairing());
      }
    }
  }
  
  void afterRender() {
    javaScriptSupport.require("bootstrap/tab");
  }
  
  /**
   * List of approved subjects to display their meaning
   * @return list of subjects
   */
  public List<Subject> getSubjects() {
    return CapstoneDomainMap.getInstance().performSubjectsByStatus(context, Status.APPROVED);
  }
  
  /**
   * List of approved proposal types to display their meaning
   * @return list of proposal types
   */
  public List<ProposalType> getProposalTypes() {
    return CapstoneDomainMap.getInstance().performPropTypesByStatus(context, Status.APPROVED);
  }
  
  @RequiresPermissions(ILACRealm.PROPOSAL_EDIT_INSTANCE)
  public void onDelete(Proposal proposal) {
    if (!proposal.isDeletable()) {
      return;
    }
    context.deleteObject(proposal);
    context.commitChanges();
  }
  
}
