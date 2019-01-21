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

    /**
     * The ImMiGene object instance
     */
    private ImmiGeneProject immiGeneProject;

    /**
     * StringBuilder for creating the TSV content
     */
    private StringBuilder tsvContent;

    /**
     *
     */
    private QWizardRowFactory factory;

    private Integer patientCounter;

    private Integer entityCounter;

    private SecondaryName secondaryName;

    private Integer experimentCounter;


    public ImmiGeneTranslator(ImmiGeneProject immiGeneProject){
        this.immiGeneProject = immiGeneProject;
        this.tsvContent = new StringBuilder();
        this.experimentCounter=1;
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
            this.factory = new QWizardRowFactory(immiGeneProject.projectID);
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
        entity.setExperiment(String.format("%s%s%s",immiGeneProject.projectID, 'E', 1));
        entity.setEntityNumber(entityCounter, immiGeneProject.projectID);
        entity.setSecondaryName(secondaryName.toEntityString());

        entity.setOrganismId(immiGeneProject.organismID.toString());

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
        bioSample.setExperiment(String.format("%s%s%s",immiGeneProject.projectID, 'E', 2));

        for(Sample sample : sampleList){
            secondaryName.setTissue(sample.id);
            secondaryName.setTimepoints(sample.timepoints);
            for(String timepoint : sample.timepoints){

                bioSample.setConditionOne(timepoint);
                for (int aliquot = 1; aliquot<= sample.aliquots; aliquot++){
                    // Set Aliquot
                    //secondaryName.setSampleAliquot(0);
                    // Set bioSample fields
                    bioSample.setSecondaryName(secondaryName.toSampleString());
                    bioSample.setPrimaryTissue(sample.tissue);
                    bioSample.setConditionTwo(sample.tissue);
                    // Test print
                    System.out.println(bioSample.toString());
                    tsvContent.append(String.format("%s%n", bioSample.toString()));

                    AbstractQWizardRow testSample = factory.getWizardRow(RowTypes.TEST_SAMPLE);
                    AbstractQWizardRow singleRun = factory.getWizardRow(RowTypes.SINGLE_SAMPLE_RUN);

                    if (aliquot > 1)
                        continue;

                    for(Experiment experiment : sample.experiments){
                        testSample.setConditionOne(timepoint);
                        testSample.setConditionTwo(sample.tissue);
                        // Set experiment and aliquot
                        for (int expAliquot = 1; expAliquot<=experiment.aliquots; expAliquot++){
                            testSample.setParent(bioSample.getEntity());
                            testSample.setSpace(immiGeneProject.space);
                            testSample.setQSampleType(experiment.experiment);
                            switch (experiment.experiment) {
                                case "SMALLMOLECULES": testSample.setExperiment(String.format("%s%s%s",immiGeneProject.projectID, 'E', 3));
                                                        break;
                                case "PROTEINS": testSample.setExperiment(String.format("%s%s%s",immiGeneProject.projectID, 'E', 4));
                                                 break;
                                case "DNA": testSample.setExperiment(String.format("%s%s%s",immiGeneProject.projectID, 'E', 5));
                                            break;
                                case "RNA": testSample.setExperiment(String.format("%s%s%s",immiGeneProject.projectID, 'E', 6));
                                            break;
                            }
                            //testSample.setExperiment(bioSample.getExperiment());
                            secondaryName.setExtractAliquot(expAliquot);
                            secondaryName.setExtractType(experiment.id);

                            testSample.setSecondaryName(secondaryName.toString());

                            tsvContent.append(String.format("%s%n", testSample.toString()));
                            System.out.println(testSample.toString());


                            // Trigger next barcode creation
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


    /**
     * Create a SINGLE SAMPLE RUN instance for the QWizard
     * @param testSample a TEST_SAMPLE object
     */
    private void createSingleSampleRun(AbstractQWizardRow testSample){
        AbstractQWizardRow singleSampleRun = factory.getWizardRow(RowTypes.SINGLE_SAMPLE_RUN);
        singleSampleRun.setEntityNumber();
        singleSampleRun.setSpace(immiGeneProject.space);
        singleSampleRun.setSecondaryName(secondaryName.toString());
        singleSampleRun.setParent(testSample.getEntity());

        tsvContent.append(String.format("%s%n", singleSampleRun.toString()));
        System.out.println(String.format("%s", singleSampleRun.toString()));
    }


}
