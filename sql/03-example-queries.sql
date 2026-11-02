SELECT COUNT(*) FROM lecturers WHERE id = '123456789';

SELECT AVG(wage) FROM lecturers;

SELECT AVG(wage) FROM lecturers WHERE department_name = 'Computer Science';

SELECT department_name, COUNT(*) AS total_lecturers, AVG(wage) AS average_wage
FROM lecturers
GROUP BY department_name;

SELECT committees.name, lecturers.name
FROM committees
JOIN lecturers ON committees.chairman_id = lecturers.id;

SELECT lecturers.name
FROM committee_members
JOIN lecturers ON committee_members.lecturer_id = lecturers.id
WHERE committee_members.committee_name = 'Research Committee';

SELECT lecturers.name, COUNT(articles.title)
FROM lecturers
JOIN articles ON lecturers.id = articles.lecturer_id
GROUP BY lecturers.id, lecturers.name
ORDER BY COUNT(articles.title) DESC
LIMIT 1;

SELECT name, wage, department_name
FROM lecturers
WHERE wage > (SELECT AVG(wage) FROM lecturers);

SELECT lecturers.name, professors.institute
FROM lecturers
JOIN professors ON lecturers.id = professors.lecturer_id;

UPDATE lecturers SET department_name = 'Computer Science' WHERE id = '8';

UPDATE committees
SET chairman_id = '333333333'
WHERE name = 'Research Committee';
