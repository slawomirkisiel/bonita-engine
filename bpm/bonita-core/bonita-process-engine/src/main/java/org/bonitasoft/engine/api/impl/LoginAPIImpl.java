/**
 * Copyright (C) 2011-2014 BonitaSoft S.A.
 * BonitaSoft, 32 rue Gustave Eiffel - 38000 Grenoble
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth
 * Floor, Boston, MA 02110-1301, USA.
 **/
package org.bonitasoft.engine.api.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.math.NumberUtils;
import org.bonitasoft.engine.api.LoginAPI;
import org.bonitasoft.engine.api.impl.transaction.CustomTransactions;
import org.bonitasoft.engine.api.impl.transaction.identity.UpdateUser;
import org.bonitasoft.engine.api.impl.transaction.platform.GetDefaultTenantInstance;
import org.bonitasoft.engine.api.impl.transaction.platform.GetTenantInstance;
import org.bonitasoft.engine.api.impl.transaction.platform.Logout;
import org.bonitasoft.engine.authentication.AuthenticationConstants;
import org.bonitasoft.engine.builder.BuilderFactory;
import org.bonitasoft.engine.commons.exceptions.SBonitaException;
import org.bonitasoft.engine.commons.transaction.TransactionContentWithResult;
import org.bonitasoft.engine.commons.transaction.TransactionExecutor;
import org.bonitasoft.engine.core.login.LoginService;
import org.bonitasoft.engine.identity.IdentityService;
import org.bonitasoft.engine.identity.model.SUser;
import org.bonitasoft.engine.identity.model.builder.SUserUpdateBuilder;
import org.bonitasoft.engine.identity.model.builder.SUserUpdateBuilderFactory;
import org.bonitasoft.engine.platform.LoginException;
import org.bonitasoft.engine.platform.LogoutException;
import org.bonitasoft.engine.platform.PlatformService;
import org.bonitasoft.engine.platform.model.STenant;
import org.bonitasoft.engine.recorder.model.EntityUpdateDescriptor;
import org.bonitasoft.engine.service.ModelConvertor;
import org.bonitasoft.engine.service.PlatformServiceAccessor;
import org.bonitasoft.engine.service.TenantServiceAccessor;
import org.bonitasoft.engine.service.TenantServiceSingleton;
import org.bonitasoft.engine.service.impl.ServiceAccessorFactory;
import org.bonitasoft.engine.session.APISession;
import org.bonitasoft.engine.session.SSessionNotFoundException;
import org.bonitasoft.engine.session.SessionNotFoundException;
import org.bonitasoft.engine.session.model.SSession;
import org.bonitasoft.engine.sessionaccessor.SessionAccessor;
import org.bonitasoft.engine.transaction.TransactionService;
import org.springframework.util.CollectionUtils;

/**
 * @author Matthieu Chaffotte
 * @author Zhang Bole
 */
public class LoginAPIImpl extends AbstractLoginApiImpl implements LoginAPI {

    @Override
    @CustomTransactions
    public APISession login(final String userName, final String password) throws LoginException {
        try {
            return loginInternal(userName, password, null);
        } catch (final LoginException e) {
            throw e;
        } catch (final Exception e) {
            throw new LoginException(e);
        }
    }

    @CustomTransactions
    protected APISession login(final String userName, final String password, final Long tenantId) throws LoginException {
        try {
            return loginInternal(userName, password, tenantId);
        } catch (final LoginException e) {
            throw e;
        } catch (final Exception e) {
            throw new LoginException(e);
        }
    }

    @Override
    @CustomTransactions
    public APISession login(final Map<String, Serializable> credentials) throws LoginException {
        try {
            return loginInternal(credentials);
        } catch (final LoginException e) {
            throw e;
        } catch (final Exception e) {
            throw new LoginException(e);
        }
    }

    protected APISession loginInternal(final String userName, final String password, final Long tenantId) throws Exception {
        checkUsernameAndPassword(userName, password);
        Map<String, Serializable> credentials = new HashMap<String, Serializable>();
        credentials.put(AuthenticationConstants.BASIC_USERNAME, userName);
        credentials.put(AuthenticationConstants.BASIC_PASSWORD, password);
        if (tenantId != null && tenantId > -1) {
            credentials.put(AuthenticationConstants.BASIC_TENANT_ID, tenantId);
        }
        return loginInternal(credentials);
    }

    protected APISession loginInternal(final Map<String, Serializable> credentials) throws Exception {
        checkCredentials(credentials);
        final Long tenantId = (NumberUtils.isNumber(String.valueOf(credentials.get(AuthenticationConstants.BASIC_TENANT_ID)))) ? NumberUtils.toLong(String
                .valueOf(credentials.get(AuthenticationConstants.BASIC_TENANT_ID))) : null;
        final String userName = (credentials.get(AuthenticationConstants.BASIC_USERNAME) != null) ? String.valueOf(credentials
                .get(AuthenticationConstants.BASIC_USERNAME)) : null;
        final PlatformServiceAccessor platformServiceAccessor = ServiceAccessorFactory.getInstance().createPlatformServiceAccessor();
        final PlatformService platformService = platformServiceAccessor.getPlatformService();
        final TransactionExecutor platformTransactionExecutor = platformServiceAccessor.getTransactionExecutor();
        // first call before create session: put the platform in cache if necessary
        final TransactionContentWithResult<STenant> getTenant;
        if (tenantId == null) {
            getTenant = new GetDefaultTenantInstance(platformService);
        } else {
            getTenant = new GetTenantInstance(tenantId, platformService);
        }
        platformTransactionExecutor.execute(getTenant);
        final STenant sTenant = getTenant.getResult();
        final long localTenantId = sTenant.getId();
        checkThatWeCanLogin(userName, platformService, sTenant);

        final TenantServiceAccessor serviceAccessor = getTenantServiceAccessor(localTenantId);
        final LoginService loginService = serviceAccessor.getLoginService();
        final IdentityService identityService = serviceAccessor.getIdentityService();
        final TransactionService transactionService = platformServiceAccessor.getTransactionService();

        final Map<String, Serializable> credentialsWithResolvedTenantId = new HashMap<String, Serializable>(credentials);
        credentialsWithResolvedTenantId.put(AuthenticationConstants.BASIC_TENANT_ID, localTenantId);
        final SSession sSession = transactionService.executeInTransaction(new LoginAndRetrieveUser(loginService, identityService,
                credentialsWithResolvedTenantId));
        return ModelConvertor.toAPISession(sSession, sTenant.getName());
    }

    protected void checkUsernameAndPassword(final String userName, final String password) throws LoginException {
        if (userName == null || userName.isEmpty()) {
            throw new LoginException("User name is null or empty");
        }
        if (password == null || password.isEmpty()) {
            throw new LoginException("Password is null or empty");
        }
    }

    protected void checkCredentials(final Map<String, Serializable> credentials) throws LoginException {
        if (CollectionUtils.isEmpty(credentials)) {
            throw new LoginException("credentials are null or empty");
        }
    }

    @SuppressWarnings("unused")
    protected void checkThatWeCanLogin(final String userName, final PlatformService platformService, final STenant sTenant) throws LoginException {
        if (!platformService.isTenantActivated(sTenant)) {
            throw new LoginException("Tenant " + sTenant.getName() + " is not activated !!");
        }
    }

    private class LoginAndRetrieveUser implements Callable<SSession> {

        private final LoginService loginService;

        private final IdentityService identityService;

        private final Map<String, Serializable> credentials;

        public LoginAndRetrieveUser(final LoginService loginService, final IdentityService identityService,
                final Map<String, Serializable> credentials) {
            this.loginService = loginService;
            this.identityService = identityService;
            this.credentials = credentials;
        }

        @Override
        public SSession call() throws Exception {
            SessionAccessor sessionAccessor = null;
            SSession session;
            try {
                session = loginService.login(credentials);
                if (!session.isTechnicalUser()) {
                    Long tenantId = NumberUtils.toLong(String.valueOf(credentials.get(
                            AuthenticationConstants.BASIC_TENANT_ID)), 0L);
                    sessionAccessor = ServiceAccessorFactory.getInstance().createSessionAccessor();
                    sessionAccessor.setSessionInfo(session.getId(), tenantId);
                    final SUser sUser = identityService.getUserByUserName(session.getUserName());
                    if (!sUser.isEnabled()) {
                        throw new LoginException("Unable to login : the user is disable.");
                    }
                    final SUserUpdateBuilder userUpdateBuilder = BuilderFactory.get(SUserUpdateBuilderFactory.class).createNewInstance();
                    final long lastConnection = System.currentTimeMillis();
                    final EntityUpdateDescriptor updateDescriptor = userUpdateBuilder.updateLastConnection(lastConnection).done();
                    final UpdateUser updateUser = new UpdateUser(identityService, sUser.getId(), updateDescriptor, null, null);
                    updateUser.execute();
                }
            } finally {
                if (sessionAccessor != null) {
                    sessionAccessor.deleteSessionId();
                }
            }
            return session;
        }

    }

    protected TenantServiceAccessor getTenantServiceAccessor(final long tenantId) {
        return TenantServiceSingleton.getInstance(tenantId);
    }

    @Override
    @CustomTransactions
    public void logout(final APISession session) throws LogoutException, SessionNotFoundException {
        final TenantServiceAccessor serviceAccessor = getTenantServiceAccessor(session.getTenantId());
        final TransactionExecutor transactionExecutor = serviceAccessor.getTransactionExecutor();
        final Logout logout = new Logout(serviceAccessor.getLoginService(), session.getId());
        try {
            transactionExecutor.execute(logout);
        } catch (final SSessionNotFoundException sbe) {
            throw new SessionNotFoundException(sbe);
        } catch (final SBonitaException sbe) {
            throw new LogoutException(sbe);
        }
    }

}
