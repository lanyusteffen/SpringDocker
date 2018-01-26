package stu.lanyu.springdocker.business;

import java.time.*;

public abstract class AbstractBusinessService {

    protected class SearchDateStamp {

        public SearchDateStamp(ZonedDateTime beginDate, ZonedDateTime endDate) {
            this.beginDate = beginDate;
            this.endDate = endDate;
        }

        private ZonedDateTime beginDate;
        private ZonedDateTime endDate;

        public ZonedDateTime getBeginDate() {
            return beginDate;
        }

        public void setBeginDate(ZonedDateTime beginDate) {
            this.beginDate = beginDate;
        }

        public ZonedDateTime getEndDate() {
            return endDate;
        }

        public void setEndDate(ZonedDateTime endDate) {
            this.endDate = endDate;
        }
    }

    protected SearchDateStamp getTodaySearchDate(boolean useUTC) {

        ZoneId zoneId = (useUTC ? ZoneId.of("UTC") : ZoneId.systemDefault());

        LocalDateTime dt = LocalDate.now().atStartOfDay();
        Instant instant = Instant.now();

        ZonedDateTime endDate = ZonedDateTime.ofInstant(instant , zoneId);

        ZonedDateTime zdt = dt.atZone(ZoneId.systemDefault());
        instant = LocalDate.now().atStartOfDay().toInstant(zdt.getOffset());
        ZonedDateTime beginDate = ZonedDateTime.ofInstant(instant , zoneId);

        return new SearchDateStamp(beginDate, endDate);
    }
}
