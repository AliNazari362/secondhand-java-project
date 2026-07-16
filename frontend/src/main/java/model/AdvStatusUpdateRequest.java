package model;

import model.enums.AdvStatus;

public class AdvStatusUpdateRequest {
    private AdvStatus status;
    private String rejectionExplanation;

    public AdvStatusUpdateRequest() {}

    public AdvStatus getStatus() { return status; }
    public void setStatus(AdvStatus status) { this.status = status; }

    public String getRejectionExplanation() { return rejectionExplanation; }
    public void setRejectionExplanation(String rejectionExplanation) { this.rejectionExplanation = rejectionExplanation; }
}