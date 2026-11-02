package college.management;

import java.util.List;
import java.util.Objects;

public class Committee implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private List<String> lecturers;
    private String chairman;
    private Lecturer.Degree requiredDegree;

    public Committee(String name,
                     List<String> lecturers,
                     String chairman,
                     Lecturer.Degree requiredDegree) {
        this.name = name;
        this.lecturers = lecturers;
        this.chairman = chairman;
        this.requiredDegree = requiredDegree;
    }

    public String getName() {
        return name;
    }

    public List<String> getLecturers() {
        return lecturers;
    }

    public String getChairman() {
        return chairman;
    }

    public Lecturer.Degree getRequiredDegree() {
        return requiredDegree;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLecturers(List<String> lecturers) {
        this.lecturers = lecturers;
    }

    public void setChairman(String chairman) {
        this.chairman = chairman;
    }

    public void setRequiredDegree(Lecturer.Degree requiredDegree) {
        this.requiredDegree = requiredDegree;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Committee)) return false;
        Committee that = (Committee) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(lecturers, that.lecturers) &&
                Objects.equals(chairman, that.chairman) &&
                requiredDegree == that.requiredDegree;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, lecturers, chairman, requiredDegree);
    }

    @Override
    public String toString() {
        String lecturersOutput = lecturers == null || lecturers.isEmpty()
                ? "No Members"
                : String.join(", ", lecturers);

        return "Name: " + name +
                ", Lecturers: " + lecturersOutput +
                ", Chairman: " + chairman +
                ", Required Degree: " + requiredDegree;
    }
}
