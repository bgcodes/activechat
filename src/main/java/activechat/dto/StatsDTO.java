package activechat.dto;

import activechat.dto.stats.ArchiveStats;
import activechat.dto.stats.MessageStats;

import java.util.List;

public class StatsDTO {

    private List<MessageStats> messageStats;

    private List<ArchiveStats> archiveStats;

    public List<MessageStats> getMessageStats() {
        return messageStats;
    }

    public void setMessageStats(List<MessageStats> messageStats) {
        this.messageStats = messageStats;
    }

    public List<ArchiveStats> getArchiveStats() {
        return archiveStats;
    }

    public void setArchiveStats(List<ArchiveStats> archiveStats) {
        this.archiveStats = archiveStats;
    }
}
