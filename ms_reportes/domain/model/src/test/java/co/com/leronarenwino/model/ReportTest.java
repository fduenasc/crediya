package co.com.leronarenwino.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ReportTest {

    @Test
    void shouldCreateReportWithAllFields() {
        String metric = "TOTAL_APROBADOS";
        Integer value = 5;
        Report report = new Report(metric, value);

        assertEquals(metric, report.metric());
        assertEquals(value, report.value());
    }

    @Test
    void shouldAllowNullValues() {
        Report report = new Report(null, null);

        assertNull(report.metric());
        assertNull(report.value());
    }

    @Test
    void shouldReturnDefaultTotalApprovedLoans() {
        Report defaultReport = Report.defaultTotalApprovedLoans();

        assertEquals("TOTAL_APROBADOS", defaultReport.metric());
        assertEquals(0, defaultReport.value());
    }
}
