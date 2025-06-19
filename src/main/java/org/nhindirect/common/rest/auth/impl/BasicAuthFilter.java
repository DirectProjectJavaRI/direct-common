

package org.nhindirect.common.rest.auth.impl;

import java.io.IOException;
import java.security.Principal;
import java.util.Locale;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.nhindirect.common.rest.auth.BasicAuthValidator;
import org.nhindirect.common.rest.auth.exceptions.BasicAuthException;

public class BasicAuthFilter implements Filter {
    protected static final String SESSION_PRINCIPAL_ATTRIBUTE = "NHINDAuthPrincipalAttr";
    protected boolean allowSessions = true;
    protected BasicAuthValidator validator;
    protected boolean forceSSL = false;

    public BasicAuthFilter() {
    }

    public BasicAuthFilter(BasicAuthValidator validator) {
        this.validator = validator;
    }

    public void setBasicAuthValidator(BasicAuthValidator validator) {
        this.validator = validator;
    }

    public void setForceSSL(boolean forceSSL) {
        this.forceSSL = forceSSL;
    }

    public void setAllowSessions(boolean allowSessions) {
        this.allowSessions = allowSessions;
    }

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest)request;
        HttpServletResponse httpResponse = (HttpServletResponse)response;
        if (this.forceSSL && !request.isSecure()) {
            httpResponse.sendError(403);
        } else if (this.isPrincipal(httpRequest)) {
            chain.doFilter(request, response);
        } else {
            if (this.allowSessions) {
                HttpSession session = httpRequest.getSession(true);
                Principal sessionPrin = (Principal)session.getAttribute("NHINDAuthPrincipalAttr");
                if (sessionPrin != null) {
                    HttpServletRequest wrappedRequest = (HttpServletRequest)(this.isPrincipal(httpRequest) ? httpRequest : new PrincipalOverrideRequestWrapper(httpRequest, sessionPrin));
                    chain.doFilter(wrappedRequest, response);
                    return;
                }
            }

            String authHeader = httpRequest.getHeader("Authorization");
            if (authHeader != null && authHeader.toUpperCase(Locale.getDefault()).startsWith("BASIC")) {
                Principal princ;
                try {
                    princ = this.validator.authenticate(authHeader);
                } catch (BasicAuthException var11) {
                    String scheme = httpRequest.isSecure() ? "https://" : "http://";
                    String realm = scheme + httpRequest.getLocalName();
                    httpResponse.setHeader("WWW-Authenticate", "BASIC " + realm);
                    httpResponse.sendError(401);
                    return;
                }

                if (this.allowSessions) {
                    HttpSession session = httpRequest.getSession(true);
                    session.setAttribute("NHINDAuthPrincipalAttr", princ);
                }

                HttpServletRequest wrappedRequest = (HttpServletRequest)(this.isPrincipal(httpRequest) ? httpRequest : new PrincipalOverrideRequestWrapper(httpRequest, princ));
                chain.doFilter(wrappedRequest, httpResponse);
            } else {
                httpResponse.sendError(401);
            }
        }
    }

    protected boolean isPrincipal(HttpServletRequest httpRequest) {
        return httpRequest.getUserPrincipal() != null;
    }

    public void destroy() {
    }

    protected static class PrincipalOverrideRequestWrapper extends HttpServletRequestWrapper {
        private final Principal principal;

        public PrincipalOverrideRequestWrapper(HttpServletRequest request, Principal principal) {
            super(request);
            this.principal = principal;
        }

        public String getRemoteUser() {
            return this.principal == null ? null : this.principal.getName();
        }

        public Principal getUserPrincipal() {
            return this.principal;
        }
    }
}
