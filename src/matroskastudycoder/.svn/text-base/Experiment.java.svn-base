/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package matroskastudycoder;

import au.com.bytecode.opencsv.CSVReader;
import java.io.FileReader;
import java.util.Iterator;
import java.util.TreeMap;

/**
 *
 * @author henderso
 */
public class Experiment {

    TreeMap<Float, Task> tasks;
    String subject = "unset";
    int condition = -1;

    float lastEndTime=-1;





    public int getCondition() {
        return condition;
    }

    public void setCondition(int condition) {
        this.condition = condition;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public TreeMap<Float, Task> getTasks() {
        return tasks;
    }

    public void setTasks(TreeMap<Float, Task> tasks) {
        this.tasks = tasks;
    }



    public Experiment(String path) {

        tasks = new TreeMap<Float, Task>();

        try {
            CSVReader reader = new CSVReader(new FileReader(path));
            String [] nextLine;
            int lineCount=0;
            while ((nextLine = reader.readNext()) != null) {
                if(lineCount >0) {
                    Task nextTask = new Task(nextLine);
                    tasks.put(nextTask.gameTime, nextTask);
                    System.out.println("added task " + nextTask);

                    if(condition < 0) {
                        subject = nextTask.subject;
                        condition = nextTask.condition;
                    }
                }
                lineCount++;
            }
        } catch (Exception e) {
            System.err.println("error in exp constructor " + e);
            e.printStackTrace();
        }
    }


     private String getChapterTime(float secsIn) {

        int hours = (int) secsIn / 3600;
        int remainder = (int) secsIn % 3600;
        int minutes = (int) remainder / 60;
        int seconds = (int) remainder % 60;
        int milliseconds = (int) (1000 * (secsIn - ((3600*hours) + (60*minutes) + seconds)));
        String s = String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds,milliseconds);
        return s;
    }

    private String getSRTtime(float secsIn) {

        int hours = (int) secsIn / 3600;
        int remainder = (int) secsIn % 3600;
        int minutes = (int) remainder / 60;
        int seconds = (int) remainder % 60;
         int milliseconds = (int) (1000 * (secsIn - ((3600*hours) + (60*minutes) + seconds)));
        String s = String.format("%02d:%02d:%02d,%03d", hours, minutes, seconds,milliseconds);
        return s;
    }

     public String toChapterTxt(float offset) {

        StringBuilder sb = new StringBuilder();
        float lastTime = -1.0f;
        int counter = 1;
        Iterator<Task> it = tasks.values().iterator();
        while(it.hasNext()) {
            Task nextTask = it.next();
            if(nextTask.gameTime+offset >=0) {
                float t = nextTask.gameTime+offset;
                if(t > lastTime) {
                    if(lastTime >0) {
                        //Only show align tasks!
                        if(nextTask.task==4) {
                            String ch = String.format("CHAPTER%02d", counter);
                            sb.append(ch + "=" + this.getChapterTime(lastTime) + "\n");
                            sb.append(ch + "NAME="+nextTask.taskString + "_" + nextTask.task);
                            sb.append("_Can_" + nextTask.can + "_Cone_" + nextTask.cone + "\n");
                            counter++;
                        }
                    }
                    lastTime = t;
                }
            }
        }
        lastEndTime=-1;
        return sb.toString();
    }


    public int addMilliseconds(String header, float start, float end, StringBuilder sb, int counterIn, float offset)  {
        

        //Fix up the start based on last end time
        if(start < lastEndTime) {
            if(lastEndTime > 0) start = lastEndTime;
        } else {
            if(lastEndTime > 0) start = lastEndTime;
        }

        for(float f= start; f < end; f=f + 0.1f) {
            sb.append(counterIn + "\n");
            sb.append(getSRTtime(f));
            sb.append(" --> ");

            float endMillis = f + 0.1f;
            if(endMillis > end) endMillis = end;

            sb.append(getSRTtime(endMillis));
            sb.append("\n");
            sb.append(header);
            String eTime = getSRTtime(f-offset);
            eTime = eTime.replace(",", ".");
            sb.append("exp time " + eTime);
            sb.append("\n\n");
            counterIn++;
            lastEndTime = endMillis;
        }
        

        return counterIn;

    }


    private String taskDescription(Task t) {
        StringBuilder sh = new StringBuilder();
        //Show information
        sh.append("T" + t.trial + " ");
        sh.append(t.taskString + "[" + t.task + "]\n");
        sh.append("Can " + t.can + " Cone " + t.cone + "\n");
        return sh.toString();
    }


    private int writeSpan(Span s, StringBuilder sb, int counterIn, float offset) {
        String h = taskDescription(s.task);
        counterIn = addMilliseconds(h, s.start, s.end, sb, counterIn, offset);
        return counterIn;
    }

    public String toSubtitlesSRT(float offset) {

        String header = "";
        StringBuilder sb = new StringBuilder();
        
        float spanStartTime = -1.0f;
        Task previousTask = null;
        int stCounter = 1;
        Iterator<Task> it = tasks.values().iterator();
        while(it.hasNext()) {

            //This is the next task
            Task nextTask = it.next();
            float currentTime = nextTask.gameTime+offset;

            if((previousTask !=null) && (spanStartTime >=0)) {
                //Close out the previous task
                Span tempSpan = new Span(spanStartTime, currentTime, nextTask);
                stCounter =writeSpan(tempSpan,sb,stCounter,offset);

            }
            spanStartTime=currentTime;
            previousTask = nextTask;
        }
        lastEndTime=-1;
        return sb.toString();

    }

    public String toHtml() {

        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<table border=1>");
        int counter=0;
        Iterator<Task> it = tasks.values().iterator();
        while(it.hasNext()) {
            Task t = it.next();
            if(counter==0) {

                sb.append("<tr>");
                sb.append("<td colspan=6>");
                sb.append("Participant: " + t.subject);
                sb.append("<br>Condition: " + t.condition);
                sb.append("</td>");

                sb.append("<tr>");
                sb.append("<td>Game time</td>");
                sb.append("<td>Trial</td>");
                sb.append("<td>Task</td>");
                sb.append("<td>Task Desc</td>");
                sb.append("<td>Can</td>");
                sb.append("<td>Cone</td>");
                sb.append("</tr>");

            }
            sb.append("<tr>");


            sb.append("<td><a href=" + t.gameTime + ">");
            sb.append(t.gameTime);
            sb.append("</a></td>");

            sb.append("<td>");
            sb.append(t.trial);
            sb.append("</td>");

            sb.append("<td>");
            sb.append(t.task);
            sb.append("</td>");

            sb.append("<td>");
            sb.append(t.taskString);
            sb.append("</td>");

            sb.append("<td>");
            sb.append(t.can);
            sb.append("</td>");

            sb.append("<td>");
            sb.append(t.cone);
            sb.append("</td>");
            
            sb.append("</tr>");
            counter++;
        }

        sb.append("</table>");
        sb.append("</html>");

        return sb.toString();
    }


    public class Task {

        protected Float gameTime;
        String subject;
        int condition;
        int trial;
        int task;
        String taskString;
        int can;
        int cone;

        @Override
        public String toString() {
            return "Task{" + "\ngameTime=" + gameTime + "\nsubject=" + subject + "\ncondition=" + condition + "\ntrial=" + trial + "\ntask=" + task + "\ntaskString=" + taskString + "\ncan=" + can + "\ncone=" + cone + '}';
        }




        public Task(String[] newLine) {
            gameTime = new Float(newLine[1]);
            subject = newLine[2];
            condition = Integer.parseInt(newLine[3]);
            trial = Integer.parseInt(newLine[4]);
            task =  Integer.parseInt(newLine[5]);
            taskString = newLine[6];
            can =  Integer.parseInt(newLine[9]);
            cone =  Integer.parseInt(newLine[10]);
        }
    }

    protected  class Span {
        float start;
        float end;
        Task task;


        public Span(float s, float e, Task t) {
            start=s;
            end =e;
            task =t;
        }
    }

}
