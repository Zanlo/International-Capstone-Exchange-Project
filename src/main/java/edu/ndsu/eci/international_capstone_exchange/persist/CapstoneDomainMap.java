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

import java.util.List;

import org.apache.cayenne.ObjectContext;

import edu.ndsu.eci.international_capstone_exchange.persist.auto._CapstoneDomainMap;
import edu.ndsu.eci.international_capstone_exchange.util.UserRole;

public class CapstoneDomainMap extends _CapstoneDomainMap {

  private static CapstoneDomainMap instance = new CapstoneDomainMap();

  private CapstoneDomainMap() {
    // singleton
  }

  public static CapstoneDomainMap getInstance() {
    return instance;
  }
  
  public User getUser(ObjectContext context, String source, String id) {
    List<User> users = performUserBySourceIdQuery(context, source, id);
    if (users.isEmpty()) {
      return null;
    }
    return users.get(0);
  }
  
  public Role getRole(ObjectContext context, UserRole role) {
    List<Role> roles = performRoleByNameQuery(context, role);
    if (roles.isEmpty()) {
      return null;
    }
    return roles.get(0);
  }
  
}
