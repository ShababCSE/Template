-- CREATE A TABLE WITH CONSTRAINTS
CREATE TABLE table_name (
    column1 NUMBER(4) PRIMARY KEY,           -- Uniquely identifies each record
    column2 VARCHAR2(50) UNIQUE,             -- Ensures all values are distinct
    column3 NUMBER(10, 2) NOT NULL,          -- Prevents NULL values
    column4 CHAR(1) CHECK (column4 IN ('Y', 'N')), -- Validates data before insertion
    column5 DATE
);

-- CREATE TABLE FROM EXISTING DATA (CTAS)
CREATE TABLE new_table AS
SELECT * FROM existing_table 
WHERE condition;                             -- Copies structure and matching data

-- ALTER TABLE: ADD COLUMN
ALTER TABLE table_name 
ADD new_column_name VARCHAR2(100);

-- ALTER TABLE: MODIFY COLUMN (e.g., adding NOT NULL)
ALTER TABLE table_name 
MODIFY existing_column NOT NULL;

-- ALTER TABLE: ADD CONSTRAINT
ALTER TABLE table_name 
ADD CONSTRAINT constraint_name UNIQUE (column_name);

-- ALTER TABLE: DROP COLUMN
ALTER TABLE table_name 
DROP COLUMN column_name;


-- UPDATE EXISTING RECORDS
UPDATE table_name
SET column_name = new_value,                 -- Can be a calculation (e.g., salary * 1.08)
    another_column = 'Text'
WHERE condition;                             -- VERY IMPORTANT: Without WHERE, it updates all rows!





-- BASIC SELECTION & ALIASING
SELECT column1, 
       column2 AS "Alias Name"               -- Double quotes if alias has spaces
FROM table_name
WHERE column3 > 5000 
ORDER BY column1 DESC;                       -- DESC for descending, ASC for ascending

-- PATTERN MATCHING (LIKE)
-- % represents zero or more characters
-- _ represents exactly one character
SELECT * FROM employees WHERE first_name LIKE 'M%k';   -- Starts with M, ends with k
SELECT * FROM employees WHERE last_name LIKE '%son';   -- Ends with son
SELECT * FROM employees WHERE job_id LIKE 'SA\_%' ESCAPE '\'; -- Escaping the wildcard '_'



-- CONCATENATION (||)
SELECT first_name || ' ' || last_name FROM table_name; -- Joins strings together

-- LENGTH
SELECT LENGTH(first_name) FROM table_name;             -- Returns character count

-- INSTR (Finds position of a substring)
-- INSTR(string, search_char, start_position)
SELECT INSTR(LOWER(first_name), 'e') FROM table_name;  -- Finds first 'e'

-- SUBSTR (Extracts part of a string)
-- SUBSTR(string, start_position, length)
SELECT SUBSTR(job_id, 1, 3) FROM table_name;           -- Grabs first 3 characters

-- LOWER / UPPER
SELECT LOWER(email) FROM table_name;                   -- Converts to lowercase



-- EXTRACT (Gets a specific part of a date)
SELECT EXTRACT(MONTH FROM hire_date) FROM table_name;  -- Returns 1-12
SELECT EXTRACT(YEAR FROM hire_date) FROM table_name;   -- Returns e.g., 2026

-- ADD_MONTHS
SELECT ADD_MONTHS(hire_date, 120) FROM table_name;     -- Adds exactly 10 years (120 months)

-- TO_CHAR (Formats dates to strings)
SELECT TO_CHAR(hire_date, 'fmDay') FROM table_name;    -- 'fm' removes trailing spaces, 'Day' gives 'Monday'
SELECT TO_CHAR(hire_date, 'DDD') FROM table_name;      -- Returns day of the year (1-365)

-- MOD (Modulo/Remainder)
SELECT MOD(10, 2) FROM dual;                           -- Returns 0. Useful for odd/even checks



SELECT department_id, 
       COUNT(*) AS total_employees,          -- Counts all rows in the group
       SUM(salary) AS total_payroll,         -- Adds up all salaries
       AVG(salary) AS average_salary         -- Calculates the mean
FROM employees
WHERE hire_date > DATE '2020-01-01'          -- WHERE filters rows BEFORE grouping
GROUP BY department_id                       -- Groups identical department_ids together
HAVING AVG(salary) >= 7000;                  -- HAVING filters rows AFTER grouping



-- CASE EXPRESSION
SELECT employee_id,
       salary,
       CASE 
           WHEN salary > 12000 THEN 'High'
           WHEN salary BETWEEN 7000 AND 12000 THEN 'Medium'
           ELSE 'Low'                        -- Optional catch-all
       END AS salary_bracket
FROM employees;
