/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.fragment.controller.covid;

import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.module.kenyacore.form.FormManager;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.util.EmrUtils;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.ui.framework.page.PageRequest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Patient summary fragment
 */
public class QuarantineFollowupFormFragmentController {
	
	static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
	
	public void controller(@FragmentParam("patient") Patient patient, @SpringBean FormManager formManager,
                           @SpringBean KenyaUiUtils kenyaUi, PageRequest pageRequest, UiUtils ui, FragmentModel model) {
		
		EncounterType encTravelHistory = MetadataUtils.existing(EncounterType.class, CommonMetadata._EncounterType.COVID_QUARANTINE_FOLLOWUP);
		Form formtravelHistory = MetadataUtils.existing(Form.class, CommonMetadata._Form.COVID_QUARANTINE_FOLLOWUP);
		
		List<Encounter> encs = EmrUtils.AllEncounters(patient, encTravelHistory, formtravelHistory);
		List<SimpleObject> simplifiedEncData = new ArrayList<SimpleObject>();
		for (Encounter e : encs) {
			SimpleObject o = buildEncounterData(e.getObs(), e);
			simplifiedEncData.add(o);
		}
		model.addAttribute("patient", patient);
		model.addAttribute("encounters", simplifiedEncData);
	}
	
	public static SimpleObject buildEncounterData(Set<Obs> obsList, Encounter e) {


		int feverConcept = 140238;
		int coughConcept = 143264;
		int shortnessOfBreathConcept = 164441;
		int soreThroatConcept = 158843;
		int referralConcept = 1788;
		int symptomsConcept = 162737;
		int yesConcept = 1065;
		int noConcept = 1066;

		String soreThroat = "";
		String cough = "";
		String fever = "";
		String difficultyBreathing = "";
		String referredForTreatment = "";

		String encDate = e != null ? DATE_FORMAT.format(e.getEncounterDatetime()) : "";
		
		for (Obs obs : obsList) {
			
			if (obs.getConcept().getConceptId().equals(feverConcept) && obs.getValueCoded().getConceptId().equals(yesConcept)) {
				fever = "Yes";
			} else if (obs.getConcept().getConceptId().equals(feverConcept) && obs.getValueCoded().getConceptId().equals(noConcept)) {
				fever = "No";
			} else if (obs.getConcept().getConceptId().equals(coughConcept) && obs.getValueCoded().getConceptId().equals(yesConcept)) {
				cough = "Yes";
			} else if (obs.getConcept().getConceptId().equals(coughConcept) && obs.getValueCoded().getConceptId().equals(noConcept)) {
				cough = "No";
			} else if (obs.getConcept().getConceptId().equals(shortnessOfBreathConcept) && obs.getValueCoded().getConceptId().equals(yesConcept)) {
				difficultyBreathing = "Yes";
			} else if (obs.getConcept().getConceptId().equals(shortnessOfBreathConcept) && obs.getValueCoded().getConceptId().equals(noConcept)) {
				difficultyBreathing = "No";
			} else if (obs.getConcept().getConceptId().equals(symptomsConcept) && obs.getValueCoded().getConceptId().equals(soreThroatConcept)) {
				soreThroat = "Yes";
			} else if (obs.getConcept().getConceptId().equals(symptomsConcept) && obs.getValueCoded().getConceptId().equals(noConcept)) {
				soreThroat = "No";
			} else if (obs.getConcept().getConceptId().equals(referralConcept)) {
				if (obs.getValueCoded().getConceptId().equals(140238)) {
					referredForTreatment = "Yes";
				} else if (obs.getValueCoded().getConceptId().equals(1066)) {
					referredForTreatment = "No";
				} else if (obs.getValueCoded().getConceptId().equals(1175)) {
					referredForTreatment = "N/A";
				}
			}
		}
		
		return SimpleObject.create("encDate", encDate, "encId", e.getEncounterId(), "soreThroat", soreThroat != null ? soreThroat : "",
		    "referredToHospital", referredForTreatment != null ? referredForTreatment : "", "fever",
		    fever != null ? fever : "", "cough", cough != null ? cough
		            : "", "difficultyBreathing", difficultyBreathing != null ? difficultyBreathing : ""
		
		);
	}
	
}