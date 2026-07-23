package model;

public class DashboardStats {
    private long totalUsers;
    private long activeUsers;
    private long bannedUsers;
    private long deletedUsers;
    private long totalAds;
    private long pendingAds;
    private long activeAds;
    private long soldAds;
    private long rejectedAds;
    private long totalMessages;
    private long totalComments;

    public DashboardStats() {}

    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }

    public long getActiveUsers() { return activeUsers; }
    public void setActiveUsers(long activeUsers) { this.activeUsers = activeUsers; }

    public long getBannedUsers() { return bannedUsers; }
    public void setBannedUsers(long bannedUsers) { this.bannedUsers = bannedUsers; }

    public long getDeletedUsers() { return deletedUsers; }
    public void setDeletedUsers(long deletedUsers) { this.deletedUsers = deletedUsers; }

    public long getTotalAds() { return totalAds; }
    public void setTotalAds(long totalAds) { this.totalAds = totalAds; }

    public long getPendingAds() { return pendingAds; }
    public void setPendingAds(long pendingAds) { this.pendingAds = pendingAds; }

    public long getActiveAds() { return activeAds; }
    public void setActiveAds(long activeAds) { this.activeAds = activeAds; }

    public long getSoldAds() { return soldAds; }
    public void setSoldAds(long soldAds) { this.soldAds = soldAds; }

    public long getRejectedAds() { return rejectedAds; }
    public void setRejectedAds(long rejectedAds) { this.rejectedAds = rejectedAds; }

    public long getTotalMessages() { return totalMessages; }
    public void setTotalMessages(long totalMessages) { this.totalMessages = totalMessages; }

    public long getTotalComments() { return totalComments; }
    public void setTotalComments(long totalComments) { this.totalComments = totalComments; }
}