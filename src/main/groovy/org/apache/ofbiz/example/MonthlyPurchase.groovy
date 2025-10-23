import org.apache.ofbiz.base.util.Debug
import org.apache.ofbiz.entity.Delegator
import org.apache.ofbiz.entity.GenericEntityException
import org.apache.ofbiz.entity.condition.EntityCondition
import org.apache.ofbiz.entity.condition.EntityOperator
import org.apache.ofbiz.service.DispatchContext
import org.apache.ofbiz.service.ServiceUtil

def ListMonthlyPurchaseOrders() {
    Delegator delegator = dctx.getDelegator()
    def logModule = "ListMonthlyPurchaseOrders"

    try {
        // Get current year
        def currentYear = java.time.Year.now().getValue()

        // Fetch all PURCHASE_ORDER orders
        def orderCondition = EntityCondition.makeCondition([
            EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "PURCHASE_ORDER")
        ], EntityOperator.AND)

        def orders = delegator.findList("OrderHeader", orderCondition, null, null, null, false)

        // Prepare monthly counts (index 0 = Jan, 11 = Dec)
        def monthlyCounts = (0..<12).collect { 0 }

        orders.each { order ->
            def orderDate = order.getTimestamp("orderDate")
            if (orderDate) {
                def orderYear = orderDate.toLocalDateTime().getYear()
                if (orderYear == currentYear) {
                    def monthIndex = orderDate.toLocalDateTime().getMonthValue() - 1
                    monthlyCounts[monthIndex] = monthlyCounts[monthIndex] + 1
                }
            }
        }

        // Convert to list of maps (for frontend display)
        def monthNames = [
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        ]

        def monthlyPurchaseList = (0..<12).collect { i ->
            [label: monthNames[i], value: monthlyCounts[i]]
        }

        Debug.logInfo("Monthly purchase data: ${monthlyPurchaseList}", logModule)

        return [success: true, monthlyPurchaseList: monthlyPurchaseList]

    } catch (GenericEntityException e) {
        Debug.logError(e, "Error fetching monthly purchase orders", logModule)
        return ServiceUtil.returnError("Error fetching monthly purchase orders: ${e.message}")
    }
}
