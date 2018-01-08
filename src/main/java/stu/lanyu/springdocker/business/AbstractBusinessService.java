package stu.lanyu.springdocker.business;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public abstract class AbstractBusinessService {

    protected class SearchDateStamp {

        protected SearchDateStamp(Date beginDate, Date endDate) {
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

    protected SearchDateStamp getTodaySearchDate() {

        LocalDate lt = LocalDate.now();
        Instant instant = lt.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Date beginDate = Date.from(instant);

        Date endDate = Date.from(Instant.now());

        return new SearchDateStamp(beginDate, endDate);
    }
}
