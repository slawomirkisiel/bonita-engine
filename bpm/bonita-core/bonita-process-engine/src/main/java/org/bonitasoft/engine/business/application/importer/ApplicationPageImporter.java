/*******************************************************************************
 * Copyright (C) 2014 BonitaSoft S.A.
 * BonitaSoft is a trademark of BonitaSoft SA.
 * This software file is BONITASOFT CONFIDENTIAL. Not For Distribution.
 * For commercial licensing information, contact:
 * BonitaSoft, 32 rue Gustave Eiffel – 38000 Grenoble
 * or BonitaSoft US, 51 Federal Street, Suite 305, San Francisco, CA 94107
 ******************************************************************************/

package org.bonitasoft.engine.business.application.importer;

import org.bonitasoft.engine.api.ImportError;
import org.bonitasoft.engine.business.application.ApplicationService;
import org.bonitasoft.engine.business.application.converter.ApplicationPageNodeConverter;
import org.bonitasoft.engine.business.application.model.SApplication;
import org.bonitasoft.engine.business.application.xml.ApplicationPageNode;
import org.bonitasoft.engine.commons.exceptions.SBonitaException;
import org.bonitasoft.engine.exception.ImportException;

/**
 * @author Elias Ricken de Medeiros
 */
public class ApplicationPageImporter {

    private final ApplicationService applicationService;
    private final ApplicationPageNodeConverter applicationPageNodeConverter;

    public ApplicationPageImporter(ApplicationService applicationService, ApplicationPageNodeConverter applicationPageNodeConverter) {
        this.applicationService = applicationService;
        this.applicationPageNodeConverter = applicationPageNodeConverter;
    }

    public ImportError importApplicationPage(ApplicationPageNode applicationPageNode, SApplication application) throws ImportException {
        try {
            ApplicationPageImportResult importResult = applicationPageNodeConverter.toSApplicationPage(applicationPageNode, application);
            if (importResult.getError() == null) {
                applicationService.createApplicationPage(importResult.getApplicationPage());
            }
            return importResult.getError();
        } catch (SBonitaException e) {
            throw new ImportException(e);
        }
    }
}