import org.apache.ofbiz.base.util.Debug
import org.apache.ofbiz.entity.Delegator
import org.apache.ofbiz.service.ServiceUtil
import java.math.BigDecimal

def ListQuoteItemAmounts() {
    Delegator delegator = dctx.getDelegator()

    try {
        // Fetch all QuoteItems
        def quoteItems = delegator.findList("QuoteItem", null, null, null, null, false)

        // Calculate total amount (quoteUnitPrice * quantity)
        BigDecimal totalAmount = quoteItems?.sum { 
            def unitPrice = it.getBigDecimal("quoteUnitPrice") ?: BigDecimal.ZERO
            def quantity = it.getBigDecimal("quantity") ?: BigDecimal.ZERO
            unitPrice * quantity
        } ?: BigDecimal.ZERO

        return [success: true, totalQuoteAmount: totalAmount]

    } catch (Exception e) {
        Debug.logError(e, "Error fetching quote item amounts", "ListQuoteItemAmounts")
        return ServiceUtil.returnError("Error fetching quote item amounts: ${e.message}")
    }
}
