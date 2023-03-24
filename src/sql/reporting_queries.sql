/* Put your final project reporting queries here */
USE cs_hu_310_final_project;

-- 1. Calculate the GPA for student given a student_id (use student_id=1)

SELECT 
	first_name, last_name, count(class_section_id) AS number_of_classes, SUM(convert_to_grade_point(letter_grade)) AS total_grade_points_earned, AVG(convert_to_grade_point(letter_grade)) AS GPA
FROM students
JOIN class_registrations ON students.student_id = class_registrations.student_id
JOIN grades ON class_registrations.grade_id = grades.grade_id
WHERE students.student_id = 1
GROUP BY students.student_id ;

-- 2. Calculate the GPA for each student (across all classes and all terms)
SELECT 
	first_name, last_name, count(class_section_id) AS number_of_classes, SUM(convert_to_grade_point(letter_grade)) AS total_grade_points_earned, AVG(convert_to_grade_point(letter_grade)) AS GPA
FROM students
JOIN class_registrations ON students.student_id = class_registrations.student_id
JOIN grades ON class_registrations.grade_id = grades.grade_id
GROUP BY students.student_id;

-- 3. Calculate the avg GPA for each class
SELECT 
	code, name, count(grades.grade_id) AS number_of_grades, SUM(convert_to_grade_point(letter_grade)) AS total_grade_points, AVG(convert_to_grade_point(letter_grade)) AS 'AVG GPA'
FROM classes
JOIN class_sections ON class_sections.class_id = classes.class_id
JOIN class_registrations ON class_sections.class_section_id = class_registrations.class_section_id
JOIN grades ON class_registrations.grade_id = grades.grade_id
GROUP BY classes.class_id;

-- 4. Calculate the avg GPA for each class and term
SELECT 
	code, classes.name, terms.name AS term, count(grades.grade_id) AS number_of_grades, SUM(convert_to_grade_point(letter_grade)) AS total_grade_points, AVG(convert_to_grade_point(letter_grade)) AS 'AVG GPA'
FROM class_sections
JOIN terms ON class_sections.term_id = terms.term_id
JOIN classes ON class_sections.class_id = classes.class_id
JOIN class_registrations ON class_sections.class_section_id = class_registrations.class_section_id
JOIN grades ON class_registrations.grade_id = grades.grade_id
GROUP BY class_sections.class_section_id
ORDER BY code, terms.name DESC;

-- 5. List all the classes being taught by an instructor (use instructor_id=1)
SELECT 
	first_name, last_name, title, code, classes.name AS class_name, terms.name AS term
FROM instructors
JOIN class_sections ON instructors.instructor_id = class_sections.instructor_id
JOIN classes ON classes.class_id = class_sections.class_id
JOIN terms ON class_sections.term_id = terms.term_id
JOIN academic_titles ON instructors.academic_title_id = academic_titles.academic_title_id
WHERE instructors.instructor_id = 1;

-- 6. List all classes with terms & instructor
SELECT 
	code, classes.name, terms.name AS term, first_name, last_name
FROM instructors
JOIN class_sections ON instructors.instructor_id = class_sections.instructor_id
JOIN classes ON classes.class_id = class_sections.class_id
JOIN terms ON class_sections.term_id = terms.term_id;

-- 7. Calculate the remaining space left in a class
SELECT 
	code, classes.name, terms.name AS term, COUNT(student_id) AS enrolled_students, maximum_students - COUNT(student_id)
FROM classes
JOIN class_sections ON classes.class_id = class_sections.class_id
JOIN terms ON class_sections.term_id = terms.term_id
JOIN class_registrations ON class_sections.class_section_id = class_registrations.class_section_id
GROUP BY class_sections.class_section_id;