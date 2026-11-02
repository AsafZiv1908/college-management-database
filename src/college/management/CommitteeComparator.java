package college.management;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;

public class CommitteeComparator implements Comparator<Committee>, Serializable {

    private final int choice;
    private final MainManagement management;

    public CommitteeComparator(int choice, MainManagement management) {
        this.choice = choice;
        this.management = management;
    }

    @Override
    public int compare(Committee c1, Committee c2) {
        return Integer.compare(metric(c1), metric(c2));
    }

    private int metric(Committee c) {
        if (choice == 2) {
            int totalArticles = 0;
            for (String name : c.getLecturers()) {
                Lecturer l = management.findLecturerByName(name);
                if (l instanceof Doctor) {
                    totalArticles += ((Doctor) l).getArticles().size();
                }
            }
            return totalArticles;
        }

        return c.getLecturers().size();
    }
}
