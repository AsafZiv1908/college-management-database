CREATE TABLE departments (
    name VARCHAR(100) PRIMARY KEY,
    num_of_students INT NOT NULL DEFAULT 0 CHECK (num_of_students >= 0)
);

CREATE TABLE lecturers (
    id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    wage INT NOT NULL CHECK (wage > 0),
    degree VARCHAR(20) NOT NULL CHECK (degree IN ('FIRST', 'SECOND', 'DR', 'PROFESSOR')),
    degree_name VARCHAR(100) NOT NULL,
    department_name VARCHAR(100) REFERENCES departments(name) ON DELETE SET NULL
);

CREATE TABLE professors (
    lecturer_id VARCHAR(20) PRIMARY KEY REFERENCES lecturers(id) ON DELETE CASCADE,
    institute VARCHAR(150) NOT NULL
);

CREATE TABLE articles (
    id SERIAL PRIMARY KEY,
    lecturer_id VARCHAR(20) NOT NULL REFERENCES lecturers(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL
);

CREATE TABLE committees (
    name VARCHAR(100) PRIMARY KEY,
    required_degree VARCHAR(20) NOT NULL CHECK (required_degree IN ('FIRST', 'SECOND', 'DR', 'PROFESSOR')),
    chairman_id VARCHAR(20) NOT NULL REFERENCES lecturers(id) ON DELETE RESTRICT
);

CREATE TABLE committee_members (
    committee_name VARCHAR(100) REFERENCES committees(name) ON DELETE CASCADE,
    lecturer_id VARCHAR(20) REFERENCES lecturers(id) ON DELETE CASCADE,
    PRIMARY KEY (committee_name, lecturer_id)
);

CREATE OR REPLACE FUNCTION verify_committee_chairman_degree()
RETURNS TRIGGER AS $$
DECLARE
    v_degree VARCHAR(20);
BEGIN
    SELECT degree INTO v_degree FROM lecturers WHERE id = NEW.chairman_id;

    IF v_degree NOT IN ('DR', 'PROFESSOR') OR v_degree IS NULL THEN
        RAISE EXCEPTION 'Error: Committee chairman must hold a DR or PROFESSOR degree!';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER check_chairman_degree_trigger
BEFORE INSERT OR UPDATE ON committees
FOR EACH ROW
EXECUTE FUNCTION verify_committee_chairman_degree();

INSERT INTO departments (name, num_of_students) VALUES
('Computer Science', 120),
('Software Engineering', 95);

INSERT INTO lecturers (id, name, wage, degree, degree_name, department_name) VALUES
('111111111', 'Dr. David Cohen', 15000, 'DR', 'Ph.D. in Computer Science', 'Computer Science'),
('222222222', 'Prof. Sarah Levi', 22000, 'PROFESSOR', 'Full Professor', 'Computer Science'),
('333333333', 'Yossi Israel', 9000, 'SECOND', 'M.Sc. in Engineering', 'Software Engineering'),
('444444444', 'Dr. Elena Rostova', 16000, 'DR', 'Ph.D. in Mathematics', 'Software Engineering'),
('555555555', 'Prof. Alan Turing', 25000, 'PROFESSOR', 'Distinguished Professor', 'Software Engineering');

INSERT INTO professors (lecturer_id, institute) VALUES
('222222222', 'MIT'),
('555555555', 'Cambridge University');

INSERT INTO articles (lecturer_id, title) VALUES
('111111111', 'Advanced Algorithms in Java'),
('222222222', 'Quantum Computing and Logic Gates'),
('222222222', 'Neural Networks Architecture');

INSERT INTO committees (name, required_degree, chairman_id) VALUES
('Curriculum Committee', 'DR', '111111111'),
('Research Committee', 'PROFESSOR', '222222222');

INSERT INTO committee_members (committee_name, lecturer_id) VALUES
('Curriculum Committee', '111111111'),
('Curriculum Committee', '444444444'),
('Research Committee', '222222222');
