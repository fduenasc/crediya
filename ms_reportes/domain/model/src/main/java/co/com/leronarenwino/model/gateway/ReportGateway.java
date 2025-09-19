package co.com.leronarenwino.model.gateway;

import co.com.leronarenwino.model.Report;
import reactor.core.publisher.Mono;

public interface ReportGateway {
    Mono<Report> getTotalApprovedLoans();
    Mono<Void> incrementTotalApprovedLoans();
}