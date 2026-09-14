-- ==============================================================================
-- SET A: ONLINE - 3 (A1_A2)[cite: 1]
-- ==============================================================================

-- ------------------------------------------------------------------------------
-- Problem 1:
-- Write a PL/SQL trigger that will enforce the following business rule:
-- No employee's salary should ever exceed the maximum salary and fall below the
-- minimum salary defined for their job in the JOBS table.[cite: 1]
-- ------------------------------------------------------------------------------
CREATE OR REPLACE TRIGGER check_salary_range
BEFORE INSERT OR UPDATE OF salary, job_id ON employees
FOR EACH ROW
DECLARE
    v_min_sal jobs.min_salary%TYPE;
    v_max_sal jobs.max_salary%TYPE;
BEGIN
    -- Fetch the min and max salary for the employee's job
    SELECT min_salary, max_salary INTO v_min_sal, v_max_sal
    FROM jobs 
    WHERE job_id = :NEW.job_id;

    -- Check if the new salary falls outside the allowed range
    IF :NEW.salary < v_min_sal OR :NEW.salary > v_max_sal THEN
        RAISE_APPLICATION_ERROR(-20001, 'Salary must be between ' || v_min_sal || ' and ' || v_max_sal);
    END IF;
END;
/

-- ------------------------------------------------------------------------------
-- Problem 2:
-- Write a PL/SQL procedure that does the following:
-- Takes two input values: employee ID and new department ID
-- If the employee exists and the department is valid:
-- Save the current job details into the JOB_HISTORY table before the change
-- Move the employee to the new department
-- Increase their salary by 15%
-- Set an OUT parameter with a valid message (a short explanation like "Employee
-- transferred successfully with 15% salary increase," or "Employee does not exist.",
-- etc.)[cite: 1]
-- ------------------------------------------------------------------------------
CREATE OR REPLACE PROCEDURE transfer_employee (
    p_emp_id IN employees.employee_id%TYPE,
    p_new_dept_id IN employees.department_id%TYPE,
    p_message OUT VARCHAR2
) AS
    v_emp_count NUMBER;
    v_dept_count NUMBER;
    v_old_job employees.job_id%TYPE;
    v_old_dept employees.department_id%TYPE;
    v_hire_date employees.hire_date%TYPE;
BEGIN
    -- Validate if the employee and department exist
    SELECT COUNT(*) INTO v_emp_count FROM employees WHERE employee_id = p_emp_id;
    SELECT COUNT(*) INTO v_dept_count FROM departments WHERE department_id = p_new_dept_id;

    IF v_emp_count > 0 AND v_dept_count > 0 THEN
        -- Get the current job details to archive
        SELECT job_id, department_id, hire_date INTO v_old_job, v_old_dept, v_hire_date
        FROM employees WHERE employee_id = p_emp_id;

        -- Save into JOB_HISTORY
        INSERT INTO job_history (employee_id, start_date, end_date, job_id, department_id)
        VALUES (p_emp_id, v_hire_date, SYSDATE, v_old_job, v_old_dept);

        -- Update employee record
        UPDATE employees
        SET department_id = p_new_dept_id,
            salary = salary * 1.15
        WHERE employee_id = p_emp_id;

        p_message := 'Employee transferred successfully with 15% salary increase.';
    ELSE
        p_message := 'Employee or Department does not exist.';
    END IF;
END;
/


-- ==============================================================================
-- SET B: ONLINE - 3 (B1_B2)[cite: 2]
-- ==============================================================================

-- ------------------------------------------------------------------------------
-- Problem 1:
-- Write a PL/SQL trigger that will enforce the following business rule:
-- If an employee has a manager, that manager must be in the same department as the
-- employee.[cite: 2]
-- ------------------------------------------------------------------------------
CREATE OR REPLACE TRIGGER check_manager_dept
BEFORE INSERT OR UPDATE OF manager_id, department_id ON employees
FOR EACH ROW
DECLARE
    v_mgr_dept employees.department_id%TYPE;
BEGIN
    -- Only check if the employee is assigned a manager
    IF :NEW.manager_id IS NOT NULL THEN
        -- Find the manager's department
        SELECT department_id INTO v_mgr_dept
        FROM employees
        WHERE employee_id = :NEW.manager_id;

        -- Compare the departments
        IF :NEW.department_id != v_mgr_dept THEN
            RAISE_APPLICATION_ERROR(-20002, 'Manager must be in the same department.');
        END IF;
    END IF;
END;
/

-- ------------------------------------------------------------------------------
-- Problem 2:
-- Write a PL/SQL function that updates an employee's salary according to the
-- following requirements:
-- Take four input parameters: employee ID, percentage change, user, and reason.
-- The company policy for salary change percentage is:
-- Maximum salary increase: 30%
-- Maximum salary decrease: 20%
-- Make proper validation of all inputs
-- Calculate the new salary and update it in the appropriate table.
-- Insert a record into SALARY_AUDIT_LOG
-- Returns appropriate status message, like 'Invalid percentage range,' or 'Invalid
-- employee ID,' or 'Salary updated successfully and audit recorded,' etc.[cite: 2]
-- ------------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION update_salary_audit (
    p_emp_id IN NUMBER,
    p_percent_change IN NUMBER,
    p_user IN VARCHAR2,
    p_reason IN VARCHAR2
) RETURN VARCHAR2 AS
    v_old_sal employees.salary%TYPE;
    v_new_sal employees.salary%TYPE;
    v_emp_exists NUMBER;
BEGIN
    -- Validate percentage range
    IF p_percent_change > 30 OR p_percent_change < -20 THEN
        RETURN 'Invalid percentage range.';
    END IF;

    -- Validate employee ID
    SELECT COUNT(*) INTO v_emp_exists FROM employees WHERE employee_id = p_emp_id;
    IF v_emp_exists = 0 THEN
        RETURN 'Invalid employee ID.';
    END IF;

    -- Get current salary and calculate the new salary
    SELECT salary INTO v_old_sal FROM employees WHERE employee_id = p_emp_id;
    v_new_sal := v_old_sal + (v_old_sal * (p_percent_change / 100));

    -- Update the salary
    UPDATE employees SET salary = v_new_sal WHERE employee_id = p_emp_id;

    -- Log the change into the audit table
    INSERT INTO SALARY_AUDIT_LOG (employee_id, old_salary, new_salary, changed_by, change_date, reason)
    VALUES (p_emp_id, v_old_sal, v_new_sal, p_user, SYSDATE, p_reason);

    RETURN 'Salary updated successfully and audit recorded.';
END;
/


-- ==============================================================================
-- SET C: ONLINE - 3 (C1_C2)[cite: 3]
-- ==============================================================================

-- ------------------------------------------------------------------------------
-- Problem 1:
-- Write a PL/SQL trigger that will enforce the following business rule:
-- The total salary paid to all employees in any single department must never exceed
-- $50,000 after any salary modification.[cite: 3]
-- ------------------------------------------------------------------------------
CREATE OR REPLACE TRIGGER check_dept_salary_limit
BEFORE INSERT OR UPDATE OF salary, department_id ON employees
FOR EACH ROW
DECLARE
    v_total_salary NUMBER;
    PRAGMA AUTONOMOUS_TRANSACTION; -- Prevents mutating table error
BEGIN
    IF :NEW.department_id IS NOT NULL THEN
        -- Sum the salaries of all OTHER employees in the department
        SELECT NVL(SUM(salary), 0) INTO v_total_salary
        FROM employees
        WHERE department_id = :NEW.department_id
        AND employee_id != :NEW.employee_id; 

        -- Add the current employee's new salary to the total
        IF (v_total_salary + :NEW.salary) > 50000 THEN
            RAISE_APPLICATION_ERROR(-20003, 'Total department salary cannot exceed $50,000.');
        END IF;
    END IF;
END;
/

-- ------------------------------------------------------------------------------
-- Problem 2:
-- Write a PL/SQL function that checks whether an employee is currently paid more than
-- they should be according to their job and years of service.
-- The function should:
-- Take one input parameter: employee ID
-- Check if the employee exists in the EMPLOYEES table. If not return NULL
-- Calculate years of service = number of full years since hire date until today (use FLOOR)
-- Apply this simple overpay rule:
-- If years of service >= 10 employee should not earn more than 95% of maximum salary
-- If years of service >= 5 and < 10 employee should not earn more than 90% of the maximum salary
-- If years of service < 5 employee should not earn more than 85% of the maximum salary
-- Return:
-- 1 if the employee is overpaid (salary > the allowed percentage of maximum salary)
-- 0 if the employee is within the allowed range
-- -1 if the employee has no job or the job data is missing[cite: 3]
-- ------------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION check_overpaid (
    p_emp_id IN NUMBER
) RETURN NUMBER AS
    v_emp_exists NUMBER;
    v_hire_date DATE;
    v_salary NUMBER;
    v_job_id employees.job_id%TYPE;
    v_max_salary jobs.max_salary%TYPE;
    v_years_of_service NUMBER;
    v_allowed_pct NUMBER;
BEGIN
    -- Check if employee exists
    SELECT COUNT(*) INTO v_emp_exists FROM employees WHERE employee_id = p_emp_id;
    IF v_emp_exists = 0 THEN
        RETURN NULL;
    END IF;

    -- Fetch employee details
    SELECT hire_date, salary, job_id INTO v_hire_date, v_salary, v_job_id
    FROM employees WHERE employee_id = p_emp_id;

    -- Check for missing job data
    IF v_job_id IS NULL THEN
        RETURN -1;
    END IF;

    -- Fetch max salary for their job
    SELECT max_salary INTO v_max_salary FROM jobs WHERE job_id = v_job_id;

    -- Calculate full years of service using FLOOR
    v_years_of_service := FLOOR(MONTHS_BETWEEN(SYSDATE, v_hire_date) / 12);

    -- Apply the overpay logic rules
    IF v_years_of_service >= 10 THEN
        v_allowed_pct := 0.95;
    ELSIF v_years_of_service >= 5 THEN
        v_allowed_pct := 0.90;
    ELSE
        v_allowed_pct := 0.85;
    END IF;

    -- Compare current salary against allowed limit
    IF v_salary > (v_max_salary * v_allowed_pct) THEN
        RETURN 1; -- Overpaid
    ELSE
        RETURN 0; -- Within range
    END IF;
END;
/
