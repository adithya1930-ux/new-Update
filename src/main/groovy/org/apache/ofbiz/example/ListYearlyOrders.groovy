import org.apache.ofbiz.entity.DelegatorFactory
import org.apache.ofbiz.accounting.invoice.InvoiceWorker
import org.apache.ofbiz.service.ServiceUtil
import org.apache.ofbiz.base.util.Debug

def listInvoiceDetails(Map context) {
    def delegator = context?.delegator ?: DelegatorFactory.getDelegator("default")
    def invoiceList = []

    try {
        def invoices = delegator.findList("Invoice", null, null, null, null, false)

        invoices?.each { invoice ->
            def invoiceId = invoice.getString("invoiceId")
            def total = InvoiceWorker.getInvoiceTotal(delegator, invoiceId)
            def amountToApply = InvoiceWorker.getInvoiceNotApplied(delegator, invoiceId)

            def invoiceData = [
                invoiceId     : invoiceId,
                invoiceTypeId : invoice.getString("invoiceTypeId"),
                statusId      : invoice.getString("statusId"),
                total         : total ?: 0,
                amountToApply : amountToApply ?: 0
            ]

            invoiceList << invoiceData
            Debug.log("Invoice: ${invoiceData}", "listInvoiceDetails")
        }

        def result = ServiceUtil.returnSuccess()
        result.put("data", invoiceList) // Make sure 'data' key exists
        return result

    } catch (Exception e) {
        Debug.logError("Error fetching invoice data: ${e.message}", e, "listInvoiceDetails")
        return ServiceUtil.returnError("Error fetching invoice data: ${e.message}")
    }
}

return listInvoiceDetails(context ?: [:])
