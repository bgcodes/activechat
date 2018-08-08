package activechat.service;

import activechat.dto.stats.ArchiveStats;
import activechat.dto.stats.MessageStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service("statsService")
public class StatsService {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public StatsService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private class MessageStatsRowMapper implements RowMapper<MessageStats> {
        @Override
        public MessageStats mapRow(ResultSet rs, int rowNum) throws SQLException {
            MessageStats messageStats = new MessageStats();
            messageStats.setAuthor(rs.getString("author"));
            messageStats.setCount(rs.getLong("count"));
            messageStats.setFirst(rs.getTimestamp("first"));
            messageStats.setLast(rs.getTimestamp("last"));
            return messageStats;
        }
    }

    private class ArchiveStatsRowMapper implements RowMapper<ArchiveStats> {
        @Override
        public ArchiveStats mapRow(ResultSet rs, int rowNum) throws SQLException {
            ArchiveStats archiveStats = new ArchiveStats();
            archiveStats.setWhite(rs.getString("white"));
            archiveStats.setResult(rs.getInt("result"));
            archiveStats.setCount(rs.getLong("count"));
            return archiveStats;
        }
    }

    public List<MessageStats> findMessageStats(Long idConversation) {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("idConversation", idConversation);
        final String sql = "SELECT author, count(*) as count, MIN(sendingDate) as first, MAX(sendingDate) as last FROM messages WHERE idConversation = :idConversation GROUP BY author";
        return namedParameterJdbcTemplate.query(sql, parameters, new MessageStatsRowMapper());
    }

    public List<ArchiveStats> findArchiveStats(Long idConversation) {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("idConversation", idConversation);
        final String sql = "SELECT white, result, count(*) as count FROM archive WHERE idConversation = :idConversation GROUP BY result, white;";
        return namedParameterJdbcTemplate.query(sql, parameters, new ArchiveStatsRowMapper());
    }
}
