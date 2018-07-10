package mc.wombyte.marcu.jhp_app.Classes;

import android.net.Uri;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import mc.wombyte.marcu.jhp_app.FileLoader;
import mc.wombyte.marcu.jhp_app.FileSaver;
import mc.wombyte.marcu.jhp_app.Storage;

public class Grade implements Serializable {

    int number;
    Date date_written;
    Date date_got;
    String description;
    String short_description;
    String misc = "";
    boolean exam = false;
    int subject_index;
    int index;

    //******************************************************* constructor *******************************************************//
    public Grade(int index, int subject_index, int number, Date date_written, Date date_got, String description, String short_description, String misc, boolean exam) {
        this.index = index;
        this.subject_index = subject_index;
        this.number = number;
        this.date_written = date_written;
        this.date_got = date_got;
        this.description = description;
        this.short_description = short_description;
        this.misc = misc;
        this.exam = exam;
    }

    public Grade(int subject_index, int number, Date date_written, Date date_got, String description, String short_description, String misc, boolean exam) {
        this.subject_index = subject_index;
        this.number = number;
        this.date_written = date_written;
        this.date_got = date_got;
        this.description = description;
        this.short_description = short_description;
        this.misc = misc;
        this.exam = exam;
    }

    private Grade(int subject_index, int index) {
        this.subject_index = subject_index;
        this.index = index;
    }

    public void setNumber(int number) { this.number = number; }
    public int getNumber() { return number; }

    public void setWrittenDate(Date date_written) { this.date_written = date_written; }
    public Date getWrittenDate() { return date_written; }

    public void setGotDate(Date date_got) { this.date_got = date_got; }
    public Date getGotDate() { return date_got; }

    public void setDescription(String description) { this.description = description; }
    public String getDescription() { return description; }

    public void setShortDescription(String short_description) { this.short_description = short_description; }
    public String getShortDescription() { return short_description; }

    public void setMisc(String misc) { this.misc = misc; }
    public String getMisc() { return misc; }

    public void setExam(boolean exam) { this.exam = exam; }
    public boolean isExam() { return exam; }

    public void setSubjectIndex(int subject_index) { this.subject_index = subject_index; }
    public int getSubjectindex() { return subject_index; }

    public void setIndex(int index) { this.index = index; }
    public int getIndex() { return index; }



    private static SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN);
    //******************************************************* saving *******************************************************//

    /**
     * @return String: name of the belonging file consisting of subject &- grade index
     */
    public String getFileName() { return subject_index + "_" + index; }

    /**
     * write the data of the transferred grade into the matching data file
     * if the file does not exist, it will be created
     * @param grade: object which data has to be saved
     * @param des: destination file in which the data is written
     * @throws IOException: writing to a file
     */
    public static void writeGradeData(File des, Grade grade) throws IOException {
        if(grade == null || !des.exists()) {
            return;
        }

        BufferedWriter bw = new BufferedWriter(new FileWriter(des));
        bw.write( FileSaver.data("NUM", String.valueOf(grade.getNumber())));
        bw.write( FileSaver.data("DWR", sdf.format(grade.getWrittenDate())));
        bw.write( FileSaver.data("DGO", sdf.format(grade.getGotDate())));
        bw.write( FileSaver.data("LDE", grade.getDescription()));
        bw.write( FileSaver.data("SDE", grade.getShortDescription()));
        bw.write( FileSaver.data("MIS", grade.getMisc()));
        bw.write( FileSaver.data("EXA", String.valueOf(grade.isExam())));
        bw.close();
    }

    /**
     * returns the correct file depending on the transmitted data
     * @param homework_name: defines to which the image belongs
     * @param img_name: name of the image
     * @return File
     */
    public static File getDestinationImageFile(String homework_name, String img_name) {
        return new File(
                "/storage/emulated/0/JHP/"
                        + Storage.current_semester
                        + "/homework"
                        + homework_name
                        + "/description/"
                        + img_name
        );
    }

    //******************************************************* reading *******************************************************//

    public static Grade readGradeData(File des) throws IOException{
        if(!des.exists()) {
            return null;
        }

        BufferedReader br = new BufferedReader(new FileReader(des));
        String[] line_data;
        int[] ids = Grade.getIndicesFromFileName(des.getParentFile().getName());

        Grade result = new Grade(ids[0], ids[1]);
        while((line_data = FileLoader.getLineData(br.readLine())) != null) {
            switch(line_data[0]) {
                case "NUM":
                    result.setNumber( Integer.parseInt(line_data[1]));
                    break;
                case "DWR":
                    try { result.setWrittenDate( sdf.parse(line_data[1])); }
                    catch(ParseException e) { e.printStackTrace(); }
                    break;
                case "DGO":
                    try { result.setGotDate( sdf.parse(line_data[1])); }
                    catch(ParseException e) { e.printStackTrace(); }
                    break;
                case "LDE":
                    result.setDescription( line_data[1]);
                    break;
                case "SDE":
                    result.setShortDescription( line_data[1]);
                    break;
                case "MIS":
                    result.setMisc( line_data[1]);
                    break;
                case "EXA":
                    result.setExam( Boolean.parseBoolean( line_data[1]));
                    break;
            }
        }

        return result;
    }

    /**
     * listing all image uri fom the transmitted name
     * name is converted to the belonging folder
     * iterating over the folder to save all uri in the arraylist
     * @param name: name of the grade folder
     * @return ArrayList: list of all image uris
     */
    public static ArrayList<Uri> readGradeImages(String name) {
        ArrayList<Uri> result = new ArrayList<>();

        File homework_folder = new File("/storage/emulated/0/JHP/" + Storage.current_semester + "/homework/" + name + "/images/solution");
        for(File image: homework_folder.listFiles()) {
            result.add(Uri.fromFile(image));
        }

        return result;
    }

    /**
     * converts a file name created by 'getFileName' back into subject -& grade index
     * @param name: file name
     * @return int[]: { subject index, grade index }
     */
    private static int[] getIndicesFromFileName(String name) {
        int _id = name.indexOf('_');
        int sid = Integer.parseInt(name.substring(0, _id));
        int gid = Integer.parseInt(name.substring(_id+1));
        return new int[] { sid, gid };
    }
}
