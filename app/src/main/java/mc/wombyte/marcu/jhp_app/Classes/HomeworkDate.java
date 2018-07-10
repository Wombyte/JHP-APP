package mc.wombyte.marcu.jhp_app.Classes;

import java.util.Date;

/**
 * Created by marcu on 21.07.2017.
 */

public class HomeworkDate extends Date {

    Date date;
    int lesson;

    public HomeworkDate(Date date, int lesson) {
        this.date = date;
        this.lesson = lesson;
    }

    /*
     * returns true if this date is after the translated date
     */
    public boolean after(HomeworkDate when) {
        if(this.date.after(when.date)) {
            return true;
        }
        if(this.date.equals(when.date)) {
            if(this.lesson > when.lesson) {
                return true;
            }
        }
        return false;
    }

    public void setDate(Date date) { this.date = date; }
    public Date date() { return date;}

    public void setLesson(int lesson) { this.lesson = lesson; }
    public int getLesson() { return lesson; }

}
