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

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.services.HttpServletRequestHandler;

import edu.ndsu.eci.international_capstone_exchange.services.FormInputTrimmerFilter;

/**
 * Tapestry service to trim leading and trailing spaces from form inputs.
 *
 * See the interface for how to contribute the service as a HttpServletRequestFilter.
 * 
 * https://gist.github.com/mrg/8943683
 */
public class FormInputTrimmerFilterImpl implements FormInputTrimmerFilter {

  /*
   * Service the HTTP request by passing it through our custom request wrapper.
   *
   * @see org.apache.tapestry5.services.HttpServletRequestFilter#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.apache.tapestry5.services.HttpServletRequestHandler)
   */
  @Override
  public boolean service(HttpServletRequest request, HttpServletResponse response, HttpServletRequestHandler handler) throws IOException {
    return handler.service(new FormInputTrimmerRequestWrapper(request), response);
  }

  /**
   * Custom request wrapper which trims all calls to get the parameter value.
   * Java Servlets do not allow you to set the parameter values, so this is
   * the best that can be done.
   */
  static class FormInputTrimmerRequestWrapper extends HttpServletRequestWrapper {
    /**
     * Constructor
     * @param request servlet request
     */
    public FormInputTrimmerRequestWrapper(HttpServletRequest request) {
      super(request);
    }

    @Override
    public String getParameter(String parameterName) {
      return StringUtils.trim(super.getParameter(parameterName));
    }

    // TODO For completeness, add getParameterValues and getParameterMap.
  }

}