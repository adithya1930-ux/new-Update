import org.apache.ofbiz.base.util.Debug
import org.apache.ofbiz.entity.Delegator
import org.apache.ofbiz.entity.GenericEntityException
import org.apache.ofbiz.entity.condition.EntityCondition
import org.apache.ofbiz.entity.condition.EntityOperator
import org.apache.ofbiz.service.DispatchContext
import org.apache.ofbiz.service.ServiceUtil

def ListProductionRunValues() {
    Delegator delegator = dctx.getDelegator()

    try {
        // Created production runs
        def createdCondition = EntityCondition.makeCondition([
            EntityCondition.makeCondition("workEffortTypeId", EntityOperator.EQUALS, "PROD_RUN"),
            EntityCondition.makeCondition("currentStatusId", EntityOperator.EQUALS, "PRUN_CREATED")
        ], EntityOperator.AND)
        def createdProductions = delegator.findList("WorkEffort", createdCondition, null, null, null, false)?.size() ?: 0

        // Closed production runs
        def closedCondition = EntityCondition.makeCondition([
            EntityCondition.makeCondition("workEffortTypeId", EntityOperator.EQUALS, "PROD_RUN"),
            EntityCondition.makeCondition("currentStatusId", EntityOperator.EQUALS, "PRUN_CLOSED")
        ], EntityOperator.AND)
        def closedProductions = delegator.findList("WorkEffort", closedCondition, null, null, null, false)?.size() ?: 0

        // Combine both counts into a list
        def countsList = [
            [label: "Created Productions", value: createdProductions],
            [label: "Closed Productions", value: closedProductions]
        ]

        // Return as OFBiz service output
        return [success: true, productionRunList: countsList]
    } catch (GenericEntityException e) {
        Debug.logError(e, "Error fetching production run counts", "ListProductionRunValues")
        return ServiceUtil.returnError("Error fetching production run counts: ${e.message}")
    }
}
