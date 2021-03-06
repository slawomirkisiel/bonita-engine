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
package org.bonitasoft.engine.bpm.contract.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.bonitasoft.engine.bpm.contract.ConstraintDefinition;
import org.bonitasoft.engine.bpm.contract.ConstraintType;
import org.junit.Test;

public class ConstraintDefinitionImplTest {

    @Test
    public void should_custom_type_be_default_constraint_type() throws Exception {
        //given
        final ConstraintDefinition constraintDefinition = new ConstraintDefinitionImpl("name", "expression", "explanation");

        //then
        assertThat(constraintDefinition.getConstraintType()).isEqualTo(ConstraintType.CUSTOM);
    }

    @Test
    public void should_retrieve_constraint_type() throws Exception {
        //given
        final ConstraintDefinition constraintDefinition = new ConstraintDefinitionImpl("name", "expression", "explanation", ConstraintType.CUSTOM);

        //then
        assertThat(constraintDefinition.getConstraintType()).isEqualTo(ConstraintType.CUSTOM);
        assertThat(constraintDefinition.getName()).isEqualTo("name");
        assertThat(constraintDefinition.getExpression()).isEqualTo("expression");
        assertThat(constraintDefinition.getExplanation()).isEqualTo("explanation");

        assertThat(constraintDefinition.getInputNames()).isNotNull().isEmpty();

    }


    @Test
    public void should_add_input_name() throws Exception {
        //given
        final ConstraintDefinitionImpl constraintDefinition = new ConstraintDefinitionImpl("name", "expression", "explanation", ConstraintType.CUSTOM);

        //when
        constraintDefinition.addInputName("inputName");

        //then
        assertThat(constraintDefinition.getInputNames()).isNotNull().hasSize(1).containsExactly("inputName");

    }
}
