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

import java.util.Base64;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.web.filter.authc.LogoutFilter;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.Decorate;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.services.ApplicationDefaults;
import org.apache.tapestry5.ioc.services.SymbolProvider;
import org.apache.tapestry5.services.BaseURLSource;
import org.apache.tapestry5.services.Environment;
import org.apache.tapestry5.services.ExceptionReporter;
import org.apache.tapestry5.services.HttpServletRequestFilter;
import org.tynamo.security.SecuritySymbols;
import org.tynamo.security.federatedaccounts.FederatedAccount.FederatedAccountType;
import org.tynamo.security.federatedaccounts.FederatedAccountSymbols;
import org.tynamo.security.federatedaccounts.pac4j.services.Pac4jFederatedRealm;
import org.tynamo.security.federatedaccounts.pac4j.services.Pac4jOauthClientLocator.SupportedClient;
import org.tynamo.security.federatedaccounts.services.FederatedAccountService;
import org.tynamo.security.federatedaccounts.services.FederatedSignInOptions;
import org.tynamo.security.federatedaccounts.services.FederatedSignInOptions.OptionType;
import org.tynamo.security.services.SecurityFilterChainFactory;
import org.tynamo.security.services.impl.SecurityFilterChain;

import edu.ndsu.eci.international_capstone_exchange.auth.FederatedAccountsRealm;
import edu.ndsu.eci.international_capstone_exchange.auth.ILACRealm;
import edu.ndsu.eci.international_capstone_exchange.auth.LocalDevRealm;
import edu.ndsu.eci.international_capstone_exchange.persist.User;
import edu.ndsu.eci.international_capstone_exchange.services.impl.ECIFederatedAccountService;
import edu.ndsu.eci.international_capstone_exchange.services.impl.EmailServiceImpl;
import edu.ndsu.eci.international_capstone_exchange.services.impl.FormInputTrimmerFilterImpl;
import edu.ndsu.eci.international_capstone_exchange.services.impl.HtmlCleanerImpl;
import edu.ndsu.eci.international_capstone_exchange.services.impl.UserInfoImpl;
import edu.ndsu.eci.international_capstone_exchange.services.impl.VelocityEmailServiceImpl;
import edu.ndsu.eci.international_capstone_exchange.services.impl.VelocityServiceImpl;
import edu.ndsu.eci.international_capstone_exchange.util.EmailConfig;
import edu.ndsu.eci.international_capstone_exchange.util.OAuthConfig;
import edu.ndsu.eci.international_capstone_exchange.util.SingleUserMode;

/**
 * This module is automatically included as part of the Tapestry IoC Registry, it's a good place to
 * configure and extend Tapestry, or to place your own service definitions.
 * 
 */
public class AppModule {

  /** from address symbol name */
  public static final String FROM_ADDRESS = "from.address";

  /** run mode environment variable and symbol name */
  public static final String RUN_MODE = "run.mode";

  /** production value in run mode for production */
  public static final String RUN_MODE_PRODUCTION = "production";

  /** development value in run mode for development */
  public static final String RUN_MODE_DEVELOPMENT = "development";

  /** local development value in run mode for active development, enables back door authentication */
  public static final String RUN_MODE_PROTOTYPE = "prototype";
  //
  //

  public static void bind(ServiceBinder binder) {
    binder.bind(FederatedAccountService.class, ECIFederatedAccountService.class);
    binder.bind(AuthorizingRealm.class, FederatedAccountsRealm.class).withId(FederatedAccountsRealm.class.getSimpleName());
    binder.bind(UserInfo.class, UserInfoImpl.class);
    binder.bind(FormInputTrimmerFilter.class, FormInputTrimmerFilterImpl.class);
    binder.bind(HtmlCleaner.class, HtmlCleanerImpl.class);
    binder.bind(EmailService.class, EmailServiceImpl.class);
    binder.bind(VelocityService.class, VelocityServiceImpl.class);
    binder.bind(VelocityEmailService.class, VelocityEmailServiceImpl.class);
  }

  public static void contributeFederatedAccountService(MappedConfiguration<String, Object> configuration) {
    // you can either map each realm to the same entity...
    configuration.add("*", User.class);
    // or, you can use different entities
    // configuration.add(Constants.TWITTER_REALM, TwitterAccount.class);
    // configuration.add(FederatedAccountType.pac4j_.name() + SupportedClient.google2.name(), GoogleAccount.class);

    // Now, you also have to map the desired id (the subject principal) to an attribute of the entity
    //    configuration.add("facebook.id", "facebookId");
    //    configuration.add("twitter.id", "twitterId");
  }

  public static void contributeFactoryDefaults(MappedConfiguration<String, Object> configuration) {
    // The values defined here (as factory default overrides) are themselves
    // overridden with application defaults by DevelopmentModule and QaModule.

    // This is something that should be removed when going to production, but is useful
    // in the early stages of development.
    configuration.override(SymbolConstants.PRODUCTION_MODE, false);
  }

  public static void contributeApplicationDefaults(MappedConfiguration<String, Object> configuration) throws NamingException {
    // Contributions to ApplicationDefaults will override any contributions to
    // FactoryDefaults (with the same key). Here we're restricting the supported
    // locales to just "en" (English). As you add localised message catalogs and other assets,
    // you can extend this list of locales (it's a comma separated series of locale names;
    // the first locale name is the default when there's no reasonable match).
    configuration.add(SymbolConstants.SUPPORTED_LOCALES, "en");

    OAuthConfig oConfig = OAuthConfig.getFromJNDI("bean/pac4j"); 

    // You should change the passphrase immediately; the HMAC passphrase is used to secure
    // the hidden field data stored in forms to encrypt and digitally sign client-side data.
    configuration.add(SymbolConstants.HMAC_PASSPHRASE, oConfig.getHmac());
    // this needs to be fixed length, and is assumed to be base64 encoded, which the HMAC likely isn't
    configuration.add(SecuritySymbols.REMEMBERME_CIPHERKERY, Base64.getEncoder().encodeToString((StringUtils.substring(oConfig.getHmac(), 0, 16)).getBytes()));

    // these are the defaults, change as needed 
    // configuration.add(FederatedAccountSymbols.COMMITAFTER_OAUTH, “true”); 
    // configuration.add(FederatedAccountSymbols.HTTPCLIENT_ON_GAE, “false”); 
    // configuration.add(FederatedAccountSymbols.SUCCESSURL, “”); // empty string implies host name only

    // set your oauth app credentials
    // Use the constants in Pac4jFederatedRealm for pac4j app credentials 
    //   configuration.add(Pac4jFederatedRealm.GOOGLE_CLIENTID, "<your_google_api_app_key_here>");

    //    configuration.add(Pac4jFederatedRealm.FACEBOOK_CLIENTID, oConfig.getFacebookId());
    //    configuration.add(Pac4jFederatedRealm.FACEBOOK_CLIENTSECRET, oConfig.getFacebookSecret());
    //    configuration.add(Pac4jFederatedRealm.TWITTER_CLIENTID, oConfig.getTwitterId());
    //    configuration.add(Pac4jFederatedRealm.TWITTER_CLIENTSECRET, oConfig.getTwitterSecret());
    configuration.add(Pac4jFederatedRealm.GOOGLE_CLIENTID, oConfig.getGoogleId());
    configuration.add(Pac4jFederatedRealm.GOOGLE_CLIENTSECRET, oConfig.getGoogleSecret());

    configuration.add(SecuritySymbols.LOGIN_URL, "/login");

    configuration.add(SymbolConstants.HOSTNAME, oConfig.getHostname());
    configuration.add(FederatedAccountSymbols.DEFAULT_RETURNPAGE, "init/route");
    //    configuration.add(SymbolConstants.HOSTPORT, "443");
    //    configuration.add(SymbolConstants.HOSTPORT_SECURE, "443");
    //    configuration.add(SymbolConstants.SECURE_ENABLED, "true");

    // Uses run.mode from Tomcat to determine if production or not.
    // run.mode comes from lift standards
    //String runMode = System.getProperty(RUN_MODE, RUN_MODE_DEVELOPMENT);
    String runMode = System.getProperty(RUN_MODE, RUN_MODE_PROTOTYPE);
    configuration.add(RUN_MODE, runMode);

    if (runMode.equals(RUN_MODE_PRODUCTION)) {
      configuration.add(SymbolConstants.PRODUCTION_MODE, Boolean.TRUE.toString());
    } else {
      configuration.add(SymbolConstants.PRODUCTION_MODE, Boolean.FALSE.toString());
    }

    // This goes to true in production, and then parts of Summernote throw errors on minify, or rather the minifier throws errors on Summernote
    configuration.add(SymbolConstants.MINIFICATION_ENABLED, Boolean.FALSE.toString());

    Context initCtx = new InitialContext();
    Context envCtx = (Context) initCtx.lookup("java:comp/env");
    EmailConfig emailConf =  (EmailConfig) envCtx.lookup("bean/emailconf");
    configuration.add(FROM_ADDRESS, emailConf.getFromAddress());
    
  }

  public static void contributeWebSecurityManager(Configuration<Realm> configuration, @InjectService("FederatedAccountsRealm") AuthorizingRealm authorizingRealm, Environment environment, UserInfo userInfo, @Symbol(RUN_MODE) String runMode) throws NamingException {

    configuration.add(authorizingRealm);
    ILACRealm ilac = new ILACRealm(environment, userInfo);
    configuration.add(ilac);

    // only include this realm if it is running on a desktop
    if (StringUtils.equals(runMode, RUN_MODE_PROTOTYPE)) {
      Context initCtx = new InitialContext();
      Context envCtx = (Context) initCtx.lookup("java:comp/env");
      SingleUserMode singleConf =  (SingleUserMode) envCtx.lookup("bean/singleuser");
      LocalDevRealm localRealm = new LocalDevRealm(singleConf.getCredential());
      configuration.add(localRealm);
    }

    //    configuration.add(new Pac4jFederatedRealm(logger, federatedAccountService));
  }

  /**
   * Use annotation or method naming convention: <code>contributeApplicationDefaults</code>
   */
  @Contribute(SymbolProvider.class)
  @ApplicationDefaults
  public static void setupEnvironment(MappedConfiguration<String, Object> configuration) {
    // Support for jQuery is new in Tapestry 5.4 and will become the only supported
    // option in 5.5.
    configuration.add(SymbolConstants.JAVASCRIPT_INFRASTRUCTURE_PROVIDER, "jquery");
    //    configuration.add(SymbolConstants.BOOTSTRAP_ROOT, "context:mybootstrap");
  }

  @Contribute(FederatedSignInOptions.class)
  public static void provideDefaultSignInBlocks(MappedConfiguration<String,OptionType> configuration) {
    //    configuration.add(FederatedAccountType.pac4j_.name() + SupportedClient.facebook.name(), OptionType.primary);
    //    configuration.add(FederatedAccountType.pac4j_.name() + SupportedClient.twitter.name(), OptionType.primary);
    configuration.add(FederatedAccountType.pac4j_.name() + SupportedClient.google2.name(), OptionType.primary);

  }

  public static void contributeSecurityConfiguration(OrderedConfiguration<SecurityFilterChain> configuration, SecurityFilterChainFactory factory) {
    configuration.add("admin", factory.createChain("/admin/**").add(factory.roles(), FederatedAccountsRealm.ADMIN_ROLE).build());
    configuration.add("account", factory.createChain("/account/**").add(factory.roles(), FederatedAccountsRealm.APPROVED_USER_ROLE).build());
    configuration.add("create-account", factory.createChain("/init/**").add(factory.user()).build());
    LogoutFilter logoutFilter = new LogoutFilter();
    logoutFilter.setName("logout");
    // @FIXME this URL may not be adequate, can't remember at the moment.
    logoutFilter.setRedirectUrl("/");
    configuration.add("logout", factory.createChain("/logout").add(logoutFilter).build());
  }

  public static void contributeServiceOverride(MappedConfiguration<Class,Object> configuration, @Symbol(SymbolConstants.HOSTNAME) final String hostname) {
    BaseURLSource source = new BaseURLSource() {
      public String getBaseURL(boolean secure) {
        return "https://" + hostname;
      }
    };

    configuration.add(BaseURLSource.class, source);
  }

  /**
   * Contributes custom request handlers to the pipeline.
   *
   * @param configuration The ordered request handler configuration.
   * @param formInputTrimmerFilter A custom filter/service to trim leading/trailing whitespace from form inputs.
   */
  public void contributeHttpServletRequestHandler(OrderedConfiguration<HttpServletRequestFilter> configuration, @InjectService("FormInputTrimmerFilter") HttpServletRequestFilter formInputTrimmerFilter) {
    configuration.add("FormInputTrimmerFilter", formInputTrimmerFilter);
  }

  /**
   * By default, Tapestry's ExceptionReporter implementation writes verbose text files to the
   * "build/exceptions" directory. This replaces that implementation with one that does nothing.
   * (The exceptions still get logged elsewhere.)
   */
  @Decorate(serviceInterface = ExceptionReporter.class)
  public static ExceptionReporter preventExceptionFileWriting(final ExceptionReporter exceptionReporter) {
    return new ExceptionReporter() {
      @Override
      public void reportException(Throwable exception) {
      }
    };
  }

}
