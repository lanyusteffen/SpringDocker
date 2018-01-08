package stu.lanyu.springdocker.business;

import java.util.Calendar;
import java.util.Date;

public abstract class AbstractBusinessService {

    protected class TodaySearchDate {

        protected TodaySearchDate(Date beginDate, Date endDate) {
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

    protected TodaySearchDate getTodaySearchDate() {

        Calendar cal = Calendar.getInstance();

        Date beginDate = new Date(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        Date endDate = new Date();

        return new TodaySearchDate(beginDate, endDate);
    }
}
