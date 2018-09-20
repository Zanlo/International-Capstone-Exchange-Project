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
package edu.ndsu.eci.international_capstone_exchange.services.impl;

import java.util.Map;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.access.DataContext;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.slf4j.Logger;
import org.tynamo.security.federatedaccounts.FederatedAccount;
import org.tynamo.security.federatedaccounts.FederatedAccountSymbols;
import org.tynamo.security.federatedaccounts.services.AbstractFederatedAccountService;
import org.tynamo.security.federatedaccounts.services.FederatedAccountService;

import edu.ndsu.eci.international_capstone_exchange.persist.CapstoneDomainMap;

public class ECIFederatedAccountService extends AbstractFederatedAccountService implements FederatedAccountService {

  private CapstoneDomainMap map = CapstoneDomainMap.getInstance();
  
  public ECIFederatedAccountService(Logger logger, @Symbol(FederatedAccountSymbols.LOCALACCOUNT_REALMNAME) String localAccountRealmName, Map<String, Object> entityTypesByRealm) {
    super(logger, localAccountRealmName, entityTypesByRealm);
    // TODO Auto-generated constructor stub
  }

  @Override
  protected void saveAccount(FederatedAccount account) {
    ObjectContext context = DataContext.createDataContext();
    context.registerNewObject(account);
    context.commitChanges();
  }

  @Override
  protected void updateAccount(FederatedAccount account) {
    // TODO Auto-generated method stub
    System.out.println("Update account: " + account);
  }

  @Override
  protected FederatedAccount findLocalAccount(Class<?> entityType, String realmName, Object remotePrincipal, Object remoteAccount) {
    ObjectContext context = DataContext.createDataContext();
    return map.getUser(context, realmName, (String) remotePrincipal);
  }

}
