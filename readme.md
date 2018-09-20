Setup:

Needs integration library for Cayenne:

https://github.com/NDSU-Information-Technology/tapestry5-cayenne

You need to get the f99b version at the moment from master.

Install with mvn install -DskipTests

At the moment the only federated accounts that work is Google.

You will need to use Apache Cayenne 3.0.2 modeler to work on project.

Copy jetty-env-template.xml to jetty-env.xml and apply your own settings. jetty-env.xml is in git ignores, as it shouldn't
be committed, so leave it in the ignore.

You will also need to include in your runtime classpath the MySQL driver, Commons DBCP, and Commons Pool. We don't include
DB dependencies in our projects, and the two Commons projects are only needed with the current Jetty 6 setup.

This will run in RunJettyRun in Eclipse. At the moment it seems to require a mvn package to get the jetty-env.xml files updated.
It users Jetty 6 due to the JNDI configuration. That could be easily update to Jetty 9 as a good task. Settings are as follows:

 * Context: /international-capstone-exchange
 * WebApp dir: target/international-capstone-exchange
 * Show Advanced Options
 * JNDI Support

Under VM arguments
*-Dtapestry.compress-whitespace=false
*-Drun.mode=prototype

Under Webapp Classpath -> User Custom classpath add the MySQL driver, Commons DBCP, and Commons Pool

mvn package to get the initial files under target/international-capstone-exchange. From there running should work. 

Log4J ends up kicking files out to /logs/international-capstone-exchange. So you'll either want to change that for local development, or
make the logs directory at the root, or create a symlink from /logs to /tmp.

At the moment it is going to require that you have HTTPS available on 443 on your system. Installed HTTPD and do a HTTP proxy. 
Keep the paths the same to make life easier.

```
ProxyPass /international-capstone-exchange http://localhost:8080/international-capstone-exchange
ProxyPassReverse /international-capstone-exchange http://localhost:8080/international-capstone-exchange
```

There is a current issue to make it so that local development can happen on http://localhost:8080, so someone please pick that up.

Cayenne modeler can generate the SQL schema, but you proably want to import the db from <todo> as that has a lot already setup.

If developing locally, you might not have the ability to do Google OAuth. If that is the case, you can use the prototype run mode to enable a backdoor. 
Just edit jetty-env.xml and set a credential. From there visit single/single. You'll need to provide a valid username from the DB, and then your password.
Since you can't register without OAuth, you'll need to manually inject a user into the DB. You will likely want to make that user an admin:

```
insert into users (create,departmentName,email,id,institutionPk,name,pk,source,ssoEmail,ssoName,status,url,workPhone) values (now(),'<some department>','<your email>','<user id>',<pick a valid institution pk>,'<your name>',100,'pac4j_google2','<your email>','<your name>','APPROVED',null,'<a valid number with country code>');
insert into roles(pk,role,userPk) values (100,'ADMIN',100);
```


Coding Standards:

 * Follow standards in the code.
 * Tables are plural, resulting Java objects are singular. 
 * Indentation is two spaces
 * Open curly braces are on same line
 * Else / else if is on same line as close curly brace
 * Javadoc everything
 * Refer back to main project owners for additional libraries. We want to keep consistent with what we are using elsewhere, and to preserve licensing.
 * License all files with Apache License 2.0 headers as seen in the project Use mvn apache-rat:check to validate.