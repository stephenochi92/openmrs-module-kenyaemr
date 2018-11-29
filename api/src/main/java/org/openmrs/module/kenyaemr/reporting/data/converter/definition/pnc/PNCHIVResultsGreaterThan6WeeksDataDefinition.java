package org.openmrs.module.kenyaemr.reporting.data.converter.definition.pnc;

import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/**
 * HIV Results >6 weeks Column
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
public class PNCHIVResultsGreaterThan6WeeksDataDefinition extends BaseDataDefinition implements EncounterDataDefinition {

    public static final long serialVersionUID = 1L;

    /**
     * Default Constructor
     */
    public PNCHIVResultsGreaterThan6WeeksDataDefinition() {
        super();
    }

    /**
     * Constructor to populate name only
     */
    public PNCHIVResultsGreaterThan6WeeksDataDefinition(String name) {
        super(name);
    }

    //***** INSTANCE METHODS *****

    /**
     * @see org.openmrs.module.reporting.data.DataDefinition#getDataType()
     */
    public Class<?> getDataType() {
        return Double.class;
    }
}