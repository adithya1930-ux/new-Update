import org.apache.ofbiz.base.util.Debug
import org.apache.ofbiz.entity.Delegator
import org.apache.ofbiz.entity.GenericEntityException
import org.apache.ofbiz.entity.condition.EntityCondition
import org.apache.ofbiz.entity.condition.EntityOperator
import org.apache.ofbiz.service.DispatchContext
import org.apache.ofbiz.service.ServiceUtil

def ListOrderValues() {
    Delegator delegator = dctx.getDelegator()

    try {
        // Total orders
        def totalOrders = delegator.findList("OrderHeader", null, null, null, null, false)?.size() ?: 0

        // Sales orders count
        def salesCondition = EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER")
        def salesOrders = delegator.findList("OrderHeader", salesCondition, null, null, null, false)?.size() ?: 0

        // Purchase orders count
        def purchaseCondition = EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "PURCHASE_ORDER")
        def purchaseOrders = delegator.findList("OrderHeader", purchaseCondition, null, null, null, false)?.size() ?: 0

        // Combine all counts into a single list
        def countsList = [
            [label: "Total Orders", value: totalOrders],
            [label: "Sales Orders", value: salesOrders],
            [label: "Purchase Orders", value: purchaseOrders]
        ]

        // Return as map for OFBiz service
        return [success: true, orderCountList: countsList]
    } catch (GenericEntityException e) {
        Debug.logError(e, "Error fetching order counts", "ListOrderValues")
        return ServiceUtil.returnError("Error fetching order counts: ${e.message}")
    }
}
