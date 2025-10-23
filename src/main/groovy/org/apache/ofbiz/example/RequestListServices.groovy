import org.apache.ofbiz.base.util.Debug
import org.apache.ofbiz.entity.Delegator
import org.apache.ofbiz.entity.GenericEntityException
import org.apache.ofbiz.entity.condition.EntityCondition
import org.apache.ofbiz.entity.condition.EntityOperator
import org.apache.ofbiz.service.DispatchContext
import org.apache.ofbiz.service.ServiceUtil

def ListRequestValues() {
    Delegator delegator = dctx.getDelegator()
    def logModule = "ListRequestValues"

    try {
        // Total requests
        def totalRequests = delegator.findList("CustRequest", null, null, null, null, false)?.size() ?: 0

        // Created requests
        def createdCondition = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "CREATED")
        def createdRequests = delegator.findList("CustRequest", createdCondition, null, null, null, false)?.size() ?: 0

        // Approved requests
        def approvedCondition = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "APPROVED")
        def approvedRequests = delegator.findList("CustRequest", approvedCondition, null, null, null, false)?.size() ?: 0

        // Completed requests
        def completedCondition = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "COMPLETED")
        def completedRequests = delegator.findList("CustRequest", completedCondition, null, null, null, false)?.size() ?: 0

        // Combine all counts into a single list
        def countsList = [
            [label: "Total Requests", value: totalRequests],
            [label: "Created Requests", value: createdRequests],
            [label: "Approved Requests", value: approvedRequests],
            [label: "Completed Requests", value: completedRequests]
        ]

        return [success: true, requestCountList: countsList]

    } catch (GenericEntityException e) {
        Debug.logError(e, "Error fetching request counts", logModule)
        return ServiceUtil.returnError("Error fetching request counts: ${e.message}")
    }
}
