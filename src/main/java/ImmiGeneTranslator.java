import models.qwizard.AbstractQWizardRow;
import models.qwizard.QWizardRowFactory;
import models.qwizard.RowTypes;
import models.qwizard.WizardHeader;
import namebuilder.SecondaryName;
import org.qbic.collections.Experiment;
import org.qbic.collections.ImmiGenePatient;
import org.qbic.collections.ImmiGeneProject;
import org.qbic.collections.Sample;

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

    private StringBuilder tsvContent;

    private QWizardRowFactory factory;

    private Integer patientCounter;

    private Integer entityCounter;

    private SecondaryName secondaryName;


    public ImmiGeneTranslator(ImmiGeneProject immiGeneProject){
        this.immiGeneProject = immiGeneProject;
        this.tsvContent = new StringBuilder();
    }

    @Override
    public void createTSV(Path outputFile) throws Exception{
        File parentDir = outputFile.getParent().toFile();
        if (!parentDir.exists()){
            throw new FileNotFoundException(String.format("The parent directory %s does not exist",
                    parentDir.toString()));
        }

        try (BufferedWriter writer = Files.newBufferedWriter(outputFile)){

            // Init QWizard row factory
            this.factory = new QWizardRowFactory(immiGeneProject.space);
            // Create empty secondary name instance
            this.secondaryName = new SecondaryName();

            this.patientCounter = 1;
            this.entityCounter = 1;

            tsvContent.append(String.format("%s%n",WizardHeader.getHeader()));


            // Iterate over all patients and create donor + recipient
            for(int i=1; i<=immiGeneProject.numberOfPatients; i++){
                // TODO implement space type object
                createPatient(immiGeneProject.donor);
                entityCounter += 1;
                createPatient(immiGeneProject.recepient);
                entityCounter += 1;
                patientCounter += 1;
                secondaryName.setEntityCounter(entityCounter);
            }

            writer.write(tsvContent.toString());

        } catch (IOException e){
            throw new IOException(String.format("IOException: %s%n", e));
        }

    }

    /**
     * Creates patient object in openBis representation
     * @param patient The patient object
     */
    private void createPatient(ImmiGenePatient patient){
        // Init the secondary name
        secondaryName.setEntityID(patient.id);
        secondaryName.setEntityCounter(patientCounter);

        AbstractQWizardRow entity = factory.getWizardRow(RowTypes.ENTITY);
        entity.setSpace(immiGeneProject.space);
        entity.setExperiment(String.format("%s%s%s",immiGeneProject.space, 'E', patientCounter));
        entity.setEntityNumber(entityCounter);
        entity.setSecondaryName(secondaryName.toEntityString());

        entity.setOrganismId("10900");

        System.out.println(entity.toString());

        tsvContent.append(String.format("%s%n",entity.toString()));

        // Create the samples
        createSamples(patient.samples, entity);
    }

    /**
     * Creates a openBis BioSample representation
     * @param sampleList A list with Samples
     */
    private void createSamples(Sample[] sampleList, AbstractQWizardRow entity){
        AbstractQWizardRow bioSample = factory.getWizardRow(RowTypes.BIO_SAMPLE);

        bioSample.setEntityNumber();
        bioSample.setSpace(immiGeneProject.space);
        bioSample.setParent(entity.getEntity());
        bioSample.setExperiment(entity.getExperiment());

        for(Sample sample : sampleList){
            secondaryName.setTissue(sample.id);
            secondaryName.setTimepoints(sample.timepoints);
            for(String timepoint : sample.timepoints){
                for (int aliquot = 1; aliquot<= sample.aliquots; aliquot++){
                    // Set Aliquot
                    secondaryName.setSampleAliquot(aliquot);
                    // Set bioSample fields
                    bioSample.setSecondaryName(secondaryName.toSampleString());
                    bioSample.setPrimaryTissue(sample.tissue);
                    // Test print
                    System.out.println(bioSample.toString());
                    tsvContent.append(String.format("%s%n", bioSample.toString()));

                    AbstractQWizardRow testSample = factory.getWizardRow(RowTypes.TEST_SAMPLE);
                    AbstractQWizardRow singleRun = factory.getWizardRow(RowTypes.SINGLE_SAMPLE_RUN);

                    for(Experiment experiment : sample.experiments){
                        // Set experiment and aliquot
                        for (int expAliquot = 1; expAliquot<=experiment.aliquots; expAliquot++){
                            testSample.setParent(bioSample.getEntity());
                            testSample.setSpace(immiGeneProject.space);
                            testSample.setQSampleType(experiment.experiment);
                            secondaryName.setExtractAliquot(expAliquot);
                            secondaryName.setExtractType(experiment.id);

                            testSample.setSecondaryName(secondaryName.toString());

                            tsvContent.append(String.format("%s%n", testSample.toString()));
                            System.out.println(testSample.toString());

                            testSample.nextID();
                        }
                    }
                    // Trigger next barcode creation
                    bioSample.nextID();
                }

                secondaryName.nextTimePoint();
            }
        }
    }


}
