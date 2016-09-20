import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import org.qbic.collections.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Paths;

/**
 * Created by sven1103 on 6/09/16.
 */
public class Main {
    public static void main(String[] args){

        if (args.length != 1){
            System.err.println("Please provide an YAML config file.");
            System.exit(1);
        }

        ImmiGeneProject project = null;

        try{
            System.out.println("Trying to open config file");
            YamlReader config = new YamlReader(new FileReader(args[0]));
            System.out.println("Parsing the YAML file...");
            project = config.read(ImmiGeneProject.class);
            //System.out.println(project.donor.samples[1].experiments[0].experiment);
        } catch (FileNotFoundException e){
            System.err.println("The config file could not be found!");
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (YamlException e){
            System.err.println("Error during YAML parsing.");
            e.printStackTrace();
            System.exit(1);
        } finally{
            System.out.println("Parsing finished.");
        }

        IProjectTranslator translator = new ImmiGeneTranslator(project);

        try{
            translator.createTSV(Paths.get("/home/sven1103/Downloads/test.tsv"));
        } catch (Exception e){
            System.err.println("Could not create the TSV file because of:");
            if (e.getMessage() == null){
                e.printStackTrace();
            } else {
                System.err.println(e.getMessage());
            }
            System.exit(1);
        } finally {
            System.out.println("Exporting to QWizard-like tsv file done.");
        }

    }

}
