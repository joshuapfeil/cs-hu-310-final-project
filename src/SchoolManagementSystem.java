import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This application will keep track of things like what classes are offered by
 * the school, and which students are registered for those classes and provide
 * basic reporting. This application interacts with a database to store and
 * retrieve data.
 * @author Joshua Pfeil
 */
public class SchoolManagementSystem {

    public static void getAllClassesByInstructor(String first_name, String last_name) {
        Connection connection = null;
        Statement sqlStatement = null;

        try {
        	connection = Database.getDatabaseConnection();
            sqlStatement = connection.createStatement();
            ResultSet out = sqlStatement.executeQuery("SELECT \r\n"
		            		+ "	first_name, last_name, title, code, classes.name AS class_name, terms.name AS term\r\n"
		            		+ "FROM instructors\r\n"
		            		+ "JOIN class_sections ON instructors.instructor_id = class_sections.instructor_id\r\n"
		            		+ "JOIN classes ON classes.class_id = class_sections.class_id\r\n"
		            		+ "JOIN terms ON class_sections.term_id = terms.term_id\r\n"
		            		+ "JOIN academic_titles ON instructors.academic_title_id = academic_titles.academic_title_id\r\n"
		            		+ "WHERE instructors.instructor_id = "
		            		+ "(SELECT instructor_id FROM instructors "
		            		+ "WHERE UPPER(first_name) = '" + first_name.toUpperCase() + "' AND UPPER(last_name) = '" + last_name.toUpperCase() + "');");
            System.out.println("Student ID | class_ection ID | First Name | Last Name | Code | Name | Term | Letter Grade");
            System.out.println("--------------------------------------------------------------------------------");
            while (out.next()) {
            	System.out.println(out.getString("first_name") + " | " + 
								out.getString("last_name") + " | " + 
								out.getString("title") + " | " + 
								out.getString("code") + " | " + 
								out.getString("class_name") + " | " + 
								out.getString("term"));
            }            
        } catch (SQLException sqlException) {
            System.out.println("Failed to get class sections");
            System.out.println(sqlException.getMessage());

        } finally {
            try {
                if (sqlStatement != null)
                    sqlStatement.close();
            } catch (SQLException se2) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }

    }

    public static void submitGrade(String studentId, String classSectionID, String grade) {
        Connection connection = null;
        Statement sqlStatement = null;

        try {
        	connection = Database.getDatabaseConnection();
            sqlStatement = connection.createStatement();
            
            sqlStatement.execute("INSERT INTO grades (letter_grade) VALUES ('" + grade + "');");
            sqlStatement.execute("UPDATE class_registrations SET grade_id = LAST_INSERT_ID() WHERE student_id = " + studentId + " AND class_section_id = " + classSectionID + ";");
            System.out.println("Grade has been submitted!");
        } catch (SQLException sqlException) {
            System.out.println("Failed to submit grade");
            System.out.println(sqlException.getMessage());

        } finally {
            try {
                if (sqlStatement != null)
                    sqlStatement.close();
            } catch (SQLException se2) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    public static void registerStudent(String studentId, String classSectionID) {
        Connection connection = null;
        Statement sqlStatement = null;

        try {
        	connection = Database.getDatabaseConnection();
            sqlStatement = connection.createStatement();
            sqlStatement.execute("INSERT INTO class_registrations (student_id, class_section_id) values(" + studentId + ", " + classSectionID + ");");
            ResultSet out = sqlStatement.executeQuery("SELECT * FROM class_registrations WHERE student_id = " + studentId + " AND class_section_id = " + classSectionID + ";");
            System.out.println("Class Registration ID | Student ID | Class Section ID");
            System.out.println("--------------------------------------------------------------------------------");
            out.next();
            System.out.println("" + out.getInt("class_registration_id") + " | " + out.getInt("student_id") + " | " + out.getInt("class_section_id"));
        } catch (SQLException sqlException) {
            System.out.println("Failed to register student");
            System.out.println(sqlException.getMessage());

        } finally {
            try {
                if (sqlStatement != null)
                    sqlStatement.close();
            } catch (SQLException se2) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    public static void deleteStudent(String studentId) {
        Connection connection = null;
        Statement sqlStatement = null;

        try {
        	connection = Database.getDatabaseConnection();
            sqlStatement = connection.createStatement();
            sqlStatement.execute("DELETE FROM students WHERE student_id = " + studentId + ";");
            System.out.println("--------------------------------------------------------------------------------");
            System.out.println("Student with id: " + studentId + " was deleted");
        } catch (SQLException sqlException) {
            System.out.println("Failed to delete student");
            System.out.println(sqlException.getMessage());

        } finally {
            try {
                if (sqlStatement != null)
                    sqlStatement.close();
            } catch (SQLException se2) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }


    public static void createNewStudent(String firstName, String lastName, String birthdate) {
        Connection connection = null;
        Statement sqlStatement = null;

        try {
        	connection = Database.getDatabaseConnection();
            sqlStatement = connection.createStatement();
            sqlStatement.execute("INSERT INTO students (first_name, last_name, birthdate) values('" + firstName + "', '" + lastName + "', '" + birthdate + "');");
            ResultSet out = sqlStatement.executeQuery("SELECT * FROM students WHERE UPPER(first_name) = '" + firstName.toUpperCase() + "' AND UPPER(last_name) = '" + lastName.toUpperCase() + "';");
            System.out.println("Student ID | First Name | Last Name | Birthdate");
            System.out.println("--------------------------------------------------------------------------------");
            while (out.next()) {
				System.out.println(out.getInt("student_id") + " | " + out.getString("first_name") + " | " + out.getString("last_name")  + " | " + out.getDate("birthdate"));
			}
		} catch (SQLException sqlException) {
            System.out.println("Failed to create student");
            System.out.println(sqlException.getMessage());

        } finally {
            try {
                if (sqlStatement != null)
                    sqlStatement.close();
            } catch (SQLException se2) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }

    }

    public static void listAllClassRegistrations() {
        Connection connection = null;
        Statement sqlStatement = null;

        try {
        	connection = Database.getDatabaseConnection();
            sqlStatement = connection.createStatement();
            ResultSet out = sqlStatement.executeQuery("SELECT students.student_id, class_sections.class_section_id, first_name, last_name, classes.code, classes.name, terms.name AS term, grades.letter_grade "
            										+ "FROM class_registrations "
            										+ "LEFT JOIN students ON students.student_id = class_registrations.student_id "
            										+ "LEFT JOIN class_sections ON class_sections.class_section_id = class_registrations.class_section_id "
            										+ "LEFT JOIN grades ON grades.grade_id = class_registrations.grade_id "
            										+ "JOIN classes ON class_sections.class_id = classes.class_id "
            										+ "JOIN terms ON class_sections.term_id = terms.term_id;");
            System.out.println("Student ID | class_ection ID | First Name | Last Name | Code | Name | Term | Letter Grade");
            System.out.println("--------------------------------------------------------------------------------");
            while (out.next()) {
				System.out.println(out.getInt("student_id") + " | " + 
									out.getInt("class_section_id") + " | " + 
									out.getString("first_name") + " | " + 
									out.getString("last_name") + " | " + 
									out.getString("code") + " | " + 
									out.getString("name") + " | " + 
									out.getString("term") + " | " +
									out.getString("letter_grade"));
			}
            } catch (SQLException sqlException) {
            System.out.println("Failed to get class sections");
            System.out.println(sqlException.getMessage());

        } finally {
            try {
                if (sqlStatement != null)
                    sqlStatement.close();
            } catch (SQLException se2) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    public static void listAllClassSections() {
        Connection connection = null;
        Statement sqlStatement = null;

        try {
        	connection = Database.getDatabaseConnection();
            sqlStatement = connection.createStatement();
            ResultSet out = sqlStatement.executeQuery("SELECT class_section_id, code, classes.name, terms.name AS term "
            										+ "FROM class_sections "
            										+ "JOIN classes ON class_sections.class_id = classes.class_id "
            										+ "JOIN terms ON class_sections.term_id = terms.term_id;");
            System.out.println("Class Section ID | Code | Name | term");
            System.out.println("--------------------------------------------------------------------------------");
            while (out.next()) {
				System.out.println(out.getInt("class_section_id") + " | " + out.getString("code") + " | " + out.getString("name")  + " | " + out.getString("term"));
			}
            } catch (SQLException sqlException) {
            System.out.println("Failed to get class sections");
            System.out.println(sqlException.getMessage());

        } finally {
            try {
                if (sqlStatement != null)
                    sqlStatement.close();
            } catch (SQLException se2) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    public static void listAllClasses() {
        Connection connection = null;
        Statement sqlStatement = null;

        try {
        	connection = Database.getDatabaseConnection();
            sqlStatement = connection.createStatement();
            ResultSet out = sqlStatement.executeQuery("SELECT * FROM classes;");
            System.out.println("Class ID | Code | Name | Description");
            System.out.println("--------------------------------------------------------------------------------");
            while (out.next()) {
				System.out.println(out.getInt("class_id") + " | " + out.getString("code") + " | " + out.getString("name")  + " | " + out.getString("description"));
			}
        } catch (SQLException sqlException) {
            System.out.println("Failed to get classes");
            System.out.println(sqlException.getMessage());

        } finally {
            try {
                if (sqlStatement != null)
                    sqlStatement.close();
            } catch (SQLException se2) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }


    public static void listAllStudents() {
        Connection connection = null;
        Statement sqlStatement = null;

        try {
            connection = Database.getDatabaseConnection();
            sqlStatement = connection.createStatement();
            ResultSet out = sqlStatement.executeQuery("SELECT * FROM students;");
            System.out.println("Student ID | First Name | Last Name | Birthdate");
            System.out.println("--------------------------------------------------------------------------------");
            while (out.next()) {
				System.out.println(out.getInt("student_id") + " | " + out.getString("first_name") + " | " + out.getString("last_name")  + " | " + out.getDate("birthdate"));
			}
        } catch (SQLException sqlException) {
            System.out.println("Failed to get students");
            System.out.println(sqlException.getMessage());

        } finally {
            try {
                if (sqlStatement != null)
                    sqlStatement.close();
            } catch (SQLException se2) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    /***
     * Splits a string up by spaces. Spaces are ignored when wrapped in quotes.
     *
     * @param command - School Management System cli command
     * @return splits a string by spaces.
     */
    public static List<String> parseArguments(String command) {
        List<String> commandArguments = new ArrayList<String>();
        Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(command);
        while (m.find()) commandArguments.add(m.group(1).replace("\"", ""));
        return commandArguments;
    }

    public static void main(String[] args) {
        System.out.println("Welcome to the School Management System");
        System.out.println("-".repeat(80));

        Scanner scan = new Scanner(System.in);
        String command = "";

        do {
            System.out.print("Command: ");
            command = scan.nextLine();
            ;
            List<String> commandArguments = parseArguments(command);
            command = commandArguments.get(0);
            commandArguments.remove(0);

            if (command.equals("help")) {
                System.out.println("-".repeat(38) + "Help" + "-".repeat(38));
                System.out.println("test connection \n\tTests the database connection");

                System.out.println("list students \n\tlists all the students");
                System.out.println("list classes \n\tlists all the classes");
                System.out.println("list class_sections \n\tlists all the class_sections");
                System.out.println("list class_registrations \n\tlists all the class_registrations");
                System.out.println("list instructor <first_name> <last_name>\n\tlists all the classes taught by that instructor");


                System.out.println("delete student <studentId> \n\tdeletes the student");
                System.out.println("create student <first_name> <last_name> <birthdate> \n\tcreates a student");
                System.out.println("register student <student_id> <class_section_id>\n\tregisters the student to the class section");

                System.out.println("submit grade <studentId> <class_section_id> <letter_grade> \n\tcreates a student");
                System.out.println("help \n\tlists help information");
                System.out.println("quit \n\tExits the program");
            } else if (command.equals("test") && commandArguments.get(0).equals("connection")) {
                Database.testConnection();
            } else if (command.equals("list")) {
                if (commandArguments.get(0).equals("students")) listAllStudents();
                if (commandArguments.get(0).equals("classes")) listAllClasses();
                if (commandArguments.get(0).equals("class_sections")) listAllClassSections();
                if (commandArguments.get(0).equals("class_registrations")) listAllClassRegistrations();

                if (commandArguments.get(0).equals("instructor")) {
                    getAllClassesByInstructor(commandArguments.get(1), commandArguments.get(2));
                }
            } else if (command.equals("create")) {
                if (commandArguments.get(0).equals("student")) {
                    createNewStudent(commandArguments.get(1), commandArguments.get(2), commandArguments.get(3));
                }
            } else if (command.equals("register")) {
                if (commandArguments.get(0).equals("student")) {
                    registerStudent(commandArguments.get(1), commandArguments.get(2));
                }
            } else if (command.equals("submit")) {
                if (commandArguments.get(0).equals("grade")) {
                    submitGrade(commandArguments.get(1), commandArguments.get(2), commandArguments.get(3));
                }
            } else if (command.equals("delete")) {
                if (commandArguments.get(0).equals("student")) {
                    deleteStudent(commandArguments.get(1));
                }
            } else if (!(command.equals("quit") || command.equals("exit"))) {
                System.out.println(command);
                System.out.println("Command not found. Enter 'help' for list of commands");
            }
            System.out.println("-".repeat(80));
        } while (!(command.equals("quit") || command.equals("exit")));
        System.out.println("Bye!");
    }
}

