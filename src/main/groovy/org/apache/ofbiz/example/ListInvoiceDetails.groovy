import org.apache.ofbiz.entity.Delegator
import org.apache.ofbiz.entity.DelegatorFactory
import org.apache.ofbiz.service.ServiceUtil
import org.apache.ofbiz.base.util.Debug
import org.apache.ofbiz.accounting.invoice.InvoiceWorker
import java.math.BigDecimal

def static listInvoiceDetails(Map context) {
    Delegator delegator = context?.delegator ?: DelegatorFactory.getDelegator("default")
    List<Map<String, Object>> invoiceList = []

    try {
        // Fetch all invoices
        def invoices = delegator.findList("Invoice", null, null, null, null, false)

        if (invoices) {
            invoices.each { invoice ->
                String invoiceId = invoice.getString("invoiceId")
                BigDecimal total = InvoiceWorker.getInvoiceTotal(delegator, invoiceId) ?: BigDecimal.ZERO
                BigDecimal amountToApply = InvoiceWorker.getInvoiceNotApplied(delegator, invoiceId) ?: BigDecimal.ZERO

                invoiceList << [
                    invoiceId     : invoiceId,
                    invoiceTypeId : invoice.getString("invoiceTypeId"),
                    statusId      : invoice.getString("statusId"),
                    total         : total,
                    amountToApply : amountToApply
                ]

                Debug.log("Invoice data: ${invoiceList[-1]}", "listInvoiceDetails")
            }
        } else {
            Debug.log("No invoices found.", "listInvoiceDetails")
        }

        // Return JSON-ready map
        Map result = ServiceUtil.returnSuccess()
        result.put("data", invoiceList)
        result.put("statusCode", 200)
        result.put("statusDescription", "OK")
        return result

    } catch (Exception e) {
        Debug.logError("Error fetching invoice data: ${e.message}", e, "listInvoiceDetails")
        return ServiceUtil.returnError("Error fetching invoice data: ${e.message}")
    }
}

return listInvoiceDetails(context ?: [:])
