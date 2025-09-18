package co.com.leronarenwino.model;

public record Report(
        String metric,
        Integer value
) {
    public static Report totalApprovedLoans(Integer total) {
        return new Report("TOTAL_APROBADOS", total);
    }
}
