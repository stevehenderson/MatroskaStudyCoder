/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * SingleVideoPanel.java
 *
 * Created on Dec 19, 2010, 10:44:02 PM
 */
package matroskastudycoder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.swing.JFileChooser;

/**
 *
 * @author henderso
 */
public class SingleVideoPanel extends javax.swing.JPanel {

    MatroskaStudyCoderView parent;
    VideoFile videoFile;
    private static String AVIDEMUX_HEADER = "C:\\Program Files (x86)\\Avidemux 2.5\\avidemux2.exe ";
    private static String VLC_HEADER = "C:\\Program Files (x86)\\VideoLAN\\VLC\\vlc.exe ";
    private static String VIDEO_DIR = "C:\\final_data\\videos";
    private static String MKVMERGE_PATH = "C:\\Program Files (x86)\\MKVtoolnix\\mkvmerge.exe";

    public VideoFile getVideoFile() {
        return videoFile;
    }

    public void setVideoFile(VideoFile videoFile) {
        this.videoFile = videoFile;
    }

    /**
     * Return a quoted string
     * @param s
     * @return
     */
    private String qString(String s) {
        StringBuilder fs = new StringBuilder();
        fs.append('"');
        fs.append(s);
        fs.append('"');
        return fs.toString();
    }

    private void updateTextFiles() {

        float offset = ((Float) this.spSingleVideoOffsetSec.getModel().getValue()).floatValue();
        //System.out.println(parent.getExperiment().toSubtitlesSRT(offset));
        //System.out.println(parent.getExperiment().toChapterTxt(offset));

        String fullPath = videoFile.getVideoFilePath();
        String fullPathFixed = videoFile.videoFilePath.replaceAll("\\\\", "\\\\\\\\");
        File videoF = new File(fullPathFixed);
        String dir = videoF.getParent();
        String filename = videoF.getName();
        String[] filenameParts = filename.split("\\.");
        filename = filenameParts[0];
        String extension = filenameParts[1];

        String filePrefix = filename + "_" + extension + "_(" + parent.getExperiment().getSubject() + "_C" + parent.getExperiment().getCondition() + ")";
        String srtFilePath = dir + "\\" + filePrefix + "_captions.srt";
        String chapterFilePath = dir + "\\" + filePrefix + "_chapters.txt";
        System.out.println(srtFilePath);
        System.out.println(chapterFilePath);

        //Write the SRT subtitles
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(srtFilePath));
            out.write(parent.getExperiment().toSubtitlesSRT(offset));
            out.close();
        } catch (IOException e) {
            System.err.println(e);
        }


        //Write the chapters
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(chapterFilePath));
            out.write(parent.getExperiment().toChapterTxt(offset));
            out.close();
        } catch (IOException e) {
            System.err.println(e);
        }

        //TODO: Cue the music
        //"C:\Program Files (x86)\MKVtoolnix\mkvmerge.exe" -o "C:\\final_data\\videos\\S2\\S2.mkv"  "--forced-track" "0:no" "--display-dimensions" "0:640x480" "--forced-track" "1:no" "-a" "1" "-d" "0" "-S" "-T" "--no-global-tags" "--no-chapters" "C:\\final_data\\videos\\S2\\S2.avi" "--forced-track" "0:no" "-s" "0" "-D" "-A" "-T" "--no-global-tags" "--no-chapters" "C:\\final_data\\videos\\S2\\S2.srt" "--track-order" "0:0,0:1,1:0" "--chapters" "C:\\final_data\\videos\\S2\\S2_C1_chapters.txt"

        String outputFilePath = dir + "\\" + filePrefix + ".mkv";
        Runtime rt = Runtime.getRuntime();
        StringBuilder m = new StringBuilder();

        m.append(qString(this.MKVMERGE_PATH));
        m.append(" -o ");


        m.append(qString(outputFilePath));

        m.append(" ");
        m.append(qString("--forced-track"));

        m.append(" ");
        m.append(qString("0:no"));

        m.append(" ");
        m.append(qString("--display-dimensions"));

        m.append(" ");
        m.append(qString("0:640x480"));

        m.append(" ");
        m.append(qString("--forced-track"));

        m.append(" ");
        m.append(qString("1:no"));

        m.append(" ");
        m.append(qString("-a"));

        m.append(" ");
        m.append(qString("1"));

        m.append(" ");
        m.append(qString("-d"));

        m.append(" ");
        m.append(qString("0"));

        m.append(" ");
        m.append(qString("-S"));

        m.append(" ");
        m.append(qString("-T"));

        m.append(" ");
        m.append(qString("--no-global-tags"));

        m.append(" ");
        m.append(qString("--no-chapters"));

        m.append(" ");
        m.append(qString(fullPathFixed));

        m.append(" ");
        m.append(qString("--forced-track"));

        m.append(" ");
        m.append(qString("0:no"));

        m.append(" ");
        m.append(qString("-s"));

        m.append(" ");
        m.append(qString("0"));

        m.append(" ");
        m.append(qString("-D"));

        m.append(" ");
        m.append(qString("-A"));

        m.append(" ");
        m.append(qString("-T"));

        m.append(" ");
        m.append(qString("--no-global-tags"));

        m.append(" ");
        m.append(qString("--no-chapters"));

        m.append(" ");
        m.append(qString(srtFilePath));

        m.append(" ");
        m.append(qString("--track-order"));

        m.append(" ");
        m.append(qString("0:0,0:1,1:0"));

        m.append(" ");
        m.append(qString("--chapters"));

        m.append(" ");
        m.append(qString(chapterFilePath));

        System.out.println(m.toString());

        
        try {
            String line;
            Process p = Runtime.getRuntime().exec(m.toString());
            BufferedReader input =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = input.readLine()) != null) {
                System.out.println(line);
                if(line.contains("Progress")) {
                    String[] sh = line.split(":");
                    String sh2 = sh[1].replace('%', ' ');
                    String sh3 = sh2.trim();
                    int j = Integer.parseInt(sh3);                  
                }

            }
            input.close();
        } catch (Exception err) {
            err.printStackTrace();
        }



    }

    private void loadFile() {
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File(VIDEO_DIR));
        try {
            // Show open dialog; this method does not return until the dialog is closed
            fc.showOpenDialog(this);
            File selFile = fc.getSelectedFile();
            String fullPath = selFile.getCanonicalPath();
            System.out.println("Trying to load " + fullPath);
            this.tbSingleVideoPath.setText(fullPath);
            this.bConnect.setEnabled(true);
            videoFile.setVideoFilePath(fullPath);
        } catch (Exception e) {
            System.err.println(e);
            e.printStackTrace();
        }
    }

    private void openInVLC() {
        System.out.println("TODO:  Launch external player");
        Runtime rt = Runtime.getRuntime();
        try {
            String path = videoFile.videoFilePath.replaceAll("\\\\", "\\\\\\\\");
            rt.exec(VLC_HEADER + path);
        } catch (Exception e) {
            System.err.println("error launching vlc " + e);
        }

    }

    private void openInAviDemux() {
        System.out.println("TODO:  Launch external player");
        Runtime rt = Runtime.getRuntime();
        try {
            String path = videoFile.videoFilePath.replaceAll("\\\\", "\\\\\\\\");
            rt.exec(AVIDEMUX_HEADER + path);
        } catch (Exception e) {
            System.err.println("error launching vlc " + e);
        }

    }

    /** Creates new form SingleVideoPanel */
    public SingleVideoPanel(MatroskaStudyCoderView p) {
        parent = p;
        videoFile = new VideoFile();
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        singleVideoPanel = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        tbSingleVideoPath = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        pExplorer = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        spSingleVideoOffsetSec = new javax.swing.JSpinner();
        bCopyFromCalc = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        bAvidemux = new javax.swing.JButton();
        bConnect = new javax.swing.JButton();

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(matroskastudycoder.MatroskaStudyCoderApp.class).getContext().getResourceMap(SingleVideoPanel.class);
        setBackground(resourceMap.getColor("Form.background")); // NOI18N
        setName("Form"); // NOI18N

        singleVideoPanel.setBackground(resourceMap.getColor("singleVideoPanel.background")); // NOI18N
        singleVideoPanel.setName("singleVideoPanel"); // NOI18N

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        tbSingleVideoPath.setText(resourceMap.getString("tbSingleVideoPath.text")); // NOI18N
        tbSingleVideoPath.setDisabledTextColor(resourceMap.getColor("tbSingleVideoPath.disabledTextColor")); // NOI18N
        tbSingleVideoPath.setName("tbSingleVideoPath"); // NOI18N
        tbSingleVideoPath.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                tbSingleVideoPathKeyTyped(evt);
            }
        });

        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setName("jButton1"); // NOI18N
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButton1MousePressed(evt);
            }
        });

        pExplorer.setName("pExplorer"); // NOI18N

        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        spSingleVideoOffsetSec.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), null, null, Float.valueOf(0.1f)));
        spSingleVideoOffsetSec.setName("spSingleVideoOffsetSec"); // NOI18N
        spSingleVideoOffsetSec.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spSingleVideoOffsetSecStateChanged(evt);
            }
        });

        bCopyFromCalc.setText(resourceMap.getString("bCopyFromCalc.text")); // NOI18N
        bCopyFromCalc.setName("bCopyFromCalc"); // NOI18N
        bCopyFromCalc.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                bCopyFromCalcMousePressed(evt);
            }
        });

        jButton3.setText(resourceMap.getString("jButton3.text")); // NOI18N
        jButton3.setName("jButton3"); // NOI18N
        jButton3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButton3MousePressed(evt);
            }
        });

        bAvidemux.setText(resourceMap.getString("bAvidemux.text")); // NOI18N
        bAvidemux.setName("bAvidemux"); // NOI18N
        bAvidemux.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                bAvidemuxMousePressed(evt);
            }
        });

        bConnect.setText(resourceMap.getString("bConnect.text")); // NOI18N
        bConnect.setEnabled(false);
        bConnect.setName("bConnect"); // NOI18N
        bConnect.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                bConnectMousePressed(evt);
            }
        });

        javax.swing.GroupLayout pExplorerLayout = new javax.swing.GroupLayout(pExplorer);
        pExplorer.setLayout(pExplorerLayout);
        pExplorerLayout.setHorizontalGroup(
            pExplorerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pExplorerLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pExplorerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addGroup(pExplorerLayout.createSequentialGroup()
                        .addComponent(spSingleVideoOffsetSec, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(bCopyFromCalc, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(83, 83, 83)
                        .addComponent(bAvidemux, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(bConnect)))
                .addContainerGap())
            .addGroup(pExplorerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton3)
                .addContainerGap(333, Short.MAX_VALUE))
        );
        pExplorerLayout.setVerticalGroup(
            pExplorerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pExplorerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pExplorerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spSingleVideoOffsetSec, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bCopyFromCalc)
                    .addComponent(bAvidemux)
                    .addComponent(bConnect))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
                .addComponent(jButton3)
                .addContainerGap())
        );

        javax.swing.GroupLayout singleVideoPanelLayout = new javax.swing.GroupLayout(singleVideoPanel);
        singleVideoPanel.setLayout(singleVideoPanelLayout);
        singleVideoPanelLayout.setHorizontalGroup(
            singleVideoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(singleVideoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(singleVideoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addGroup(singleVideoPanelLayout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(10, 10, 10)
                        .addComponent(tbSingleVideoPath, javax.swing.GroupLayout.PREFERRED_SIZE, 370, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(jButton1))
                    .addComponent(pExplorer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        singleVideoPanelLayout.setVerticalGroup(
            singleVideoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(singleVideoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(singleVideoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(tbSingleVideoPath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pExplorer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(singleVideoPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(singleVideoPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MousePressed
        // TODO add your handling code here:
        loadFile();
    }//GEN-LAST:event_jButton1MousePressed

    private void tbSingleVideoPathKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbSingleVideoPathKeyTyped
        this.bConnect.setEnabled(true);
    }//GEN-LAST:event_tbSingleVideoPathKeyTyped

    private void bConnectMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bConnectMousePressed
        // TODO add your handling code here:
        openInVLC();
    }//GEN-LAST:event_bConnectMousePressed

    private void spSingleVideoOffsetSecStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spSingleVideoOffsetSecStateChanged

        float f = Float.parseFloat(this.spSingleVideoOffsetSec.getValue().toString());
        System.out.println("spinner now " + f);
        videoFile.setOffsetSec(f);
    }//GEN-LAST:event_spSingleVideoOffsetSecStateChanged

    private void bCopyFromCalcMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bCopyFromCalcMousePressed
        // TODO add your handling code here:
        this.spSingleVideoOffsetSec.getModel().setValue(parent.getCalcOffsetResult());
    }//GEN-LAST:event_bCopyFromCalcMousePressed

    private void jButton3MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton3MousePressed
        // TODO add your handling code here:
        updateTextFiles();
    }//GEN-LAST:event_jButton3MousePressed

    private void bAvidemuxMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bAvidemuxMousePressed
        // TODO add your handling code here:
        openInAviDemux();
    }//GEN-LAST:event_bAvidemuxMousePressed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bAvidemux;
    private javax.swing.JButton bConnect;
    private javax.swing.JButton bCopyFromCalc;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel pExplorer;
    private javax.swing.JPanel singleVideoPanel;
    private javax.swing.JSpinner spSingleVideoOffsetSec;
    private javax.swing.JTextField tbSingleVideoPath;
    // End of variables declaration//GEN-END:variables
}
