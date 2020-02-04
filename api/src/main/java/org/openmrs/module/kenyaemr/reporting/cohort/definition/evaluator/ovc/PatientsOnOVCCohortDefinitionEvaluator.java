/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.cohort.definition.evaluator.ovc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.ovc.PatientsOnOVCCohortDefinition;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * Evaluator for PatientsOnOVCCohortDefinition
 * Includes patients who are on OVC.
 */
@Handler(supports = {PatientsOnOVCCohortDefinition.class})
public class PatientsOnOVCCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

    private final Log log = LogFactory.getLog(this.getClass());

	@Autowired
	EvaluationService evaluationService;

    @Override
    public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {

		PatientsOnOVCCohortDefinition definition = (PatientsOnOVCCohortDefinition) cohortDefinition;

        if (definition == null)
            return null;

		Cohort newCohort = new Cohort();

		String qry="select t.patient_id\n" +
				"    from (\n" +
				"         select e.patient_id, d.patient_id as disc_patient ,max(d.visit_date) as date_discontinued, e.visit_date as enrollment_date\n" +
				"         from kenyaemr_etl.etl_ovc_enrolment e\n" +
				"             join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id and p.voided=0\n" +
				"             left outer JOIN\n" +
				"              (select patient_id,visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
				"                where program_name='OVC'\n" +
				"                group by patient_id\n" +
				"              ) d on d.patient_id = e.patient_id\n" +
				"           group by patient_id\n" +
				"          having (disc_patient is null or date(enrollment_date) >= date(date_discontinued) )\n" +
				"\n" +
				"         )t;";

		SqlQueryBuilder builder = new SqlQueryBuilder();
		builder.append(qry);
		Date startDate = (Date)context.getParameterValue("startDate");
		Date endDate = (Date)context.getParameterValue("endDate");
		builder.addParameter("startDate", startDate);
		builder.addParameter("endDate", endDate);

		List<Integer> ptIds = evaluationService.evaluateToList(builder, Integer.class, context);
		newCohort.setMemberIds(new HashSet<Integer>(ptIds));
		return new EvaluatedCohort(newCohort, definition, context);
    }
}
