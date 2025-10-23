import org.apache.ofbiz.base.util.Debug
import org.apache.ofbiz.entity.Delegator
import org.apache.ofbiz.entity.GenericEntityException
import org.apache.ofbiz.entity.condition.EntityCondition
import org.apache.ofbiz.entity.condition.EntityOperator
import org.apache.ofbiz.service.DispatchContext
import org.apache.ofbiz.service.ServiceUtil

import java.time.LocalDate
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

def ListMonthlyWorkOrderStats() {
    Delegator delegator = dctx.getDelegator()
    def logModule = "ListMonthlyWorkOrderStats"

    try {
        def currentYear = LocalDate.now().year
        def startOfYear = LocalDate.of(currentYear, 1, 1)
        def startTimestamp = java.sql.Timestamp.valueOf(startOfYear.atStartOfDay())

        // Base condition: Work Orders (Production Runs)
        def baseCondition = EntityCondition.makeCondition("workEffortTypeId", EntityOperator.EQUALS, "PROD_RUN")

        // Fetch all work orders created this year
        def allWorkOrders = delegator.findList("WorkEffort", baseCondition, null, null, null, false)

        // Initialize month map
        def monthlyData = (1..12).collectEntries { month ->
            def monthName = LocalDate.of(currentYear, month, 1).month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
            [(monthName): [created: 0, completed: 0]]
        }

        allWorkOrders.each { wo ->
            def createdDate = wo.getTimestamp("createdDate")
            def completedDate = wo.getTimestamp("actualCompletionDate")
            def statusId = wo.getString("currentStatusId")

            // Count created
            if (createdDate) {
                def localDate = createdDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                if (localDate.year == currentYear) {
                    def monthName = localDate.month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                    monthlyData[monthName].created++
                }
            }

            // Count completed (statusId check for safety)
            if (completedDate && statusId == "PRUN_COMPLETED") {
                def localDate = completedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                if (localDate.year == currentYear) {
                    def monthName = localDate.month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                    monthlyData[monthName].completed++
                }
            }
        }

        // Convert to list for JSON response
        def statsList = monthlyData.collect { month, values ->
            [month: month, created: values.created, completed: values.completed]
        }

        return [success: true, monthlyWorkOrderStats: statsList]

    } catch (GenericEntityException e) {
        Debug.logError(e, "Error fetching monthly work order stats", logModule)
        return ServiceUtil.returnError("Error fetching monthly work order stats: ${e.message}")
    }
}
