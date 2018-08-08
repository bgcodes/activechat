package activechat.dto.stats;

import java.util.Date;

public class MessageStats {

    private String author;

    private Long count;

    private Long first;

    private Long last;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Long getFirst() {
        return first;
    }

    public void setFirst(Date first) {
        this.first = first.toInstant().toEpochMilli();
    }

    public Long getLast() {
        return last;
    }

    public void setLast(Date last) {
        this.last = last.toInstant().toEpochMilli();
    }
}
