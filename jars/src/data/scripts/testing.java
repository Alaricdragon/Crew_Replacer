package data.scripts;

import java.util.ArrayList;

public class testing {
    public static void main(String[] args) {
        crewReplacer_Job job = new crewReplacer_Job();
        for(int a = 0; a < 50; a++) {
            int b = (int) (Math.random() * 40);
            job.addNewCrew("name" + a, (float) Math.random() * 25,b);
            System.out.println(a +": "+"priority of crew: " + b);
        }
        job.organizePriority();
        for(int a = 0; a < job.crewPriority.size(); a++){
            System.out.println(a + ": " + job.crewPriority.get(a) + ", priority: ");
            for(int b = 0; b < job.crewPriority.get(a).size(); b++){
                System.out.println("    " + "" + job.Crews.get(job.crewPriority.get(a).get(b)).crewPriority);
            }
        }
    }
}
