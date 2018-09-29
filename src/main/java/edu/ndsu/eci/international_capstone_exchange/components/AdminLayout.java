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
package edu.ndsu.eci.international_capstone_exchange.components;

import org.apache.tapestry5.annotations.Import;
import edu.ndsu.eci.international_capstone_exchange.persist.User;
import edu.ndsu.eci.international_capstone_exchange.services.UserInfo;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.apache.tapestry5.annotations.Environmental;


@Import(stylesheet = "css/adminLayout.css")

public class AdminLayout extends Layout{

    @Environmental
    private JavaScriptSupport javaScriptSupport;

    /** user info service */
    @Inject
    private UserInfo userInfo;

    /** logged in user */
    @Property
    private User user;


    public void setupRender() {

        user = userInfo.getUser();
    }
}
