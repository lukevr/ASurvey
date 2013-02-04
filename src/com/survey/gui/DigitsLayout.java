package com.survey.gui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;

/**
 * Layout for displaying and controlling buttons with digits
 * 
 */

public class DigitsLayout extends ViewGroup{
    
    private static final int MARGIN_WIDTH = 10;
    private static final int MARGIN_HEIGHT = 10;
    private static final int OFFSET_MARGIN_WIDTH = 7;
    private static final int OFFSET_MARGIN_HEIGHT = 5;
    
//-----------------------------------------------------------------    

    private int buttonWidth;
    private int buttonHeight;
    
    private ScaledTextView[] buttons = new ScaledTextView[9];   
    private ScaledTextView currentButton = null;
    
    private boolean multiSelect = false;
    
    private OnStateChangedListener onStateChangedListener = null;

//-----------------------------------------------------------------    
    
    public DigitsLayout(Context context) {
        super(context);
        initFields(null);        
    }
    
    public DigitsLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initFields(attrs);
        
    }
    
    public DigitsLayout(Context context, AttributeSet attrs, int param) {
        super(context, attrs, param);
        initFields(attrs);
        
    }
    
    /**
     * make basic initializations here
     */
    
    private void initFields(AttributeSet attrs){
        //setWillNotDraw(false);
        setFocusable(true);
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
        setBackgroundColor(0);
        
        for(int i = 0; i < 9; i++){
            if(attrs != null){
                buttons[i] = new ScaledTextView(getContext(), attrs);
            }else{
                buttons[i] = new ScaledTextView(getContext());
            }
            
            //buttons[i].setBackgroundResource(R.drawable.digits_button_background);
            buttons[i].setText(String.valueOf(i + 1));
            this.addViewInLayout(buttons[i], -1, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        }
        
    }
    
    /**
     * set this flag to true, if you want several buttons to be selected at a time
     * @param v
     */
    
    public void setMultiSellect(boolean v){
        this.multiSelect = v;
    }
    
    public boolean getMultiSelect(){
        return this.multiSelect;
    }

    public void setOnStateChangedListener(OnStateChangedListener listener){
        this.onStateChangedListener = listener;
    }

    @Override
    public void onMeasure(int WidthMeasureSpec, int HeightMeasureSpec){
        
    	buttonWidth = ((MeasureSpec.getSize(WidthMeasureSpec) - MARGIN_WIDTH) / 3) - OFFSET_MARGIN_WIDTH; 
    	buttonHeight = ((MeasureSpec.getSize(HeightMeasureSpec) - MARGIN_HEIGHT) / 3) - OFFSET_MARGIN_HEIGHT;
    	
        int ws = MeasureSpec.makeMeasureSpec(buttonWidth, MeasureSpec.EXACTLY);
        int hs = MeasureSpec.makeMeasureSpec(buttonHeight, MeasureSpec.EXACTLY);
        
        for(ScaledTextView button : buttons){
            button.measure(ws, hs);
        }
        
        int width = MeasureSpec.getSize(WidthMeasureSpec);
        int height = MeasureSpec.getSize(HeightMeasureSpec);
        
        setMeasuredDimension(width, height);
        
    }
    
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        
        int w = r - l;
        int h = b - t;
        
        int x = 0;
        int y = 0;
        
        int i = 0;
        
        int offx = (MARGIN_WIDTH + OFFSET_MARGIN_WIDTH)  / 2;
        int offy = (MARGIN_HEIGHT + OFFSET_MARGIN_HEIGHT)/ 2;
        
        int dx = (w  - MARGIN_WIDTH) / 3;
        int dy = (h - MARGIN_HEIGHT)/ 3;
        
        for(y = 0; y < 3; y ++){
            for(x = 0; x < 3; x ++){
                
                ScaledTextView button = buttons[i];
                button.layout(offx + x * dx, offy + y * dy, offx + x * dx + buttonWidth, offy + y * dy + buttonHeight);                
                
                i ++;
                
            }
        }
        
        
    }
    
    /**
     * always intercept touch event
     */
    
    public boolean onInterceptTouchEvent(MotionEvent event){
        return true;
    }
    
    public boolean onTouchEvent (MotionEvent event){
        
        switch(event.getAction()){
        
        case MotionEvent.ACTION_DOWN:
            currentButton = findButtonByXY((int)event.getX(), (int)event.getY());
            if(currentButton != null){
                currentButton.setPressed(true);
                currentButton.postInvalidate();
            }
            
            return true;
            
        case MotionEvent.ACTION_UP:           
            selectButton(currentButton);            
            return true;
            
        case MotionEvent.ACTION_CANCEL:
            return true;
        }
        
        return false;
        
    }
    
    private void selectButton(ScaledTextView button){
        
        if(button != null){
            
            button.setPressed(false);
            
            if(button.isSelected()){
                button.setSelected(false);
            }else{
                button.setSelected(true);
            }
            
            if(!multiSelect){
                
                for(ScaledTextView currBtn : buttons){

                    if(currBtn != button){
                        
                        currBtn.setPressed(false);
                        currBtn.setSelected(false);
                        currBtn.postInvalidate();
                        
                    }
                    
                }
                
            }
            
            if(onStateChangedListener != null){
                onStateChangedListener.onStateChanged(getButtonsCurrentState());
            }
            
        }
        
    }
    
    public void clearButtonsSelection(){
        
        for(ScaledTextView button : buttons){

            button.setPressed(false);
            button.setSelected(false);
            button.postInvalidate();            
            
        }
        
    }
    
    private ScaledTextView findButtonByXY(int x, int y){
        
        for(ScaledTextView button : buttons){
            
            if(x > button.getLeft() && x < button.getRight() &&
               y > button.getTop() && y < button.getBottom()){
               return button; 
            }
            
        }
        
        return null;
        
    }
    
    /**
     * Form state string here
     * @return
     */
    
    public String getButtonsCurrentState(){
        
        int i = 101;
        String result = "";
        
        for(ScaledTextView button : buttons){

            if(result.equals("") == false){
                result = result + ",";
            }
            
            if(button.isSelected()){
                result = result + String.valueOf(i);
            }else{
                result = result + String.valueOf(-i);
            }
            i = i + 1;
            
        }
        
        return result;
        
    }
    
//---------------------------------------------------------------    
    
    /**
     * interface for listening buttons state changes
     */
    
    public static interface OnStateChangedListener{
        
        public void onStateChanged(String newState);
        
    };
    
    
}