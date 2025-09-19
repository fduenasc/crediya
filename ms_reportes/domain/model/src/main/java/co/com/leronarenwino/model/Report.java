package co.com.leronarenwino.model;

public record Report(
        String metric,
        Integer value
) {
    public static Report defaultTotalApprovedLoans() {
        return new Report("TOTAL_APROBADOS", 0);
    }
}
