package college.management;

import java.io.Serializable;
import java.util.Comparator;

public class DrProfessorCompare implements Comparator<Doctor>, Serializable {

    @Override
    public int compare(Doctor d1, Doctor d2) {
        int articles1 = d1.getArticles().size();
        int articles2 = d2.getArticles().size();

        return Integer.compare(articles1, articles2);
    }
}
