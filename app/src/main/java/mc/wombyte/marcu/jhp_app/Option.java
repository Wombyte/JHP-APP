package mc.wombyte.marcu.jhp_app;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;

/**
 * Created by marcu on 03.04.2018.
 */

public class Option {

    int bg_color;
    int fg_color;
    String text;
    String description;
    int y_text_offset; //to center the text in activity_scroll_fragment
    int x_description_offset; //to save the description length
    Drawable symbol;
    boolean contentIsText;
    boolean closeMenu = true;

    /*
     * Constructor
     */
    public Option(int bg_color, int fg_color, String text, String description) {
        this.bg_color = bg_color;
        this.fg_color = fg_color;
        this.text = text;
        this.description = description;
        contentIsText = true;
    }
    public Option(int bg_color, int fg_color, String text, String description, boolean closeMenu) {
        this.bg_color = bg_color;
        this.fg_color = fg_color;
        this.text = text;
        this.description = description;
        contentIsText = true;
        this.closeMenu = closeMenu;
    }
    public Option(int bg_color, int fg_color, Drawable symbol, String description) {
        this.bg_color = bg_color;
        this.fg_color = fg_color;
        this.symbol = symbol;
        this.description = description;
        setSymbolColor();
        contentIsText = false;
    }
    public Option(int bg_color, int fg_color, Drawable symbol, String description, boolean closeMenu) {
        this.bg_color = bg_color;
        this.fg_color = fg_color;
        this.symbol = symbol;
        this.description = description;
        setSymbolColor();
        contentIsText = false;
        this.closeMenu = closeMenu;
    }

    //******************************************************* method *******************************************************//
    public void setSymbolColor() {
        Drawable d = symbol;
        d = d.mutate();
        d.setColorFilter(fg_color, PorterDuff.Mode.SRC_ATOP);
        symbol = d;
    }

    /*
     * sets the bounds of the symbol (position in menu fragment)
     */
    public void setSymbolBounds(int x, int y, int r) {
        symbol.setBounds(
                x - r/2,
                y - r/2,
                x + r/2,
                y + r/2
        );
    }

    /*
     * sets the alpha value to the transferred value
     */
    public void setSymbolAlpha(int alpha) {
        symbol.setAlpha(alpha);
    }

    //******************************************************* getter *******************************************************//
    public int getBackgroundColor() { return bg_color; }
    public int getForegroundColor() { return fg_color; }
    public String getText() {
        if(!contentIsText) {
            return null;
        }
        return text;
    }
    public int getTextOffsetY() { return y_text_offset; }
    public String getDescription() { return description; }
    public int getDescriptionOffsetX() { return x_description_offset; }
    public Drawable getSymbol() {
        if(contentIsText) {
            return null;
        }
        return symbol;
    }
    public boolean isContentText() { return contentIsText; }
    public boolean isContentSymbol() { return !contentIsText; }
    public boolean isCloseMenu() { return closeMenu; }

    //******************************************************* setter *******************************************************//
    public void setBackground(int bg) { this.bg_color = bg; }
    public void setForeground(int fg) {
        this.fg_color = fg;
        if(isContentSymbol()) {
            setSymbolColor();
        }
    }
    public void setText(String text) {
        this.text = text;
        contentIsText = true;
    }
    public void setTextOffsetY(int y) { this.y_text_offset = y; }
    public void setDescription(String description) { this.description = description; }
    public void setDescriptionOffsetX(int x_description_offset) { this.x_description_offset = x_description_offset; }
    public void setSymbol(Drawable symbol) {
        this.symbol = symbol;
        setSymbolColor();
        contentIsText = false;
    }
    public void setCloseMenu(boolean closeMenu) { this.closeMenu = closeMenu; }

}
