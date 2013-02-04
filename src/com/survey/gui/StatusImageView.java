package com.survey.gui;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

/**
 * 
 * This class is used to display status images
 * progress can be form 0 to 1
 * The more images been added, the more different states
 * this view can display at the interval of progress value [0..1]
 * 
 */

public class StatusImageView extends View{

    /**
     * state images
     */
    
    private ArrayList<Drawable> bitmaps = new ArrayList<Drawable>();
    private double progress = 0;
    
//-----------------------------------------------------------------------------    
    
    public StatusImageView(Context context){
        super(context);                
    }
    
    public StatusImageView(Context context, AttributeSet attrs){
        super(context, attrs);
    }
    
    public StatusImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
    }
    
//------------------------------------------------------------------------------
    
    public void setProgress(double progress){
        
        if(progress < 0){
            progress = 0;
        }
        
        if(progress > 1){
            progress = 1;
        }
        
        this.progress = progress;
        postInvalidate();
        
    }
    
    public double getProgress(){
        return progress;
    }
    
    public void addStateBitmapDrawable(Drawable bmp){
        if(bmp != null){
            bitmaps.add(bmp);
        }
    }
    
    public void addStateBitmapDrawable(int resId){
        addStateBitmapDrawable((BitmapDrawable) getResources().getDrawable(resId));
    }
    
//------------------------------------------------------------------------------    
    
    @Override
    public void onDraw(Canvas canvas){
        
        if(bitmaps.size() > 0){
        
            int index = (int) Math.round((progress * (double)(bitmaps.size() - 1)));            
            Drawable bmp = bitmaps.get(index);
            
            bmp.setBounds(0, 0, getWidth(), getHeight());
            bmp.draw(canvas);
            
        
        }
        
    }

}