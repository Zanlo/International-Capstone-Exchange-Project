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

import org.apache.tapestry5.services.HttpServletRequestFilter;

/**
* Tapestry HttpServletRequestFilter service designed to be used inside a
* contributeHttpServletRequestHandler startup contribution:
*
* public void contributeHttpServletRequestHandler(OrderedConfiguration&lt;HttpServletRequestFilter&gt; configuration,
* {@literal @}InjectService("FormInputTrimmerFilter") HttpServletRequestFilter formInputTrimmerFilter)
* {
* configuration.add("FormInputTrimmerFilter", formInputTrimmerFilter);
* }
*
* This service will then filter all form inputs and trim leading/trailing spaces.
* 
* Thank you to Michael Gentry on the Cayenne user list for the help.
* https://gist.github.com/mrg/8943683
*/
public interface FormInputTrimmerFilter extends HttpServletRequestFilter {

}