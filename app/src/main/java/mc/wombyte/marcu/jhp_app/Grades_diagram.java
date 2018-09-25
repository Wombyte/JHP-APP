package mc.wombyte.marcu.jhp_app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

import mc.wombyte.marcu.jhp_app.classes.Grade;

/**
 * Created by marcu on 27.12.2017.
 */

public class Grades_diagram extends View {

    public final static int NO_SUBJECT = -1;

    ArrayList<Grade> grades_list = new ArrayList<>();
    int color_average;
    int color_grade;

    Paint paint = new Paint();
    Path grades_path = new Path();
    Path average_path = new Path();
    float height, width;
    int subject_index = -1;
    int[] grades_interval;
    float[] grade_heights;

    float dy;
    float y_offset;
    float[][][] points;

    //average function
    float x1, y1;
    float x2, y2;
    float m, n;

    /*
     * constructor to use it in code
     * defines and calculates all vars
     */
    public Grades_diagram(Context context, int subject_index) {
        super(context);
        this.subject_index = subject_index;
    }

    /*
     * constructor for android
     */
    public Grades_diagram(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /*
     * Method to resize the view in the dialog
     * without it, the dialog would fill the whole height
     */
    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = (int) ((double) (width * 0.7));
        this.setMeasuredDimension(width, height);
    }

    //******************************************************* important method *******************************************************//

    /*
     * methode to draw (is called on invalidate())
     * used general vars are changed in scrolledBy()
     */
    @Override public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        width = getWidth();
        height = (int) ((double) width * 0.7);

        paint.setColor( getResources().getColor(R.color.background));
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0f, 0f, width, height, paint);

        if(subject_index != NO_SUBJECT) {
            dy = height/grades_interval.length;
            y_offset = dy/2;

            //axes
            paint.setStrokeWidth( getResources().getDimensionPixelSize(R.dimen.grade_diagram_axes_width));
            paint.setColor( getResources().getColor(R.color.colorPrimary));
            canvas.drawLine(
                    (float) getResources().getDimensionPixelSize(R.dimen.grade_diagram_padding_start),
                    (float) 0,
                    (float) getResources().getDimensionPixelSize(R.dimen.grade_diagram_padding_start),
                    height,
                    paint
            );
            canvas.drawLine(
                    (float) getResources().getDimensionPixelSize(R.dimen.grade_diagram_padding_start),
                    height-1,
                    width - getResources().getDimensionPixelSize(R.dimen.grade_diagram_padding_end),
                    height-1,
                    paint
            );
            //lines
            paint.setStrokeWidth( getResources().getDimensionPixelSize(R.dimen.grade_diagram_line_width));
            paint.setColor( getResources().getColor(R.color.colorAccent));
            paint.setTextSize( getResources().getDimensionPixelSize(R.dimen.grade_diagram_axes_text_size));
            paint.setTypeface(Typeface.DEFAULT_BOLD);
            for(int i = 0; i < grades_interval.length; i++) {
                grade_heights[i] = y_offset + dy*i;
                canvas.drawLine(
                        (float) getResources().getDimensionPixelSize(R.dimen.grade_diagram_padding_start),
                        y_offset + dy*i,
                        width - getResources().getDimensionPixelSize(R.dimen.grade_diagram_padding_end),
                        y_offset + dy*i,
                        paint
                );
                canvas.drawText(
                        String.valueOf(grades_interval[i]),
                        (float) (getResources().getDimensionPixelSize(R.dimen.grade_diagram_padding_start) - getResources().getDimensionPixelSize(R.dimen.grade_diagram_padding_start)/1.2),
                        y_offset + dy*i + getResources().getDimensionPixelSize(R.dimen.grade_diagram_axes_text_size)/3f,
                        paint
                );
            }

            calculatePoints();
            if(points[0].length > 0) {
                //grade graph
                paint.setColor(color_grade);
                for (int i = 0; i < points[0].length; i++) {
                    canvas.drawCircle(
                            points[0][i][0],
                            points[0][i][1],
                            getResources().getDimensionPixelSize(R.dimen.grade_diagram_point_radius),
                            paint
                    );
                }
                //average graph
                paint.setColor(color_average);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(getResources().getDimensionPixelSize(R.dimen.grade_diagram_average_graph_width));
                average_path.moveTo(points[1][0][0], points[1][0][1]);
                for (int i = 0; i < points[1].length; i++) {
                    average_path.lineTo(points[1][i][0], points[1][i][1]);
                }
                canvas.drawPath(average_path, paint);
                //draw vertical lines from grade to belonging average
                paint.setColor(color_grade);
                paint.setStrokeWidth(getResources().getDimensionPixelSize(R.dimen.grade_diagram_grade_graph_width));
                for (int i = 0; i < points[0].length; i++) {
                    grades_path.moveTo(points[0][i][0], points[0][i][1]);
                    grades_path.lineTo(points[1][i][0], points[1][i][1]);
                }
                canvas.drawPath(grades_path, paint);
            }
        }

    }

    //******************************************************* update *******************************************************//
    /*
     * sets the right option_index, to draw the diagram
     */
    public void setDiagramDatas(int subject_index, ArrayList<Grade> grade_list, int color_grade, int color_average) {
        this.subject_index = subject_index;
        this.grades_list = grade_list;
        this.color_grade = color_grade;
        this.color_average = color_average;

        grades_interval = getGradeInterval();
        grade_heights = new float[grades_interval.length];
        invalidate();
    }

    //******************************************************* methods *******************************************************//
    /*
     * creates an interval of grades
     * in grade mode 1 is the first item (later at top)
     * in point mode 15 is the first one
     */
    private int[] getGradeInterval() {
        int max = Storage.getHeighestGrade(subject_index);
        int min = Storage.getLowestGrade(subject_index);
        int dif = Math.abs(max-min);
        int[] result = new int[dif+1];
        if(Storage.settings.grades_isRatingInGrades()) {
            for(int i = 0; i <= dif; i++) {
                result[i] = min+i;
            }
        }
        else {
            for(int i = 0; i <= dif; i++) {
                result[i] = max-i;
            }
        }

        return result;
    }

    /*
     * calculates the points for the different grades and averages
     * if there is only one grade, the grade is duplicated to create a horizontal line in the graph
     * else the necessary points are calculated by the two methods below
     */
    private void calculatePoints() {
        //getting the right list of grades
        if(subject_index == Storage.ALL_SUBJECTS) {
            grades_list = Storage.getGradeList();
            color_grade = getResources().getColor(R.color.colorPrimary);
            color_average = getResources().getColor(R.color.colorPrimaryDark);
        }
        else {
            grades_list = Storage.grades.get(subject_index);
            color_grade = Storage.subjects.get(subject_index).getColor();
            color_average = Storage.subjects.get(subject_index).getDarkColor();
        }

        if(grades_list.size() == 1) {
            points = new float[2][2][2]; //[grade, average][count][x, option_y]
            points[0][0] = points[1][0] = getGradePoint(0);
            points[0][1] = points[1][1] = getGradePoint(0);
            points[0][1][0] += width -getResources().getDimensionPixelSize(R.dimen.grade_diagram_padding_start) - getResources().getDimensionPixelSize(R.dimen.grade_diagram_padding_end);
        }
        else {
            points = new float[2][grades_list.size()][2];
            for(int i = 0; i < grades_list.size(); i++) {
                points[0][i] = getGradePoint(i);
                points[1][i] = getAveragePoint(i);
            }
        }
    }

    /*
     * calculate points of a grade
     */
    private float[] getGradePoint(int grade_index) {
        int number = grades_list.get(grade_index).getNumber();
        float x_perc; //percentage of the graph area widh
        float y; //heigth of the point, read from the height of the lines
        int index;

        //get the height of the point, index = max_index - (number-mingrade)
        index = grade_heights.length-1 -(number - Storage.getLowestGrade(subject_index));
        y = grade_heights[index];
        if(grades_list.size()-1 != 0) { //if to avoid division by 0
            x_perc = (float) grade_index / (float) (grades_list.size() - 1);
        }
        else {
            x_perc = 0;
        }
        //width of the graph area, without border and padding
        float width = this.width - getResources().getDimensionPixelSize(R.dimen.grade_diagram_padding_start) - getResources().getDimensionPixelSize(R.dimen.grade_diagram_padding_end);
        return new float[] {
                x_perc*width + getResources().getDimensionPixelSize(R.dimen.grade_diagram_padding_start),
                y
        };
    }

    /*
     * calculates the points of the average
     * it creates a linear function out of the two the min and max grade
     */
    private float[] getAveragePoint(int average_index) {
        float x_perc;
        float width = this.width - getResources().getDimensionPixelSize(R.dimen.grade_diagram_padding_start) - getResources().getDimensionPixelSize(R.dimen.grade_diagram_padding_end);
        float average = (float) Storage.calculateAverage(subject_index, average_index);

        if(grades_list.size()-1 != 0) {
            x_perc = (float) average_index / (float) (grades_list.size() - 1);
        }
        else {
            x_perc = 0;
        }
        return new float[] {
                x_perc*width + getResources().getDimensionPixelSize(R.dimen.grade_diagram_padding_start),
                heightFunction(average)
        };
    }

    /*
     * calculates the rigth height for the translated average
     * look at: linear function from two points
     */
    private float heightFunction(float average) {
        x1 = grades_interval[0];
        y1 = grade_heights[0];
        x2 = grades_interval[grades_interval.length-1];
        y2 = grade_heights[grade_heights.length-1];
        m = (y1-y2)/(x1-x2);
        n = y1 - m*x1;
        return m*average + n;
    }

}

