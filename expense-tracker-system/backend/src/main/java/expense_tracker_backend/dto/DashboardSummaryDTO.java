package expense_tracker_backend.dto;

public class DashboardSummaryDTO {

    private double totalSpent;
    private double averageSpent;
    private String topCategory;
    private double topCategoryAmount;
    private long transactionCount;

    public DashboardSummaryDTO(
            double totalSpent,
            double averageSpent,
            String topCategory,
            double topCategoryAmount,
            long transactionCount
    ) {
        this.totalSpent = totalSpent;
        this.averageSpent = averageSpent;
        this.topCategory = topCategory;
        this.topCategoryAmount = topCategoryAmount;
        this.transactionCount = transactionCount;
    }

    public double getTotalSpent() {
        return totalSpent;
    }

    public double getAverageSpent() {
        return averageSpent;
    }

    public String getTopCategory() {
        return topCategory;
    }

    public double getTopCategoryAmount() {
        return topCategoryAmount;
    }

    public long getTransactionCount() {
        return transactionCount;
    }
}