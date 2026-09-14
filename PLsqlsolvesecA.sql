-- ==============================================================================
-- SECTION A: ONLINE - CSE 216 (A1_A2)[cite: 4]
-- ==============================================================================

-- ------------------------------------------------------------------------------
-- Problem 1 (Initial Setup):
-- The organization wants to generate a summary of employees who act as managers. 
-- Create the MANAGER_SUMMARY table.[cite: 4]
-- ------------------------------------------------------------------------------
CREATE TABLE MANAGER_SUMMARY (
    MANAGER_ID NUMBER,
    DEPARTMENT_ID NUMBER,
    MANAGER_NAME VARCHAR2(100),
    DIRECT_REPORT_COUNT NUMBER,
    GENERATED_BY VARCHAR2(30),
    GENERATED_ON DATE
);

-- ------------------------------------------------------------------------------
-- Problem 1(a):
-- Write a function GET_DIRECT_REPORT_COUNT (P_EMP_ID) that returns the number 
-- of employees who directly report to the employee identified by P_EMP_ID. 
-- Return 0 if the employee has no direct reports.[cite: 4]
-- ------------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION GET_DIRECT_REPORT_COUNT (
    P_EMP_ID IN NUMBER
) RETURN NUMBER AS
    v_count NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_count
    FROM employees
    WHERE manager_id = p_emp_id;
    
    RETURN v_count;
END;
/

-- ------------------------------------------------------------------------------
-- Problem 1(b):
-- Write a procedure BUILD_MANAGER_SUMMARY that processes employees in a given 
-- department. If they are managers (>0 direct reports), insert them into the 
-- summary table and output the total managers and total reports. 
-- If empty, print "No employees in department <id>" and return 0s.[cite: 4]
-- ------------------------------------------------------------------------------
CREATE OR REPLACE PROCEDURE BUILD_MANAGER_SUMMARY (
    P_DEPT_ID IN NUMBER,
    P_MANAGER_COUNT OUT NUMBER,
    P_TOTAL_REPORTS OUT NUMBER
) AS
    v_emp_count NUMBER := 0;
    v_reports NUMBER;
BEGIN
    -- Initialize OUT variables
    P_MANAGER_COUNT := 0;
    P_TOTAL_REPORTS := 0;

    -- Check if department has any employees
    SELECT COUNT(*) INTO v_emp_count 
    FROM employees 
    WHERE department_id = P_DEPT_ID;

    IF v_emp_count = 0 THEN
        DBMS_OUTPUT.PUT_LINE('No employees in department ' || P_DEPT_ID);
        RETURN; -- Exit early
    END IF;

    -- Delete previous summary rows for this department
    DELETE FROM MANAGER_SUMMARY WHERE DEPARTMENT_ID = P_DEPT_ID;

    -- Process every employee in the given department
    FOR emp IN (SELECT employee_id, first_name || ' ' || last_name AS full_name, department_id 
                FROM employees 
                WHERE department_id = P_DEPT_ID) 
    LOOP
        -- Call function to check direct reports
        v_reports := GET_DIRECT_REPORT_COUNT(emp.employee_id);
        
        -- If they are a manager, record them
        IF v_reports > 0 THEN
            INSERT INTO MANAGER_SUMMARY (
                MANAGER_ID, DEPARTMENT_ID, MANAGER_NAME, 
                DIRECT_REPORT_COUNT, GENERATED_BY, GENERATED_ON
            ) VALUES (
                emp.employee_id, emp.department_id, emp.full_name, 
                v_reports, USER, SYSDATE
            );
            
            -- Update running totals
            P_MANAGER_COUNT := P_MANAGER_COUNT + 1;
            P_TOTAL_REPORTS := P_TOTAL_REPORTS + v_reports;
        END IF;
    END LOOP;
END;
/

-- ------------------------------------------------------------------------------
-- Problem 1(c):
-- Write an anonymous block that executes the procedure for Department 50 
-- and prints the two OUT parameter values.[cite: 4]
-- ------------------------------------------------------------------------------
SET SERVEROUTPUT ON;
DECLARE
    v_mgr_count NUMBER;
    v_total_reps NUMBER;
BEGIN
    BUILD_MANAGER_SUMMARY(50, v_mgr_count, v_total_reps);
    DBMS_OUTPUT.PUT_LINE('Total Managers in Dept 50: ' || v_mgr_count);
    DBMS_OUTPUT.PUT_LINE('Total Direct Reports: ' || v_total_reps);
END;
/


-- ==============================================================================
-- Problem 2: Department Relocation Salary Adjustment[cite: 4]
-- ==============================================================================

-- ------------------------------------------------------------------------------
-- Problem 2 (Initial Setup):
-- Create the DEPT_RELOCATION_LOG table.[cite: 4]
-- ------------------------------------------------------------------------------
CREATE TABLE DEPT_RELOCATION_LOG (
    DEPARTMENT_ID NUMBER,
    OLD_LOCATION_ID NUMBER,
    NEW_LOCATION_ID NUMBER,
    OLD_CITY VARCHAR2(30),
    NEW_CITY VARCHAR2(30),
    RELOCATION_TYPE VARCHAR2(15),
    RAISE_PCT NUMBER,
    EMPLOYEES_AFFECTED NUMBER,
    CHANGED_BY VARCHAR2(30),
    CHANGED_ON DATE
);

-- ------------------------------------------------------------------------------
-- Problem 2 (Trigger):
-- Create a row-level trigger on DEPARTMENTS that fires whenever LOCATION_ID is updated. 
-- Determine relocation distance (DOMESTIC, REGIONAL, OVERSEAS), apply a salary 
-- raise to the department's employees, and insert a row into DEPT_RELOCATION_LOG.[cite: 4]
-- ------------------------------------------------------------------------------
CREATE OR REPLACE TRIGGER trg_dept_relocation
AFTER UPDATE OF location_id ON departments
FOR EACH ROW
DECLARE
    v_old_city locations.city%TYPE;
    v_old_country locations.country_id%TYPE;
    v_old_region countries.region_id%TYPE;
    
    v_new_city locations.city%TYPE;
    v_new_country locations.country_id%TYPE;
    v_new_region countries.region_id%TYPE;
    
    v_reloc_type VARCHAR2(15);
    v_raise_pct NUMBER;
    v_emp_count NUMBER;
BEGIN
    -- If location did not actually change, skip processing
    IF :OLD.location_id = :NEW.location_id THEN
        RETURN;
    END IF;

    -- Look up OLD city, country, and region
    SELECT l.city, l.country_id, c.region_id
    INTO v_old_city, v_old_country, v_old_region
    FROM locations l JOIN countries c ON l.country_id = c.country_id
    WHERE l.location_id = :OLD.location_id;

    -- Look up NEW city, country, and region
    SELECT l.city, l.country_id, c.region_id
    INTO v_new_city, v_new_country, v_new_region
    FROM locations l JOIN countries c ON l.country_id = c.country_id
    WHERE l.location_id = :NEW.location_id;

    -- Decide the relocation type and raise percentage
    IF v_old_country = v_new_country THEN
        v_reloc_type := 'DOMESTIC';
        v_raise_pct := 3;
    ELSIF v_old_region = v_new_region THEN
        v_reloc_type := 'REGIONAL';
        v_raise_pct := 8;
    ELSE
        v_reloc_type := 'OVERSEAS';
        v_raise_pct := 12;
    END IF;

    -- Count affected employees currently in the department
    SELECT COUNT(*) INTO v_emp_count
    FROM employees
    WHERE department_id = :NEW.department_id;

    -- Update salaries for the affected employees
    UPDATE employees
    SET salary = ROUND(salary * (1 + (v_raise_pct / 100)), 2)
    WHERE department_id = :NEW.department_id;

    -- Insert record into the log table
    INSERT INTO DEPT_RELOCATION_LOG (
        DEPARTMENT_ID, OLD_LOCATION_ID, NEW_LOCATION_ID, 
        OLD_CITY, NEW_CITY, RELOCATION_TYPE, RAISE_PCT, 
        EMPLOYEES_AFFECTED, CHANGED_BY, CHANGED_ON
    ) VALUES (
        :NEW.department_id, :OLD.location_id, :NEW.location_id,
        v_old_city, v_new_city, v_reloc_type, v_raise_pct,
        v_emp_count, USER, SYSDATE
    );
END;
/

-- ------------------------------------------------------------------------------
-- Problem 2 (Test Driver):
-- Demonstrate your trigger using suitable UPDATE statements and verification queries.[cite: 4]
-- ------------------------------------------------------------------------------
-- Test 1: DOMESTIC move (Southlake US -> South San Francisco US)
UPDATE DEPARTMENTS SET LOCATION_ID = 1500 WHERE DEPARTMENT_ID = 60;

-- Test 2: REGIONAL move (Munich DE -> London GB, both Europe)
UPDATE DEPARTMENTS SET LOCATION_ID = 2400 WHERE DEPARTMENT_ID = 70;

-- Test 3: OVERSEAS move (Toronto CA, Americas -> Tokyo JP, Asia)
UPDATE DEPARTMENTS SET LOCATION_ID = 1200 WHERE DEPARTMENT_ID = 20;

-- Test 4: Assign the same location (London to London)
UPDATE DEPARTMENTS SET LOCATION_ID = 2400 WHERE DEPARTMENT_ID = 40;

-- Test 5: Two empty departments in ONE statement (Seattle -> Toronto)
UPDATE DEPARTMENTS SET LOCATION_ID = 1800 WHERE DEPARTMENT_ID IN (130, 140);

-- Verify the Log
SELECT DEPARTMENT_ID, OLD_LOCATION_ID, NEW_LOCATION_ID, 
       OLD_CITY, NEW_CITY, RELOCATION_TYPE, 
       RAISE_PCT, EMPLOYEES_AFFECTED
FROM DEPT_RELOCATION_LOG
ORDER BY DEPARTMENT_ID;

-- Verify the Employees' Salaries changed correctly
SELECT EMPLOYEE_ID, DEPARTMENT_ID, SALARY
FROM EMPLOYEES
WHERE DEPARTMENT_ID IN (20, 40, 60, 70)
ORDER BY DEPARTMENT_ID, EMPLOYEE_ID;

-- Clean up
ROLLBACK;
