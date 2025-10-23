import org.apache.ofbiz.base.util.Debug
import org.apache.ofbiz.entity.Delegator
import org.apache.ofbiz.entity.GenericEntityException
import org.apache.ofbiz.entity.condition.EntityCondition
import org.apache.ofbiz.entity.condition.EntityOperator
import org.apache.ofbiz.service.DispatchContext
import org.apache.ofbiz.service.ServiceUtil

def ListMonthlyQuotes() {
    Delegator delegator = dctx.getDelegator()
    def logModule = "ListMonthlyQuotes"

    try {
        // Get current year
        def currentYear = java.time.Year.now().getValue()

        // Fetch all Quote records
        def orders = delegator.findList("Quote", null, null, null, null, false)

        // Prepare monthly counts (index 0 = Jan, 11 = Dec)
        def monthlyCounts = (0..<12).collect { 0 }

        orders.each { quote ->
            // Use 'createdDate' field for quote date
            def quoteDate = quote.getTimestamp("createdStamp")
            if (quoteDate) {
                def quoteYear = quoteDate.toLocalDateTime().getYear()
                if (quoteYear == currentYear) {
                    def monthIndex = quoteDate.toLocalDateTime().getMonthValue() - 1
                    monthlyCounts[monthIndex] = monthlyCounts[monthIndex] + 1
                }
            }
        }

        // Convert to list of maps for frontend display
        def monthNames = [
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        ]

        def monthlyQuoteList = (0..<12).collect { i ->
            [label: monthNames[i], value: monthlyCounts[i]]
        }

        Debug.logInfo("Monthly quote data: ${monthlyQuoteList}", logModule)

        return [success: true, monthlyQuoteList: monthlyQuoteList]

    } catch (GenericEntityException e) {
        Debug.logError(e, "Error fetching monthly quotes", logModule)
        return ServiceUtil.returnError("Error fetching monthly quotes: ${e.message}")
    }
}
