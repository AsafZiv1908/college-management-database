package college.management;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Lecturer implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    public enum Degree { FIRST, SECOND, DR, PROFESSOR }

    private String id;
    private String name;
    private String department;
    private int wage;
    private List<String> committee;
    private Degree degree;
    private String degreeName;

    public Lecturer(String id, String name, String department, int wage, List<String> committee, Degree degree, String degreeName) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.wage = wage;
        this.committee = committee;
        this.degree = degree;
        this.degreeName = degreeName;
    }

    public Lecturer(String id, String name, String department, List<String> committee) {
        this(id, name, department, 0, committee, null, null);
    }

    public Lecturer(String name, String id, Degree degree, String degreeName, int wage, String department) {
        this(id, name, department, wage, new ArrayList<>(), degree, degreeName);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }

    public int getWage() {
        return wage;
    }

    public List<String> getCommittee() {
        return committee;
    }

    public Degree getDegree() {
        return degree;
    }

    public String getDegreeName() {
        return degreeName;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setWage(int wage) {
        this.wage = wage;
    }

    public void setCommittee(List<String> committee) {
        this.committee = committee;
    }

    public void setDegree(Degree degree) {
        this.degree = degree;
    }

    public void setDegreeName(String degreeName) {
        this.degreeName = degreeName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Lecturer)) return false;
        Lecturer that = (Lecturer) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Name: " + name +
                ", ID: " + id +
                ", Degree: " + degree +
                ", Degree Name: " + degreeName +
                ", Wage: " + wage + "₪" +
                ", Department: " + (department != null ? department : "No Department Assigned") +
                ", Committees: " + committee;
    }
}