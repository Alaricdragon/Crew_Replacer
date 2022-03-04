package data.scripts;

import java.util.ArrayList;

public class testing {
    //crew_replacer_startup.crew_replacer.addCrewType("");
    public static void main(String[] args) {
        /*
        //crew_replacer = new crew_replacer();


        //throw new ClassCastException("Hello!");
        //float things = crew_replacer.getCrew("");
        crewReplacer_Job job = crewReplacer_Main.getJob("the job of life");
        float aaa = 0;
        int[] tests = {
                1,45,7,8,4,2
        };
        ArrayList<Float> input = new ArrayList<Float>();
        for(int a = 0; a < 40; a++) {
            float random = tests[(int) (Math.random() * tests.length)];
            input.add(random);
            job.addNewCrew("crew" + a,aaa, random, aaa, aaa, true);
            System.out.println(a + ": " + random);
        }
        System.out.println("organizeing data...");
        job.organizePriority();
        System.out.println("done organizeing data...");
        displayPriority(job,input);*/
        crewReplacer_Job job = new crewReplacer_Job();
        float[] randoms = {20/*,40,60,80,200,350,40*/};
        //float[] randoms = {5,3000};
        /*float[] randoms2 = {0.1f, 11};
        float[] randoms3 = {5000,50};
        ArrayList<Float> input = new ArrayList<Float>();
        ArrayList<Float> input2 = new ArrayList<Float>();
        float power = 375;
        for(int a = 0 ;a < 2; a++){
            float random = randoms[(int) (Math.random() * randoms.length)];
            //float random = randoms[a];
            //float random2 = randoms2[(int) (Math.random() * randoms2.length)];
            float random2 = randoms2[a];
            //float random3 = randoms3[(int) (Math.random() * randoms3.length)];
            float random3 = randoms3[a];
            //float random2  = 5;
            crewReplacer_Crew temps = new crewReplacer_Crew();
            temps.name = "name" + a;
            temps.crewPower = random2;
            temps.crewPriority = random;
            //temps.tempcrew = random3;
            job.addCrew(temps);
            //job.addNewCrew("name" + a,random,random2,0,0,true);
            input.add(random);
            input2.add(random2);
        }
        job.organizePriority();
        float crewPoweToUse = 500;
        ArrayList<Float> output = job.getCrewForJob(crewPoweToUse);
        float units_used = 0;
        float unit_power_used = 0;
        float Possablecrewused = 0;
        float crewused = 0;
        float crewpowerused = 0;
        for(int a = 0; a < output.size(); a++) {
            //System.out.println("deploying " + output.get(a) + " of crew type " + a + " of the possable " + job.Crews.get(a).tempcrew + " for a power of " + job.Crews.get(a).crewPower * output.get(a));
            //Possablecrewused += job.Crews.get(a).tempcrew;
            crewused += output.get(a);
            crewpowerused += job.Crews.get(a).crewPower * output.get(a);
        }
        System.out.println("used an " + crewused + " out of the possable " + Possablecrewused + " crew");
        System.out.println("used an " + crewpowerused + " out of the required " + crewPoweToUse + " crewpower");*/
    }
}
