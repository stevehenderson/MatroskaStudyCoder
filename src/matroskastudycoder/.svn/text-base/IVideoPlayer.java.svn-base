/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package matroskastudycoder;

/**
 * An interface for the video player
 * @author henderso
 */
public interface IVideoPlayer {

    /*
     * Initialize the video player
     */
    public void init(String host, String port) throws Exception;

    /*
     * Get the current run time of the media
     */
    public String getCurrentTimeSec();

    /*
     * Get the current status
     */
    public int getCurrentStatus();


    public boolean isInitialized();

    public void reset();

    /*
     * Stop the media player
     */
    public void plStop();

    /*
     * Start the media player
     */
    public void plPlay();


    public void plPause();

    /*
     * Load the prescribed media files
     */
    public void setVideoFile(String fullPath);

    /*
     * Get the time offset (in sec) that should be applied to the experiment
     * clock time
     */
    public int getTimeOffsetSec();

    /**
     * Set the timeoffset that should be applied to the experiment clock
     * @return
     */
    public void setTimeOffsetSec(int i);

    public void seekMasterTime(int i);

}
