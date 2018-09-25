package mc.wombyte.marcu.jhp_app;

import java.io.BufferedWriter;
import java.util.ArrayList;

/**
 * Created by marcu on 08.10.2017.
 */

public class SettingField extends SettingStructure {

    private String id = "";
    private int value_int = 0;
    private byte value_byte = (byte) 0;
    private int byte_options_id;
    OptionChangeListener listener = null;
    private double value_double = 0.0;
    private String value_string = "";
    private ArrayList<String> value_string_list = new ArrayList<>();
    private boolean value_boolean = false;
    private int[] value_1d_int_array = new int[] {};

    double min = Double.MIN_VALUE;
    double max = Double.MAX_VALUE;

    int type;

    final static int NONE = -1;
    final static int BOOLEAN = 0;
    final static int BYTE = 1;
    final static int DOUBLE = 2;
    final static int INTEGER = 3;
    final static int INTEGER_ARRAY = 4;
    final static int STRING = 5;
    final static int STRING_LIST = 6;


    /*
     * Constructor
     */
    public SettingField() { }

    public SettingField(String id, int type, int description_id) {
        this.id = id;
        this.type = type;
        this.description_id = description_id;
        chooseView();
    }

    public SettingField(String id, int type, int description_id, int symbol_id) {
        this.id = id;
        this.type = type;
        this.description_id = description_id;
        this.symbol_id = symbol_id;
        chooseView();
    }

    public SettingField(String id, int type, SettingFragment fragment, int description_id) {
        this.id = id;
        this.type = type;
        this.fragment = fragment;
        this.has_fragment = true;
        this.description_id = description_id;
        chooseView();
    }

    public SettingField(String id, int type, SettingFragment fragment, int description_id, int symbol_id) {
        this.id = id;
        this.type = type;
        this.fragment = fragment;
        this.has_fragment = true;
        this.description_id = description_id;
        this.symbol_id = symbol_id;
        chooseView();
    }

    //******************************************************* constructor method *******************************************************//
    /*
     * method needed by all constructors
     * sets the correct view id depending on the transferred class
     */
    private void chooseView() {
        switch(type) {
            case INTEGER:
                settings_view_id = Settings.SETTINGS_VIEW_DIALOG;
                this.has_dialog = true;
                break;
            case DOUBLE:
                settings_view_id = Settings.SETTINGS_VIEW_DIALOG;
                this.has_dialog = true;
                break;
            case STRING:
                settings_view_id = Settings.SETTINGS_VIEW_DIALOG;
                this.has_dialog = true;
                break;
            case STRING_LIST:
                settings_view_id = Settings.SETTINGS_VIEW_FRAGMENT;
                this.has_fragment = true;
                break;
            case BOOLEAN:
                settings_view_id = Settings.SETTINGS_VIEW_SWITCH;
                break;
            case BYTE:
                settings_view_id = Settings.SETTINGS_VIEW_DIALOG;
                this.has_dialog = true;
                break;
            case INTEGER_ARRAY:
                settings_view_id = Settings.SETTINGS_VIEW_FRAGMENT;
                this.has_fragment = true;
                break;
            case NONE:
                settings_view_id = Settings.SETTINGS_VIEW_FRAGMENT;
                this.has_fragment = true;
                break;
            default:
                settings_view_id = Settings.SETTINGS_VIEW_SPACE;
                break;
        }
    }

    //******************************************************* methods *******************************************************//
    /*
     * print the content of this field into the transferred bw
     */
    @Override public void print(BufferedWriter bw) {
        try {
            switch(type) {
                case INTEGER:
                    bw.write(id + ": " + String.valueOf(value_int));
                    bw.newLine();
                    break;
                case DOUBLE:
                    bw.write(id + ": " + String.valueOf(value_double));
                    bw.newLine();
                    break;
                case STRING:
                    bw.write(id + ": " + value_string);
                    bw.newLine();
                    break;
                case STRING_LIST:
                    bw.write(id + ": " + listToString(value_string_list));
                    bw.newLine();
                    break;
                case BOOLEAN:
                    bw.write(id + ": " + String.valueOf(value_boolean));
                    bw.newLine();
                    break;
                case BYTE:
                    bw.write(id + ": " + String.valueOf(value_byte));
                    bw.newLine();
                    break;
                case INTEGER_ARRAY:
                    bw.write(id + ": " + arrayToString(value_1d_int_array));
                    bw.newLine();
                    break;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * returns this field with the transmitted int as long_description_id
     */
    public SettingField addLongDescription(int description_long_id) {
        this.description_long_id = description_long_id;
        return this;
    }
    public String getId() {
        return id;
    }

    /*
     * @param 'array_id': string title_strings the Setting(with type Byte) includes
     */
    public void setArrayId(int array_id) {
        this.byte_options_id = array_id;
    }

    /*
     * @return: index of the option-arry
     */
    public int getArrayId() {
        return byte_options_id;
    }

    /*
     * provides a method, that is run after another option is selected
     */
    public void onOptionChange(byte id) {
        listener.onOptionChange(id);
    }

    /*
     * checks whether the active value is an instance of the transferred class
     * @param 'type': id that have to checked
     */
    public boolean hasValueOf(int type) {
        return this.type == type;
    }

    /*
     * returns the value of this setting
     */
    public Integer getIntValue() { return value_int; }
    public Byte getByteValue() { return value_byte; }
    public Double getDoubleValue() { return value_double; }
    public Boolean getBooleanValue() { return value_boolean; }
    public String getStringValue() { return value_string; }
    public int[] get1DIntArrayValue() { return value_1d_int_array; }

    /*
     * sets the transferred value if the classes matches
     */
    public void setValue(Object value) {
        switch(type) {
            case INTEGER:
                value_int = (int) value;
                break;
            case DOUBLE:
                value_double = (double) value;
                break;
            case STRING:
                value_string = (String) value;
                break;
            case BOOLEAN:
                value_boolean = (boolean) value;
                break;
            case BYTE:
                value_byte = (byte) value;
                break;
            case INTEGER_ARRAY:
                value_1d_int_array = (int[]) value;
                break;
        }
    }

    public void setBounds(double min, double max) {
        this.min = min;
        this.max = max;
    }

    /*
     * sets a new input_listener for an option change event
     */
    public void setOnOptionChangeListener(OptionChangeListener listener) {
        this.listener = listener;
    }

    /*
     * converts the int[] into a string
     */
    private String arrayToString(int[] array) {
        if(array == null || array.length == 0) {
            return "[]";
        }
        String result = "[";
        for(int i: array) {
            result = result.concat(i + ", ");
        }
        result = result.substring(0, result.length()-2);
        result = result.concat("]");
        return result;
    }

    /*
     * converts the Stringlist into a string
     */
    private String listToString(ArrayList<String> array) {
        if(array == null || array.size() == 0) {
            return "[]";
        }
        String result = "[";
        for(String string: array) {
            result = result.concat(string + ", ");
        }
        result = result.substring(0, result.length()-2);
        result = result.concat("]");
        return result;
    }

    public interface OptionChangeListener {
        void onOptionChange(byte id);
    }

    public void setDialog() {
        this.settings_view_id = Settings.SETTINGS_VIEW_DIALOG;
        this.has_dialog = true;
    }

    public void setFragment(SettingFragment fragment) {
        this.fragment = fragment;
        this.settings_view_id = Settings.SETTINGS_VIEW_FRAGMENT;
        this.has_fragment = true;
    }
}