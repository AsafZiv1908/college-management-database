package college.management;

import college.management.exceptions.*;
import java.util.Scanner;
import java.io.IOException;

// Iness Goldes
// Asaf Ziv
public class CollegeManagementApp {
    public static void main (String[] args) {
        Scanner scan = new Scanner(System.in);

        System.out.println("Enter the College name: ");
        String collegeName = scan.nextLine();
        MainManagement management = new MainManagement(collegeName);

        try {
            management.loadData();
        } catch (Exception e) {
            System.out.println("Couldn't load saved data: " + e.getMessage());
        }

        int menuAns;

        do {
            System.out.println("===== Menu =====");
            System.out.println("0 - Exit");
            System.out.println("1 - Add a Lecturer to a College");
            System.out.println("2 - Add a College Committee");
            System.out.println("3 - Add Member to a Committee");
            System.out.println("4 - Update Committee Chairman");
            System.out.println("5 - Remove Committee Member");
            System.out.println("6 - Add a Department of Education");
            System.out.println("7 - Show College's Lecturers average wage");
            System.out.println("8 - Show Department's Lecturers average wage");
            System.out.println("9 - Show information of all Lecturers");
            System.out.println("10 - Show information of all Committees");
            System.out.println("11 - Assign Lecturer to a Department");
            System.out.println("12 - Compare between Dr/Professors");
            System.out.println("13 - Compare between Committees");
            System.out.println("14 - Duplicate committee information");
            System.out.println("===== Please enter your choice: =====");

            menuAns = scan.nextInt();
            scan.nextLine();

            switch (menuAns) {
                case 0 -> {
                    try {
                        management.saveData();
                    } catch (IOException e) {
                        System.out.println("Couldn't save data: " + e.getMessage());
                    }
                    System.out.println("You exited the program");
                }

                case 1 -> {
                    try {
                        management.addLecturer();
                    } catch (InvalidInputException e) {
                        System.out.println(e.getMessage());
                    }
                }

                case 2 -> {
                    try {
                        management.addCommittee();
                    } catch (NoLecturerFoundException | InvalidDegreeException | DuplicateCommitteeException | InvalidInputException e) {
                        System.out.println(e.getMessage());
                    }
                }

                case 3 -> {
                    try {
                        management.addMemberToCommittee();
                    } catch (NoLecturerFoundException | NoCommitteesFoundException | NoChairmanFoundException | InvalidDegreeException e) {
                        System.out.println(e.getMessage());
                    }
                }

                case 4 -> {
                    try {
                        management.updateCommitteeChairman();
                    } catch (NoCommitteesFoundException | NoLecturerFoundException | InvalidDegreeException | NoChairmanFoundException e) {
                        System.out.println(e.getMessage());
                    }
                }

                case 5 -> {
                    try {
                        management.removeCommitteeMember();
                    } catch (NoCommitteesFoundException | NoLecturerFoundException | NoChairmanFoundException e) {
                        System.out.println(e.getMessage());
                    }
                }

                case 6 -> {
                    try {
                        management.addDepartment();
                    } catch (DuplicateCommitteeException e) {
                        System.out.println(e.getMessage());
                    }
                }

                case 7 -> {
                    try {
                        management.lecturerAvgWage();
                    } catch (NoLecturerFoundException e) {
                        System.out.println(e.getMessage());
                    }
                }

                case 8 -> {
                    try {
                        management.departmentAvgWage();
                    } catch (NoLecturerFoundException e) {
                        System.out.println(e.getMessage());
                    }
                }

                case 9 -> {
                    try {
                        management.lecturerInfo();
                    } catch (NoLecturerFoundException e) {
                        System.out.println(e.getMessage());
                    }
                }

                case 10 -> {
                    try {
                        management.committeeInfo();
                    } catch (NoCommitteesFoundException e) {
                        System.out.println(e.getMessage());
                    }
                }

                case 11 -> {
                    try {
                        management.assignLecturerDepartment();
                    } catch (NoLecturerFoundException | NoDepartmentFoundException | LecturerAlreadyInCommitteeException e) {
                        System.out.println(e.getMessage());
                    }
                }

                case 12 -> {
                    try {
                        management.compareDoctorsByArticles();
                    } catch (NoDoctorsFoundException e) {
                        System.out.println(e.getMessage());
                    }
                }

                case 13 -> {
                    try {
                        management.compareCommittees();
                    } catch (NoCommitteesFoundException e) {
                        System.out.println(e.getMessage());
                    }
                }

                case 14 -> {
                    try {
                        management.duplicateCommittee();
                    } catch (NoCommitteesFoundException e) {
                        System.out.println(e.getMessage());
                    }
                }

                default -> System.out.println("Invalid choice, please try again!");
            }

        } while(menuAns != 0);

        scan.close();
    }
}
