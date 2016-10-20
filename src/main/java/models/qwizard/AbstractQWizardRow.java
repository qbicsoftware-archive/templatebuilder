package models.qwizard;

import models.barcode.BarcodeProducer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by fillinger on 11/22/15.
 * @author Sven Fillinger, sven.fillinger@student.uni-tuebingen.de
 */
public abstract class AbstractQWizardRow{

    private final List<String> columnFields;
    private BarcodeProducer barcodeFactory;

    public AbstractQWizardRow(){
        this.columnFields = Arrays.asList(new String[14]).stream().map(value -> "").collect(Collectors.toList());
    }

    public AbstractQWizardRow(BarcodeProducer barcodeFactory){
        this.columnFields = Arrays.asList(new String[14]).stream().map(value -> "").collect(Collectors.toList());
        this.barcodeFactory = barcodeFactory;
    }

    public void setEntityNumber() {
        this.columnFields.set(0, barcodeFactory.getBarcode());
    }

    public void setEntityNumber(int number, String project) {
        this.columnFields.set(0, String.format("%sENTITY-%d", project, number));
    }

    public void setIdentifier(String identifier){
        this.columnFields.set(0, identifier);
    }

    public String getEntity(){
        return this.columnFields.get(0);
    }

    public void setSampleType(String string){
        this.columnFields.set(1, string);
    }

    public void setSpace(String space) {
        this.columnFields.set(2, space);
    }

    public String getSpace() {return this.columnFields.get(2);}

    public void setExperiment(String string){
        this.columnFields.set(3, string);
    }

    public void setSecondaryName(String string) {
        this.columnFields.set(4, string);
    }

    public void setParent(String string){
        this.columnFields.set(5, string);
    }

    public String getParent(){
        return this.columnFields.get(5);
    }

    public void setPrimaryTissue(String string){
        this.columnFields.set(6, string);
    }

    public String getPrimaryTissue(){return this.columnFields.get(6);}

    public void setTissueDetailed(String string){
        this.columnFields.set(7, string);
    }

    public String getTissueDetailed(){
        return this.columnFields.get(7);
    }

    public void setOrganismId(String string){
        this.columnFields.set(9, string);
    }

    public void setQSampleType(String string){
        this.columnFields.set(10, string);
    }

    public String getQSampleType(){
        return this.columnFields.get(10);
    }

    public String getExperiment(){return this.columnFields.get(3);}

    public void nextID(){this.setIdentifier(this.barcodeFactory.getBarcode());}

    public String toString(){
        return String.join("\t", columnFields);
    }

    public void setConditionOne(String condition){this.columnFields.set(12, condition);}

    public void setConditionTwo(String condition){this.columnFields.set(13, condition);}

}
