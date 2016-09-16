import org.qbic.collections.ImmiGeneProject;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by sven1103 on 16/09/16.
 */
public class ImmiGeneTranslator implements IProjectTranslator {

    private ImmiGeneProject immiGeneProject;

    public ImmiGeneTranslator(ImmiGeneProject immiGeneProject){
        this.immiGeneProject = immiGeneProject;
    }

    @Override
    public void createTSV(Path outputFile) throws Exception{
        File parentDir = outputFile.getParent().toFile();
        if (!parentDir.exists()){
            throw new FileNotFoundException(String.format("The parent directory %s does not exist",
                    parentDir.toString()));
        }

        try (BufferedWriter writer = Files.newBufferedWriter(outputFile)){
            // TODO implement the qwizard rows

        } catch (IOException e){
            throw new IOException(String.format("IOException: %s%n", e));
        }

    }


}
