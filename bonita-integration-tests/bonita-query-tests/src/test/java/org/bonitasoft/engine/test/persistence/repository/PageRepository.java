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
package org.bonitasoft.engine.test.persistence.repository;

import org.bonitasoft.engine.page.SPage;
import org.bonitasoft.engine.page.SPageContent;
import org.hibernate.Query;
import org.hibernate.SessionFactory;

public class PageRepository extends TestRepository {

    public PageRepository(final SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public SPageContent getPageContent(final long id) {
        final Query namedQuery = getNamedQuery("getPageContent");
        namedQuery.setParameter("id", id);
        return (SPageContent) namedQuery.uniqueResult();
    }

    public SPage getPageByName(final String name) {
        final Query namedQuery = getNamedQuery("getPageByName");
        namedQuery.setParameter("pageName", name);
        return (SPage) namedQuery.uniqueResult();
    }
}
