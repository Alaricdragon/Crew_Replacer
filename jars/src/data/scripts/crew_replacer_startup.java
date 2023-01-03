package data.scripts;
import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.thoughtworks.xstream.XStream;
import data.scripts.crews.marine;
import org.apache.log4j.Logger;

/*
 */
public class crew_replacer_startup extends BaseModPlugin {
    @Override
    public void onApplicationLoad() {
        startup2();
    }

    @Override
    public void onGameLoad(boolean newGame) {
        crewReplacer_Main.organizePriority();
        super.onGameLoad(newGame);
    }
    @Override
    public void configureXStream(XStream x) {

    }
        private static void startup2(){
        crewReplacer_Job tempJob = crewReplacer_Main.getJob("salvage_crew");
        tempJob.addNewCrew("crew",1,10);

        tempJob = crewReplacer_Main.getJob("salvage_heavyMachinery");
        tempJob.addNewCrew("heavy_machinery",1,10);

        /*tempJob = crewReplacer_Main.getJob("fleet");
        tempJob.addNewCrew("crew",1,10);*/

        /*tempJob = crewReplacer_Main.getJob("survey_crew");
        tempJob.addNewCrew("crew",1,10);*/

        tempJob = crewReplacer_Main.getJob("raiding_marines");
        marine tempcrew = new marine();
        tempcrew.crewPower = 1;
        tempcrew.crewPriority = 10;
        tempcrew.name = "marines";
        tempJob.addCrew(tempcrew);
        //supplyDemandChangeInit();


        tempJob = crewReplacer_Main.getJob("Mission_hijack_marines");
        tempJob.addNewCrew("marines",1,10);
        tempJob.addNewCrew("crew",1,10);



        tempJob = crewReplacer_Main.getJob("CoronalHyperShunt_repair_Metals");
        tempJob.addNewCrew("metals",1,10);
        tempJob = crewReplacer_Main.getJob("CoronalHyperShunt_repair_RareMetals");
        tempJob.addNewCrew("rare_metals",1,10);
        tempJob = crewReplacer_Main.getJob("CoronalHyperShunt_repair_Crew");
        tempJob.addNewCrew("crew",1,10);

        /*
        tempJob = crewReplacer_Main.getJob("CoronalHyperShunt_repair_Crew");
        tempJob.addNewCrew("supplies",1,10);*/
    }
}