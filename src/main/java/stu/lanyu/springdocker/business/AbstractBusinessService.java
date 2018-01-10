package stu.lanyu.springdocker.business;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public abstract class AbstractBusinessService {

    protected class SearchDateStamp {

        public SearchDateStamp(Date beginDate, Date endDate) {
            this.beginDate = beginDate;
            this.endDate = endDate;
        }

        private Date beginDate;
        private Date endDate;

        public Date getBeginDate() {
            return beginDate;
        }

        public void setBeginDate(Date beginDate) {
            this.beginDate = beginDate;
        }

        public Date getEndDate() {
            return endDate;
        }

        public void setEndDate(Date endDate) {
            this.endDate = endDate;
        }
    }

    protected SearchDateStamp getTodaySearchDate(boolean useUTC) {

        LocalDate lt = LocalDate.now();
        Instant instant = lt.atStartOfDay(useUTC ? ZoneId.of("UTC") : ZoneId.systemDefault()).toInstant();
        Date beginDate = Date.from(instant);

        Date endDate = Date.from(Instant.now().atZone(useUTC ? ZoneId.of("UTC") : ZoneId.systemDefault()).toInstant());

        return new SearchDateStamp(beginDate, endDate);
    }

    protected SearchDateStamp getBeforeTodaySearchDate(boolean useUTC) {

        LocalDate localEndDate = LocalDate.now();
        Instant instantForEnd = localEndDate.atStartOfDay(useUTC ? ZoneId.of("UTC") : ZoneId.systemDefault()).toInstant();
        Date endDate = Date.from(instantForEnd);

        LocalDate localBeginDate = localEndDate.minusYears(1);
        Instant instantForBegin = localBeginDate.atStartOfDay(useUTC ? ZoneId.of("UTC") : ZoneId.systemDefault()).toInstant();
        Date beginDate = Date.from(instantForBegin);

        return new SearchDateStamp(beginDate, endDate);
    }
}
