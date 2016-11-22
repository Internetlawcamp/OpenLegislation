package gov.nysenate.openleg.service.entity.committee;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.Committee;
import gov.nysenate.openleg.model.entity.CommitteeId;
import gov.nysenate.openleg.model.entity.CommitteeSessionId;
import gov.nysenate.openleg.service.entity.committee.data.CommitteeDataService;
import org.junit.Test;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;

public class CommitteeServiceTests extends BaseTests{
    private static Logger logger = LogManager.getLogger();

    @Autowired
    CommitteeDataService committeeDataService;

    @Test
    public void getCommitteeTest() throws Exception {
        CommitteeId committeeId = new CommitteeId(Chamber.SENATE, "Aging");
        Committee committee;
        committee = committeeDataService.getCommittee(committeeId);
        committee = committeeDataService.getCommittee(committeeId);
    }
}
