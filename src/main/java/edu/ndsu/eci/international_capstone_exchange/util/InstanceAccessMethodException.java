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

public class InstanceAccessMethodException extends RuntimeException {

  /** serial id */
  private static final long serialVersionUID = 4416211584377019458L;

  /**
   * Constructor with message
   * @param message exception message
   */
  public InstanceAccessMethodException(String message) {
    super(message);
  }

}
