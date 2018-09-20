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
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.Persistent;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.SimpleEmail;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.BeanEditForm;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.googlecode.tapestry5cayenne.PersistentEntitySelectModel;
import com.googlecode.tapestry5cayenne.annotations.Cayenne;

import edu.ndsu.eci.international_capstone_exchange.auth.ILACRealm;
import edu.ndsu.eci.international_capstone_exchange.persist.CapstoneDomainMap;
import edu.ndsu.eci.international_capstone_exchange.persist.Proposal;
import edu.ndsu.eci.international_capstone_exchange.persist.ProposalType;
import edu.ndsu.eci.international_capstone_exchange.persist.Subject;
import edu.ndsu.eci.international_capstone_exchange.persist.User;
import edu.ndsu.eci.international_capstone_exchange.services.HtmlCleaner;
import edu.ndsu.eci.international_capstone_exchange.services.UserInfo;
import edu.ndsu.eci.international_capstone_exchange.services.VelocityEmailService;
import edu.ndsu.eci.international_capstone_exchange.util.ProposalStatus;
import edu.ndsu.eci.international_capstone_exchange.util.Status;

public class ProposalSubmission {

  /** user info service */
  @Inject
  private UserInfo userInfo;
  /** cayenne context */
  @Inject
  private ObjectContext context;

  /** page to go back to */
  @InjectPage
  private Dashboard dashboard;

  /** alerts */
  @Inject
  private AlertManager alerts;

  /** form object */
  @Property
  private Proposal proposal;

  /** form */
  @Component
  private BeanEditForm form;

  /** selected subjects from palette */
  @Property
  List<Subject> selectedSubjects;
  
  /** selected proposal types */
  @Property
  List<ProposalType> selectedPropTypes;

  /** encoder for palette */
  @Inject
  @Cayenne
  @Property
  private ValueEncoder<Persistent> encoder;

  /** html cleaner */
  @Inject
  private HtmlCleaner cleaner;
  
  @Inject
  private VelocityEmailService emailService;

  /**
   * Setup the form if it is a new submission
   */
  public void onActivate() {
    if (proposal != null) {
      return;
    }
    proposal = new Proposal();
    // required for autoboxing
    proposal.setCost(0.0);
  }

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
    if (selectedSubjects == null && proposal.getSubjects() != null) {
      selectedSubjects = new ArrayList<>(proposal.getSubjects());
    }
    if (selectedPropTypes == null && proposal.getTypes() != null) {
      selectedPropTypes = new ArrayList<>(proposal.getTypes());
    }
  }

  /**
   * Validate the form
   */
  public void onValidateFromForm() {
    if (StringUtils.isBlank(proposal.getDescription())) {
      form.recordError("Must provide a description");
      context.rollbackChanges();
    }
  }

  /**
   * On form success submission
   * @return return page
   * @throws Exception 
   * @throws ParseErrorException 
   * @throws ResourceNotFoundException 
   */
  public Object onSuccessFromForm() throws ResourceNotFoundException, ParseErrorException, Exception {
    if (proposal.getCreated() == null) {
      proposal.setCreated(new Date());
    }
    proposal.setLastModified(new Date());
    proposal.setProposalStatus(ProposalStatus.PENDING);
    proposal.setDescription(cleaner.cleanCapstone(proposal.getDescription()));
    proposal.setUser((User) context.localObject(userInfo.getUser().getObjectId(), null));
    
    fixupSubjects();
    fixupPropTypes();
    
    context.commitChanges();
    alerts.success("Proposal submitted");
    notifyAdmins();
    return dashboard;
  }
  
  private void notifyAdmins() throws ResourceNotFoundException, ParseErrorException, Exception {
    VelocityContext velContext = new VelocityContext();
    velContext.put("proposal", proposal);
    emailService.sendAdminEmail(velContext, "proposal-submitted.vm", "Proposal submission");
  }
  
  private void fixupSubjects() {
    Set<Subject> existing = new HashSet<>(proposal.getSubjects());
    Set<Subject> newSubjects = new HashSet<>(selectedSubjects);
    
    SetView<Subject> newView = Sets.difference(newSubjects, existing);
    
    for (Subject subject : newView) {
      proposal.addToSubjects(subject);
    }

    SetView<Subject> oldView = Sets.difference(existing, newSubjects);
    
    for (Subject subject : oldView) {
      proposal.removeFromSubjects(subject);
    }
  }
  
  private void fixupPropTypes() {
    Set<ProposalType> existing = new HashSet<>(proposal.getTypes());
    Set<ProposalType> newPropTypes = new HashSet<>(selectedPropTypes);
    
    SetView<ProposalType> newView = Sets.difference(newPropTypes, existing);
    
    for (ProposalType propType : newView) {
      proposal.addToTypes(propType);
    }

    SetView<ProposalType> oldView = Sets.difference(existing, newPropTypes);
    
    for (ProposalType propType : oldView) {
      proposal.removeFromTypes(propType);
    }
  }

  /**
   * Subject checklist model
   * @return checklist model
   */
  public SelectModel getSubjectsModel() {
    return new PersistentEntitySelectModel<>(Subject.class, getSubjects());
  }

  /**
   * Subject checklist model
   * @return checklist model
   */
  public SelectModel getPropTypesModel() {
    return new PersistentEntitySelectModel<>(ProposalType.class, getProposalTypes());
  }
  
  /**
   * Possible subjects
   * @return sort approved subjects
   */
  private List<Subject> getSubjects() {
    return CapstoneDomainMap.getInstance().performSubjectsByStatus(context, Status.APPROVED);
  } 
  
  
  /**
   * Possible proposal types
   * @return sort approved proposal types
   */
  private List<ProposalType> getProposalTypes() {
    return CapstoneDomainMap.getInstance().performPropTypesByStatus(context, Status.APPROVED);
  } 
}
