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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.commons.mail.SimpleEmail;
import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

import com.googlecode.tapestry5cayenne.annotations.CommitAfter;

import edu.ndsu.eci.international_capstone_exchange.persist.CapstoneDomainMap;
import edu.ndsu.eci.international_capstone_exchange.persist.Pairing;
import edu.ndsu.eci.international_capstone_exchange.persist.Proposal;
import edu.ndsu.eci.international_capstone_exchange.persist.ProposalType;
import edu.ndsu.eci.international_capstone_exchange.persist.Subject;
import edu.ndsu.eci.international_capstone_exchange.persist.User;
import edu.ndsu.eci.international_capstone_exchange.services.UserInfo;
import edu.ndsu.eci.international_capstone_exchange.services.VelocityEmailService;
import edu.ndsu.eci.international_capstone_exchange.util.PairingScore;
import edu.ndsu.eci.international_capstone_exchange.util.ProposalStatus;
import edu.ndsu.eci.international_capstone_exchange.util.ScoreComparator;

public class SetPairing {

  @Property
  private Proposal source;
  
  @Inject
  private AlertManager alerts;
  
  @Inject
  private ObjectContext context;
  
  @Property
  private PairingScore row;
  
  @Property
  private Subject subjectRow;
  
  @Property
  private ProposalType typeRow;
  
  @InjectPage
  private Proposals successPage;
  
  // FIXME this is a horrible abuse to get the > symbol in without it going to &gt;
  @Property
  private String ifStatement = "  if (window.pageYOffset >= containerPosition) {";
  
  @Inject
  private UserInfo userInfo;
  
  @Inject
  private VelocityEmailService emailService;
  
  public void onActivate(Proposal proposal) {
    if (proposal.getProposalStatus() != ProposalStatus.PENDING) {
      // FIXME probably do something a bit more dramatic here
      alerts.error("Not a valid proposal to pair");
    } else {
      source = proposal;
    }
  }
  
  public List<PairingScore> getProposals() {
    List<PairingScore> scores = new ArrayList<>();
    
    for (Proposal proposal : CapstoneDomainMap.getInstance().performProposalsByStatus(context, ProposalStatus.PENDING)) {
      if (proposal == source) {
        continue;
      }
      scores.add(new PairingScore(source, proposal));
    }
    scores.sort(Collections.reverseOrder(new ScoreComparator()));
    return scores;
  }
  
  public Proposal onPassivate() {
    return source;
  }
  
  @CommitAfter
  public Object onPair(Proposal dest) throws ResourceNotFoundException, ParseErrorException, Exception {
    alerts.success("Proposals paired");
    Pairing pairing = new Pairing();
    pairing.setTmstamp(new Date());
    pairing.setName(source.getInstitution() + " / " + dest.getInstitution() + " " + Calendar.getInstance().get(Calendar.YEAR));
    source.setPairing(pairing);
    source.setProposalStatus(ProposalStatus.PAIRED);
    dest.setPairing(pairing);
    dest.setProposalStatus(ProposalStatus.PAIRED);
    pairing.setAdmin((User) context.localObject(userInfo.getUser().getObjectId(), null));
    
    notifyPairing(pairing);
    
    return successPage;
  }
  
  private void notifyPairing(Pairing pairing) throws ResourceNotFoundException, ParseErrorException, Exception {
    Proposal propA = pairing.getProposals().get(0);
    Proposal propB = pairing.getProposals().get(1);
    
    VelocityContext velContext = new VelocityContext();
    velContext.put("proposal", propA);
    emailService.sendUserEmail(velContext, "proposal-paired.vm", propB.getUser(), "International Capstone Proposal Paired");
    
    velContext = new VelocityContext();
    velContext.put("proposal", propB);
    emailService.sendUserEmail(velContext, "proposal-paired.vm", propA.getUser(), "International Capstone Proposal Paired");
  }
  
}
