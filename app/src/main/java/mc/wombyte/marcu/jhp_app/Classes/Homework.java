package mc.wombyte.marcu.jhp_app.Classes;

import android.net.Uri;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import mc.wombyte.marcu.jhp_app.FileLoader;
import mc.wombyte.marcu.jhp_app.FileSaver;
import mc.wombyte.marcu.jhp_app.Storage;

/**
 * Created by Marcus on 01.08.2016.
 */
public class Homework {

    HomeworkDate date;
    String description;
    ArrayList<Uri> description_images = new ArrayList<>();
    String short_description;
    String misc;
    int index;
    int subject_index;

    HomeworkSolution solution = new HomeworkSolution();

    //******************************************************* Constructor *******************************************************//

    public Homework(int subject_index, int index, HomeworkDate date, String short_description, String misc, String description, HomeworkSolution solution) {
        this.subject_index = subject_index;
        this.index = index;
        this.date = date;
        this.short_description = short_description;
        this.misc = misc;
        this.description = description;
        this.solution = solution;
    }

    public Homework(int subject_index, HomeworkDate date, String short_description, String misc, String description, boolean state) {
        this.subject_index = subject_index;
        this.date = date;
        this.short_description = short_description;
        this.misc = misc;
        this.description = description;
        this.getSolution().setState(state);
    }

    private Homework(int subject_index, int homework_index) {
        this.subject_index = subject_index;
        this.index = homework_index;
    }

    //******************************************************* Methods *******************************************************//

    /**
     * checks whether this homework is already past
     * @return boolean:
     */
    public boolean isPast() {
        Calendar c = Calendar.getInstance();
        Date now = new Date();

        if(this.date.getLesson() > -1) {
            c.setTime(this.date);
            c.set(Calendar.HOUR_OF_DAY, Storage.schedule.times[this.date.getLesson()][1][0]);
            c.set(Calendar.MINUTE, Storage.schedule.times[this.date.getLesson()][1][1]);
        }
        return now.after(c.getTime());
    }

    //******************************************************* Getter / Setter *******************************************************//
    public void setIndex(int index) { this.index = index; }
    public int getIndex() { return index; }

    public void setDate(HomeworkDate date) { this.date = date; }
    public HomeworkDate getDate() { return date; }

    public void setDescription(String description) { this.description = description; }
    public String getDescription() { return description; }

    public void setDescriptionImages(ArrayList<Uri> description_images) { this.description_images = description_images; }
    public void addDescriptionImage(Uri image) { this.description_images.add(image); }
    public ArrayList<Uri> getDescriptionImages() { return description_images; }

    public void setShortDescription(String short_description) {this.short_description = short_description;}
    public String getShortDescription() { return short_description; }

    public void setMisc(String misc) { this.misc = misc; }
    public String getMisc() { return misc; }

    public void setSubjectIndex(int subject_index) { this.subject_index = subject_index; }
    public int getSubjectindex() { return subject_index; }

    public void setSolution(HomeworkSolution solution) { this.solution = solution; }
    public HomeworkSolution getSolution() { return solution; }



    public static final int SOLUTION_IMAGE = 0;
    public static final int DESCRIPTION_IMAGE = 1;
    private static SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN);

    //******************************************************* Saving *******************************************************//

    /**
     * @return String: name of the belonging file consisting of subject &- homework index
     */
    public String getFileName() { return subject_index + "_" + index; }

    /**
     * writes the homework data of "homework" into the matching file
     * @param des: destination file where the data is written in
     * @param homework: object to be saved
     * @param c: Calendar to delete homework if past
     */
    public static void writeHomeworkData(File des, Homework homework, Calendar c) throws IOException{
        if(!des.exists()) {
            return;
        }
        if(homework == null || !c.getTime().before(homework.date.date)) {
            FileSaver.deleteHomework(homework);
            return;
        }

        BufferedWriter bw = new BufferedWriter(new FileWriter(des));
        bw.write( FileSaver.data("DAT", sdf.format(homework.date.date)));
        bw.write( FileSaver.data("LES", String.valueOf(homework.date.lesson)));
        bw.write( FileSaver.data("SDE", homework.short_description));
        bw.write( FileSaver.data("LDE", homework.description));
        bw.write( FileSaver.data("MIS", homework.misc));
        bw.write( FileSaver.data("FIN", String.valueOf(homework.getSolution().isFinished())));
        for(String link: homework.getSolution().getDocs()) {
            bw.write( FileSaver.data("DOC", link));
        }
        for(String link: homework.getSolution().getSheets()) {
            bw.write( FileSaver.data("SHT", link));
        }
        for(String link: homework.getSolution().getSlides()) {
            bw.write( FileSaver.data("SLD", link));
        }

        bw.close();
    }

    /**
     * returns the correct file depending on the transmitted data
     * @param mode: defines which kind of homework image it is
     * @param homework_name: defines to which the image belongs
     * @param img_name: name of the image
     * @return File
     */
    public static File getDestinationImageFile(int mode, String homework_name, String img_name) {
        if(mode == SOLUTION_IMAGE) {
            return new File("/storage/emulated/0/JHP/homework/" + homework_name + "/images/solution/" + img_name);
        }
        if(mode == DESCRIPTION_IMAGE) {
            return new File("/storage/emulated/0/JHP/homework/" + homework_name + "/images/description/" + img_name);
        }
        return new File("/storage/emulated/0/JHP");
    }

    //******************************************************* Reading *******************************************************//

    /**
     * reads the data from the transmitted file
     * and tries to save it as a homework
     * if the 'des' file does not exist the method returns null
     * @param des: destination file from which the data is read
     * @return Homework: read data as a homework object
     * @throws IOException: reading from a file
     */
    public static Homework readHomeworkData(File des) throws IOException{
        if(!des.exists()) {
            return null;
        }

        BufferedReader br = new BufferedReader(new FileReader(des));
        String date = "";
        int lesson = -10;
        int[] ids = Homework.getIndicesFromFileName(des.getParentFile().getName());
        String[] line_data;

        Homework result = new Homework(ids[0], ids[1]);
        while((line_data = FileLoader.getLineData(br.readLine())) != null) {
            switch (line_data[0]) {
                case "DAT":
                    date = line_data[1];
                    break;
                case "LES":
                    lesson = Integer.parseInt(line_data[1]);
                    break;
                case "SDE":
                    result.setShortDescription(line_data[1]);
                    break;
                case "LDE":
                    result.setDescription(line_data[1]);
                    break;
                case "MIS":
                    result.setMisc(line_data[1]);
                    break;
                case "FIN":
                    result.getSolution().setState(Boolean.parseBoolean(line_data[1]));
                    break;
                case "DOC":
                    result.getSolution().addDoc(line_data[1]);
                    break;
                case "SHT":
                    result.getSolution().addSheet(line_data[1]);
                    break;
                case "SLD":
                    result.getSolution().addSlide(line_data[1]);
                    break;
            }
        }
        if (!date.equals("") && lesson != -10) {
            try {
                result.setDate(new HomeworkDate(sdf.parse(date), lesson));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    /**
     * listing all image uri fom the transmitted name
     * name is converted to the belonging folder
     * iterating over the folder to save all uri in the arraylist
     * @param mode: decides from which folder the images are read
     * @param name: name of the homework folder
     * @return ArrayList: list of all image uris
     */
    public static ArrayList<Uri> readHomeworkImages(int mode, String name) {
        ArrayList<Uri> result = new ArrayList<>();

        File homework_folder;
        if(mode == DESCRIPTION_IMAGE) {
            homework_folder = new File("/storage/emulated/0/JHP/homework/" + name + "/images/description");
        }
        else {
            homework_folder = new File("/storage/emulated/0/JHP/homework/" + name + "/images/solution");
        }

        for(File image: homework_folder.listFiles()) {
            result.add(Uri.fromFile(image));
        }

        return result;
    }

    /**
     * reads and saves the uris of all description images
     * @return ArrayList: all read uris
     */
    public ArrayList<Uri> readDescriptionImages() {
        return readHomeworkImages(Homework.DESCRIPTION_IMAGE, getFileName());
    }

    /**
     * reads and saves the uris of all solution images
     * @return ArrayList: all read uris
     */
    public ArrayList<Uri> readSolutionImages() {
        return readHomeworkImages(Homework.SOLUTION_IMAGE, getFileName());
    }

    /**
     * converts a file name created by 'getFileName' back into subject -& homework index
     * @param name: file name
     * @return int[]: { subject index, homework index }
     */
    private static int[] getIndicesFromFileName(String name) {
        int _id = name.indexOf('_');
        int sid = Integer.parseInt(name.substring(0, _id));
        int hid = Integer.parseInt(name.substring(_id+1));
        return new int[] { sid, hid };
    }
}