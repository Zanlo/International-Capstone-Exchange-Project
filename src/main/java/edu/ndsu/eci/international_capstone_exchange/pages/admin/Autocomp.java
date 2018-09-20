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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import edu.ndsu.eci.international_capstone_exchange.persist.Institution;

/**
 * Probably better off using Solr
 *
 */
public class Autocomp {
  
  @Property
  private String countryName;

  @Inject
  private ObjectContext context;
  
  private Set<String> institutions;
  
  public void onActivate() {
    List<Institution> insts = context.performQuery(new SelectQuery(Institution.class));
    institutions = new HashSet<>();
    for (Institution inst : insts) {
      institutions.add(inst.getName().toLowerCase());
    }
  }
  
  public List<String> onProvideCompletionsFromCountryName(String partial) {
    
    List<String> matches = new ArrayList<String>();
    partial = partial.toLowerCase();

    for (String countryName : institutions) {
        if (countryName.contains(partial)) {
            matches.add(countryName);
        }
    }

    return matches;
  }
  
  
}
