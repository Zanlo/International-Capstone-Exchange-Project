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

import java.util.Date;
import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.BeanEditForm;
import org.apache.tapestry5.ioc.annotations.Inject;

import com.googlecode.tapestry5cayenne.annotations.CommitAfter;

import edu.ndsu.eci.international_capstone_exchange.persist.ProposalType;
import edu.ndsu.eci.international_capstone_exchange.services.HtmlCleaner;

public class ProposalTypes {

  @PageActivationContext
  @Property
  private ProposalType propType;
  
  @Component
  private BeanEditForm form;
  
  @Inject
  private ObjectContext context;
  
  @Property
  private ProposalType row;
  
  @Inject
  private HtmlCleaner cleaner;
  
  public List<ProposalType> getPropTypes() {
    return context.performQuery(new SelectQuery(ProposalType.class));
  }
  
  @CommitAfter
  public void onSuccessFromForm() {
    propType.setCreated(new Date());
    propType.setDescription(cleaner.cleanCapstone(propType.getDescription()));;
    context.registerNewObject(propType);
    propType = null;
  }
}
