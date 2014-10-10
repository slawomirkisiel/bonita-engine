/**
 * Copyright (C) 2012 BonitaSoft S.A.
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
package org.bonitasoft.engine.core.process.instance.api.exceptions.event.trigger;


/**
 * @author Elias Ricken de Medeiros
 *
 */
public class SWaitingEventCreationException extends SEventTriggerInstanceException {

    private static final long serialVersionUID = 8177794618577658085L;

    public SWaitingEventCreationException(String message, Throwable cause) {
        super(message, cause);
    }

    public SWaitingEventCreationException(String message) {
        super(message);
    }

    public SWaitingEventCreationException(Throwable cause) {
        super(cause);
    }

}
