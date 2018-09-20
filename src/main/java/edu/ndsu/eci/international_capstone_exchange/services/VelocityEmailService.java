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

import org.apache.commons.mail.SimpleEmail;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

import edu.ndsu.eci.international_capstone_exchange.persist.User;

public interface VelocityEmailService {

  /**
   * Get a pre-configured email object with the body populated from velocity
   * @param context populated context
   * @param templateName path (with file name) for template to use
   * @return pre-configured email object
   * @throws Exception 
   * @throws ParseErrorException 
   * @throws ResourceNotFoundException 
   */
  public SimpleEmail setupSimpleEmail(VelocityContext context, String templateName, String subject) throws ResourceNotFoundException, ParseErrorException, Exception;

  public boolean sendAdminEmail(VelocityContext context, String templateName, String subject) throws ResourceNotFoundException, ParseErrorException, Exception;
  
  public boolean sendUserEmail(VelocityContext context, String templateName, User user, String subject) throws ResourceNotFoundException, ParseErrorException, Exception;

}
