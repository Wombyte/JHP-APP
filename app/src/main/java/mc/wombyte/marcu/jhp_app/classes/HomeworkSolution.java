package mc.wombyte.marcu.jhp_app.classes;

import android.net.Uri;

import java.util.ArrayList;

/**
 * Created by marcus on 02.07.2018.
 */
public class HomeworkSolution {

    private String text;
    private ArrayList<Uri> images ;
    private ArrayList<String> docs;
    private ArrayList<String> sheets;
    private ArrayList<String> slides;
    private boolean finished;

    public HomeworkSolution() {
        text = "";
        images = new ArrayList<>();
        docs = new ArrayList<>();
        sheets = new ArrayList<>();
        slides = new ArrayList<>();
        finished = false;
    }

    //Getter
    public String getText() { return text; }
    public ArrayList<Uri> getImages() { return images; }
    public ArrayList<String> getDocs() { return docs; }
    public ArrayList<String> getSheets() { return sheets; }
    public ArrayList<String> getSlides() { return slides; }
    public boolean isFinished() { return finished; }

    //Adder
    public void addImage(Uri image) { images.add(image); }
    public void addDoc(String doc) { docs.add(doc); }
    public void addSheet(String sheet) { sheets.add(sheet); }
    public void addSlide(String slide) { slides.add(slide); }

    public void addImage(Uri image, int i) { images.add(i, image); }
    public void addDoc(String doc, int i) { docs.add(i, doc); }
    public void addSheet(String sheet, int i) { sheets.add(i, sheet); }
    public void addSlide(String slide, int i) { slides.add(i, slide); }

    //Setter
    public void setText(String text) { this.text = text; }
    public void setImages(ArrayList<Uri> images) { this.images = (ArrayList<Uri>) images.clone(); }
    public void setDocs(ArrayList<String> docs) { this.docs = (ArrayList<String>) docs.clone(); }
    public void setSheets(ArrayList<String> sheets) { this.sheets = (ArrayList<String>) sheets.clone(); }
    public void setSlides(ArrayList<String> slides) { this.slides = (ArrayList<String>) slides.clone(); }
    public void setState(boolean finished) { this.finished = finished; }

}

