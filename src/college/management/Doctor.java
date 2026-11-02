package college.management;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Doctor extends Lecturer implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private List<String> articles;
    private int numOfArticles;

    public Doctor(String name,
                  String id,
                  Degree degree,
                  String degreeName,
                  int wage,
                  String department) {
        super(name, id, degree, degreeName, wage, department);
        this.articles = new ArrayList<>();
        this.numOfArticles = 0;
    }

    public List<String> getArticles() {
        return articles;
    }

    public void setArticles(List<String> articles) {
        this.articles = articles;
        this.numOfArticles = articles == null ? 0 : articles.size();
    }

    @Override
    public String toString() {
        String articlesList = articles.isEmpty()
                ? "No articles published"
                : String.join(", ", articles);
        return super.toString() + ", Articles: " + articlesList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Doctor)) return false;
        if (!super.equals(o)) return false;
        Doctor doctor = (Doctor) o;
        return numOfArticles == doctor.numOfArticles &&
                Objects.equals(articles, doctor.articles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), articles, numOfArticles);
    }
}
