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

/**
 * Service: ListMonthlyProductionStats
 * Purpose: Returns monthly stats for Created and Closed Production Runs
 */
def ListMonthlyProductionStats() {
    Delegator delegator = dctx.getDelegator()

    try {
        // Fetch all Production Runs (WorkEffort entity)
        def condition = EntityCondition.makeCondition("workEffortTypeId", EntityOperator.EQUALS, "PROD_RUN")
        def allRuns = delegator.findList("WorkEffort", condition, null, null, null, false)

        // Initialize counters
        def createdMonthly = [:].withDefault { 0 }
        def closedMonthly = [:].withDefault { 0 }

        // Iterate and group by month
        allRuns.each { run ->
            // Count created runs (based on estimatedStartDate)
            if (run.estimatedStartDate && run.currentStatusId == "PRUN_CREATED") {
                def monthKey = getMonthYear(run.estimatedStartDate)
                createdMonthly[monthKey]++
            }

            // Count closed runs (based on actualCompletionDate)
            if (run.actualCompletionDate && run.currentStatusId == "PRUN_CLOSED") {
                def monthKey = getMonthYear(run.actualCompletionDate)
                closedMonthly[monthKey]++
            }
        }

        // Combine unique months
        def allMonths = (createdMonthly.keySet() + closedMonthly.keySet()).sort()

        // Prepare output list
        def monthlyStats = allMonths.collect { month ->
            [
                month   : month,
                created : createdMonthly[month] ?: 0,
                closed  : closedMonthly[month] ?: 0
            ]
        }

        return [success: true, monthlyProductionStats: monthlyStats]
    } catch (GenericEntityException e) {
        Debug.logError(e, "Error fetching monthly production stats", "ListMonthlyProductionStats")
        return ServiceUtil.returnError("Error fetching monthly production stats: ${e.message}")
    }
}

/**
 * Helper: Converts java.util.Date to "MMM yyyy" (e.g. "Jan 2025")
 */
private String getMonthYear(Date date) {
    def localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
    def monthName = localDate.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
    return "${monthName} ${localDate.year}"
}
