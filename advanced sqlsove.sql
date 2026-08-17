-- ======================================================================
-- Online - 2 (C1/C2).pdf
-- ======================================================================

-- 1. Problem: Find employees earning above their department's average salary 
-- in departments with more than 4 employees.
SELECT e.employee_id, e.first_name, e.last_name, e.salary, e.department_id
FROM employees e
JOIN (SELECT department_id, AVG(salary) AS avg_sal, COUNT(employee_id) AS emp_count
      FROM employees
      GROUP BY department_id) d ON e.department_id = d.department_id
WHERE e.salary > d.avg_sal 
  AND d.emp_count > 4;

-- 2. Problem: Find employees who either earn more than their manager's salary 
-- or have a salary greater than their department's average salary. 
-- Print employee details with the type as either "Higher Than Manager" or "Above Dept Avg".
SELECT e.employee_id, 
       e.first_name || ' ' || e.last_name AS full_name, 
       e.salary,
       CASE 
           WHEN e.salary > m.salary THEN 'Higher Than Manager'
           WHEN e.salary > d.avg_sal THEN 'Above Dept Avg'
       END AS type
FROM employees e
LEFT JOIN employees m ON e.manager_id = m.employee_id
LEFT JOIN (SELECT department_id, AVG(salary) AS avg_sal FROM employees GROUP BY department_id) d 
       ON e.department_id = d.department_id
WHERE e.salary > m.salary 
   OR e.salary > d.avg_sal;

-- 3. Problem: Write a SQL query for employees whose salary beats their department average 
-- and whose manager's salary beats the company average. Show full_name, salary, department_name, 
-- and label it 'Dept Top Earner' if salary > 1.5 times dept average, else 'Dept Above Avg'.
SELECT e.first_name || ' ' || e.last_name AS full_name, 
       e.salary, 
       d.department_name,
       CASE 
           WHEN e.salary > 1.5 * da.avg_sal THEN 'Dept Top Earner'
           ELSE 'Dept Above Avg'
       END AS earner_status
FROM employees e
JOIN departments d ON e.department_id = d.department_id
JOIN employees m ON e.manager_id = m.employee_id
JOIN (SELECT department_id, AVG(salary) AS avg_sal FROM employees GROUP BY department_id) da 
  ON e.department_id = da.department_id
WHERE e.salary > da.avg_sal
  AND m.salary > (SELECT AVG(salary) FROM employees);

-- 4. Problem: Find employee_id, full name, and department name of employees 
-- whose department is located in the same city as their manager’s department.
SELECT e.employee_id, 
       e.first_name || ' ' || e.last_name AS full_name, 
       ed.department_name
FROM employees e
JOIN departments ed ON e.department_id = ed.department_id
JOIN locations el ON ed.location_id = el.location_id
JOIN employees m ON e.manager_id = m.employee_id
JOIN departments md ON m.department_id = md.department_id
JOIN locations ml ON md.location_id = ml.location_id
WHERE el.city = ml.city;

-- 5. Problem: Write an SQL query to list all departments that satisfy the following conditions: 
-- (i) every employee in the department earns more than 5000, 
-- (ii) the department has at least one employee with job history, 
-- (iii) the maximum salary in the department is greater than the overall company average salary. 
-- For each such department, display the department name, number of employees, average salary, 
-- and a column called Salary_Level that shows 'Elite' if the department’s average salary 
-- is greater than 1.5 times the company average salary, 'Above Average' otherwise.
SELECT d.department_name, 
       COUNT(e.employee_id) AS number_of_employees, 
       AVG(e.salary) AS average_salary,
       CASE 
           WHEN AVG(e.salary) > 1.5 * (SELECT AVG(salary) FROM employees) THEN 'Elite'
           ELSE 'Above Average'
       END AS Salary_Level
FROM departments d
JOIN employees e ON d.department_id = e.department_id
GROUP BY d.department_id, d.department_name
HAVING MIN(e.salary) > 5000
   AND MAX(e.salary) > (SELECT AVG(salary) FROM employees)
   AND d.department_id IN (SELECT DISTINCT department_id FROM job_history);


-- ======================================================================
-- Online - 2 (B1/B2).pdf
-- ======================================================================

-- 1. Problem: List managers whose departments have average salaries higher 
-- than the overall company average, for departments located in Toronto and Oxford.
SELECT m.employee_id, m.first_name, m.last_name
FROM employees m
JOIN departments d ON m.employee_id = d.manager_id
JOIN locations l ON d.location_id = l.location_id
JOIN (SELECT department_id, AVG(salary) AS avg_sal FROM employees GROUP BY department_id) da
  ON d.department_id = da.department_id
WHERE da.avg_sal > (SELECT AVG(salary) FROM employees)
  AND l.city IN ('Toronto', 'Oxford');

-- 2. Problem: Find employees who both work in departments with more than 5 employees 
-- AND have salaries greater than the overall average salary across all employees.
SELECT employee_id, first_name, last_name, salary
FROM employees
WHERE department_id IN (SELECT department_id 
                        FROM employees 
                        GROUP BY department_id 
                        HAVING COUNT(employee_id) > 5)
  AND salary > (SELECT AVG(salary) FROM employees);

-- 3. Problem: Write a SQL query for employees in departments that have managers, 
-- with no job history records, and salary > dept average. Show full_name, salary, dept_name, 
-- and label 'Stable High Earner' if salary > 1.7 times dept average, else 'Dept Above Avg'.
SELECT e.first_name || ' ' || e.last_name AS full_name, 
       e.salary, 
       d.department_name,
       CASE 
           WHEN e.salary > 1.7 * da.avg_sal THEN 'Stable High Earner'
           ELSE 'Dept Above Avg'
       END AS earner_label
FROM employees e
JOIN departments d ON e.department_id = d.department_id
JOIN (SELECT department_id, AVG(salary) AS avg_sal FROM employees GROUP BY department_id) da 
  ON e.department_id = da.department_id
WHERE d.manager_id IS NOT NULL
  AND e.employee_id NOT IN (SELECT employee_id FROM job_history)
  AND e.salary > da.avg_sal;

-- 4. Problem: Find employees who are either in departments with more than 5 employees 
-- or have a job with minimum salary above 10000. 
-- Display: employee_id, first_name, last_name, department_id, job_id, salary.
SELECT e.employee_id, 
       e.first_name, 
       e.last_name, 
       e.department_id, 
       e.job_id, 
       e.salary
FROM employees e
JOIN jobs j ON e.job_id = j.job_id
WHERE e.department_id IN (SELECT department_id 
                          FROM employees 
                          GROUP BY department_id 
                          HAVING COUNT(employee_id) > 5)
   OR j.min_salary > 10000;

-- 5. Problem: Write an SQL query to find employees who satisfy exactly one of the following conditions: 
-- (i) they work in a department with more than 5 employees, or 
-- (ii) their job has a minimum salary greater than 10000. 
-- Employees who satisfy both conditions or neither condition must be excluded. 
-- Display employee ID, full name, department ID, job ID, and salary.
SELECT e.employee_id, 
       e.first_name || ' ' || e.last_name AS full_name, 
       e.department_id, 
       e.job_id, 
       e.salary
FROM employees e
JOIN jobs j ON e.job_id = j.job_id
WHERE (e.department_id IN (SELECT department_id FROM employees GROUP BY department_id HAVING COUNT(employee_id) > 5) 
       AND j.min_salary <= 10000)
   OR ((e.department_id NOT IN (SELECT department_id FROM employees GROUP BY department_id HAVING COUNT(employee_id) > 5) 
        OR e.department_id IS NULL) 
       AND j.min_salary > 10000);


-- ======================================================================
-- Online - 2 (A1/A2).pdf
-- ======================================================================

-- 1. Problem: Find job titles in departments where employees have worked more than 5 years on average, 
-- but only include those jobs which have a maximum salary higher than the average max salary across all jobs.
SELECT DISTINCT j.job_title
FROM employees e
JOIN departments d ON e.department_id = d.department_id
JOIN jobs j ON e.job_id = j.job_id
WHERE e.department_id IN (
    SELECT department_id 
    FROM employees 
    GROUP BY department_id 
    HAVING AVG(MONTHS_BETWEEN(SYSDATE, hire_date) / 12) > 5
)
AND j.max_salary > (SELECT AVG(max_salary) FROM jobs);

-- 2. Problem: Find employees who earn more than their department's average salary 
-- but do NOT work in departments with more than 5 employees.
SELECT e.employee_id, e.first_name, e.last_name, e.salary
FROM employees e
JOIN (SELECT department_id, AVG(salary) AS avg_sal FROM employees GROUP BY department_id) d 
  ON e.department_id = d.department_id
WHERE e.salary > d.avg_sal
  AND (e.department_id NOT IN (SELECT department_id 
                               FROM employees 
                               GROUP BY department_id 
                               HAVING COUNT(employee_id) > 5) 
       OR e.department_id IS NULL);

-- 3. Problem: Write a SQL query to find employees from the USA who have a manager (using EXISTS), 
-- no job_history records (using NOT EXISTS), and salary greater than their department average. 
-- Display full_name, salary, and use a CASE statement to label 'USA Star' if salary > 1.4 times 
-- department average, otherwise 'USA Above'.
SELECT e.first_name || ' ' || e.last_name AS full_name,
       e.salary,
       CASE 
           WHEN e.salary > 1.4 * da.avg_sal THEN 'USA Star'
           ELSE 'USA Above'
       END AS label
FROM employees e
JOIN departments d ON e.department_id = d.department_id
JOIN locations l ON d.location_id = l.location_id
JOIN countries c ON l.country_id = c.country_id
JOIN (SELECT department_id, AVG(salary) as avg_sal FROM employees GROUP BY department_id) da 
  ON e.department_id = da.department_id
WHERE c.country_id = 'US'
  AND EXISTS (SELECT 1 FROM employees m WHERE m.employee_id = e.manager_id)
  AND NOT EXISTS (SELECT 1 FROM job_history jh WHERE jh.employee_id = e.employee_id)
  AND e.salary > da.avg_sal;

-- 4. Problem: Write an SQL query to list all departments where every employee earns more than 5000. 
-- For each department, display the department name, the number of employees in that department, 
-- and a column called Salary_Level that uses a CASE statement to show 'Above' if the department’s 
-- average salary is higher than the overall company average salary, or 'Below or Equal' if it is not.
SELECT d.department_name, 
       COUNT(e.employee_id) AS number_of_employees,
       CASE 
           WHEN AVG(e.salary) > (SELECT AVG(salary) FROM employees) THEN 'Above'
           ELSE 'Below or Equal'
       END AS Salary_Level
FROM departments d
JOIN employees e ON d.department_id = e.department_id
GROUP BY d.department_id, d.department_name
HAVING MIN(e.salary) > 5000;

-- 5. Problem: Write an SQL query to find employees who earn more than the highest salary 
-- of at least one other department. Only include employees whose department has at least 3 employees 
-- and who do not have any records in the JOB_HISTORY table. 
-- For each qualifying employee, display the employee ID, full name, department name, and salary.
SELECT e.employee_id, 
       e.first_name || ' ' || e.last_name AS full_name, 
       d.department_name, 
       e.salary
FROM employees e
JOIN departments d ON e.department_id = d.department_id
WHERE e.salary > (
      SELECT MIN(max_sal) 
      FROM (SELECT MAX(salary) AS max_sal 
            FROM employees 
            WHERE department_id IS NOT NULL 
            GROUP BY department_id)
      )
  AND e.department_id IN (SELECT department_id 
                          FROM employees 
                          GROUP BY department_id 
                          HAVING COUNT(employee_id) >= 3)
  AND e.employee_id NOT IN (SELECT employee_id FROM job_history);



/* =====================================================================
   Sec A.pdf Solutions 
   ===================================================================== */

/*
(1) Find all employees who earn strictly more than the overall average salary of the entire company[cite: 5].
For each matching employee, display:
1. EMPLOYEE_ID
2. FULL_NAME (First and Last name concatenated)
3. DEPARTMENT_NAME
4. SALARY
Order results by SALARY descending[cite: 5].
*/
SELECT e.employee_id, 
       e.first_name || ' ' || e.last_name AS full_name, 
       d.department_name, 
       e.salary
FROM employees e
JOIN departments d ON e.department_id = d.department_id
WHERE e.salary > (SELECT AVG(salary) FROM employees)
ORDER BY e.salary DESC;

/*
(2) For each department, find the employee(s) with the second-highest salary[cite: 5].
Display the employee_id, full name, salary, department_name, and job_title[cite: 5].
Exclude departments that have fewer than two employees[cite: 5]. Sort the results by
department_name in ascending order and salary in descending order[cite: 5].
*/
WITH ValidDepartments AS (
    SELECT department_id
    FROM employees
    GROUP BY department_id
    HAVING COUNT(employee_id) >= 2
),
RankedSalaries AS (
    SELECT e.employee_id, 
           e.first_name || ' ' || e.last_name AS full_name, 
           e.salary, 
           e.department_id, 
           e.job_id,
           DENSE_RANK() OVER (PARTITION BY e.department_id ORDER BY e.salary DESC) as salary_rank
    FROM employees e
    WHERE e.department_id IN (SELECT department_id FROM ValidDepartments)
)
SELECT r.employee_id, 
       r.full_name, 
       r.salary, 
       d.department_name, 
       j.job_title
FROM RankedSalaries r
JOIN departments d ON r.department_id = d.department_id
JOIN jobs j ON r.job_id = j.job_id
WHERE r.salary_rank = 2
ORDER BY d.department_name ASC, r.salary DESC;

/*
(3) Find those employees whose salary is higher than the average salary of the
department he/she works in[cite: 5]. Print employee last name, salary, and department
name[cite: 5]. You cannot use join in the main query[cite: 5]. Use correlated sub-query in WHERE
clause[cite: 5]. You can use sub-query in the SELECT clause to print the department name[cite: 5].
*/
SELECT e.last_name, 
       e.salary, 
       (SELECT d.department_name 
        FROM departments d 
        WHERE d.department_id = e.department_id) AS department_name
FROM employees e
WHERE e.salary > (SELECT AVG(e2.salary) 
                  FROM employees e2 
                  WHERE e2.department_id = e.department_id);

/*
(4) Find all employees who belong to departments that satisfy both of the
following conditions using INTERSECT: 1. The department has more than 3 employees[cite: 5].
2. The department has an average salary greater than 6,000$[cite: 5].
For all current employees working in these intersecting departments, display:
● EMPLOYEE_ID
● FULL_NAME (First and Last name concatenated)
● DEPARTMENT_NAME
● SALARY
● SALARY_TIER: A CASE statement returning 'Top Tier' if salary>10,000$,
otherwise 'Standard Tier'[cite: 5].
● Order results by DEPARTMENT_NAME ascending, then SALARY
descending[cite: 5].
*/
WITH IntersectingDepts AS (
    SELECT department_id
    FROM employees
    GROUP BY department_id
    HAVING COUNT(employee_id) > 3
    
    INTERSECT
    
    SELECT department_id
    FROM employees
    GROUP BY department_id
    HAVING AVG(salary) > 6000
)
SELECT e.employee_id, 
       e.first_name || ' ' || e.last_name AS full_name, 
       d.department_name, 
       e.salary,
       CASE 
           WHEN e.salary > 10000 THEN 'Top Tier'
           ELSE 'Standard Tier'
       END AS salary_tier
FROM employees e
JOIN departments d ON e.department_id = d.department_id
WHERE e.department_id IN (SELECT department_id FROM IntersectingDepts)
ORDER BY d.department_name ASC, e.salary DESC;
