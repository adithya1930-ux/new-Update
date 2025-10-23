import org.apache.ofbiz.base.util.Debug
import org.apache.ofbiz.entity.Delegator
import org.apache.ofbiz.entity.GenericEntityException
import org.apache.ofbiz.entity.condition.EntityCondition
import org.apache.ofbiz.entity.condition.EntityOperator
import org.apache.ofbiz.service.DispatchContext
import org.apache.ofbiz.service.ServiceUtil

def ListWorkOrderValues() {
    Delegator delegator = dctx.getDelegator()
    def logModule = "ListWorkOrderValues"

    try {
        // Base condition for Work Orders (Production Runs)
        def baseCondition = EntityCondition.makeCondition("workEffortTypeId", EntityOperator.EQUALS, "PROD_RUN")

        // Total Work Orders
        def totalWorkOrders = delegator.findList("WorkEffort", baseCondition, null, null, null, false)?.size() ?: 0

        // Created Work Orders
        def createdCondition = EntityCondition.makeCondition([
            baseCondition,
            EntityCondition.makeCondition("currentStatusId", EntityOperator.EQUALS, "PRUN_CREATED")
        ], EntityOperator.AND)
        def createdWorkOrders = delegator.findList("WorkEffort", createdCondition, null, null, null, false)?.size() ?: 0

        // Approved (Scheduled) Work Orders
        def approvedCondition = EntityCondition.makeCondition([
            baseCondition,
            EntityCondition.makeCondition("currentStatusId", EntityOperator.EQUALS, "PRUN_SCHEDULED")
        ], EntityOperator.AND)
        def approvedWorkOrders = delegator.findList("WorkEffort", approvedCondition, null, null, null, false)?.size() ?: 0

        // Completed Work Orders
        def completedCondition = EntityCondition.makeCondition([
            baseCondition,
            EntityCondition.makeCondition("currentStatusId", EntityOperator.EQUALS, "PRUN_COMPLETED")
        ], EntityOperator.AND)
        def completedWorkOrders = delegator.findList("WorkEffort", completedCondition, null, null, null, false)?.size() ?: 0

        // Combine results
        def countsList = [
            [label: "Total Work Orders", value: totalWorkOrders],
            [label: "Created Work Orders", value: createdWorkOrders],
            [label: "Approved Work Orders", value: approvedWorkOrders],
            [label: "Completed Work Orders", value: completedWorkOrders]
        ]

        return [success: true, workOrderCountList: countsList]

    } catch (GenericEntityException e) {
        Debug.logError(e, "Error fetching work order counts", logModule)
        return ServiceUtil.returnError("Error fetching work order counts: ${e.message}")
    }
}
