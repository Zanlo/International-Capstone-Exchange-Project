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
package edu.ndsu.eci.international_capstone_exchange.services;

import edu.ndsu.eci.international_capstone_exchange.persist.User;

/**
 * Gets information about the logged in user.
 *
 */
public interface UserInfo {
  
  /**
   * Get the db object for the logged in user
   * @return db object for the logged in user
   */
  public User getUser();

  /**
   * Test if user is an admin
   * @return true if admin, false otherwise
   */
  public boolean isAdmin();
  
  /** 
   * Test if user is logged in
   * @return true if logged in, false otherwise
   */
  public boolean isLoggedIn();
}
