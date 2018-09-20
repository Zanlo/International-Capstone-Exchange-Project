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
package edu.ndsu.eci.international_capstone_exchange.util;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.ndsu.eci.international_capstone_exchange.persist.Proposal;
import edu.ndsu.eci.international_capstone_exchange.persist.ProposalType;
import edu.ndsu.eci.international_capstone_exchange.persist.Subject;

public class PairingScore {

  private int score;
  private Proposal dest;
  
  public PairingScore(Proposal source, Proposal dest) {
    this.dest = dest;
    computeScore(source, dest);
  }
  
  private void computeScore(Proposal source, Proposal dest) {
    score = 0;
    if (dest.getInstitution().getCountry().equals(source.getInstitution().getCountry())) {
      score -= 1000;
    }
    Set<Subject> subjs = new HashSet<>(source.getSubjects());
    
    for (Subject sub : dest.getSubjects()) {
      if (subjs.contains(sub)) {
        score += 100;
      }
    }
  }
  
  public int getScore() {
    return score;
  }
  
  public Proposal getDest() {
    return dest;
  }
  
  public String getDescription() {
    return dest.getDescription();
  }
  
  public double getCost() {
    return dest.getCost();
  }
  
  public Date getCreated() {
    return dest.getCreated();
  }
  
  public int getDurationInWeeks() {
    return dest.getDurationInWeeks();
  }
  
  public Date getLastModified() {
    return dest.getLastModified();
  }
  
  public double getPerStudentWeekly() {
    return dest.getPerStudentWeekly();
  }
  
  public Date getPotentialStart() {
    return dest.getPotentialStart();
  }
  
  public int getTeamSize() {
    return dest.getTeamSize();
  }
  
  public String getInstitution() {
    return dest.getInstitution().getName();
  }
  
  public String getCountry() {
    return dest.getInstitution().getCountry().getName();
  }
  
  public List<Subject> getSubjects() {
    return dest.getSubjects();
  }
  
  public String getName() {
    return dest.getName();
  }
  
  public String getUser() {
    return dest.getUser().getName();
  }
  
  public List<ProposalType> getTypes() {
    return dest.getTypes();
  }

}
