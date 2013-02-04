package com.survey.gui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * TextView which can scale it's text size to fit bounds
 *
 */

public class ScaledTextView extends View{

	private String text;
	private String scalingText = null;
	private TextPaint paint;
    
//-----------------------------------------------------------------------------    
    
    public ScaledTextView(Context context){
        super(context);
        initFields();
    }
    
    public ScaledTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initFields();
    }
    
    /**
     * make basic initializations
     */
    
    private void initFields(){
        this.setWillNotDraw(false);
        this.setPressed(false);
        this.setClickable(true);
        paint = new TextPaint();
        paint.setColor(0xFF000000);
        paint.setAntiAlias(true);
        paint.setTextSize(7);
    }
    
//-----------------------------------------------------------------------------
    
    public void setText(String text){
    	this.text = text;
    }
    
    public String getText(){
    	return text;
    }
    
    /**
     * Text to scale text view for
     * if null then the default text will be scaled
     * @param text
     */
    
    public void setScaligText(String text){
    	this.scalingText = text;
    }

    
//-----------------------------------------------------------------------------    
    
    /**
     * This textView can be clickable, so let's change it's pressed and selected state in onTouch event
     */
    
    public boolean onTouchEvent (MotionEvent event){
        super.onTouchEvent(event);
       
        switch(event.getAction()){
        
        case MotionEvent.ACTION_DOWN:
            setPressed(true);
            postInvalidate();            
            return true;
            
        case MotionEvent.ACTION_UP:
            setPressed(false);
            postInvalidate();   
            return true;
            
        case MotionEvent.ACTION_CANCEL:
            setPressed(false);
            postInvalidate();
            return true;
        }        
        
        return false;
        
    }
    
//-----------------------------------------------------------------------------    
    
    /**
     * Draw this view
     */
    
    @Override
    public void onDraw(Canvas canvas){
        
    	canvas.save();
    	
    	if(text != null && !text.equals("")){
	
    		Rect textRect = new Rect();
    		if(scalingText != null){
    			paint.getTextBounds(scalingText, 0, scalingText.length(), textRect);
    		}else{
    			paint.getTextBounds(text, 0, text.length(), textRect);
    		}
    		
    		float textWidth = textRect.right - textRect.left;
    		float textHeight = - textRect.top;
    		
    		float sk = 0.8f;
    		
    		float sx = sk * ((float) getWidth() / textWidth);
    		float sy = sk * ((float) getHeight() / textHeight);
    		
    		float s = sx;
    		if(sy < sx){
    			s = sy;
    		}
    		
    		textWidth = paint.measureText(text);
    		
    		float tw2 = textWidth / 2;
    		float th2 = - textHeight / 2;
    		float w2 = getWidth() / 2;
    		float h2 = getHeight() / 2;
    		
    		canvas.scale(s, s, w2, h2);
    		
    		canvas.drawText(text, w2 - tw2, h2 - th2, paint);    		
    		
    		
    	}
    	
    	canvas.restore();
        
    }

}