package college.management;

import java.sql.Connection;
import java.sql.SQLException;
import college.management.exceptions.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class MainManagement {
    private String collegeName;
    private Connection connection;
    private final Scanner scan = new Scanner(System.in);

    public MainManagement(String collegeName) {
        this.collegeName = collegeName;
        try {
            this.connection = DatabaseConnections.getConnection();
            System.out.println("Successfully connected to PostgreSQL database!");
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
        }
    }

    public void addLecturer() throws InvalidInputException {
        System.out.println("Enter Lecturer's full name:");
        String name = scan.nextLine();

        System.out.println("Enter Lecturer's ID:");
        String id = scan.nextLine();

        String checkSql = "SELECT COUNT(*) FROM lecturers WHERE id = ?";
        try (java.sql.PreparedStatement pstmt = connection.prepareStatement(checkSql)) {
            pstmt.setString(1, id);
            java.sql.ResultSet rs = pstmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                throw new InvalidInputException("Lecturer ID already exists in the database.");
            }
        } catch (java.sql.SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            return;
        }

        Lecturer.Degree degree = null;
        while (degree == null) {
            System.out.println("Enter Lecturer's Degree (FIRST, SECOND, DR, PROFESSOR):");
            String inp = scan.nextLine().toUpperCase();
            try { degree = Lecturer.Degree.valueOf(inp); }
            catch (IllegalArgumentException e) { throw new InvalidInputException("Invalid degree value."); }
        }

        System.out.println("Enter Lecturer's Degree Name:");
        String degreeName = scan.nextLine();

        System.out.println("Enter Lecturer's wage:");
        int wage = scan.nextInt();
        if (wage <= 0) throw new InvalidInputException("Wage must be positive.");
        scan.nextLine();

        System.out.println("Enter Lecturer's Department (or leave empty):");
        String department = scan.nextLine();
        if (department.isEmpty()) department = null;

        String insertLecSql = "INSERT INTO lecturers (id, name, wage, degree, degree_name, department_name) VALUES (?, ?, ?, ?, ?, ?)";
        try (java.sql.PreparedStatement pstmt = connection.prepareStatement(insertLecSql)) {
            pstmt.setString(1, id);
            pstmt.setString(2, name);
            pstmt.setInt(3, wage);
            pstmt.setString(4, degree.toString());
            pstmt.setString(5, degreeName);
            pstmt.setString(6, department);
            pstmt.executeUpdate();
        } catch (java.sql.SQLException e) {
            System.out.println("Database error adding lecturer: " + e.getMessage());
            return;
        }

        if (degree == Lecturer.Degree.PROFESSOR) {
            System.out.println("Enter Professorship Institute:");
            String inst = scan.nextLine();
            String insertProfSql = "INSERT INTO professors (lecturer_id, institute) VALUES (?, ?)";
            try (java.sql.PreparedStatement pstmt = connection.prepareStatement(insertProfSql)) {
                pstmt.setString(1, id);
                pstmt.setString(2, inst);
                pstmt.executeUpdate();
            } catch (java.sql.SQLException e) {
                System.out.println("Database error adding professor details: " + e.getMessage());
            }
            addArticlesLoopDB(id);
        } else if (degree == Lecturer.Degree.DR) {
            addArticlesLoopDB(id);
        }

        System.out.println("Lecturer added successfully!");
    }

    private void addArticlesLoopDB(String lecturerId) {
        String a;
        do {
            System.out.println("Add article? (yes/no)");
            a = scan.nextLine().toLowerCase();
            if (a.equals("yes")) {
                System.out.println("Enter title:");
                String title = scan.nextLine();
                String sql = "INSERT INTO articles (lecturer_id, title) VALUES (?, ?)";
                try (java.sql.PreparedStatement pstmt = connection.prepareStatement(sql)) {
                    pstmt.setString(1, lecturerId);
                    pstmt.setString(2, title);
                    pstmt.executeUpdate();
                } catch (java.sql.SQLException e) {
                    System.out.println("Failed to add article: " + e.getMessage());
                }
            } else if (!a.equals("no")) {
                System.out.println("Enter yes/no");
            }
        } while (!a.equals("no"));
    }

    public void addDepartment() throws DuplicateCommitteeException {
        System.out.println("Enter Department Name:");
        String name = scan.nextLine();

        String checkSql = "SELECT COUNT(*) FROM departments WHERE name = ?";
        try (java.sql.PreparedStatement pstmt = connection.prepareStatement(checkSql)) {
            pstmt.setString(1, name);
            java.sql.ResultSet rs = pstmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                throw new DuplicateCommitteeException("Department already exists in the database.");
            }
        } catch (java.sql.SQLException e) {
            System.out.println("Database error during check: " + e.getMessage());
            return;
        }

        System.out.println("Enter number of students:");
        int n = scan.nextInt();
        scan.nextLine();

        String insertSql = "INSERT INTO departments (name, num_of_students) VALUES (?, ?)";
        try (java.sql.PreparedStatement pstmt = connection.prepareStatement(insertSql)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, n);
            pstmt.executeUpdate();
            System.out.println("Department added successfully to the database!");
        } catch (java.sql.SQLException e) {
            System.out.println("Failed to add department: " + e.getMessage());
        }
    }

    public void addCommittee() throws NoLecturerFoundException, InvalidDegreeException, DuplicateCommitteeException, InvalidInputException {
        System.out.println("Enter Committee Name:");
        String name = scan.nextLine();

        String checkExistSql = "SELECT COUNT(*) FROM committees WHERE name = ?";
        try (java.sql.PreparedStatement pstmt = connection.prepareStatement(checkExistSql)) {
            pstmt.setString(1, name);
            java.sql.ResultSet rs = pstmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                throw new DuplicateCommitteeException("Committee exists.");
            }
        } catch (java.sql.SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            return;
        }

        System.out.println("Enter Chairman ID:");
        String cid = scan.nextLine();

        String checkChairSql = "SELECT degree FROM lecturers WHERE id = ?";
        try (java.sql.PreparedStatement pstmt = connection.prepareStatement(checkChairSql)) {
            pstmt.setString(1, cid);
            java.sql.ResultSet rs = pstmt.executeQuery();
            if (!rs.next()) {
                throw new NoLecturerFoundException("Lecturer not found.");
            }
            String deg = rs.getString("degree");
            if (!deg.equals("DR") && !deg.equals("PROFESSOR")) {
                throw new InvalidDegreeException("Chairman must be DR/PROFESSOR.");
            }
        } catch (java.sql.SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            return;
        }

        System.out.println("Enter required degree (FIRST, SECOND, DR, PROFESSOR):");
        Lecturer.Degree req;
        try {
            req = Lecturer.Degree.valueOf(scan.nextLine().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException("Invalid degree.");
        }

        String insertSql = "INSERT INTO committees (name, chairman_id, required_degree) VALUES (?, ?, ?)";
        try (java.sql.PreparedStatement pstmt = connection.prepareStatement(insertSql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, cid);
            pstmt.setString(3, req.toString());
            pstmt.executeUpdate();
            System.out.println("Committee added.");
        } catch (java.sql.SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    public void addMemberToCommittee() throws NoLecturerFoundException, NoCommitteesFoundException, NoChairmanFoundException, InvalidDegreeException {
        System.out.println("Enter Lecturer ID:");
        String lecId = scan.nextLine();

        String checkLecSql = "SELECT degree FROM lecturers WHERE id = ?";
        String lecDegree = "";
        try (java.sql.PreparedStatement pstmt = connection.prepareStatement(checkLecSql)) {
            pstmt.setString(1, lecId);
            java.sql.ResultSet rs = pstmt.executeQuery();
            if (!rs.next()) throw new NoLecturerFoundException("Lecturer not found.");
            lecDegree = rs.getString("degree");
        } catch (java.sql.SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            return;
        }

        System.out.println("Enter Committee Name:");
        String comName = scan.nextLine();

        String checkComSql = "SELECT chairman_id, required_degree FROM committees WHERE name = ?";
        try (java.sql.PreparedStatement pstmt = connection.prepareStatement(checkComSql)) {
            pstmt.setString(1, comName);
            java.sql.ResultSet rs = pstmt.executeQuery();
            if (!rs.next()) throw new NoCommitteesFoundException("Committee not found.");

            String chairmanId = rs.getString("chairman_id");
            String reqDegree = rs.getString("required_degree");

            if (chairmanId.equals(lecId)) {
                throw new NoChairmanFoundException("Chairman already member.");
            }
            if (!lecDegree.equals(reqDegree)) {
                throw new InvalidDegreeException("Degree mismatch.");
            }
        } catch (java.sql.SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            return;
        }

        String insertMemberSql = "INSERT INTO committee_members (committee_name, lecturer_id) VALUES (?, ?)";
        try (java.sql.PreparedStatement pstmt = connection.prepareStatement(insertMemberSql)) {
            pstmt.setString(1, comName);
            pstmt.setString(2, lecId);
            pstmt.executeUpdate();
            System.out.println("Member added.");
        } catch (java.sql.SQLException e) {
            System.out.println("Database error (maybe already a member?): " + e.getMessage());
        }
    }

    public void updateCommitteeChairman() throws NoCommitteesFoundException, NoLecturerFoundException, InvalidDegreeException, NoChairmanFoundException {
        System.out.println("Enter Committee Name:");
        String comName = scan.nextLine();

        String checkComSql = "SELECT name FROM committees WHERE name = ?";
        try (java.sql.PreparedStatement pstmt = connection.prepareStatement(checkComSql)) {
            pstmt.setString(1, comName);
            if (!pstmt.executeQuery().next()) throw new NoCommitteesFoundException("Committee not found.");
        } catch (java.sql.SQLException e) { System.out.println("DB error: " + e.getMessage()); return; }

        System.out.println("Enter new Chairman ID:");
        String newChairId = scan.nextLine();

        String checkLecSql = "SELECT degree FROM lecturers WHERE id = ?";
        try (java.sql.PreparedStatement pstmt = connection.prepareStatement(checkLecSql)) {
            pstmt.setString(1, newChairId);
            java.sql.ResultSet rs = pstmt.executeQuery();
            if (!rs.next()) throw new NoLecturerFoundException("Lecturer not found.");
            String deg = rs.getString("degree");
            if (!deg.equals("DR") && !deg.equals("PROFESSOR")) throw new InvalidDegreeException("Chairman must be DR/PROFESSOR.");
        } catch (java.sql.SQLException e) { System.out.println("DB error: " + e.getMessage()); return; }

        String checkMemberSql = "SELECT 1 FROM committee_members WHERE committee_name = ? AND lecturer_id = ?";
        try (java.sql.PreparedStatement pstmt = connection.prepareStatement(checkMemberSql)) {
            pstmt.setString(1, comName);
            pstmt.setString(2, newChairId);
            if (!pstmt.executeQuery().next()) throw new NoChairmanFoundException("New chairman must be a member of the committee.");
        } catch (java.sql.SQLException e) { System.out.println("DB error: " + e.getMessage()); return; }

        String updateSql = "UPDATE committees SET chairman_id = ? WHERE name = ?";
        try (java.sql.PreparedStatement pstmt = connection.prepareStatement(updateSql)) {
            pstmt.setString(1, newChairId);
            pstmt.setString(2, comName);
            pstmt.executeUpdate();
            System.out.println("Chairman updated successfully.");
        } catch (java.sql.SQLException e) { System.out.println("DB error: " + e.getMessage()); }
    }

    public void removeCommitteeMember() throws NoCommitteesFoundException, NoLecturerFoundException, NoChairmanFoundException {
        System.out.println("Enter Committee Name:");
        String comName = scan.nextLine();

        String checkComSql = "SELECT chairman_id FROM committees WHERE name = ?";
        String chairmanId = null;
        try (java.sql.PreparedStatement pstmt = connection.prepareStatement(checkComSql)) {
            pstmt.setString(1, comName);
            java.sql.ResultSet rs = pstmt.executeQuery();
            if (!rs.next()) throw new NoCommitteesFoundException("Committee not found.");
            chairmanId = rs.getString("chairman_id");
        } catch (java.sql.SQLException e) { System.out.println("DB error: " + e.getMessage()); return; }

        System.out.println("Enter Lecturer ID to remove:");
        String lecId = scan.nextLine();

        if (lecId.equals(chairmanId)) {
            throw new NoChairmanFoundException("Cannot remove the chairman from the committee.");
        }

        String deleteSql = "DELETE FROM committee_members WHERE committee_name = ? AND lecturer_id = ?";
        try (java.sql.PreparedStatement pstmt = connection.prepareStatement(deleteSql)) {
            pstmt.setString(1, comName);
            pstmt.setString(2, lecId);
            int rows = pstmt.executeUpdate();
            if (rows == 0) {
                throw new NoLecturerFoundException("Lecturer not found in this committee.");
            }
            System.out.println("Member removed successfully.");
        } catch (java.sql.SQLException e) { System.out.println("DB error: " + e.getMessage()); }
    }

    public void assignLecturerDepartment() throws NoLecturerFoundException, NoDepartmentFoundException, LecturerAlreadyInCommitteeException {
        System.out.println("Enter Lecturer ID:");
        String lecId = scan.nextLine();

        System.out.println("Enter Department Name:");
        String dname = scan.nextLine();

        String checkDep = "SELECT COUNT(*) FROM departments WHERE name = ?";
        try (java.sql.PreparedStatement pstmt = connection.prepareStatement(checkDep)) {
            pstmt.setString(1, dname);
            java.sql.ResultSet rs = pstmt.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                throw new NoDepartmentFoundException("Department not found in database.");
            }
        } catch (java.sql.SQLException e) {
            System.out.println("DB Error: " + e.getMessage());
            return;
        }

        String updateSql = "UPDATE lecturers SET department_name = ? WHERE id = ?";
        try (java.sql.PreparedStatement pstmt = connection.prepareStatement(updateSql)) {
            pstmt.setString(1, dname);
            pstmt.setString(2, lecId);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new NoLecturerFoundException("Lecturer ID not found.");
            } else {
                System.out.println("Lecturer assigned to department successfully.");
            }
        } catch (java.sql.SQLException e) {
            System.out.println("Failed to assign department: " + e.getMessage());
        }
    }

    public void compareCommittees() throws NoCommitteesFoundException {
        System.out.println("Enter the name of the first committee:");
        String c1Name = scan.nextLine();
        System.out.println("Enter the name of the second committee:");
        String c2Name = scan.nextLine();

        String checkExistSql = "SELECT COUNT(*) FROM committees WHERE name IN (?, ?)";
        try (java.sql.PreparedStatement pstmt = connection.prepareStatement(checkExistSql)) {
            pstmt.setString(1, c1Name);
            pstmt.setString(2, c2Name);
            java.sql.ResultSet rs = pstmt.executeQuery();
            rs.next();
            if (rs.getInt(1) < 2 && !c1Name.equals(c2Name)) {
                throw new NoCommitteesFoundException("One or both committees not found in database!");
            }
        } catch (java.sql.SQLException e) {
            System.out.println("DB error: " + e.getMessage());
            return;
        }

        System.out.println("Compare by: (1) Number of Members OR (2) Number of Articles by Members?");
        int choice = scan.nextInt();
        scan.nextLine();

        int c1Score = 0;
        int c2Score = 0;

        String query;
        if (choice == 1) {
            query = "SELECT COUNT(*) FROM committee_members WHERE committee_name = ?";
        } else {
            query = "SELECT COUNT(*) FROM articles a JOIN committee_members cm ON a.lecturer_id = cm.lecturer_id WHERE cm.committee_name = ?";
        }

        try (java.sql.PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, c1Name);
            java.sql.ResultSet rs1 = pstmt.executeQuery();
            if (rs1.next()) c1Score = rs1.getInt(1);

            pstmt.setString(1, c2Name);
            java.sql.ResultSet rs2 = pstmt.executeQuery();
            if (rs2.next()) c2Score = rs2.getInt(1);
        } catch (java.sql.SQLException e) {
            System.out.println("DB error: " + e.getMessage());
            return;
        }

        if (c1Score > c2Score) {
            System.out.println(c1Name + " is larger by the chosen criteria (" + c1Score + " vs " + c2Score + ").");
        } else if (c2Score > c1Score) {
            System.out.println(c2Name + " is larger by the chosen criteria (" + c2Score + " vs " + c1Score + ").");
        } else {
            System.out.println("The committees are equal by the chosen criteria (" + c1Score + " each).");
        }
    }

    public void compareDoctorsByArticles() throws NoDoctorsFoundException {
        String sql = "SELECT l.name, COUNT(a.title) AS article_count " +
                "FROM lecturers l " +
                "JOIN articles a ON l.id = a.lecturer_id " +
                "WHERE l.degree IN ('DR', 'PROFESSOR') " +
                "GROUP BY l.id, l.name " +
                "ORDER BY article_count DESC " +
                "LIMIT 1";

        try (java.sql.PreparedStatement pstmt = connection.prepareStatement(sql);
             java.sql.ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                String name = rs.getString("name");
                int count = rs.getInt("article_count");
                System.out.println("Top lecturer is: " + name + " with " + count + " articles.");
            } else {
                throw new NoDoctorsFoundException("No Doctors/Professors with articles found in the system!");
            }
        } catch (java.sql.SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    public void duplicateCommittee() throws NoCommitteesFoundException {
        System.out.println("Enter committee name to duplicate:");
        String origName = scan.nextLine();

        String newName = "new-" + origName;

        String getOrigSql = "SELECT chairman_id, required_degree FROM committees WHERE name = ?";
        String chairId = null, reqDeg = null;

        try (java.sql.PreparedStatement pstmt = connection.prepareStatement(getOrigSql)) {
            pstmt.setString(1, origName);
            java.sql.ResultSet rs = pstmt.executeQuery();
            if (!rs.next()) throw new NoCommitteesFoundException("Committee not found.");
            chairId = rs.getString("chairman_id");
            reqDeg = rs.getString("required_degree");
        } catch (java.sql.SQLException e) { System.out.println("DB error: " + e.getMessage()); return; }

        String insertComSql = "INSERT INTO committees (name, chairman_id, required_degree) VALUES (?, ?, ?)";
        try (java.sql.PreparedStatement pstmt = connection.prepareStatement(insertComSql)) {
            pstmt.setString(1, newName);
            pstmt.setString(2, chairId);
            pstmt.setString(3, reqDeg);
            pstmt.executeUpdate();
        } catch (java.sql.SQLException e) {
            System.out.println("DB error duplicating committee: " + e.getMessage());
            return;
        }

        String copyMembersSql = "INSERT INTO committee_members (committee_name, lecturer_id) " +
                "SELECT ?, lecturer_id FROM committee_members WHERE committee_name = ?";
        try (java.sql.PreparedStatement pstmt = connection.prepareStatement(copyMembersSql)) {
            pstmt.setString(1, newName);
            pstmt.setString(2, origName);
            pstmt.executeUpdate();
            System.out.println("Duplicated: " + newName);
        } catch (java.sql.SQLException e) {
            System.out.println("DB error copying members: " + e.getMessage());
        }
    }

    public void lecturerAvgWage() throws NoLecturerFoundException {
        String sql = "SELECT AVG(wage) AS avg_wage FROM lecturers";
        try (java.sql.PreparedStatement pstmt = connection.prepareStatement(sql);
             java.sql.ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                double avg = rs.getDouble("avg_wage");
                if (rs.wasNull()) {
                    throw new NoLecturerFoundException("No lecturers in the database.");
                }
                System.out.println("Average wage of all lecturers: " + avg + "₪");
            }

        } catch (java.sql.SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    public void departmentAvgWage() throws NoLecturerFoundException {
        System.out.println("Enter Department Name:");
        String dn = scan.nextLine();

        String sql = "SELECT AVG(wage) AS avg_wage FROM lecturers WHERE department_name = ?";
        try (java.sql.PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, dn);

            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    double avg = rs.getDouble("avg_wage");
                    if (rs.wasNull()) {
                        throw new NoLecturerFoundException("No lecturers found in department: " + dn);
                    }
                    System.out.println("Average wage in " + dn + " department: " + avg + "₪");
                }
            }

        } catch (java.sql.SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    public void lecturerInfo() throws NoLecturerFoundException {
        String sql = "SELECT * FROM lecturers";
        boolean hasLecturers = false;

        try (java.sql.PreparedStatement pstmt = connection.prepareStatement(sql);
             java.sql.ResultSet rs = pstmt.executeQuery()) {

            System.out.println("===== Lecturers in Database =====");

            while (rs.next()) {
                hasLecturers = true;
                String id = rs.getString("id");
                String name = rs.getString("name");
                int wage = rs.getInt("wage");
                String degree = rs.getString("degree");
                String degreeName = rs.getString("degree_name");
                String department = rs.getString("department_name");

                System.out.println("ID: " + id +
                        " | Name: " + name +
                        " | Degree: " + degree + " (" + degreeName + ")" +
                        " | Wage: " + wage + "₪" +
                        " | Department: " + (department != null ? department : "None"));
            }

            if (!hasLecturers) {
                throw new NoLecturerFoundException("No lecturers found in the database.");
            }

        } catch (java.sql.SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    public void committeeInfo() throws NoCommitteesFoundException {
        String sql = "SELECT c.name, l.name AS chairman_name, c.required_degree FROM committees c JOIN lecturers l ON c.chairman_id = l.id";
        boolean hasCommittees = false;

        try (java.sql.PreparedStatement pstmt = connection.prepareStatement(sql);
             java.sql.ResultSet rs = pstmt.executeQuery()) {

            System.out.println("===== Committees in Database =====");

            while (rs.next()) {
                hasCommittees = true;
                String name = rs.getString("name");
                String chairmanName = rs.getString("chairman_name");
                String reqDegree = rs.getString("required_degree");

                System.out.println("Committee: " + name + " | Chairman: " + chairmanName + " | Required Degree: " + reqDegree);

                String membersSql = "SELECT l.name FROM committee_members cm JOIN lecturers l ON cm.lecturer_id = l.id WHERE cm.committee_name = ?";
                try (java.sql.PreparedStatement pstmt2 = connection.prepareStatement(membersSql)) {
                    pstmt2.setString(1, name);
                    java.sql.ResultSet rs2 = pstmt2.executeQuery();
                    System.out.print("  Members: ");
                    boolean hasMembers = false;
                    while (rs2.next()) {
                        System.out.print(rs2.getString("name") + ", ");
                        hasMembers = true;
                    }
                    if (!hasMembers) System.out.print("None");
                    System.out.println();
                }
            }

            if (!hasCommittees) {
                throw new NoCommitteesFoundException("No committees.");
            }

        } catch (java.sql.SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    public Lecturer findLecturerByName(String name) {
        String sql = "SELECT * FROM lecturers WHERE name = ?";
        try (java.sql.PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            java.sql.ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String id = rs.getString("id");
                String lecName = rs.getString("name");
                int wage = rs.getInt("wage");
                String degStr = rs.getString("degree");
                String degName = rs.getString("degree_name");
                String dept = rs.getString("department_name");

                Lecturer.Degree deg = Lecturer.Degree.valueOf(degStr);

                Lecturer lec;
                if (deg == Lecturer.Degree.PROFESSOR) {
                    String profSql = "SELECT institute FROM professors WHERE lecturer_id = ?";
                    String inst = "";
                    try (java.sql.PreparedStatement pStmt = connection.prepareStatement(profSql)) {
                        pStmt.setString(1, id);
                        java.sql.ResultSet pRs = pStmt.executeQuery();
                        if (pRs.next()) inst = pRs.getString("institute");
                    }
                    Professor p = new Professor(id, lecName, inst, new ArrayList<>());
                    p.setWage(wage);
                    p.setDepartment(dept);
                    p.setDegree(deg);
                    p.setDegreeName(degName);
                    lec = p;
                } else if (deg == Lecturer.Degree.DR) {
                    Doctor d = new Doctor(lecName, id, deg, degName, wage, dept);
                    lec = d;
                } else {
                    lec = new Lecturer(id, lecName, dept, new ArrayList<>());
                    lec.setWage(wage);
                    lec.setDegree(deg);
                    lec.setDegreeName(degName);
                }
                return lec;
            }
        } catch (java.sql.SQLException e) {
            System.out.println("Database error searching for lecturer: " + e.getMessage());
        }
        return null;
    }

    public void saveData() throws IOException {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed cleanly.");
            }
        } catch (SQLException e) {
            System.out.println("Error closing database connection: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public void loadData() throws IOException, ClassNotFoundException {
    }
}