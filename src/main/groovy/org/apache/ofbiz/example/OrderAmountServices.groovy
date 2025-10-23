import org.apache.ofbiz.base.util.Debug
import org.apache.ofbiz.entity.Delegator
import org.apache.ofbiz.entity.GenericValue
import org.apache.ofbiz.entity.condition.EntityCondition
import org.apache.ofbiz.entity.condition.EntityOperator
import org.apache.ofbiz.service.ServiceUtil
import java.math.BigDecimal

def ListOrderAmounts() {
    Delegator delegator = dctx.getDelegator()

    try {
        def salesCondition = EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER")
        def salesOrders = delegator.findList("OrderHeader", salesCondition, null, null, null, false)

        BigDecimal totalSalesAmount = salesOrders?.sum { it.getBigDecimal("grandTotal") ?: BigDecimal.ZERO } ?: BigDecimal.ZERO

        def purchaseCondition = EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "PURCHASE_ORDER")
        def purchaseOrders = delegator.findList("OrderHeader", purchaseCondition, null, null, null, false)

        BigDecimal totalPurchaseAmount = purchaseOrders?.sum { it.getBigDecimal("grandTotal") ?: BigDecimal.ZERO } ?: BigDecimal.ZERO

        def amountsList = [
            [label: "Total Sales Order Amount (USD)", value: totalSalesAmount],
            [label: "Total Purchase Order Amount (USD)", value: totalPurchaseAmount]
        ]

        return [success: true, orderAmountList: amountsList]

    } catch (Exception e) {
        Debug.logError(e, "Error fetching order amounts", "ListOrderAmounts")
        return ServiceUtil.returnError("Error fetching order amounts: ${e.message}")
    }
}
