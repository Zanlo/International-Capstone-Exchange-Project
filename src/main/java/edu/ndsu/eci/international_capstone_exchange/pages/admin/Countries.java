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

import edu.ndsu.eci.international_capstone_exchange.persist.Country;

public class Countries {

  @PageActivationContext
  @Property
  private Country country;
  
  @Component
  private BeanEditForm form;
  
  @Inject
  private ObjectContext context;
  
  @Property
  private Country row;
  
  public List<Country> getCountries() {
    return context.performQuery(new SelectQuery(Country.class));
  }
  
  @CommitAfter
  public void onSuccessFromForm() {
    country.setCreated(new Date());
    context.registerNewObject(country);
    country = null;
  }
}
