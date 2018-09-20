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
package edu.ndsu.eci.international_capstone_exchange.auth;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.permission.WildcardPermission;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.tapestry5.plastic.MethodInvocation;
import org.apache.tapestry5.services.Environment;

import edu.ndsu.eci.international_capstone_exchange.util.InstanceAccessMethodException;


public class BaseILACRealm extends AuthorizingRealm {
  /** reflection exception error message */
  private static final String REFLECTION_ERROR_MESSAGE = "Reflection error";

  /** logger */
  private static final Logger LOGGER = Logger.getLogger(BaseILACRealm.class);
  
  /** Tapestry environment */
  private Environment environment;
  
  /** map of permissions to methods */
  private Map<String, Method> permissionMap;
  
  /** pattern to perform actual stemming for full permission */
  private final Pattern stemmingPattern = Pattern.compile("^((?:.+?)(?:\\:pk)?)(?:\\:[0-9]+)?$");
  /** pattern to determine if a permission can be stemmed */
  private final Pattern stemablePattern = Pattern.compile("^.+?:pk:[0-9]+$");
  /** pattern to get the pk value from a stemable permission */
  private final Pattern pkValuePattern = Pattern.compile("^.+?:pk:([0-9]+)$");
  /** if stemming should be enabled or not */
  private boolean useStemming = false;
  
  /**
   * Constructor
   * @param environment tapestry environment service
   */
  public BaseILACRealm(Environment environment) {
    super();
    this.environment = environment;
    findMethods();
  }
  
  /**
   * Enable or disable stemming checks for permissions.
   * If stemming isn't needed, disabling checks will result in higher performance.
   * @param useStemming true to enable checks, false to disable checks
   */
  public void setStemming(boolean useStemming) {
    this.useStemming = useStemming; 
  }
  
  /**
   * Get if stemming checks are enabled or disabled.
   * @return true if checks are enabled, false otherwise
   */
  public boolean isStemming() {
    return useStemming;
  }
  
  /**
   * Find the methods marked by the proper annotation.
   */
  private void findMethods() {
    permissionMap = new HashMap<>();
    Method[] methods = this.getClass().getDeclaredMethods();
    // Go through each method, regardless of visibility
    for (int i = 0; i < methods.length; i++) {
      // if the annotation isn't present, skip
      if (!methods[i].isAnnotationPresent(InstanceAccessMethod.class)) {
        continue;
      }
      
      // make sure method signature is valid
      assertValidMethodSig(methods[i]);
      
      Annotation[] methodAnnotations = methods[i].getAnnotations();
      
      for (Annotation methodAnnotation : methodAnnotations) {
        // find the correct annotation
        if (methodAnnotation instanceof InstanceAccessMethod) {
          for (String perm : ((InstanceAccessMethod) methodAnnotation).value()) {
            permissionMap.put(perm, methods[i]);
          }
        }
      }
    }
  }
  
  /**
   * Get the stemmed permission.
   * If stemming is turned off, permission is returned
   * @param permission permission to stem
   * @return stemmed result
   */
  public String getStemmedPermission(String permission) {
    // skip regex checks if disabled
    if (!useStemming) {
      return permission;
    }
    
    Matcher matcher = stemmingPattern.matcher(permission);
    matcher.matches();
    // remember 0 is entire string
    return matcher.group(1);
  }
  
  /**
   * Test if permission is stemmable, if stemming is enabled
   * @param permission permission to check
   * @return true if stemable and stemming is enabled, false otherwise
   */
  public boolean isStemmable(String permission) {
    if (!useStemming) {
      return false;
    }
    
    return stemablePattern.matcher(permission).matches();
  }
  
  /**
   * Get the pk out of a stemable permission.
   * Goes kaboom if permission isn't stemmable.
   * @param permission permission to operate on
   * @return int value from end of permission
   */
  public int getPermissionPk(String permission) {
    Matcher matcher = pkValuePattern.matcher(permission);
    matcher.matches();
    return Integer.parseInt(matcher.group(1));
  }

  /**
   * "Assert" that a method is valid.
   * Not a true assertion as those only run with special flags,
   * and this runs the checks every time.
   * Throws a runtime exception of the preconditions don't hold up:
   * 1) Return type of boolean
   * 2) No parameters, or one parameter of type String
   * @param method method to check
   */
  private void assertValidMethodSig(Method method) {
    // must have a return type of boolean, otherwise throw a runtime exception
    if (!method.getReturnType().getName().equals("boolean")) {
      LOGGER.fatal("Function " + method + " must be of return type boolean but found " + method.getReturnType().getName());
      throw new InstanceAccessMethodException(method + " does not have the correct turn type.");
    }
    
    // Must have either zero parameters or one string parameter, otherwise throw a runtime exception
    Class<?>[] types = method.getParameterTypes();
    if (types.length > 1 || (types.length == 1 && types[0] != String.class)) {
      LOGGER.fatal("Function " + method + " must either take no arguements, or one string argument");
      throw new InstanceAccessMethodException(method + " does not have the correct parameter count and/or types.");
    }    
  }
  
  @Override
  protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
    return null;
  }

  @Override
  protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) {
    return null;
  }

  @Override
  public boolean isPermitted(PrincipalCollection principals, Permission permission) {
    // make sure this is the right type of permission and that is has useful information
    if (!validate(permission)) {
      return false;
    }
    
    String permissionName = getPermissionName(permission);
    String stemmedName = getStemmedPermission(permissionName);

    // if we don't handle this permission
    if (!permissionMap.containsKey(stemmedName)) {
      return false;
    }

    try {
      if (permissionMap.get(stemmedName).getParameterTypes().length == 1) {
        return (boolean) permissionMap.get(stemmedName).invoke(this, permissionName);
      } else {
        return (boolean) permissionMap.get(stemmedName).invoke(this);
      }
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      // This should probably never happen due to the checks on setting everything up.
      LOGGER.fatal("Failed to invoke permission check", e);
      return false;
    }
  }
  
  /**
   * Get Tapestry method invocation
   * @return method invocation
   */
  public MethodInvocation getInvocation() {
    return environment.peek(MethodInvocation.class);
  }
  
  /**
   * Get the permission name
   * @param permission permission to get name of
   * @return permission name
   */
  protected String getPermissionName(Permission permission) {
    return permission.toString().replaceAll("[\\[\\]]", "");
  }
  
  /**
   * Get the permission parts.
   * All of this because getParts() is protected.
   * @param permission permission to inspect
   * @return parts of the permission
   */
  @SuppressWarnings("unchecked")
  protected List<Set<String>> getParts(Permission permission) {
    List<Set<String>> parts = Collections.EMPTY_LIST;

    Method method = null;

    try {
      method = permission.getClass().getDeclaredMethod("getParts");
      method.setAccessible(true);
      parts = (List<Set<String>>) method.invoke(permission);
      // a bad permission could result in this being null, ad we prefer empty to null
      if (parts == null) {
        parts = Collections.EMPTY_LIST;
      }
    } catch (InvocationTargetException | IllegalArgumentException | IllegalAccessException | SecurityException | NoSuchMethodException e) {
      LOGGER.info(REFLECTION_ERROR_MESSAGE, e);
    } finally {
      if (method != null) {
        method.setAccessible(false);
      }
    }
    return parts;
  }

  /**
   * Validates that this permission is of the correct type, and contains useful information
   * @param permission permission to check
   * @return true if permission warrants further investigation, false otherwise
   */
  protected boolean validate(Permission permission) {
    if (!(permission instanceof WildcardPermission)) {
      return false;
    }
    
    if (getParts(permission).isEmpty()) {
      return false;
    }
    
    if (!isStemmable(getPermissionName(permission)) && getInvocation() == null) {
      return false;
    }
    
    return true;
  }
}
