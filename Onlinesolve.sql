SELECT Employee_ID, 
       first_name || ' ' || last_name AS "Full Name", 
       Job_ID, 
       Salary
FROM EMPLOYEES
WHERE first_name LIKE 'M%k' 
  AND LENGTH(first_name) >= 4;



SELECT first_name || ' ' || last_name AS full_name, email
FROM EMPLOYEES
WHERE INSTR(LOWER(first_name), 'e') != 0
  AND INSTR(LOWER(first_name), 'e') = LENGTH(last_name) - INSTR(LOWER(last_name), 'e', -1) + 1
  AND EXTRACT(MONTH FROM hire_date) BETWEEN 7 AND 12;




SELECT department_id, SUM(salary) AS total_qualified_payroll
FROM EMPLOYEES
WHERE EXTRACT(MONTH FROM ADD_MONTHS(hire_date, 120)) BETWEEN 1 AND 6
GROUP BY department_id;



-- i & ii. Create table and add constraints
CREATE TABLE projects (
    project_id NUMBER(4) PRIMARY KEY,
    project_name VARCHAR2(50) UNIQUE,
    budget NUMBER(10, 2) CHECK (budget > 50000),
    start_date DATE,
    end_date DATE,
    status CHAR(1) CHECK (status IN ('N', 'A', 'F'))
);

-- iii. Add new column
ALTER TABLE projects ADD manager_name VARCHAR2(40);



-- i. Add boolean-style column
ALTER TABLE Employees ADD is_hired_in_first_half CHAR(1) CHECK (is_hired_in_first_half IN ('Y', 'N'));

-- ii. Update column based on hire date
UPDATE Employees
SET is_hired_in_first_half = CASE
    WHEN EXTRACT(MONTH FROM hire_date) BETWEEN 1 AND 6 THEN 'Y'
    ELSE 'N'
END;

-- iii. Add work_email and format it
ALTER TABLE Employees ADD work_email VARCHAR2(100);

UPDATE Employees
SET work_email = LOWER(first_name || '.' || SUBSTR(job_id, 1, INSTR(job_id, '_') - 1) || '.' || TO_CHAR(hire_date, 'YYYY') || '@company.com');





SELECT Employee_ID, first_name || ' ' || last_name AS "Full Name"
FROM EMPLOYEES
WHERE last_name LIKE '%son' AND LENGTH(last_name) >= 5;




SELECT TO_CHAR(hire_date, 'fmDay') AS hire_day, COUNT(*) AS total_hired
FROM EMPLOYEES
WHERE MOD(TO_NUMBER(TO_CHAR(hire_date, 'DDD')), 2) = 1
GROUP BY TO_CHAR(hire_date, 'fmDay')
HAVING AVG(salary) >= 7000
ORDER BY total_hired DESC;




SELECT employee_id, salary,
       CASE
           WHEN salary > 10000 THEN 'High Fixed'
           WHEN salary = 10000 THEN 'Standard Fixed'
       END AS income_status
FROM EMPLOYEES;





-- i & ii. Create table structure and insert latest year hires
CREATE TABLE YOUNG_EMPLOYEES AS
SELECT * FROM EMPLOYEES
WHERE EXTRACT(YEAR FROM hire_date) = (SELECT MAX(EXTRACT(YEAR FROM hire_date)) FROM EMPLOYEES);

-- iii. Add constraints
ALTER TABLE YOUNG_EMPLOYEES MODIFY first_name NOT NULL;
ALTER TABLE YOUNG_EMPLOYEES ADD CONSTRAINT ye_email_uk UNIQUE (email);
ALTER TABLE YOUNG_EMPLOYEES ADD CONSTRAINT ye_email_chk CHECK (email LIKE '%@%');
ALTER TABLE YOUNG_EMPLOYEES ADD CONSTRAINT ye_sal_chk CHECK (salary >= 2000);

-- iv. Drop column
ALTER TABLE YOUNG_EMPLOYEES DROP COLUMN COMMISSION_PCT;





-- i. Add column
ALTER TABLE Employees ADD salary_grade CHAR(1) CHECK (salary_grade IN ('L', 'M', 'H'));

-- ii. Set salary grades
UPDATE Employees
SET salary_grade = CASE
    WHEN salary < 7000 THEN 'L'
    WHEN salary BETWEEN 7000 AND 12000 THEN 'M'
    WHEN salary > 12000 THEN 'H'
END;

-- iii. Increase salary for SA_ jobs hired in March
UPDATE Employees
SET salary = salary * 1.08
WHERE job_id LIKE 'SA\_%' ESCAPE '\'
  AND EXTRACT(MONTH FROM hire_date) = 3;
