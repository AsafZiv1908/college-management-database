package college.management;

import java.util.List;
import java.util.Objects;

public class Department implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private int numOfStudents;
    private List<String> lecturers;

    public Department(String name, int numOfStudents, List<String> lecturers) {
        this.name = name;
        this.numOfStudents = numOfStudents;
        this.lecturers = lecturers;
    }

    public String getName()               { return name; }
    public int    getNumOfStudents()      { return numOfStudents; }
    public List<String> getLecturers()    { return lecturers; }

    public void setLecturers(List<String> lecturers) {
        this.lecturers = lecturers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Department)) return false;
        Department that = (Department) o;
        return Objects.equals(name, that.name);
    }
    @Override
    public int hashCode() { return Objects.hash(name); }
    @Override
    public String toString() {
        return "Department{" +
                "name='" + name + '\'' +
                ", numOfStudents=" + numOfStudents +
                ", lecturers=" + lecturers +
                '}';
    }
}
