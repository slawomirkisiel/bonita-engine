/**
 * Copyright (C) 2015 BonitaSoft S.A.
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
package org.bonitasoft.engine.services;

import java.io.IOException;

/**
 * @author Baptiste Mesta
 * @author Celine Souchet
 */
public interface TenantPersistenceService extends PersistenceService {

    /**
     * Delete the tenant by given tenant id.
     * 
     * @param tenantId
     *            the tenant id
     * @throws SPersistenceException
     * @throws IOException
     * @throws SRetryableException
     * @since 6.0
     */
    void deleteTenant(long tenantId) throws SPersistenceException, IOException;

}
