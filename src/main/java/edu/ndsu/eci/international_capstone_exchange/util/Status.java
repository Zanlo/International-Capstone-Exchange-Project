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

import org.apache.cayenne.ExtendedEnumeration;

/**
 * Generic status enum that can be used anywhere these limited
 * options make sense. Initial areas for target would include
 * new accounts, new departments, new institutions, and new topics.
 *
 */
public enum Status implements ExtendedEnumeration {

  /** indicates that the item has been declined / rejected */
  DECLINED,
  /** item has been approved for use */
  APPROVED,
  /** item is pending approval */
  PENDING,
  /** item was approved but is now decommissioned */
  DECOMMISSIONED,
  /** item is in the initialization phase, used to detect brand new accounts */
  INIT;

  @Override
  public Object getDatabaseValue() {
    return this.toString();
  }
}
