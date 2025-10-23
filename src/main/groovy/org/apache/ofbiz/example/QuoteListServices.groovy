import org.apache.ofbiz.base.util.Debug
import org.apache.ofbiz.entity.Delegator
import org.apache.ofbiz.entity.GenericEntityException
import org.apache.ofbiz.entity.condition.EntityCondition
import org.apache.ofbiz.entity.condition.EntityOperator
import org.apache.ofbiz.service.DispatchContext
import org.apache.ofbiz.service.ServiceUtil

def ListQuoteValues() {
    Delegator delegator = dctx.getDelegator()

    try {
        // Total quotes
        def totalQuotes = delegator.findList("Quote", null, null, null, null, false)?.size() ?: 0

        // Created quotes
        def createdCondition = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "CREATED")
        def createdQuotes = delegator.findList("Quote", createdCondition, null, null, null, false)?.size() ?: 0

        // Approved quotes
        def approvedCondition = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "APPROVED")
        def approvedQuotes = delegator.findList("Quote", approvedCondition, null, null, null, false)?.size() ?: 0

        // Ordered quotes
        def orderedCondition = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "ORDERED")
        def orderedQuotes = delegator.findList("Quote", orderedCondition, null, null, null, false)?.size() ?: 0

        // Completed quotes
        def completedCondition = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "COMPLETED")
        def completedQuotes = delegator.findList("Quote", completedCondition, null, null, null, false)?.size() ?: 0

        // Combine all counts into a single list
        def countsList = [
            [label: "Total Quotes", value: totalQuotes],
            [label: "Created Quotes", value: createdQuotes],
            [label: "Approved Quotes", value: approvedQuotes],
            [label: "Ordered Quotes", value: orderedQuotes],
            [label: "Completed Quotes", value: completedQuotes]
        ]

        // Return as map for OFBiz service
        return [success: true, quoteCountList: countsList]

    } catch (GenericEntityException e) {
        Debug.logError(e, "Error fetching quote counts", "ListQuoteValues")
        return ServiceUtil.returnError("Error fetching quote counts: ${e.message}")
    }
}
