package mc.wombyte.marcu.jhp_app;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import mc.wombyte.marcu.jhp_app.classes.Homework;
import mc.wombyte.marcu.jhp_app.classes.Lesson;

/**
 * Created by marcu on 18.02.2018.
 */

public class NotificationJHP{

    private final static int NOTIFICATION_ID = 7515320;
    private final static String CHANNEL_ID = "jhp_not";
    private final static int IMPORTANCE = NotificationManager.IMPORTANCE_LOW;
    private CharSequence name;

    private Context context;
    private NotificationChannel channel;
    private NotificationManager manager;
    private Notification.Builder builder;
    private Notification.InboxStyle inbox_style;
    private Intent toHomeworkView;
    private Intent toNewHomework;
    private Intent toNewGrade;
    private PendingIntent pIntent_content;
    private PendingIntent pIntent_action1;
    private PendingIntent pIntent_action2;

    private int last_subject_id = -1;

    private String[] title_strings;
    private String[] content_strings;
    private int homework_amount = 0;
    private int subjects_with_homework = 0;
    private String title_text = "";
    private String content_text = "";
    private ArrayList<String> expanded_texts = new ArrayList<>();
    private String text_action1 = "";
    private String text_action2 = "";


    public NotificationJHP(Context context) {
        this.context = context;
        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        name = context.getString(R.string.notification_channel_name);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(CHANNEL_ID, name, IMPORTANCE);
        }
        prepare();
    }

    /*
     * prepares the notification to be build
     * creates intents & pending intents
     * loads / alters icons
     */
    public void prepare() {
        //reading data, if the app is started new
        if (!Storage.read_already) {
            System.out.println("load data");
            Storage.read_already = true;
            FileLoader fileReader = new FileLoader();
            fileReader.readData();
        }

        last_subject_id = Storage.getLastSubjectId();

        //preparing the Intents & PendingIntents
        prepareIntents();

        //preparing all vars for the notification
        title_strings = context.getResources().getStringArray(R.array.notification_title);
        content_strings = context.getResources().getStringArray(R.array.notification_content);

        int[] listed_homework = listHomework();
        homework_amount = listed_homework[0];
        subjects_with_homework = listed_homework[1];

        title_text = getTitleText(homework_amount, subjects_with_homework);
        content_text = getContentText();

        text_action1 = context.getResources().getString(R.string.notification_new_homework);
        text_action2 = context.getResources().getString(R.string.notification_new_grade);

        //preparing expanded view
        inbox_style = new Notification.InboxStyle();
        inbox_style.setBigContentTitle(title_text);
        for(String s: expanded_texts) {
            inbox_style.addLine(s);
        }

        //channel
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(channel);
            builder = new Notification.Builder(context, CHANNEL_ID);
            builder.setChannelId(CHANNEL_ID);
        }
        else {
            builder = new Notification.Builder(context);
        }

        //preparing notification by creating the builder
        builder.setContentTitle(title_text);
        if(!content_text.equals(""))
            builder.setContentText(content_text);
        builder.setSmallIcon(R.drawable.smallicon);
        builder.setContentIntent(pIntent_content);
        builder.setAutoCancel(false);
        builder.addAction(R.drawable.symbol_homework_add, text_action1, pIntent_action1);
        builder.addAction(R.drawable.symbol_grades_add, text_action2, pIntent_action2);
        builder.setStyle(inbox_style);

        //prepare alarm (updater)
        prepareAlarm();
    }

    /*
     * shows the notification
     */
    public void show() {
        prepare();
        manager.notify(NOTIFICATION_ID, builder.build());
    }

    //******************************************************* Methods *******************************************************//
    /*
     * creates all intents and the refering pendingIntents
     */
    private void prepareIntents() {
        toHomeworkView = new Intent();
        toHomeworkView.setClass(context, MainActivity.class);
        toHomeworkView.putExtra("FRAGMENT_INDEX", 2);
        pIntent_content = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), toHomeworkView, 0);

        toNewHomework = new Intent();
        toNewHomework.setClass(context, Homework_activity.class);
        toNewHomework.putExtra("PREVIOUS_CLASS", MainActivity.class);
        toNewHomework.putExtra("HOMEWORK_INDEX", -1);
        if(last_subject_id != -1) {
            toNewHomework.putExtra("SUBJECT_INDEX", last_subject_id);
        }
        pIntent_action1 = PendingIntent.getActivity(context, (int) System.currentTimeMillis() + 10, toNewHomework, 0);

        if(last_subject_id != -1) {
            toNewGrade = new Intent();
            toNewGrade.setClass(context, Grade_activity.class);
            toNewGrade.putExtra("PREVIOUS_CLASS", MainActivity.class);
            toNewGrade.putExtra("SUBJECT_INDEX", last_subject_id);
            toNewGrade.putExtra("INDEX", -1);
            pIntent_action2 = PendingIntent.getActivity(context, (int) System.currentTimeMillis() + 20, toNewGrade, 0);
        }
    }

    /*
     * creates the title text from the string-array 'title_strings' (Resources)
     */
    private String getTitleText(int homework_amount, int subjects_with_homework) {
        if(homework_amount == 0) {
            return title_strings[4];
        }
        if(subjects_with_homework == 1) {
            return title_strings[3];
        }

        return homework_amount + " " + title_strings[0] + " " + title_strings[1] + " " + subjects_with_homework + " " + title_strings[2];
    }

    /*
     * creates the content text from the string-array 'content_strings' (Resources)
     */
    private String getContentText() {
        String result = "";

        Lesson current_lesson = Storage.schedule.getNextLesson();
        if(current_lesson != null) {
            result = content_strings[0] + " ";
            result += Storage.subjects.get( current_lesson.getSubjectIndex()).getName() + " ";
            result += content_strings[1] + " ";
            result += current_lesson.getRoom();
        }
        else {
            //remains from method listHomework()
        }

        return result;
    }

    /*
     * prepares the AlarmManager, which is responsible for the Updates
     */
    private void prepareAlarm() {
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        int hours = Storage.schedule.getTime(0, 0, 0);
        int minutes = Storage.schedule.getTime(0, 0, 1);
        if(minutes != 0) {
            minutes -= 1;
        }
        else {
            minutes = 59;
            hours--;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, minutes);

        manager.cancel(pendingIntent);
        manager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                TimeUnit.MINUTES.toMillis(5),
                pendingIntent
        );
    }

    //******************************************************* Calculations *******************************************************//
    /*
     * searches thru all homework, to check how many homework arn´t done in how many subjects
     * @return: int[homework_amount ; subject_amount]
     * @add: fills content_text with the homework
     */
    private int[] listHomework() {
        int homework_number = 0;
        content_text = "";
        expanded_texts.clear();
        ArrayList<Integer> subjects = new ArrayList<>();
        ArrayList<Homework> list = Storage.getHomeworkList();

        for(Homework homework: list) {
            if(!homework.getSolution().isFinished()) {
                //title
                homework_number++;
                if(!subjects.contains(homework.getSubjectindex())) {
                    subjects.add(homework.getSubjectindex());
                }

                //content
                String kind = homework.getShortDescription();
                if(kind.equals( context.getResources().getString(R.string.grades_kind_misc))) {
                    kind = homework.getMisc();
                }
                String subject_name = Storage.subjects.get( homework.getSubjectindex()).getName();

                expanded_texts.add(kind + " " + title_strings[1] + " " + subject_name + ": " + homework.getDescription());
            }
        }

        //creating content_text for the case: after school
        for(Integer id: subjects) {
            content_text = content_text.concat( Storage.subjects.get(id).getName() + ", ");
        }
        if(subjects.size() > 0) {
            content_text = content_text.substring(0, content_text.lastIndexOf(','));
        }

        return new int[] {homework_number, subjects.size()};
    }
}
