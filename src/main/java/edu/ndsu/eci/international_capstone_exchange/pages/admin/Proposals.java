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
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import edu.ndsu.eci.international_capstone_exchange.persist.CapstoneDomainMap;
import edu.ndsu.eci.international_capstone_exchange.persist.Proposal;
import edu.ndsu.eci.international_capstone_exchange.util.ProposalStatus;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import edu.ndsu.eci.international_capstone_exchange.persist.Subject;

public class Proposals {

  @Inject
  private ObjectContext context;
  
  @Property
  private Proposal row;

  @Property
  private Subject subject;

  @Inject
  private JavaScriptSupport javaScriptSupport;
  
  public List<Proposal> getPendingProposals() {
    return CapstoneDomainMap.getInstance().performProposalsByStatus(context, ProposalStatus.PENDING);
  }

  public List<Proposal> getPairedProposals() {
    return CapstoneDomainMap.getInstance().performProposalsByStatus(context, ProposalStatus.PAIRED);
  }
  
  public List<Proposal> getAllProposals() {
    return context.performQuery(new SelectQuery(Proposal.class));
  }

  void afterRender() {
    javaScriptSupport.require("bootstrap/tab");
  }
}
