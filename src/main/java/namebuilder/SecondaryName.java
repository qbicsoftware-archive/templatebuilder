package namebuilder;

import java.lang.reflect.Field;

/**
 * Created by sven1103 on 9/09/16.
 */
public class SecondaryName {

    /**
     * All the field variables for the secondary name
     */
    private String entityID;
    private Integer entityCounter;
    private String[] timepoints;
    private String tissue;
    private Integer sampleAliquot;
    private String extractType;
    private Integer extractAliquot;
    private int currentTimePointIndex;

    /**
     * Default constructor
      */
    public SecondaryName(){}

    /**
     * Increase the timepoint by 1
     */
    public void nextTimePoint(){
        if (currentTimePointIndex >= timepoints.length-1){
            currentTimePointIndex = 0;
        } else{
            currentTimePointIndex++;
        }
    }

    /**
     * Retrieve the current time point
     * @return The current time point
     */
    public String getCurrentTimePoint(){
        if (timepoints == null){
            return "";
        }
        return this.timepoints[currentTimePointIndex];
    }

     /**
     * Increase the sample aliquot number
     */
    public int nextSampleAliquot(){
        if (sampleAliquot != null){
            sampleAliquot++;
            return sampleAliquot;
        } else
            return -1;
    }

    /**
     * Increase the extract aliquot number
     */
    public int nextExtractAliquot(){
        if (extractAliquot != null) {
            extractAliquot++;
            return extractAliquot;
        } else
            return -1;
    }

    /**
     * Get the entity identifier
     * @return The entityID
     */
    public String getEntityID() {
        if (entityID == null){
            return "";
        }
        return entityID;
    }

    /**
     * Set the entity identifier
     * @param entityID: The identifier as String
     */
    public void setEntityID(String entityID) {
        this.entityID = entityID;
    }

    /**
     * Get the entity counter
     * @return The current entity counter
     */
    public int getEntityCounter() {
        if (entityCounter == null){
            return -100;
        }
        return entityCounter;
    }

    /**
     * Set the entity counter
     * @param entityCounter The start of the entity counter
     */
    public void setEntityCounter(int entityCounter) {
        this.entityCounter = entityCounter;
    }

    /**
     * Get the list of time points
     * @return An array of time points as String
     */
    public String[] getTimepoint() {
        return timepoints;
    }

    /**
     * Set the time points
     * @param timepoint Expects an array of Strings as time points
     */
    public void setTimepoints(String[] timepoint) {
        this.timepoints = timepoint;
        this.currentTimePointIndex = 0;
    }

    /**
     * Get the sample tissue
     * @return The sample tissue type
     */
    public String getTissue() {
        return tissue;
    }

    /**
     * The sample tissue
     * @param tissue:  Tissue type as String
     */
    public void setTissue(String tissue) {
        this.tissue = tissue;
    }

    /**
     * Get the current aliquot number
     * @return The aliquot number (int)
     */
    public int getSampleAliquot() {
        return sampleAliquot;
    }

    /**
     * Set the sample aliquot
     * @param sampleAliquot An number (int) for the sample aliquot
     */
    public void setSampleAliquot(int sampleAliquot) {
        this.sampleAliquot = sampleAliquot;
    }

    /**
     * Get the extract type, like DNA, RNA etc.
     * @return The extract type
     */
    public String getExtractType() {
        return extractType;
    }

    /**
     * Set the extract type
     * @param extractType An extract type
     */
    public void setExtractType(String extractType) {
        this.extractType = extractType;
    }

    /**
     * Get the current extract aliquot number
     * @return Current extract aliquot number
     */
    public int getExtractAliquot() {
        return extractAliquot;
    }

    /**
     * Set the extract aliquot number
     * @param extractAliquot The extract aliquot number
     */
    public void setExtractAliquot(int extractAliquot) {
        this.extractAliquot = extractAliquot;
    }

    //TODO implement toString() function
    @Override
    public String toString() {
        StringBuilder sampleName = new StringBuilder();
        for (Field field : this.getClass().getDeclaredFields()) {
            try {
                Object fieldValue = field.get(this);
                if (fieldValue != null && !field.getType().isPrimitive()){
                    if (field.getType().isArray())
                        sampleName.append(((String [])field.get(this))[currentTimePointIndex]);
                    else
                        sampleName.append(field.get(this));
                    sampleName.append(':');
                }

            } catch(Exception e){
                continue;
            }
        }
        sampleName.deleteCharAt(sampleName.length()-1);
        return sampleName.toString();
    }

    /**
     * Short version of the secondary name:
     * [entityID]:[entityNumber]
     * @return The ready formatted String
     */
    public String toEntityString() {
        StringBuilder sampleName = new StringBuilder();
        sampleName.append(this.entityID).append(':').append(this.entityCounter);
        return sampleName.toString();
    }


    /**
     * Short version of the secondary name:
     * [entityID]:[entityNumber]:[timepoint]:[tissue]:[aliquot]
     * @return The ready formatted String
     */
    public String toSampleString() {
        StringBuilder sampleName = new StringBuilder();
        try{
            sampleName.append(toEntityString()).append(':').append(this.getCurrentTimePoint()).append(':')
                    .append(this.tissue).append(':').append(this.getSampleAliquot());
        } catch (Exception e){
            sampleName = new StringBuilder();
            sampleName.append(toEntityString()).append(':').append(this.getCurrentTimePoint()).append(':')
                    .append(this.tissue);
        }
        return sampleName.toString();
    }




}
