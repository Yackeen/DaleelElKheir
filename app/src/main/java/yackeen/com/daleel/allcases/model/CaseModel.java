package yackeen.com.daleel.allcases.model;

/**
 * Created by Ibrahim on 05/02/2018.
 */

public class CaseModel {

    private String id, name, image, dueDate, organization, city, governorate, category, caseType,
            caseStatue, requiredAmount, currentAmount, description, sharedLink;
    private String urgentCase;

    public CaseModel() {
    }

    public CaseModel(String id, String name, String image, String dueDate,
                     String organization, String city, String governorate, String category, String caseType,
                     String caseStatue, String requiredAmount, String currentAmount, String description, String urgentCase,
                     String sharedLink) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.dueDate = dueDate;
        this.organization = organization;
        this.city = city;
        this.governorate = governorate;
        this.category = category;
        this.caseType = caseType;
        this.caseStatue = caseStatue;
        this.requiredAmount = requiredAmount;
        this.currentAmount = currentAmount;
        this.urgentCase = urgentCase;
        this.description = description;
        this.sharedLink = sharedLink;
    }


    public String getSharedLink() {
        return sharedLink;
    }

    public void setSharedLink(String sharedLink) {
        this.sharedLink = sharedLink;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrgentCase() {
        return urgentCase;
    }

    public void setUrgentCase(String urgentCase) {
        this.urgentCase = urgentCase;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getGovernorate() {
        return governorate;
    }

    public void setGovernorate(String governorate) {
        this.governorate = governorate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCaseType() {
        return caseType;
    }

    public void setCaseType(String caseType) {
        this.caseType = caseType;
    }

    public String getCaseStatue() {
        return caseStatue;
    }

    public void setCaseStatue(String caseStatue) {
        this.caseStatue = caseStatue;
    }

    public String getRequiredAmount() {
        return requiredAmount;
    }

    public void setRequiredAmount(String requiredAmount) {
        this.requiredAmount = requiredAmount;
    }

    public String getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(String currentAmount) {
        this.currentAmount = currentAmount;
    }
}
