package gov.nysenate.openleg.controller.pdf;

import gov.nysenate.openleg.client.view.bill.BillPdfView;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.service.bill.data.BillAmendNotFoundEx;
import gov.nysenate.openleg.service.bill.data.BillDataService;
import gov.nysenate.openleg.service.bill.data.BillNotFoundEx;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@RestController
@RequestMapping(value = "/pdf/bills")
public class BillPdfCtrl
{
    private static final Logger logger = LogManager.getLogger();

    @Autowired protected BillDataService billData;

    @RequestMapping(value = "/{sessionYear:[\\d]{4}}/{printNo}")
    public ResponseEntity<byte[]> getBillPdf(@PathVariable int sessionYear, @PathVariable String printNo,
                                             HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            BillId billId = new BillId(printNo, sessionYear);
            Optional<String> alternateUrl = billData.getAlternateBillPdfUrl(billId);
            if (alternateUrl.isPresent()) {
                String urlString = alternateUrl.get();
                URI url = new URI(urlString);
                if (!url.isAbsolute()) {
                    urlString = request.getContextPath() + urlString;
                }
                response.sendRedirect(urlString);
            } else {
                Bill bill = billData.getBill(BaseBillId.of(billId));
                ByteArrayOutputStream pdfBytes = new ByteArrayOutputStream();
                BillPdfView.writeBillPdf(bill, billId.getVersion(), pdfBytes);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.parseMediaType("application/pdf"));
                return new ResponseEntity<>(pdfBytes.toByteArray(), headers, HttpStatus.OK);
            }
        } catch (BillNotFoundEx | BillAmendNotFoundEx ex) {
            response.sendError(404, ex.getMessage());
        } catch (IOException | URISyntaxException | COSVisitorException ex) {
            logger.error("Exception in bill pdf viewer.", ex);
            response.sendError(404, "PDF text for " + printNo + " " + sessionYear + " is not available.");
        }
        return null;
    }
}
