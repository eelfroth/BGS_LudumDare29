package com.BauhausGamesSyndicate.LudumDare29;



import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import java.text.NumberFormat;

/**
 *The FPS diagramm collects some fps values and creates a diagram and analyzes it.
 * @author Benedikt Vogler
 */
public class FPSdiag {
    private final int[] data = new int[50];
    private float timeStepMin;
    private int field;//the current field number
    private final int xPos, yPos, width, maxHeight;
    private boolean visible = true;
    private StringBuilder memoryText;
    private long freeMemory;
    private long allocatedMemory;
    private long maxMemory;
    private long usedMemory;
    private final SpriteBatch batch;

    /**
     *
     * @param xPos the position of the diagram from left
     * @param yPos the position of the diagram (its bottom)
     */
    public FPSdiag(int xPos, int yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
        width = 4;
        maxHeight=150;   
        batch = new SpriteBatch(); 
    }
    
    /**
     *Updates the diagramm
     * @param delta
     */
    public void update(float delta){
        timeStepMin += delta;
        if (timeStepMin>100){//update only every t ms
            timeStepMin = 0;
            
            field++;//move to next field
            if (field >= data.length) field = 0; //start over           
            
            data[field] = (int) (1/Gdx.graphics.getDeltaTime());//save fps
        }
        
        Runtime runtime = Runtime.getRuntime();
        NumberFormat format = NumberFormat.getInstance();

        memoryText = new StringBuilder(100);
        maxMemory = runtime.maxMemory();
        allocatedMemory = runtime.totalMemory();
        freeMemory = runtime.freeMemory();
        usedMemory = allocatedMemory-freeMemory;

        memoryText.append(format.format(usedMemory / 1024));
        memoryText.append("/").append(format.format(allocatedMemory / 1024)).append(" MB");
//        memoryText.append("free: ").append(format.format(freeMemory / 1024));
//        memoryText.append("allocated: ").append(format.format(allocatedMemory / 1024));
//        memoryText.append("max: ").append(format.format(maxMemory / 1024));
//        memoryText.append("total free: ").append(format.format((freeMemory + (maxMemory - allocatedMemory)) / 1024));
    }
    
    /**
     *Renders the diagramm
     * @param shr y-down shape renderer
     * @param font
     */
    public void render(ShapeRenderer shr, BitmapFont font){
        if (visible){
            
            //render font
            batch.begin();
            font.draw(batch, memoryText.toString(), xPos, yPos);
            font.draw(batch, "FPS:"+ Gdx.graphics.getFramesPerSecond(), 10, 10);
            batch.end();
            
            //render diagramm
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA,GL20.GL_ONE_MINUS_SRC_ALPHA);
            Gdx.gl.glLineWidth(1);
            
            
            shr.begin(ShapeRenderer.ShapeType.Filled);
            //background
            shr.setColor(new Color(0.5f, 0.5f, 0.5f, 0.2f));
            shr.rect(xPos, yPos, getWidth(), maxHeight);
            
            //render current field bar
            shr.setColor(new Color(1, 0, 1, 0.8f));
            shr.rect(xPos+width*field, yPos+maxHeight-data[field], width, data[field]);
            
            //render RAM
            shr.setColor(new Color(.2f, 1, .2f, 0.8f));
            shr.rect(
                xPos,
                yPos,
                usedMemory*width*data.length/allocatedMemory,
                20
            );
            
            shr.setColor(new Color(0.5f, 0.5f, 0.5f, 0.8f));
            shr.rect(
                xPos + usedMemory*width*data.length/allocatedMemory,
                yPos,
                width*data.length - width*data.length*usedMemory/allocatedMemory,
                20
            );
            
            shr.end();
            
            //render lines
            shr.begin(ShapeRenderer.ShapeType.Line);
            
            //render steps
            shr.setColor(Color.GRAY);
            shr.line(xPos, yPos+maxHeight, xPos+width*data.length, yPos+maxHeight);
            shr.line(xPos, yPos+maxHeight-30, xPos+width*data.length, yPos+maxHeight-30);
            shr.line(xPos, yPos+maxHeight-60, xPos+width*data.length, yPos+maxHeight-60);
            shr.line(xPos, yPos+maxHeight-120, xPos+width*data.length, yPos+maxHeight-120);
            
            
            for (int i = 0; i < data.length-1; i++) { //render each field in memory
                shr.setColor(new Color(0, 0, 1, 0.9f));
                shr.line(xPos+width*i+width/2, yPos-data[i]+maxHeight, xPos+width*(i+1.5f), yPos-data[i+1]+maxHeight);
            }

            //render average            
            shr.setColor(new Color(1, 0, 1, 0.8f));
            shr.line(xPos, yPos+maxHeight-getAverage(), xPos+width*data.length, yPos-getAverage()+maxHeight);

            shr.end(); 
            
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }
    }
    
    /**
     *Get a recorded FPS value. The time between savings is at least the timeStepMin
     * @param pos the array position
     * @return FPS value
     * @see #getTimeStepMin() 
     */
    public int getSavedFPS(int pos){
        return data[pos];
    }

    /**
     * The minimum time between two FPS values.
     * @return 
     */
    public float getTimeStepMin() {
        return timeStepMin;
    }
    
    /**
     *Returns the average value.
     * @return
     */
    public int getAverage(){
        int avg = 0;
        int length = 0;
        for (float fps : data) {
            avg += fps;
            if (fps > 0) length ++;//count how many field are filled
        }
        if (length > 0) avg /= length;
        return avg;
    }

    /**
     * Is the diagramm visible?
     * @return 
     */
    public boolean isVisible() {
        return visible;
    }

   /**
    * Set the FPSdiag visible. You must nevertheless call render() to let it appear.
    * @param visible 
    */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     *
     * @return 
     */
    public int getxPos() {
        return xPos;
    }

    /**
     * 
     * @return Y-Up
     */
    public int getyPos() {
        return yPos;
    }

    /**
     * Width of FPS diag.
     * @return in pixels
     */
    public int getWidth() {
        return width*data.length;
    }
}
