package college.management;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Professor extends Lecturer implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private String institute;
    private List<String> articles;

    public Professor(String id, String name, String institute, List<String> articles) {
        super(id, name, null, new ArrayList<>());
        this.institute = institute;
        this.articles = articles;
    }

    public String getInstitute() {
        return institute;
    }

    public void setInstitute(String institute) {
        this.institute = institute;
    }

    public List<String> getArticles() {
        return articles;
    }

    public void setArticles(List<String> articles) {
        this.articles = articles;
    }

    @Override
    public String toString() {
        String articlesOutput;
        if (articles == null || articles.isEmpty()) {
            articlesOutput = "No articles published";
        } else {
            articlesOutput = String.join(", ", articles);
        }

        return super.toString() +
                ", Institute: " + institute +
                ", Articles: " + articlesOutput;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Professor)) return false;
        if (!super.equals(o)) return false;
        Professor that = (Professor) o;
        return Objects.equals(institute, that.institute) &&
                Objects.equals(articles, that.articles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), institute, articles);
    }
}
